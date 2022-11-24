package utils;

import LGL.align.GlobalAlignment;
import errors.MyError;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SplitByRes {
    public static String[] replaceN(String[] ligs) {
        int Nlig = 0;

        for(int i = 0; i < ligs.length; ++i) {
            if (ligs[i].contains("N") || ligs[i].contains("n")) {
                Nlig += 3;
            }
        }

        String[] ligs2 = new String[Nlig + ligs.length];
        int i;
        if (Nlig > 0) {
            int k = 0;

            for(i = 0; i < ligs.length; ++i) {
                if (ligs[i].contains("N")) {
                    ligs2[k] = ligs[i].replace('N', 'A');
                    ligs2[k + 1] = ligs[i].replace('N', 'T');
                    ligs2[k + 2] = ligs[i].replace('N', 'C');
                    ligs2[k + 3] = ligs[i].replace('N', 'G');
                    k += 4;
                } else if (ligs[i].contains("n")) {
                    ligs2[k] = ligs[i].replace('n', 'A');
                    ligs2[k + 1] = ligs[i].replace('n', 'T');
                    ligs2[k + 2] = ligs[i].replace('n', 'C');
                    ligs2[k + 3] = ligs[i].replace('n', 'G');
                    k += 4;
                } else {
                    ligs2[k] = ligs[i];
                    ++k;
                }
            }
        } else {
            ligs2 = ligs;
        }

        String[] var7 = ligs2;
        int var6 = ligs2.length;

        for(i = 0; i < var6; ++i) {
            String str = var7[i];
//            System.out.println("[Enzyme] " + str);
        }

        List mylist = Arrays.asList(ligs2);
        Set myset = new HashSet(mylist);
        String[] ligs3 = (String[])myset.toArray(new String[0]);
        return ligs3;
    }

    public static String getLigationSite(String ligation_site) {
        String site = "";
        if (ligation_site.equalsIgnoreCase("HindIII")) {
            site = "A^AGCTT";
        } else if (ligation_site.equalsIgnoreCase("MboI")) {
            site = "^GATC";
        } else if (ligation_site.equalsIgnoreCase("BglII")) {
            site = "A^GATCT";
        } else if (ligation_site.equalsIgnoreCase("DpnII")) {
            site = "^GATC";
        } else if (ligation_site.equalsIgnoreCase("Sau3AI")) {
            site = "^GATC";
        } else if (ligation_site.equalsIgnoreCase("NlaIII")) {
            site = "CATG^";
        } else if (ligation_site.equalsIgnoreCase("Hinf1")) {
            site = "G^ANTC";
        } else if (ligation_site.equalsIgnoreCase("AluI")) {
            site = "AG^CT";
        } else if (! ligation_site.contains("^")) {
            System.out.println("Error: the restriction position has to be specified using '^'\nPlease, use '^' to specify the cutting position\ni.e A^GATCT for HindIII digestion.\n");
            System.exit(0);
        } else {
            site = ligation_site;
        }
//        错误字符排除
        for(int j = 0; j < site.length(); ++j) {
            if (site.charAt(j) != 'C' && site.charAt(j) != 'c' && site.charAt(j) != 'T' && site.charAt(j) != 't' && site.charAt(j) != 'G' && site.charAt(j) != 'g' && site.charAt(j) != 'A' && site.charAt(j) != 'a' && site.charAt(j) != '^' && site.charAt(j) != 'N' && site.charAt(j) != 'n') {
                System.out.println("Error: \nPlease print HindIII/MboI/BglII/DpnII or restriction site with '^' and contains 'ATCG' without other character!!! " + site + " : " + site.charAt(j));
                System.exit(0);
            }
        }

        return site;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java -cp pore-c_tool.jar utils.SplitByRes <fastq> <fastq.splited> <ResSite>");
            System.exit(0);
        }
        String fq = args[0];
        MyUtil.checkPath(fq);
        String fqs = args[1];
        String ligation_site = args[2];
        String[] ligations = ligation_site.split(",");
        String[] ligation_sites1 = ligation_site.split(",");
        for(int k = 0; k < ligations.length; ++k) {
            ligation_sites1[k] = getLigationSite(ligations[k]);
        }
        String[] ligation_sites = replaceN(ligation_sites1);
        System.out.println("[Enzyme site number] " + ligation_sites.length);
        System.out.println(Arrays.toString(ligation_sites));

        BufferedReader reader;
        BufferedOutputStream writer;

        if (MyUtil.isGZipped(new File(fq))){
            reader  = new BufferedReader(
                    new InputStreamReader(new GZIPInputStream(new FileInputStream(fq))));
        }else{
            reader = new BufferedReader((
                    new FileReader(
                            new File(fq)
                    )
                    ));
        }

        writer = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(fqs)));

        String line;
        String aline;
        long num=0;
        Read read = new Read();

        while((line= reader.readLine())!=null){
            num ++;
            if (num!=1 && num%4==1) {
                int start=0;
                for (String site :
                        ligation_sites) {
                    site=site.replace("^","");
                    while (read.getSeq().indexOf(site,start)!=-1) {
                        int preStart =start;
                        start = read.getSeq().indexOf(site,preStart);
                        aline = (read.getName().split("\\s")[0]+"_"+preStart+"-"+start+"\n"
                                +read.getSeq().substring(preStart,start)+"\n"
                                +read.getAdded()+"\n"
                                +read.getQuality().substring(preStart,start)
                        );
                        writer.write((aline+"\n").getBytes(StandardCharsets.UTF_8));
                        start+=site.length();
                    }
                    if (start < read.getSeq().length()) {
                        aline = (read.getName().split("\\s")[0]+"_" + start + "-" + read.getSeq().length()+ "\n"
                                + read.getSeq().substring(start, read.getSeq().length()) + "\n"
                                + read.getAdded() + "\n"
                                + read.getQuality().substring(start, read.getSeq().length())
                        );
                        writer.write((aline + "\n").getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
            switch ((int) (num%4)){
                case 1:
                    read.setName(line);
                    break;
                case 2:
                    read.setSeq(line);
                    break;
                case 0:
                    read.setQuality(line);
                    break;
                case 3:
                    read.setAdded(line);
                    break;
            }
        }

        reader.close();
        writer.close();
    }
}
