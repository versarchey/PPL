package process;

import java.io.*;
import java.util.Calendar;

public class VirDigest {
     static void split_genome_byressite(String genomefile, String[] resS, String outfile, String ressite) throws IOException {
        //replace and find postion of '^' in res
         //存储每种酶的酶切位点位于识别序列的位置
        int[] posoff = findstartinres_arry(resS);
        //存储每种酶识别序列的长度
        int[] reslen = new int[resS.length];
        for(int i=0; i<resS.length; i++) {
            resS[i] = resS[i].replace("^", "").toUpperCase();
            reslen[i] = resS[i].length();
        }

        Calendar rightNow = Calendar.getInstance();

        //out res file point
        BufferedWriter resBufferedWriter = new BufferedWriter(new FileWriter(outfile));
        // for genoem file
        BufferedReader reader = new BufferedReader(new FileReader(genomefile));
        String line = reader.readLine();
        String chrom = "";
        //String chrseq = "";
        StringBuilder chrseqbuilder = new StringBuilder();
        String newline = "";
        //int index = -1;
        int chrlen = 0;
        int pstart = 0, pend = 0;
        int Nblock = 0;
         long num =0 ;
         int[] index_residx = new int[2];
        index_residx[0] = -1;
        //用于临时存储新一批次的酶切位点
        int[] resloci = new int[resS.length];
        while( line != null) {
            if(line.startsWith(">")) {
                if(!chrom.equals("") && !chrseqbuilder.equals("")) {
                    //System.out.println("111 "+ index_residx[0] + " " + index_residx[1] + " " + resloci[0]);
                    //开始寻找当前染色体的所有酶切位点
                    chrlen = chrseqbuilder.length();
                    //找到该染色体的第一个酶切位点
                    index_residx = findsmIndex(chrseqbuilder, resS, 0, resloci,chrlen); //[index, reslen]
                    //当找不到的时候，退出循环
                    while(index_residx[0]>=0 && index_residx[0]<chrlen-1) {
                        //print res site in outfile
                        if(index_residx[0] + posoff[index_residx[1]]>chrlen) {
                            pend = chrlen;
                        }else {
                            //识别序列所在染色体位置+酶切所在识别序列位置=酶切所在染色体位置
                            pend = index_residx[0]+posoff[index_residx[1]];
                        }
                        if(index_residx[0]>0 && pstart!=pend) {
                            newline = chrom + "\t" + pstart + "\t" + pend+"\t"+(pend-pstart)+"\t"+num;
                            num++;
                            //老的终止位点是新的开始位置
                            pstart = pend;
                            //System.out.println(newline);
                            resBufferedWriter.write(newline);
                            resBufferedWriter.newLine();
                            Nblock ++;
                        }

                        //index = chrseqbuilder.indexOf(res, index+reslen);
                        index_residx = findsmIndex(chrseqbuilder, resS, index_residx[0] + 1, resloci,chrlen); //reslen[index_residx[1]] cause many site for multipe eny
                        //System.out.println("xxx "+ index_residx[0] + " " + index_residx[1] + " " + pend);
                    }
                    pend=chrlen;

                    // last block in this chrom
                    if(pend<=chrlen && pstart!=pend) { //pend!=0 && pend ==0 means no res in chr
                        //pend++; // not add 1
//                        newline = chrom + "\t" + pend + "\t" + chrlen;
                        newline = chrom + "\t" + pstart + "\t" + pend+"\t"+(pend-pstart)+"\t"+num;
                        num++;
                        //System.out.println(newline);
                        resBufferedWriter.write(newline);
                        resBufferedWriter.newLine();
                        Nblock ++;
                    }
                    //init
                    pstart = 0;
                    rightNow = Calendar.getInstance();
                    System.out.println("[" + rightNow.getTime().toString() +"] Split " + chrom + " to " + Nblock + " blocks by " + ressite);
                    Nblock = 0;
                }

                //chrom = line.substring(1).split(" ")[0].split("\t")[0];
                chrom = line.substring(1).split("[ \t]+")[0];
                //System.out.println("Spliting genome file, "
                //		+ chrom);
                //init chrseq
                chrseqbuilder = chrseqbuilder.delete( 0, chrlen);
            }else {
                // whether need replace '\r\n' in java
                chrseqbuilder.append(line.toUpperCase());
            }
            line = reader.readLine();
        }
        // print last chrom
        chrlen = chrseqbuilder.length();
        index_residx = findsmIndex(chrseqbuilder, resS, 0, resloci, chrlen);
        Nblock=0;
        while(index_residx[0]>=0 && index_residx[0]<chrlen-1) {
            //print res site in outfile
            if(index_residx[0] + posoff[index_residx[1]]>chrlen) {
                pend = chrlen;
            }else {
                pend = index_residx[0]+posoff[index_residx[1]];
            }
            if(index_residx[0]>0 && pstart!=pend) {
//                newline = chrom + "\t" + pstart + "\t" + pend;
                newline = chrom + "\t" + pstart + "\t" + pend+"\t"+(pend-pstart)+"\t"+num;
                num++;
                pstart = pend;
                resBufferedWriter.write(newline);
                resBufferedWriter.newLine();
                Nblock ++;
            }
            //index = chrseqbuilder.indexOf(res, index+);
            index_residx = findsmIndex(chrseqbuilder, resS, index_residx[0] + 1, resloci, chrlen); //reslen[index_residx[1]]
        }
         pend=chrlen;
         if(pend<=chrlen && pstart!=pend) { //pend!=0 &&
            //pend++;
//            newline = chrom + "\t" + pend + "\t" + chrlen;
            newline = chrom + "\t" + pstart + "\t" + pend+"\t"+(pend-pstart)+"\t"+num;
            num++;
            resBufferedWriter.write(newline);
            resBufferedWriter.newLine();
            Nblock ++;
        }
        rightNow = Calendar.getInstance();
        System.out.println("[" + rightNow.getTime().toString() +"] Split " + chrom + " to " + Nblock + " blocks splited by " + ressite
                + "\n[" + rightNow.getTime().toString() +"] Split genome done!");
        //init
        chrseqbuilder = chrseqbuilder.delete( 0, chrlen);
        resBufferedWriter.close();
    }

    //获取所有酶切位点所在的位置
     static int[] findstartinres_arry(String[] resS) {
        int[] sites = new int[resS.length];
        for(int i=0; i<resS.length; i++) {
            sites[i] = findstartinres(resS[i]);
            if(sites[i] == -1) {
                System.out.println("Error: can not find '^' in ligation site!!!!\n");
                System.exit(0);
            }
        }
        return sites;
    }

     static int findstartinres(String res) {
        int i = -1;
        for(i=0; i<res.length(); i++) {
            if(res.charAt(i)=='^') {
                return i;
            }
        }
        return i;
    }

    //寻找改染色体从start位置开始第一个酶切位点，[index, type]
     static int[] findsmIndex(StringBuilder chrseqbuilder, String[] resS, int start, int[] resloci, int chrlen) {
        //存储新的酶切位点和酶的类型
        int newindex = chrlen+1,
                residx = -1;
        //获得各酶的第一个酶切位点
        for(int i=0;i<resS.length; i++) {
            if(resloci[i]<=start) {
                //若该酶在该染色体没有酶切位点，则返回-1
                resloci[i] = chrseqbuilder.indexOf(resS[i], start);
            }
        }
        //获得最近的一个酶切位点
        for(int i=0; i<resloci.length; i++) {
            //当该酶在该染色体有酶切位点，且酶切位点比之前找到的更近，则存储
            if(resloci[i]>=0 && resloci[i] < newindex) {
                newindex = resloci[i];
                residx = i;
            }
        }
        //
        int[] inarray = new int[2];
        inarray[0] = newindex;
        //当至少有一个酶有酶切位点的时候
        if(residx>=0) {
            resloci[residx] = chrseqbuilder.indexOf(resS[residx], start);
        }else {
            residx=0;
        }
        inarray[1] = residx;
        return inarray;
    }

    public void processVD(Path p) throws IOException {
        if (p.restrictionsiteFile.equalsIgnoreCase("None"))
            p.restrictionsiteFile = p.OUTPUT_DIRECTORY + "/" + p.OUTPUT_PREFIX + "/" + p.OUTPUT_PREFIX +".res.bed";
//        if (!checkPathV(p.restrictionsiteFile)){
            split_genome_byressite(p.genomefile, p.ligation_sites, p.restrictionsiteFile, p.ligation_site);
//        } else {
//            System.out.println("Warning: Restriction site file exited. We will not regenerate again. "
//                    + "\n                If you want to regenerate a new enzyme digestion site file, "
//                    + "\n                please remove the file and run the program again. "
//            );
//        }
    }
}
