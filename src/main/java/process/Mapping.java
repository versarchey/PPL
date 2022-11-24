package process;

import errors.MyError;
import process.mapping.Bed2AlignTable;
import utils.MyUtil;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class Mapping {

    private Path p;
    private String outPrefix;
    private Calendar rightNow = Calendar.getInstance();
//    private LinkerFiltering lf;


    public Mapping(Path path) {
        p = path;
        outPrefix = p.OUTPUT_DIRECTORY+"/"+p.OUTPUT_PREFIX+"/"+p.OUTPUT_PREFIX;
//        lf = new LinkerFiltering(p);
    }

    public void map() throws IOException {// mapping reads to a reference genome
        //I believe this script should move to outdir, so changed at 11.21 by some handsome guy
        String file = p.OUTPUT_DIRECTORY + "/" + p.OUTPUT_PREFIX + "/" + p.OUTPUT_PREFIX + ".mapping.sh";
        new File(file).delete();

        //比对
        //生成比对的脚本文件
        if (p.ligation_type.equals("res")){
            if (p.splitReads.equals("Y")){
                System.out.println("[" + rightNow.getTime().toString() +"] INFO: Mapping with Res-Split mode ...");
                this.mapResReadSplit(file);
            }else {
                System.out.println("[" + rightNow.getTime().toString() +"] INFO: Mapping with Res-NoSplit mode ...");
                this.mapResRead(file);
            }
        } else if (p.ligation_type.equals("linker")){
            if (p.splitReads.equals("Y")){
                System.out.println("[" + rightNow.getTime().toString() +"] INFO: Mapping with Linker-Split mode ...");
                this.mapLinkerReadSplit(file);
            }else {
                System.out.println("[" + rightNow.getTime().toString() +"] INFO: Mapping with Linker-NoSplit mode ...");
                this.mapLinkerRead(file);
            }
        }

        //执行脚本，生成bam sorted by name和bed
        MyUtil.runShell(file);

        //生成align table
        System.out.println("[" + rightNow.getTime().toString() +"] Convert "+outPrefix+".bed"+" to "+outPrefix+".aligntable"+" ...");
        Bed2AlignTable.main(new String[] {
                outPrefix + ".bed",
                outPrefix
        });
    }

    //生成比对的脚本文件
    public void mapResRead(String file) throws IOException {
        String line = "";
        String[] fastqs = p.Fastq_file.split(",");
        //比对所有文件生成bam
        if (!p.skipmap.equalsIgnoreCase("Y")) {
            if (MyUtil.isGZipped(new File(fastqs[0]))){
                line="zcat";
            }else {
                line="cat";
            }
            for (int i = 0; i < fastqs.length; i++) {
                line += " " + fastqs[i] + " ";
            }
            String bwaSetting;
            if (p.aligner.equals("bwasw")) {
                bwaSetting = "bwa bwasw -H -b 5 -q 2 -r 1 -T 15 -z 10 -t " + p.NTHREADS;
            }else {
                bwaSetting = "minimap2 -a -x map-ont -B 3 -O 2 -E 2 -k13 -t " + p.NTHREADS;
            }
            line += "|" + bwaSetting + " " + p.GENOME_INDEX + " - ";
//            line += " | samtools view -hbS | " + "samtools sort -n -@ " + p.NTHREADS + " -o " + outPrefix + ".bam "; //-5M
            line += " | samtools view -hbS -@ " + p.NTHREADS + " > " + outPrefix + ".bam "; //-5M
            MyUtil.writeFile(file, line, true);
        }else {
            System.out.println("[" + rightNow.getTime().toString() +"] Skip align by bwa, only Generate "+outPrefix+".bed"+" from "+outPrefix + ".bam"+" ...");
        }
        //bam2bed
        if (p.skipmap.equalsIgnoreCase("Y") && new File(outPrefix+".bed").exists()) {
            System.out.println("[" + rightNow.getTime().toString() +"] Skip generating .bed, because .bed existing ...");
            line = "echo 'Shell: Do nothing'";
            MyUtil.writeFile(file, line, true);
            return;
        }
        line = "bedtools bamtobed -cigar -i " + outPrefix + ".bam " + " > " + outPrefix + ".bed ";
        MyUtil.writeFile(file, line, true);
    }

    public void mapResReadSplit(String file) throws IOException {
        String line = "";
        String[] fastqs = p.Fastq_file.split(",");
        //比对所有文件生成bam
        if (!p.skipmap.equalsIgnoreCase("Y")) {
            if (MyUtil.isGZipped(new File(fastqs[0]))){
                line="zcat";
            }else {
                line="cat";
            }
            for (int i = 0; i < fastqs.length; i++) {
                line += " " + fastqs[i] + " ";
            }
//            String bwaSetting = "bwa bwasw -H -b 5 -q 2 -r 1 -T 15 -z 10 -t " + p.NTHREADS;
            String bwaSetting = "bwa mem -B 3 -O 2 -t " + p.NTHREADS;
            line += "|" + bwaSetting + " " + p.GENOME_INDEX + " - ";
//            line += " | samtools view -hbS | " + "samtools sort -n -@ " + p.NTHREADS + " -o " + outPrefix + ".bam "; //-5M
            line += " | samtools view -hbS -@ " + p.NTHREADS + " > " + outPrefix + ".bam "; //-5M
            MyUtil.writeFile(file, line, true);
        }else {
            System.out.println("[" + rightNow.getTime().toString() +"] Skip align by bwa, only Generate "+outPrefix+".bed"+" from "+outPrefix + ".bam"+" ...");
        }
        //bam2bed
        if (p.skipmap.equalsIgnoreCase("Y") && new File(outPrefix+".bed").exists()) {
            System.out.println("[" + rightNow.getTime().toString() +"] Skip generating .bed, because .bed existing ...");
            line = "echo 'Shell: Do nothing'";
            MyUtil.writeFile(file, line, true);
            return;
        }
        line = "bedtools bamtobed -cigar -i " + outPrefix + ".bam " + " > " + outPrefix + ".bed ";
        MyUtil.writeFile(file, line, true);
    }

    public void mapLinkerRead(String file){
        String line = "";
        String[] fastqs = p.Fastq_file.split(",");
        //比对所有文件生成bam
        if (!p.skipmap.equalsIgnoreCase("Y")) {
            if (MyUtil.isGZipped(new File(fastqs[0]))){
                line="zcat";
            }else {
                line="cat";
            }
            for (int i = 0; i < fastqs.length; i++) {
                line += " " + fastqs[i] + " ";
            }
            String bwaSetting;
            if (p.aligner.equals("bwasw")) {
                bwaSetting = "bwa bwasw -H -b 5 -q 2 -r 1 -T 15 -z 10 -t " + p.NTHREADS;
            }else {
                bwaSetting = "minimap2 -a -x map-ont -B 3 -O 2 -E 2 -k13 -t " + p.NTHREADS;
            }
            line += "|" + bwaSetting + " " + p.GENOME_INDEX + " - ";
//            line += " | samtools view -hbS | " + "samtools sort -n -@ " + p.NTHREADS + " -o " + outPrefix + ".bam "; //-5M
            line += " | samtools view -hbS -@ " + p.NTHREADS + " > " + outPrefix + ".bam "; //-5M
            MyUtil.writeFile(file, line, true);
        }else {
            System.out.println("[" + rightNow.getTime().toString() +"] Skip align by bwa, only Generate "+outPrefix+".bed"+" from "+outPrefix + ".bam"+" ...");
        }
        //bam2bed
        if (p.skipmap.equalsIgnoreCase("Y") && new File(outPrefix+".bed").exists()) {
            System.out.println("[" + rightNow.getTime().toString() +"] Skip generating .bed, because .bed existing ...");
            line = "echo 'Shell: Do nothing'";
            MyUtil.writeFile(file, line, true);
            return;
        }
        line = "bedtools bamtobed -cigar -i " + outPrefix + ".bam " + " > " + outPrefix + ".bed ";
        MyUtil.writeFile(file, line, true);
    }

    public void mapLinkerReadSplit(String file){

    }

//    public void mapHichipRead(String file) {
//        String line = "";
//        if(p.Fastq_file_1.contains(",")) {
//            String[] fastq1s = p.Fastq_file_1.split(",");
//            String[] fastq2s = p.Fastq_file_2.split(",");
//            for(int i=0;i<fastq1s.length;i++) {
//                if(!p.skipmap.equalsIgnoreCase("Y")) {
//                    if(p.bamout.equalsIgnoreCase("Y")) {
//                        line = "bwa mem -M -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+fastq1s[i]+" | samtools view -hbS -o "+outPrefix+"."+i+ ".R1.bam - "; //-5M
//                        lf.writeFile(file, line, true);
//                        line = "bwa mem -M -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+fastq2s[i]+" | samtools view -hbS -o "+outPrefix+"."+i+".R2.bam - ";
//                        lf.writeFile(file, line, true);
//                    }else {
//                        line = "bwa mem -M -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+fastq1s[i]+" 1>"+outPrefix+"."+i+ ".R1.sam 2>"+outPrefix+"."+i+
//                                ".R1.sam.output.info.txt"; //-5M
//                        lf.writeFile(file, line, true);
//                        line = "bwa mem -M -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+fastq2s[i]+" 1>"+outPrefix+"."+i+".R2.sam 2>"+outPrefix+"."+i+
//                                ".R2.sam.output.info.txt";
//                        lf.writeFile(file, line, true);
//                    }
//                }
//
//                //merge sam
//                if(p.bamout.equalsIgnoreCase("Y")) {
//                    line = "samtools merge -f -O BAM -@ "+p.NTHREADS+" "+outPrefix+"."+i+".merge.bam " + outPrefix+"."+i+".R1.bam " + outPrefix+"."+i+".R2.bam";
//                    lf.writeFile(file, line, true);
//                }else {
//                    line = "samtools merge -f -O BAM -@ "+p.NTHREADS+" "+outPrefix+"."+i+".merge.bam " + outPrefix+"."+i+".R1.sam " + outPrefix+"."+i+".R2.sam";
//                    lf.writeFile(file, line, true);
//                }
//                //sort by name
//                line = "samtools sort -O SAM -n -@ "+p.NTHREADS+" -o "+outPrefix+"."+i+".merge.srt.sam " + outPrefix+"."+i+".merge.bam ";
//                lf.writeFile(file, line, true);
//                //merge PETs
//
//                //System.out.print("___++_--+-+-+-+-+ "+p.PROGRAM_DIRECTORY+"\n");
//    	    	/*
//    	    	line = "java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.UniqueSam "+outPrefix+"."+i+ ".R1.sam "+outPrefix+"."+i+".R1.clean.sam HiChIP";
//    	    	lf.writeFile(file, line, true);
//    	    	line = "java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.UniqueSam "+outPrefix+"."+i+".R2.sam "+outPrefix+"."+i+".R2.clean.sam HiChIP";
//    	    	lf.writeFile(file, line, true);
//    	    	*/
//
//                //MappingStat for R{1..8}
//                line = "java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.MappingMerge "+outPrefix+"."+i+".merge.srt.sam "+
//                        outPrefix+"."+i+" "+p.MAPPING_CUTOFF + " HiChIP";
//                lf.writeFile(file, line, true);
//
//
//    	    	/*
//    	        line = "samtools view -Sb "+outPrefix+"."+i+".bedpe.selected.uniq.1.sam |bamToBed -i > "+outPrefix+"."+i+".R1.uniq.bed";
//    	    	lf.writeFile(file, line, true);
//    	    	line = "samtools view -Sb "+outPrefix+"."+i+".bedpe.selected.uniq.2.sam |bamToBed -i > "+outPrefix+"."+i+".R2.uniq.bed";
//    	    	lf.writeFile(file, line, true);
//
//    	    	line = "paste "+outPrefix+"."+i+".R1.uniq.bed "+outPrefix+"."+i+".R2.uniq.bed |"+
//    	    	"awk '{score=$5;if(score>$11){score=$11};if(($1==$7 && $2<$8) || ($1<$7))"+
//    	    			"{print $1\"\\t\"$2\"\\t\"$3\"\\t\"$7\"\\t\"$8\"\\t\"$9\"\\t\"$4\"\\t\"score\"\\t\"$6\"\\t\"$12}"+
//    	    			"else{print $7\"\\t\"$8\"\\t\"$9\"\\t\"$1\"\\t\"$2\"\\t\"$3\"\\t\"$4\"\\t\"score\"\\t\"$12\"\\t\"$6}}' > "+
//    	    			outPrefix+"."+i+".bedpe";
//    	    	lf.writeFile(file, line, true);
//    	    	*/
//            }
//            line = "cat ";
//            for(int i=0;i<fastq1s.length;i++) {
//                line = line + " " + outPrefix+"."+i+".bedpe";
//            }
//            line = line + " > " + outPrefix+".bedpe";
//            lf.writeFile(file, line, true);
//
//            //rm temp bedpe file
//            line = "rm ";
//            for(int i=0;i<fastq1s.length;i++) {
//                line = line + " " + outPrefix+"."+i+".bedpe";
//            }
//            lf.writeFile(file, line, true);
//
//        }else {
//            if(!p.skipmap.equalsIgnoreCase("Y")) {
//                if(p.bamout.equalsIgnoreCase("Y")) {
//                    line = "bwa mem -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+p.Fastq_file_1+" | samtools view -hbS -o "+outPrefix+".R1.sam - ";
//                    lf.writeFile(file, line, true);
//                    line = "bwa mem -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+p.Fastq_file_2+" | samtools view -hbS -o "+outPrefix+".R2.sam - ";
//                    lf.writeFile(file, line, true);
//                }else {
//                    line = "bwa mem -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+p.Fastq_file_1+" 1>"+outPrefix+".R1.sam 2>"+outPrefix+
//                            ".R1.sam.output.info.txt";
//                    lf.writeFile(file, line, true);
//                    line = "bwa mem -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+p.Fastq_file_2+" 1>"+outPrefix+".R2.sam 2>"+outPrefix+
//                            ".R2.sam.output.info.txt";
//                    lf.writeFile(file, line, true);
//                }
//            }
//
//            //merge sam
//            if(p.bamout.equalsIgnoreCase("Y")) {
//                line = "samtools merge -f -O BAM -@ "+p.NTHREADS+" "+outPrefix+".merge.bam " + outPrefix+".R1.bam " + outPrefix+".R2.bam";
//                lf.writeFile(file, line, true);
//            }else {
//                line = "samtools merge -f -O BAM -@ "+p.NTHREADS+" "+outPrefix+".merge.bam " + outPrefix+".R1.sam " + outPrefix+".R2.sam";
//                lf.writeFile(file, line, true);
//            }
//            //sort by name
//            line = "samtools sort -O SAM -n -@ "+p.NTHREADS+" -o "+outPrefix+".merge.srt.sam " + outPrefix+".merge.bam ";
//            lf.writeFile(file, line, true);
//            //merge PETs
//
//	    	/*
//	      	//System.out.print("___++_--+-+-+-+-+ "+p.PROGRAM_DIRECTORY+"\n");
//	    	line = "java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.UniqueSam "+outPrefix+ ".R1.sam "+outPrefix+".R1.clean.sam HiChIP";
//	    	lf.writeFile(file, line, true);
//	    	line = "java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.UniqueSam "+outPrefix+".R2.sam "+outPrefix+".R2.clean.sam HiChIP";
//	    	lf.writeFile(file, line, true);
//	    	*/
//
//            //MappingStat for R{1..8}
//            line = "java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.MappingMerge "+outPrefix+".merge.srt.sam "+
//                    outPrefix+" "+p.MAPPING_CUTOFF + " HiChIP";
//            lf.writeFile(file, line, true);
//
//	    	/*
//	    	line = "samtools view -Sb "+outPrefix+".bedpe.selected.uniq.1.sam |bamToBed -i > "+outPrefix+".R1.uniq.bed";
//	    	lf.writeFile(file, line, true);
//	    	line = "samtools view -Sb "+outPrefix+".bedpe.selected.uniq.2.sam |bamToBed -i > "+outPrefix+".R2.uniq.bed";
//	    	lf.writeFile(file, line, true);
//
//	    	line = "paste "+outPrefix+".R1.uniq.bed "+outPrefix+".R2.uniq.bed |"+
//	    	"awk '{score=$5;if(score>$11){score=$11};if(($1==$7 && $2<$8) || ($1<$7))"+
//	    			"{print $1\"\\t\"$2\"\\t\"$3\"\\t\"$7\"\\t\"$8\"\\t\"$9\"\\t\"$4\"\\t\"score\"\\t\"$6\"\\t\"$12}"+
//	    			"else{print $7\"\\t\"$8\"\\t\"$9\"\\t\"$1\"\\t\"$2\"\\t\"$3\"\\t\"$4\"\\t\"score\"\\t\"$12\"\\t\"$6}}' > "+
//	    			outPrefix+".bedpe";
//	    	lf.writeFile(file, line, true);
//	    	*/
//        }
//
//    }
//
//    public void mapShortRead(String file) {
//        String line = "for x in R1 R2";
//        lf.writeFile(file, line, true);
//        line = "do";
//        lf.writeFile(file, line, true);
//        line = "    cat "+outPrefix+".1_1.${x}.fastq "+outPrefix+".2_2.${x}.fastq > "+outPrefix+".${x}.fastq";
//        lf.writeFile(file, line, true);
//        line = "    bwa aln -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".${x}.fastq 1> "+outPrefix+".${x}.fastq.sai 2> "+outPrefix+
//                ".${x}.sai.output.info.txt";
//        lf.writeFile(file, line, true);
//        line = "    bwa samse "+p.GENOME_INDEX+" "+outPrefix+".${x}.fastq.sai "+outPrefix+".${x}.fastq 1> "+outPrefix+".${x}.sam 2> "+outPrefix+
//                ".${x}.sam.output.info.txt";
//        lf.writeFile(file, line, true);
//        line = "    rm "+outPrefix+".${x}.fastq";
//        lf.writeFile(file, line, true);
//        line = "    rm "+outPrefix+".${x}.fastq.sai";
//        lf.writeFile(file, line, true);
//        line = "done";
//        lf.writeFile(file, line, true);
//        line = "java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.MappingStatistics "+outPrefix+".R1.sam "+outPrefix+".R2.sam "+outPrefix+" "+
//                p.MAPPING_CUTOFF+" "+p.NTHREADS;
//        lf.writeFile(file, line, true);
//        line = "rm "+outPrefix+".R1.sam";
//        lf.writeFile(file, line, true);
//        line = "rm "+outPrefix+".R2.sam";
//        lf.writeFile(file, line, true);
//        line = "cut -f8    < "+outPrefix+".bedpe | LANG=C sort -n | uniq -c > "+outPrefix+".bedpe.qc.dist.txt";
//        lf.writeFile(file, line, true);
//        line = "cut -f9,10 < "+outPrefix+".bedpe | LANG=C sort -n | uniq -c > "+outPrefix+".bedpe.strand.dist.txt";
//        lf.writeFile(file, line, true);
//    }
//
//    public void mapLongRead(String file, String header) {
//        String line = header;
//        lf.writeFile(file, line, true);
//        line = "do";
//        lf.writeFile(file, line, true);
//        if(!p.skipmap.equalsIgnoreCase("Y")) {
//            if(p.MAPMEM.equalsIgnoreCase("false")) {
//                line = "    bwa aln -n 2 -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".${x}.R1.fastq 1> "+outPrefix+".${x}.R1.sai 2>"+outPrefix+
//                        ".${x}.R1.sai.output.info.txt";
//                lf.writeFile(file, line, true);
//                line = "    bwa samse "+p.GENOME_INDEX+" "+outPrefix+".${x}.R1.sai "+outPrefix+".${x}.R1.fastq 1>"+outPrefix+".${x}.R1.sam 2>"+outPrefix+
//                        ".${x}.R1.sam.output.info.txt";
//                lf.writeFile(file, line, true);
//                line = "    bwa aln -n 2 -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".${x}.R2.fastq 1> "+outPrefix+".${x}.R2.sai 2>"+outPrefix+
//                        ".${x}.R2.sai.output.info.txt";
//                lf.writeFile(file, line, true);
//                line = "    bwa samse "+p.GENOME_INDEX+" "+outPrefix+".${x}.R2.sai "+outPrefix+".${x}.R2.fastq 1>"+outPrefix+".${x}.R2.sam 2>"+outPrefix+
//                        ".${x}.R2.sam.output.info.txt";
//                lf.writeFile(file, line, true);
//                line = "    bwa aln -n 2 -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".${x}.R3.fastq 1> "+outPrefix+".${x}.R3.sai 2>"+outPrefix+
//                        ".${x}.R3.sai.output.info.txt";
//                lf.writeFile(file, line, true);
//                line = "    bwa samse "+p.GENOME_INDEX+" "+outPrefix+".${x}.R3.sai "+outPrefix+".${x}.R3.fastq 1>"+outPrefix+".${x}.R3.sam 2>"+outPrefix+
//                        ".${x}.R3.sam.output.info.txt";
//                lf.writeFile(file, line, true);
//                line = "    bwa mem -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".${x}.R4.fastq 1>"+outPrefix+".${x}.R4.sam 2>"+outPrefix+
//                        ".${x}.R4.sam.output.info.txt";
//                lf.writeFile(file, line, true);
//                line = "    bwa mem -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".${x}.R5.fastq 1>"+outPrefix+".${x}.R5.sam 2>"+outPrefix+
//                        ".${x}.R5.sam.output.info.txt";
//                lf.writeFile(file, line, true);
//                line = "    bwa aln -n 2 -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".${x}.R6.fastq 1> "+outPrefix+".${x}.R6.sai 2>"+outPrefix+
//                        ".${x}.R6.sai.output.info.txt";
//                lf.writeFile(file, line, true);
//                line = "    bwa samse "+p.GENOME_INDEX+" "+outPrefix+".${x}.R6.sai "+outPrefix+".${x}.R6.fastq 1>"+outPrefix+".${x}.R6.sam 2>"+outPrefix+
//                        ".${x}.R6.sam.output.info.txt";
//                lf.writeFile(file, line, true);
//            }else {
//                line = "    bwa mem -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".${x}.R1.fastq 1> "+outPrefix+".${x}.R1.sam 2>"+outPrefix+
//                        ".${x}.R1.sam.output.info.txt";
//                lf.writeFile(file, line, true);
//                line = "    bwa mem -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".${x}.R2.fastq 1> "+outPrefix+".${x}.R2.sam 2>"+outPrefix+
//                        ".${x}.R2.sam.output.info.txt";
//                lf.writeFile(file, line, true);
//                line = "    bwa mem -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".${x}.R3.fastq 1> "+outPrefix+".${x}.R3.sam 2>"+outPrefix+
//                        ".${x}.R3.sam.output.info.txt";
//                lf.writeFile(file, line, true);
//                line = "    bwa mem -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".${x}.R4.fastq 1>"+outPrefix+".${x}.R4.sam 2>"+outPrefix+
//                        ".${x}.R4.sam.output.info.txt";
//                lf.writeFile(file, line, true);
//                line = "    bwa mem -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".${x}.R5.fastq 1>"+outPrefix+".${x}.R5.sam 2>"+outPrefix+
//                        ".${x}.R5.sam.output.info.txt";
//                lf.writeFile(file, line, true);
//                line = "    bwa mem -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".${x}.R6.fastq 1> "+outPrefix+".${x}.R6.sam 2>"+outPrefix+
//                        ".${x}.R6.sam.output.info.txt";
//                lf.writeFile(file, line, true);
//            }
//            line = "    bwa mem -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".${x}.R7.fastq 1>"+outPrefix+".${x}.R7.sam 2>"+outPrefix+
//                    ".${x}.R7.sam.output.info.txt";
//            lf.writeFile(file, line, true);
//            line = "    bwa mem -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".${x}.R8.fastq 1>"+outPrefix+".${x}.R8.sam 2>"+outPrefix+
//                    ".${x}.R8.sam.output.info.txt";
//            lf.writeFile(file, line, true);
//
//        }
//    	/*
//    	//System.out.print("___++_--+-+-+-+-+ "+p.PROGRAM_DIRECTORY+"\n");
//    	line = "    java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.UniqueSam "+outPrefix+".${x}.R4.sam "+outPrefix+".${x}.R4.clean.sam ChIA-PET";
//    	lf.writeFile(file, line, true);
//    	line = "    java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.UniqueSam "+outPrefix+".${x}.R5.sam "+outPrefix+".${x}.R5.clean.sam ChIA-PET";
//    	lf.writeFile(file, line, true);
//    	line = "    java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.UniqueSam "+outPrefix+".${x}.R7.sam "+outPrefix+".${x}.R7.clean.sam ChIA-PET";
//    	lf.writeFile(file, line, true);
//    	line = "    java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.UniqueSam "+outPrefix+".${x}.R8.sam "+outPrefix+".${x}.R8.clean.sam ChIA-PET";
//    	lf.writeFile(file, line, true);
//    	if(p.MAPMEM.equalsIgnoreCase("true")) {
//    		line = "    java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.UniqueSam "+outPrefix+".${x}.R1.sam "+outPrefix+".${x}.R1.clean.sam ChIA-PET";
//        	lf.writeFile(file, line, true);
//        	line = "    java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.UniqueSam "+outPrefix+".${x}.R2.sam "+outPrefix+".${x}.R2.clean.sam ChIA-PET";
//        	lf.writeFile(file, line, true);
//        	line = "    java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.UniqueSam "+outPrefix+".${x}.R3.sam "+outPrefix+".${x}.R3.clean.sam ChIA-PET";
//        	lf.writeFile(file, line, true);
//        	line = "    java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.UniqueSam "+outPrefix+".${x}.R6.sam "+outPrefix+".${x}.R6.clean.sam ChIA-PET";
//        	lf.writeFile(file, line, true);
//    	}
//    	*/
//        //merge sam
//        line = "samtools merge -f -O BAM -@ "+p.NTHREADS+" "+outPrefix+".${x}.merge.bam " + outPrefix+".${x}.R1.sam " + outPrefix+".${x}.R2.sam " +
//                outPrefix+".${x}.R3.sam " + outPrefix+".${x}.R4.sam " + outPrefix+".${x}.R5.sam " + outPrefix+".${x}.R6.sam " +
//                outPrefix+".${x}.R7.sam " + outPrefix+".${x}.R8.sam";
//        lf.writeFile(file, line, true);
//        //sort by name
//        line = "samtools sort -O SAM -n -@ "+p.NTHREADS+" -o "+outPrefix+".${x}.merge.srt.sam " + outPrefix+".${x}.merge.bam ";
//        lf.writeFile(file, line, true);
//        //merge PETs
//        line = "java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.MappingMerge "+outPrefix+".${x}.merge.srt.sam "+
//                outPrefix+".${x}"+" "+p.MAPPING_CUTOFF + " ChIA-PET";
//        lf.writeFile(file, line, true);
//
//        line = "done";
//        lf.writeFile(file, line, true);
//
//        //.ambiguous.R1.fastq
//        String ambiguous_bedpe = "";
//        if(p.MAP_ambiguous.equalsIgnoreCase("Y")) {
//            line = "    bwa mem -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".ambiguous.R1.fastq 1>"+outPrefix+".ambiguous.R1.sam 2>"+outPrefix+
//                    ".ambiguous.R1.sam.output.info.txt";
//            lf.writeFile(file, line, true);
//            line = "    bwa mem -t "+p.NTHREADS+" "+p.GENOME_INDEX+" "+outPrefix+".ambiguous.R2.fastq 1>"+outPrefix+".ambiguous.R2.sam 2>"+outPrefix+
//                    ".ambiguous.R2.sam.output.info.txt";
//            lf.writeFile(file, line, true);
//            //merge sam
//            line = "samtools merge -f -O BAM -@ "+p.NTHREADS+" "+outPrefix+".ambiguous.merge.bam " + outPrefix+".ambiguous.R1.sam " + outPrefix+".ambiguous.R2.sam ";
//            lf.writeFile(file, line, true);
//            //sort by name
//            line = "samtools sort -O SAM -n -@ "+p.NTHREADS+" -o "+outPrefix+".ambiguous.merge.srt.sam " + outPrefix+".ambiguous.merge.bam ";
//            lf.writeFile(file, line, true);
//            //merge PETs
//            line = "java -cp "+p.PROGRAM_DIRECTORY+"/ChIA-PET.jar LGL.util.MappingMerge "+outPrefix+".ambiguous.merge.srt.sam "+
//                    outPrefix+".ambiguous"+" "+p.MAPPING_CUTOFF + " ChIA-PET";
//            lf.writeFile(file, line, true);
//
//            ambiguous_bedpe = outPrefix+".ambiguous.bedpe";
//        }
//
//
//        if(p.ALLMAP.equalsIgnoreCase("true")) {
//            line = "cat "+outPrefix+".1_2.bedpe "+outPrefix+".2_1.bedpe "+outPrefix+".1_1.bedpe "+outPrefix+".2_2.bedpe " + ambiguous_bedpe + " > "+outPrefix+".bedpe";
//        }else if(p.MAP2Linker.equalsIgnoreCase("true")) {
//            line = "cat "+outPrefix+".1_1.bedpe "+outPrefix+".2_2.bedpe " + ambiguous_bedpe + " > "+outPrefix+".bedpe";
//        } else {
//            line = "cat "+outPrefix+".1_2.bedpe "+outPrefix+".2_1.bedpe "+ ambiguous_bedpe +" > "+outPrefix+".bedpe";
//        }
//        lf.writeFile(file, line, true);
//    }
//
//    public void mergebedpefile(String file) {
//        String line = "cut -f8    < "+outPrefix+".bedpe"+" | LANG=C sort -n | uniq -c > "+outPrefix+".bedpe.qc.dist.txt";
//        lf.writeFile(file, line, true);
//        line = "cut -f9,10 < "+outPrefix+".bedpe"+" | LANG=C sort -n | uniq -c > "+outPrefix+".bedpe.strand.dist.txt";
//        lf.writeFile(file, line, true);
//        /**/
//        if(p.keeptemp.equals("N") && p.MODE.equals("0")) {
//            line = "rm "+outPrefix+"*clean.sam";
//            lf.writeFile(file, line, true);
//            line = "rm "+outPrefix+"*uniq*sam";
//            lf.writeFile(file, line, true);
//            line = "rm "+outPrefix+"*uniq.bed";
//            lf.writeFile(file, line, true);
//        }
//        if(p.hichipM.equals("N") && p.keeptemp.equals("N")) {
//            if(p.ALLMAP.equalsIgnoreCase("true") || p.MAP2Linker.equalsIgnoreCase("true")) {
//                line = "rm "+outPrefix+".1_1.bedpe";
//                lf.writeFile(file, line, true);
//                line = "rm "+outPrefix+".2_2.bedpe";
//                lf.writeFile(file, line, true);
//            }
//            if(p.MAPMEM.equalsIgnoreCase("false")){
//                line = "rm "+outPrefix+"*sai";
//                lf.writeFile(file, line, true);
//            }
//            if(p.MAP2Linker.equalsIgnoreCase("false")) {
//                line = "rm "+outPrefix+".1_2.bedpe";
//                lf.writeFile(file, line, true);
//
//                line = "rm "+outPrefix+".2_1.bedpe";
//                lf.writeFile(file, line, true);
//            }
//            if(p.MAP_ambiguous.equalsIgnoreCase("Y")) {
//                line = "rm "+outPrefix+".ambiguous.bedpe";
//                lf.writeFile(file, line, true);
//            }
//        }
//        /**/
//    }
}
