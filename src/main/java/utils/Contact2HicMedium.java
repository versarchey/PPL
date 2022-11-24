package utils;

import process.contact.ReadAT;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Contact2HicMedium {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java -cp pore-c_tool.jar utils.Contact2HicMedium <monomers> <hic_medium>");
            System.exit(0);
        }

        String cF=args[0];
        String pF=args[1];
//        int binSize=Integer.parseInt(args[2]);

        String line;
        List<ReadAT> concatemer=new ArrayList<ReadAT>();
        //2pairs
        MyUtil.checkPath(cF);
        BufferedReader mmReader = new BufferedReader( new FileReader(cF));
        BufferedWriter pairWriter = new BufferedWriter(new FileWriter(pF));
        concatemer.clear();
        Set<ReadAT> mms = new HashSet<>();
//        line="## pairs format v1.0\n" +
//                "#columns: readID chr1 position1 chr2 position2 strand1 strand2";
//        pairWriter.write(line);
//        pairWriter.newLine();
        while ((line= mmReader.readLine())!=null){
            String[] fields = line.split("\t");
            ReadAT one = new ReadAT(fields);
            //将同一个concatemer的比对在一个批次进行处理
            if (concatemer.size() == 0){
                concatemer.add(one);
            } else if (one.readName.equals(concatemer.get(0).readName)){
                concatemer.add(one);
            } else {
                mms.clear();
                for (ReadAT mm1 :
                        concatemer) {
                    mms.add(mm1);
                    for (ReadAT mm2 :
                            concatemer) {
                        if (mms.contains(mm2))
                            continue;
                        else{
                            if (mm1.chr.compareTo(mm2.chr)>0 ){
                                ReadAT tmp = mm1;
                                mm1 = mm2;
                                mm2 = tmp;
                            }
                            line=
                                    mm1.readName+"\t"
                                    +(mm1.strand=='+'?0:1)+"\t"
                                    +mm1.chr+"\t"
                                    +((mm1.start+mm1.end)/2)+"\t"
                                    +0+"\t"
                                    +(mm2.strand=='-'?0:1)+"\t"
                                    +mm2.chr+"\t"
                                    +((mm2.start+mm2.end)/2)+"\t"
                                    +1+"\t"
                                    +mm1.mapq+"\t"
                                    +mm2.mapq
                            ;
                            pairWriter.write(line);
                            pairWriter.newLine();
                        }
                    }
                }
                concatemer.clear();
                concatemer.add(one);
            }
        }

        //处理最后一个
        {
            mms.clear();
            for (ReadAT mm1 :
                    concatemer) {
                mms.add(mm1);
                for (ReadAT mm2 :
                        concatemer) {
                    if (mms.contains(mm2))
                        continue;
                    else {
                        if (mm1.chr.compareTo(mm2.chr) > 0) {
                            ReadAT tmp = mm1;
                            mm1 = mm2;
                            mm2 = tmp;
                        }
                        line =
                                mm1.readName + "\t"
                                        + (mm1.strand == '+' ? 0 : 1) + "\t"
                                        + mm1.chr + "\t"
                                        + ((mm1.start + mm1.end) / 2) + "\t"
                                        + 0 + "\t"
                                        + (mm2.strand == '-' ? 0 : 1) + "\t"
                                        + mm2.chr + "\t"
                                        + ((mm2.start + mm2.end) / 2) + "\t"
                                        + 1 + "\t"
                                        + mm1.mapq + "\t"
                                        + mm2.mapq
                        ;
                        pairWriter.write(line);
                        pairWriter.newLine();
                    }
                }
            }
            concatemer.clear();
        }

        mmReader.close();
        pairWriter.close();

        String cmd = "sort -k3,3 -k7,7 --parallel `nproc` "+pF+" > "+pF+".sorted";
        FileWriter fileWriter = new FileWriter(new File("./tmp.sh"));
        fileWriter.write(cmd);
        fileWriter.close();

        System.out.println(cmd);
        MyUtil.runShell("./tmp.sh");

        MyUtil.shellrun("rm tmp.sh", "0");
    }

}
