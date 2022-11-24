package process.contact;

import com.google.common.collect.Lists;
import process.Path;
import process.contact.FragmentPurifying;
import process.mapping.Bed2AlignTable;
import process.mapping.ReadBed;
import utils.MyUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AlignTable2Contact {
    public AlignTable2Contact(String dataType, String inputFile, String outPrefix, int cutoff){

    }

    //res-split模式
    public AlignTable2Contact(String inputFile, String outPrefix, Path p) throws IOException {
        int cutoff = Integer.parseInt(p.MAPPING_CUTOFF);
        
        BufferedReader atReader = new BufferedReader( new FileReader(inputFile));
        new File(outPrefix + ".monomers").delete();
        BufferedWriter mmWriter = new BufferedWriter( new FileWriter(outPrefix+".monomers"));
        new File(outPrefix + ".singleton").delete();
        BufferedWriter singletonWriter = new BufferedWriter( new FileWriter(outPrefix+".singleton"));
        new File(outPrefix + ".lowQuality").delete();
        BufferedWriter lowQualityWriter = new BufferedWriter( new FileWriter(outPrefix+".lowQuality"));
        BufferedWriter resRemoveWriter = null;
        if (p.ligation_type.equals("res")){
            new File(outPrefix + ".resRemove").delete();
            resRemoveWriter = new BufferedWriter( new FileWriter(outPrefix+".resRemove"));
        }
        new File(outPrefix + ".disRemove").delete();
        BufferedWriter disRemoveWriter = new BufferedWriter( new FileWriter(outPrefix+".disRemove"));
        BufferedWriter shortRemoveWriter = null;
//        if (p.splitReads.equals("N")) {
            new File(outPrefix + ".notOnShortestPath").delete();
            shortRemoveWriter = new BufferedWriter(new FileWriter(outPrefix + ".notOnShortestPath"));
//        }

        String line;
        List<ReadAT> concatemer=new ArrayList<ReadAT>();
        //用于处理overlap的比对
        GraphConcatemer graphConcatemer;
        //用于处理位于同一酶切位点的匹配
        FragmentPurifying fragmentPurifying=null;
        if (p.ligation_type.equals("res")) {
            fragmentPurifying = new FragmentPurifying(p.restrictionsiteFile, p.minfragsize, p.maxfragsize);
        }
        DistancePurifying distancePurifying = new DistancePurifying();
        OverlapPurifying overlapPurifying = new OverlapPurifying();

        //开始逐一处理比对结果
        int count=0;
        int countRes=0;
        int countDis=0;
        int countOverlap=0;
        int countSingle=0;
        int countLow=0;
        int countValid=0;
        while ((line = atReader.readLine())!=null){
            count++;
            if (count % 1000000 == 0) {
                System.out.println(count + " mapping was handled");
            }

            String[] fields = line.split("\t");

            //过滤低分比对
            if (Integer.parseInt(fields[7]) < cutoff){
                lowQualityWriter.write(line);
                lowQualityWriter.newLine();
                countLow++;
                continue;
            }
            ReadAT one = new ReadAT(fields);

            //将同一个concatemer的比对在一个批次进行处理
            if (concatemer.size() == 0){
                concatemer.add(one);
            } else if (one.readName.equals(concatemer.get(0).readName)){
                concatemer.add(one);
            } else {
                //过滤掉重合比对
                if (concatemer.size() > 1) {
                    //如果提前不分割reads，则使用图算法过滤掉重叠比对的部分
                    if (p.splitReads.equals("N")) {
                        graphConcatemer = new GraphConcatemer(concatemer);
                        graphConcatemer.IdentifyMonomersFromConcatemer();
                        //将被删除的比对写入
                        concatemer.removeAll(graphConcatemer.monomers);
                        if (!concatemer.isEmpty()) {
                            for (ReadAT monomer :
                                    concatemer) {
                                shortRemoveWriter.write(monomer.toContactLine());
                                shortRemoveWriter.newLine();
                                countOverlap++;
                            }
                        }
                        concatemer= graphConcatemer.monomers;
                    } else {
                        //如果提前不分割reads，则使用overlap算法过滤掉重叠比对的部分
                        List<ReadAT> concatemerOverlap = overlapPurifying.purify(concatemer, 20);
                        for (ReadAT monomer :
                                concatemerOverlap) {
                            shortRemoveWriter.write(monomer.toContactLine());
                            shortRemoveWriter.newLine();
                            countOverlap++;
                        }
                        overlapPurifying.clear();
                    }
                }

                //通过酶切过滤
                if (concatemer.size() > 1 && p.removeResblock.equalsIgnoreCase("Y")) {
                    if(p.ligation_type.equals("res")) {
                        //filter by res sites
                        //临时存储被去除的匹配
                        List<ReadAT> concatemerResRemove = fragmentPurifying.purify(concatemer);
                        for (ReadAT monomer :
                                concatemerResRemove) {
                            resRemoveWriter.write(monomer.toContactLine());
                            resRemoveWriter.newLine();
                            countRes++;
                        }
                        fragmentPurifying.clear();
                    }
                }

                //通过距离阈值过滤
                if (concatemer.size() > 1 && p.removeDis.equalsIgnoreCase("Y")) {
                    List<ReadAT> concatemerDisRemove = distancePurifying.purify(concatemer, p.distanceCutoff);
                    for (ReadAT monomer :
                            concatemerDisRemove) {
                        disRemoveWriter.write(monomer.toContactLine());
                        disRemoveWriter.newLine();
                        countDis++;
                    }
                    distancePurifying.clean();
                }

                //写入最终过滤的结果
                concatemer.sort(new ComparatorReadAT());
                if (concatemer.size() > 1) {
                    for (ReadAT monomer :
                            concatemer) {
                        mmWriter.write(monomer.toContactLine());
                        mmWriter.newLine();
                        countValid++;
                    }
                }else{
                    for (ReadAT monomer :
                            concatemer) {
                        singletonWriter.write(monomer.toContactLine());
                        singletonWriter.newLine();
                        countSingle++;
                    }
                }

                concatemer.clear();
                concatemer.add(one);
            }

        }


        //处理最后一个
        {
            //过滤掉重合比对
            if (concatemer.size() > 1) {
                //如果提前不分割reads，则使用图算法过滤掉重叠比对的部分
                if (p.splitReads.equals("N")) {
                    graphConcatemer = new GraphConcatemer(concatemer);
                    graphConcatemer.IdentifyMonomersFromConcatemer();
                    //将被删除的比对写入
                    concatemer.removeAll(graphConcatemer.monomers);
                    if (!concatemer.isEmpty()) {
                        for (ReadAT monomer :
                                concatemer) {
                            shortRemoveWriter.write(monomer.toContactLine());
                            shortRemoveWriter.newLine();
                            countOverlap++;
                        }
                    }
                    concatemer= graphConcatemer.monomers;
                } else {
                    //如果提前不分割reads，则使用overlap算法过滤掉重叠比对的部分
                    List<ReadAT> concatemerOverlap = overlapPurifying.purify(concatemer, 20);
                    for (ReadAT monomer :
                            concatemerOverlap) {
                        shortRemoveWriter.write(monomer.toContactLine());
                        shortRemoveWriter.newLine();
                        countOverlap++;
                    }
                    overlapPurifying.clear();
                }
            }
            //通过酶切过滤
            if (concatemer.size() > 1) {
                if(p.ligation_type.equals("res")) {
                    //filter by res sites
                    //临时存储被去除的匹配
                    List<ReadAT> concatemerResRemove = fragmentPurifying.purify(concatemer);
                    for (ReadAT monomer :
                            concatemerResRemove) {
                        resRemoveWriter.write(monomer.toContactLine());
                        resRemoveWriter.newLine();
                        countRes++;
                    }
                    fragmentPurifying.clear();
                }
            }

            //通过距离阈值过滤
            if (concatemer.size() > 1) {
                List<ReadAT> concatemerDisRemove = distancePurifying.purify(concatemer, p.distanceCutoff);
                for (ReadAT monomer :
                        concatemerDisRemove) {
                    disRemoveWriter.write(monomer.toContactLine());
                    disRemoveWriter.newLine();
                    countDis++;
                }
                distancePurifying.clean();
            }

            //写入最终过滤的结果
            concatemer.sort(new ComparatorReadAT());
            if (concatemer.size() > 1) {
                for (ReadAT monomer :
                        concatemer) {
                    mmWriter.write(monomer.toContactLine());
                    mmWriter.newLine();
                    countValid++;
                }
            }else{
                for (ReadAT monomer :
                        concatemer) {
                    singletonWriter.write(monomer.toContactLine());
                    singletonWriter.newLine();
                    countSingle++;
                }
            }

            concatemer.clear();
        }


        atReader.close();
        mmWriter.close();
        lowQualityWriter.close();
        if (p.ligation_type.equals("res")) {
            resRemoveWriter.close();
        }
        disRemoveWriter.close();
        singletonWriter.close();
        shortRemoveWriter.close();

        System.out.println();
        System.out.println("count of all mappings= " + count);
        System.out.println("count of all valid mappings = " + countValid);
        System.out.println("count of low mapq(<"+cutoff+") mappings = " + countLow);
        System.out.println("count of single mappings = " + countSingle);
        System.out.println("count of all self-ligation mappings = " + (countRes+countDis));
        System.out.println("count of self-ligation mappings(dis<"+p.distanceCutoff+") = " + countDis);
        if (p.ligation_type.equals("res")) {
            System.out.println("count of self-ligation mappings(by restriction fragment) = " + countRes);
        }
//        if (p.splitReads.equals("N")) {
            System.out.println("count of multi mappings at same reads position = " + countOverlap);
//        }
        System.out.println();

        new File(outPrefix + ".contacts").delete();
        BufferedWriter contactsWriter = new BufferedWriter( new FileWriter(outPrefix+".contacts"));
        contactsWriter.close();
    }

    public static void run(Path p, String outPrefix) throws IOException {
        new AlignTable2Contact(outPrefix + ".aligntable",outPrefix,p);
    }

}
