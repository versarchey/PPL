//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package process;

import errors.MyError;
import utils.MyUtil;

import static errors.MyError.*;
import static utils.MyUtil.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Path {
    //共同必要参数（5）
    public String ligation_type = "";
    public String genomefile = "";
    //    参数（linker） (necessary=5)
    public String linker = "";
    public String AutoLinker = "true";
    //    参数（酶切）   (necessary=6)
    public String ligation_site = "-";
    public String[] ligation_sites;
    //灵活必要参数
    public String GENOME_INDEX = "";
    public String Fastq_file = "";
    public String skipmap = "N";
    public String restrictionsiteFile = "None";
    //非必要参数
    public String CHROM_SIZE_INFO = "";
    public String GENOME_LENGTH = "";
    public int Ngenome = 0;
    public HashMap<String, Integer> chrMAP = new HashMap();
    public HashMap<Integer, String> chrMAP_r = new HashMap();
    public String START_STEP = "1";
    public String STOP_STEP = "100";
    public String OUTPUT_DIRECTORY = "./";
    public String OUTPUT_PREFIX = "out";
    public String fastp = "";
    public String NTHREADS = "1";
    public String removeResblock = "N";
    public String removeDis = "N";
    public long distanceCutoff = 1000;
    public String bamout = "Y";
    public String MAPPING_CUTOFF = "5";
    public int minfragsize = 20;
    public int maxfragsize = 1000000;
    public String splitReads = "N";
    public String aligner="minimap2";
    public String ALLMAP = "false";
    public String MAP2Linker = "false";
    public String search_all_linker = "N";
    public int printallreads = 0;
    public int shortestAnchor = 0;
    public int shortestPeak = 1500;
    public String kmerlen = "9";
    //old
    String Fastq_file_1 = "";
    String Fastq_file_2 = "";
    String MODE = "1";
    String minimum_linker_alignment_score = "14";
    String CYTOBAND_DATA;
    String SPECIES;
    String PROGRAM_DIRECTORY;
    String minimum_tag_length = "18";
    String maximum_tag_length = "1000";
    String minSecondBestScoreDiff = "3";
    String output_data_with_ambiguous_linker_info = "1";
    String MERGE_DISTANCE = "2";
    String SELF_LIGATION_CUFOFF = "8000";
    String provide_slc = "N";
    String EXTENSION_LENGTH = "500";
    String EXTENSION_MODE = "1";
    String MIN_COVERAGE_FOR_PEAK = "5";
    String PEAK_MODE = "2";
    String MIN_DISTANCE_BETWEEN_PEAK = "500";
    String GENOME_COVERAGE_RATIO = "0.8";
    String PVALUE_CUTOFF_PEAK = "0.00001";
    String INPUT_ANCHOR_FILE = "null";
    String macs2 = "N";
    String nomodel = "N";
    String broadpeak = "";
    String PVALUE_CUTOFF_INTERACTION = "0.05";
    String FQMODE = "paired-end";
    String XOR_cluster = "N";
    String MAPMEM = "false";
    String printreadID = "N";
    String skipheader = "1000000";
    String linkerreads = "100000";
    String addcluster = "N";
    String hichipM = "N";
    String keeptemp = "N";
    int peakcutoff = 10000;
    int minInsertsize = 1;
    int maxInsertsize = 1000;
    String zipbedpe = "N";
    String zipsam = "N";
    String deletesam = "N";
    String MAP_ambiguous = "N";

    public Path() {
    }

    public Path(String genomefile,  String ligation_site, String restrictionsiteFile) {
        this.ligation_site = ligation_site;
        this.restrictionsiteFile = restrictionsiteFile;
        this.genomefile = genomefile;
        MyUtil.checkPath(genomefile);

        String[] ligations = this.ligation_site.split(",");
        String[] ligation_sites1 = this.ligation_site.split(",");
        for(int k = 0; k < ligations.length; ++k) {
            ligation_sites1[k] = this.getLigationSite(ligations[k]);
        }
        this.ligation_sites = this.replaceN(ligation_sites1);
        System.out.println("[Enzyme site number] " + this.ligation_sites.length);
    }

    public void checkParams(){

        //检查运行步骤设置的合法性
        if (Integer.parseInt(this.STOP_STEP)< Integer.parseInt(this.START_STEP) ||
        Integer.parseInt(this.START_STEP) <= 0){
            stepError();
        }
        if (Integer.parseInt(this.START_STEP) >=3 ){
            this.skipmap="Y";
        }
        //使用了bwasw，没有跳过了比对，也没给定索引文件
        if (this.skipmap.equals("N") && this.aligner.equals("bwasw")) {
            if (this.GENOME_INDEX.equals(""))
                MyError.notEnoughParamError();
        }
        //当选择跳过比对时，检查是否存在bam文件
        if (this.skipmap.equals("Y")){
//            for (int i=0; i<this.Fastq_file.split(",").length; i++)
//                if (!MyUtil.checkPathV(this.OUTPUT_DIRECTORY+'/'+this.OUTPUT_PREFIX+'/'+this.OUTPUT_PREFIX+"."+i+".bam")){
//                    System.out.println("!!!If you want to skip mapping step, "+this.OUTPUT_DIRECTORY+'/'+this.OUTPUT_PREFIX+'/'+this.OUTPUT_PREFIX+"."+i+".bam "+"is needed!!!");
//                    MyUtil.checkPath(this.OUTPUT_DIRECTORY+'/'+this.OUTPUT_PREFIX+'/'+this.OUTPUT_PREFIX+"."+i+".bam");
//                }
            if (!(MyUtil.checkPathV(this.OUTPUT_DIRECTORY+'/'+this.OUTPUT_PREFIX+'/'+this.OUTPUT_PREFIX+".bam")||MyUtil.checkPathV(this.OUTPUT_DIRECTORY+'/'+this.OUTPUT_PREFIX+'/'+this.OUTPUT_PREFIX+".bed"))){
                System.out.println("!!!If you want to skip mapping step, "+this.OUTPUT_DIRECTORY+'/'+this.OUTPUT_PREFIX+'/'+this.OUTPUT_PREFIX+".bam/.bed"+"is needed!!!");
                MyUtil.checkPath(this.OUTPUT_DIRECTORY+'/'+this.OUTPUT_PREFIX+'/'+this.OUTPUT_PREFIX+".bam");
            }
        }
        //没有跳过比对，但是没有提供索引文件和fq文件
        if (this.skipmap.equals("N")){
            if (this.GENOME_INDEX.equals("")||this.Fastq_file.equals(""))
                MyError.notEnoughParamError();
        }
        //运行模式没有选择
        if (this.ligation_type.equals("")){
            MyError.notEnoughParamError();
        }
        //没有给定基因组
        if (this.genomefile.equals("")){
            MyError.notEnoughParamError();
        }
        //选择了res模式，但是没有给定酶切位点或酶切文件
        if (this.ligation_type.equals("res") && (this.restrictionsiteFile.equals("")&&this.ligation_site.equals("-"))){
            MyError.notEnoughParamError();
        }
        //从第三部开始执行，检查aligntable文件
        if (this.START_STEP.equals("3")){
            MyUtil.checkPath(this.OUTPUT_DIRECTORY+'/'+this.OUTPUT_PREFIX+'/'+this.OUTPUT_PREFIX+".aligntable");
        }
    }

    public String getLigationSite(String ligation_site) {
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

    public String[] replaceN(String[] ligs) {
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

    public void setParameter(String[] args) throws IOException {
        this.PROGRAM_DIRECTORY = (new Path()).getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if (this.PROGRAM_DIRECTORY.endsWith(".jar")) {
            this.PROGRAM_DIRECTORY = this.PROGRAM_DIRECTORY.substring(0, this.PROGRAM_DIRECTORY.lastIndexOf("/") + 1);
        }

        this.OUTPUT_DIRECTORY = this.PROGRAM_DIRECTORY;
        int necessary = 0;
        //读取参数设置
        for(int i = 0; i < args.length; i += 2) {
            //连接模式设置
            if (args[i].equals("--ligation_type")){
                this.ligation_type = args[i+1].toLowerCase();
                if (this.ligation_type.equalsIgnoreCase("res" )&& this.ligation_type.equalsIgnoreCase("linker")){
                    System.out.println("Error: ligation_type " + args[i + 1] + " is incorrect");
                    System.exit(0);
                }
                ++necessary;
            }
            //必要参数设置
            else if (args[i].equals("--fastq")) {
                this.Fastq_file = args[i + 1];
                String[] fqs = this.Fastq_file.split(",");
                System.out.println(Arrays.toString(fqs));
                for (String fq :
                        fqs) {
                    this.checkPath(fq);
                }
                ++necessary;
            } else if (args[i].equals("--CHROM_SIZE_INFO")) {
                this.CHROM_SIZE_INFO = args[i + 1];
                this.checkPath(args[i + 1]);
                ++necessary;
                BufferedReader reader = new BufferedReader(new FileReader(this.CHROM_SIZE_INFO));
                long genomeLength=0;
                for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                    this.chrMAP.put(line.split("[ \t]+")[0], this.Ngenome);
                    this.chrMAP_r.put(this.Ngenome, line.split("[ \t]+")[0]);
                    genomeLength+=Integer.parseInt(line.split("[ \t]+")[1]);
//                    System.out.println(Integer.parseInt(line.split("[ \t]+")[1]));
//                    System.out.println("genomeLength = " + genomeLength);
                    ++this.Ngenome;
                }
                this.GENOME_LENGTH=""+genomeLength;
            } else if (args[i].equals("--GENOME_INDEX")){
                this.GENOME_INDEX = args[i + 1];
                //检查bwa索引文件是否完整
                String prefix = (new File(args[i + 1])).getName();
                File parentDir = (new File(args[i + 1])).getParentFile();
                File[] files = parentDir.listFiles();
                int n = 0;
                File[] var11 = files;
                int var10 = files.length;
                for(int var9 = 0; var9 < var10; ++var9) {
                    File f = var11[var9];
                    if (f.getName().equals(prefix + ".amb") || f.getName().equals(prefix + ".ann") || f.getName().equals(prefix + ".bwt") || f.getName().equals(prefix + ".pac") || f.getName().equals(prefix + ".sa")) {
                        ++n;
                    }
                }

                if (n != 5) {
                    System.out.println("Error: please check genome index (*.amb, *.ann, *.bwt, *pac and *.sa, these files are necessary)");
                    System.exit(0);
                }

                ++necessary;
            } else if (args[i].equals("--genomefile")) {
                this.genomefile = args[i + 1];
                this.checkPath(this.genomefile);
                ++necessary;
            }
            //酶切连接模式相关设置
            else if (args[i].equals("--ligation_site")) {

            } else if (args[i].equals("--restrictionsiteFile")) {
                this.restrictionsiteFile = args[i + 1];
                checkPath(this.restrictionsiteFile);
                if (this.ligation_site=="-"){
                    necessary++;
                }
            }
            //linker连接模式相关设置
            else if (args[i].equals("--linker")) {
                this.linker = args[i + 1];
                checkPath(linker);
                this.AutoLinker = "false";
//                ++necessary;
            }
            //非必要设置
            else if (args[i].equals("--start_step")) {
                this.START_STEP = args[i + 1];
                checkDigit(this.START_STEP);
            } else if (args[i].equals("--stop_step")) {
                this.STOP_STEP = args[i + 1];
                checkDigit(this.STOP_STEP);
            } else if (args[i].equals("--thread")) {
                this.NTHREADS = args[i + 1];
                checkDigit(this.NTHREADS);
            } else if (args[i].equals("--fastp")) {
                this.fastp = args[i + 1];
                if (!this.fastp.equals("fastp")) {
                    this.checkPath(args[i + 1]);
                }
            } else if (args[i].equals("--skipmap")) {
                this.skipmap = args[i + 1].toUpperCase();
                checkNY(this.skipmap);
            } else if (args[i].equals("--bamout")) {
                this.bamout = args[i + 1].toUpperCase();
                checkNY(this.bamout);
            } else if (args[i].equals("--MAPPING_CUTOFF")) {
                this.MAPPING_CUTOFF = args[i + 1];
                checkDigit(this.MAPPING_CUTOFF);
            }else if (args[i].equals("--minfragsize")) {
                this.minfragsize = Integer.parseInt(args[i + 1]);
            }else if (args[i].equals("--maxfragsize")) {
                this.maxfragsize = Integer.parseInt(args[i + 1]);
            }else if (args[i].equals("--resRemove")) {
                this.removeResblock = args[i + 1].toUpperCase();
                checkNY(this.removeResblock);
            }else if (args[i].equals("--disRemove")) {
                this.removeDis = args[i + 1].toUpperCase();
                checkNY(this.removeDis);
            }else if (args[i].equals("--distanceCutoff")) {
                this.distanceCutoff = Long.parseLong(args[i + 1]);
            }else if (args[i].equals("--splitReads")) {
                this.splitReads = args[i + 1].toUpperCase();
                checkNY(this.skipmap);
            }else if (args[i].equals("--aligner")) {
                this.aligner = args[i + 1].toLowerCase();
                if (this.aligner.equals("bwasw")||this.aligner.equals("minimap2")){
                    continue;
                }else {
                    MyError.UnknownAlignerError(this.aligner);
                }
            }
            //输出设置
            else if (args[i].equals("--output")) {
                this.OUTPUT_DIRECTORY = args[i + 1];
                checkPath(this.OUTPUT_DIRECTORY);
            } else if (args[i].equals("--prefix")) {
                this.OUTPUT_PREFIX = args[i + 1];
            }
            //未知参数
            else {
//                System.out.println("Error: unexpected paramater: " + args[i]);
//                System.exit(0);
                unexpectedParamError(args[i]);
            }
        }
//        //缺少必要参数
//        if (necessary < 5 || this.ligation_type.isEmpty()){
//            notEnoughParamError();
//        } else if (this.ligation_type.equals("res") && necessary < 6){
//            notEnoughParamError();
//        } else if (this.ligation_type.equals("linker") && necessary < 5){
//            notEnoughParamError();
//
//        }
    }

    public void checkPath(String str) {
        File f = new File(str);
        if (!f.exists()) {
            System.out.println("Error: " + str + " doesn't exist");
            System.exit(0);
        }

    }


    public String getPROGRAM_DIRECTORY() {
        return this.PROGRAM_DIRECTORY;
    }

    public String getOUTPUT_DIRECTORY() {
        return this.OUTPUT_DIRECTORY;
    }

    public String getOUTPUT_PREFIX() {
        return this.OUTPUT_PREFIX;
    }

    public String getFastq_file_1() {
        return this.Fastq_file_1;
    }

    public String getFastq_file_2() {
        return this.Fastq_file_2;
    }

    public String getLinker() {
        return this.linker;
    }

    public String getMinimum_linker_alignment_score() {
        return this.minimum_linker_alignment_score;
    }

    public String getMinimum_tag_length() {
        return this.minimum_tag_length;
    }

    public String getMaximum_tag_length() {
        return this.maximum_tag_length;
    }

    public String getMinSecondBestScoreDiff() {
        return this.minSecondBestScoreDiff;
    }

    public String getOutput_data_with_ambiguous_linker_info() {
        return this.output_data_with_ambiguous_linker_info;
    }

    public String getNTHREADS() {
        return this.NTHREADS;
    }

    public String getMAPPING_CUTOFF() {
        return this.MAPPING_CUTOFF;
    }

    public String getMERGE_DISTANCE() {
        return this.MERGE_DISTANCE;
    }
}
