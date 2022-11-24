package utils;

import process.contact.ReadAT;

import java.io.*;
import java.util.*;

public class Contact2Matrix {
    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.out.println("Usage: java -cp pore-c_tool.jar utils.Contact2Matrix <chromSize> <monomers> <outFile> <binSize>");
            System.exit(0);
        }
        String chromName = args[0];
        String monomers = args[1];
        String outFileName = args[2];
        int binSize = Integer.parseInt(args[3]);

        MyUtil.checkPath(chromName);
        MyUtil.checkPath(monomers);

        BufferedReader cReader = new BufferedReader(new FileReader(new File(chromName)));
        BufferedReader mReader = new BufferedReader(new FileReader(new File(monomers)));
        BufferedWriter mWriter = new BufferedWriter(new FileWriter(new File(outFileName)));
        BufferedWriter pWriter = new BufferedWriter(new FileWriter(new File(outFileName + ".bed")));

        String line;
        //生成bin区间文件
        System.out.println(outFileName+".bed "+ "is generating, bin-size: "+ binSize);
        Map<Long, Region> regionMap = new HashMap<>(); //存储bin区间
        long num=1;
        long i;
        while ((line = cReader.readLine())!=null){
            String[] fields = line.split("\t");
//            System.out.println(Arrays.toString(fields));
            String chrom=fields[0];
            long length=Long.parseLong(fields[1]);
            //按bin划分区间
            for (i=0; i+binSize<length; i+=binSize){
                Region region = new Region(chrom, i, i + binSize);
                regionMap.put(num, region);
                pWriter.write(region+"\t"+num);
                pWriter.newLine();
            }
            //划分该染色体最后一个区间
            {
                Region region = new Region(chrom, i, length);
                regionMap.put(num, region);
                pWriter.write(region+"\t"+num);
                pWriter.newLine();
            }
            num++;
        }
        pWriter.close();
        System.out.println(outFileName+".bed "+ "is generated, bin-size: "+ binSize);

        //生成矩阵文件
        System.out.println(outFileName+ " is generating, bin-size: "+ binSize);
        Map<String,Interaction> interactionMap = new HashMap<String,Interaction>();
        List<ReadAT> concatemer=new ArrayList<ReadAT>();
        Set<ReadAT> mms = new HashSet<>();
        while ((line= mReader.readLine())!=null){
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
                            long binID1 = ((mm1.start+mm1.end)/2)/binSize+1;
                            long binID2 = ((mm2.start+mm2.end)/2)/binSize+1;
                            String mapID = binID1 + "-" + binID2;
                            if (interactionMap.containsKey(mapID)){
                                interactionMap.get(mapID).count++;
                            }else {
                                interactionMap.put(mapID, new Interaction(binID1,binID2));
                            }
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
                        long binID1 = ((mm1.start+mm1.end)/2)/binSize+1;
                        long binID2 = ((mm2.start+mm2.end)/2)/binSize+1;
                        String mapID = binID1 + "-" + binID2;
                        if (interactionMap.containsKey(mapID)){
                            interactionMap.get(mapID).count++;
                        }else {
                            interactionMap.put(mapID, new Interaction(binID1,binID2));
                        }
                    }
                }
            }
            concatemer.clear();
        }

        List<Interaction> interactions=new ArrayList<>();
        for (Object o : interactionMap.values().toArray()) {
            Interaction interaction = (Interaction) o;
            interactions.add(interaction);
        }

        interactions.sort(new InteractionComparator());

        for (Interaction interaction : interactions) {
            mWriter.write(interaction.toString());
            mWriter.newLine();
        }

        System.out.println(outFileName+ " is generated, bin-size: "+ binSize);

        cReader.close();
        mReader.close();
        mWriter.close();
    }
}
