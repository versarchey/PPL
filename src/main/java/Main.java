import errors.MyError;
import process.*;

import java.io.*;
import java.util.Calendar;

import static utils.MyUtil.*;
import static errors.MyError.*;

public class Main {
    public static void main(String[] args) throws IOException {
        //创建实例
        Calendar rightNow = Calendar.getInstance();
        QC qc = new QC();
        VirDigest vd = new VirDigest();

        //参数异常处理
        if (true) {
            if (args.length < 10) {
                MyError.notEnoughParamError();
            }
            if (args.length % 2 != 0) {
                MyError.unexpectedParamNumError(args.length);
            }
        }

        //Path对象接受参数，并检查其合法性
        Path p = new Path();
        p.setParameter(args);
        p.checkParams();
//        System.out.println(123);

        //输出目录的创建
        File file = new File(p.OUTPUT_DIRECTORY+"/"+p.OUTPUT_PREFIX);
        if (!file.exists()) {
            file.mkdirs();
        }
        //统计信息输出流
        BufferedWriter localPrintWriter = new BufferedWriter(new FileWriter(p.OUTPUT_DIRECTORY + "/" + p.OUTPUT_PREFIX + "/" + p.OUTPUT_PREFIX + ".basic_statistics.txt", false));

        //删除原输出目录下的结果（）
        String rmcmd = "rm "+p.OUTPUT_DIRECTORY +"/"+ p.OUTPUT_PREFIX +".*.bedpe.txt";
        shellrun(rmcmd, "rm");

        //Pore-C分析开始
        System.out.println("[" + rightNow.getTime().toString() +"] start Pore-C analysis");
        long time = System.currentTimeMillis() / 1000;

        //step1.1 选择质控
        if(!p.fastp.isEmpty() && Integer.valueOf(p.START_STEP) <= 1 && p.skipmap.equals("N")) {
            System.out.println("[" + rightNow.getTime().toString() +"] Step1.1: Fastq file quality control start" );
            time = System.currentTimeMillis() / 1000;
            qc.processQC(p);
        } else {
            System.out.println("[" + rightNow.getTime().toString() +"] Step1.1: Fastq file quality control skipped" );
        }

        //数据类型分流
        ///res类型分析开始
        if (p.ligation_type.equals("res")){
            System.out.println("[" + rightNow.getTime().toString() +"] Restriction enzyme digestion and ligation mode selected ");
            //step1.2 模拟酶切（不可通过start参数跳过）
            System.out.println("[" + rightNow.getTime().toString() +"] Step1.2: Checking and processing restriction file ...");
            if(p.removeResblock.equals("Y")) {
                //没有提供酶切文件
                if(p.restrictionsiteFile.equals("None")) {
                    //生成酶切文件
                    if(!p.ligation_site.equals("-")) {
                        System.out.println("[" + rightNow.getTime().toString() +"] Without restriction file, generating by ligation site!!!");
                        System.out.println("[Enzyme site number] " + p.ligation_sites.length);
                        for (String Enzyme :
                                p.ligation_sites) {
                            System.out.println("[Enzyme] " + Enzyme);
                        }
                        //generate res file by ligation site
                        vd.processVD(p);
                        //System.exit(0);
                    }else {
                        resError();
                    }
                }
                //有提供酶切文件
                else {
                    //check file path is valid
                    System.out.println("[" + rightNow.getTime().toString() +"] Use restriction file: " + p.restrictionsiteFile );
                }
            }

            //统计reads的数量并写入统计文件
//            long nReads_res = Count.countReads(p);
//            localPrintWriter.write("Total reads\t" + nReads_res/4);
//            localPrintWriter.newLine();
//            localPrintWriter.close();

            //step2.1 mapping
            if (Integer.valueOf(p.START_STEP) <= 2 ) {
                System.out.println("[" + rightNow.getTime().toString() +"] Step2.1: Mapping to genome ...");
                Mapping mapping = new Mapping(p);
                mapping.map();
            }
            //step2.2 filter
            if (Integer.valueOf(p.STOP_STEP) <= 2) {
                System.exit(0);
            }

            //step3.1 align_table2contacts
            if (Integer.valueOf(p.START_STEP) <= 3){
                System.out.println("[" + rightNow.getTime().toString() +"] Step3.1: Extract contacts from aligntable ...");
                Contact contact = new Contact(p);
                contact.extratContact();
                System.out.println("[" + rightNow.getTime().toString() +"] Extracting success");

            }
            if (Integer.valueOf(p.STOP_STEP) <= 3) {
                System.exit(0);
            }
        }
        ///linker类型分析开始
        else if (p.ligation_type.equals("linker")){
            System.out.println("[" + rightNow.getTime().toString() +"] Linker ligation mode selected ");

            //step2.1 mapping
            if (Integer.valueOf(p.START_STEP) <= 2 ) {
                System.out.println("[" + rightNow.getTime().toString() +"] Step2.1: Mapping to genome ...");
                Mapping mapping = new Mapping(p);
                mapping.map();
            }
            //step2.2 filter
            if (Integer.valueOf(p.STOP_STEP) <= 2) {
                System.exit(0);
            }

            //step3.1 align_table2contacts
            if (Integer.valueOf(p.START_STEP) <= 3){
                System.out.println("[" + rightNow.getTime().toString() +"] Step3.1: Extract contacts from aligntable ...");
                Contact contact = new Contact(p);
                contact.extratContact();
                System.out.println("[" + rightNow.getTime().toString() +"] Extracting success");

            }
            if (Integer.valueOf(p.STOP_STEP) <= 3) {
                System.exit(0);
            }
        }
    }
}
