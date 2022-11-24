package errors;

public class MyError {
    static void printParams(){
        System.out.println("Usage: java -jar <path of ChIA_PET.jar> [options]");
        System.out.println("Necessary settings:");
        System.out.println("    --ligation_type\tassay type: linker or res, when linker mode is selected and restrictionsiteFile is appointed in the sametime,\n linker mode will be running");
        System.out.println("    --GENOME_INDEX\tthe path of BWA index");
        System.out.println("    --CHROM_SIZE_INFO\tthe file that contains the length of each chromosome, example file is in ChIA-PET_Tool_V3/chrInfo,\n                     \tthis is necessary for > step 2 analysis.\n                     \tNote. please make sure chromosome name in this file is same as name in genome file!!!");
        System.out.println("    --genomefile\tgenome fasta file path, needed for with --ligation_site and without --restrictionsiteFile\n                only needed for hichip data");
        System.out.println("    --fastq\tpath of fastq file");
        System.out.println("\"res\" type need:");
        System.out.println("    --ligation_site\tIt can be the name of restriction enzyme, such as HindIII, MboI, DpnII, Bglii, Sau3AI, Hinf1, NlaIII, AluI \n                or the site of enzyme digestion, A^AGCTT, ^GATC, ^GATC, A^GATCT, G^ANTC, CATG^, AG^CT or others.\n                multipe restriction enzyme can be seperated by comma, such as G^ANTC,^GATC.\n                restriction site with '^' and contains 'ATCG' without other character!!! \n                if the genomic enzyme digestion file --restrictionsiteFile is provided,\n                this parameter does not need to be provided. \n                only needed for hichip data");
        System.out.println("    --restrictionsiteFile\trestriction site file, can be genarated while has --ligation_site and without this paramater or \n                provide restriction enzyme information with --ligation_site, we will automatically generate the file.\n                only needed for hichip data");
        System.out.println("\"linker\" type need:");
        System.out.println("    --linker\tpath of linker file, need for ChIA-PET mode");
        System.out.println("Optional settings:");
        System.out.println("    --start_step\tstart with which step, 1: linker filtering; 2: mapping to genome; 3: removing redundancy; 4: categorization of PETs; 5: peak calling; 6: interaction calling; 7: visualizing, default: 1");
        System.out.println("    --stop_step\tstop with which step, 1: linker filtering; 2: mapping to genome; 3: removing redundancy; 4: categorization of PETs; 5: peak calling; 6: interaction calling; 7: visualizing, default: 100, should be bigger than --start_step");
        System.out.println("    --output\tpath of output, default: ChIA-PET_Tool_V3/output");
        System.out.println("    --prefix\tprefix of output files, default: out");
        System.out.println("    --skipmap\tSkip mapping read1 and read2, start from paired R1.sam and R2.sam, only valid in HiChIP mode now. default: N");
        System.out.println("    --MAPPING_CUTOFF\tcutoff of mapping quality score for filtering out low-quality or multiply-mapped reads, default: 20");
        System.out.println("    --bamout\tSkip mapping read1 and read2, start from paired R1.sam and R2.sam, only valid in HiChIP mode now. default: N");
        System.out.println("    --fastp\tfastp path, strong suggest for ChIA-PET data.");
        System.out.println("    --minfragsize\tMinimum restriction fragment length to consider, default 20");
        System.out.println("    --maxfragsize\tMaximum restriction fragment length to consider, default 1000000");
        System.out.println("    --splitReads\tN or Y");
        System.out.println("    --aligner\tOnly for split mode, bwasw or minimap2 please, default minimap2");


        System.out.println("################################################################################");
//        System.out.println("    --mode\tmode of tool, 0: short read; 1: long read, need for ChIA-PET data");
//        System.out.println("    --GENOME_LENGTH\tthe number of base pairs in the whole genome");
//        System.out.println("    --autolinker\tdetect linker by our program, then no need provide --linker and --mode paramater.");
//        System.out.println("    --fastq1\tpath of read1 fastq file");
//        System.out.println("    --fastq2\tpath of read2 fastq file");
//        System.out.println("    --skipheader\tskip header N reads for detect linker, default 1000000.");
//        System.out.println("    --linkerreads\tN reads used for detect linker, default 100000.");
//        System.out.println("    --hichip\tY(es) or N(o)[default] or O(nly print restriction site file without run other step), need for hichip data");
//        System.out.println("    --ResRomove\tY or N, whether remove PET in same restriction contig. default: Y");
//        System.out.println("    --minfragsize\tMinimum restriction fragment length to consider, default 20");
//        System.out.println("    --maxfragsize\tMaximum restriction fragment length to consider, default 1000000");
//        System.out.println("    --minInsertsize\tMinimum restriction fragment skip of mapped reads to consider, default 1");
//        System.out.println("    --fqmode\tsingle-end or paired-end (default), only required --fastq1 when single-end mode for ChIA-PET data");
//        System.out.println("    --minimum_linker_alignment_score\tminimum alignment score");
//        System.out.println("    --CYTOBAND_DATA\tthe ideogram data used to plot intra-chromosomal peaks and interactions, example file is in ChIA-PET_Tool_V3/chrInfo");
//        System.out.println("    --SPECIES\t1: human; 2: mouse; 3: others");
//        System.out.println("Other options:");
//        System.out.println("    --minimum_tag_length\t minimum tag length, default: 18");
//        System.out.println("    --maximum_tag_length\t maximum tag length, default: 1000");
//        System.out.println("    --minSecondBestScoreDiff\tthe score difference between the best-aligned and the second-best aligned linkers, default: 3");
//        System.out.println("    --output_data_with_ambiguous_linker_info\twhether to print the linker-ambiguity PETs, 0: not print; 1: print, default: 1");
//        System.out.println("    --printreadID\t write read ID to bedpe file, default: N");
//        System.out.println("    --printallreads\t print all reads no matter strand, default: 0[print all]; 1, only print valid strand reads.");
//        System.out.println("    --search_all_linker\t search all linkers in reads or just search one time, default: N");
//        System.out.println("    --thread\tthe number of threads used in linker filtering and mapping to genome, default: 1");
//        System.out.println("    --MERGE_DISTANCE\tthe distance limit to merge the PETs with similar mapping locations, default: 2");
//        System.out.println("    --SELF_LIGATION_CUFOFF\tthe distance threshold between self-ligation PETs and intra- chromosomal inter-ligation PETs, default: 8000 for ChIA, and 1000 for HiChIP");
//        System.out.println("    --EXTENSION_LENGTH\tthe extension length from the location of each tag, default: 500");
//        System.out.println("    --MIN_COVERAGE_FOR_PEAK\tthe minimum coverage to define peak regions, default: 5");
//        System.out.println("    --PEAK_MODE\t1: peak region mode, which takes all the overlapping PET regions above the coverage threshold as peak regions; 2: peak summit mode, which takes the highest coverage of overlapping regions as peak regions, default: 2");
//        System.out.println("    --MIN_DISTANCE_BETWEEN_PEAK\tthe minimum distance between two peaks, default: 500");
//        System.out.println("    --GENOME_COVERAGE_RATIO\tthe estimated proportion of the genome covered by the reads, default: 0.8");
//        System.out.println("    --PVALUE_CUTOFF_PEAK\tp-value to filter peaks that are not statistically significant, default: 0.00001");
//        System.out.println("    --INPUT_ANCHOR_FILE\ta file which contains user-specified anchors for interaction calling, default: null");
//        System.out.println("    --PVALUE_CUTOFF_INTERACTION\tp-value to filter false positive interactions, default: 0.5");
//        System.out.println("    --zipbedpe\tgzip bedpe related file, after analysis done. default: N. Y for gzip, N for not.");
//        System.out.println("    --zipsam\tConvert sam file to bam, after analysis done. default: N");
//        System.out.println("    --deletesam\tDelete sam files. default: N");
//        System.out.println("    --keeptemp\tKeep temp sam and bedpe file. default: N");
//        System.out.println("    --map_ambiguous\tAlso mapping ambiguous reads without linker. default: N");
//        System.out.println("    --macs2\tmacs2 path, using macs2 callpeak to detect anchor peak with alignment file. default: N");
//        System.out.println("    --nomodel\tmacs2 parameter, Whether or not to build the shifting model in macs2. default: N");
//        System.out.println("    --shortestP\textend and keep shorest peak length longer than N for loop calling, suggest 1500, user can set 0 to skip this step. default: 1500");
//        System.out.println("    --shortestA\textend and keep shorest anchor length longer than N for loop calling, user can set 0 to skip this step. default: 0");
//        System.out.println("    --XOR_cluster\tWhether keep loops if only one side of anchor is overlap with peak. default: N");
//        System.out.println("    --addcluster\tKeep all regions with more than 2 count reads as potential anchor for calling loop. default: N. if peaks number of macs2 smaller than 10000, this paramater will work automaticly.");

    }
    public static void notEnoughParamError(){
        System.out.println("Error: please set the necessary parameters");
        printParams();
        System.exit(0);
    }
    public static void unexpectedParamError(String param){
        System.out.println("Error: unexpected paramater: "+param);
        printParams();
        System.exit(0);
    }
    public static void unexpectedParamNumError(int paramNum){
        System.out.println("Error: unexpected paramater number: "+paramNum);
        printParams();
        System.exit(0);
    }
    public static void stepError(){
        System.out.println("Error: Stop step must bigger than start step!!!");
        System.exit(0);
    }
    public static void resError(){
        System.out.println("Error: in \"res\" mode, please defind --restrictionsiteFile or --ligation_site!!!!");
        printParams();
        System.exit(0);
    }
    public static void fileNotFoundError(String str) {
        System.out.println("Error: " + str + " doesn't exist");
        System.exit(0);
    }
    public static void inputNotNumError(String input) {
        System.out.println("Error: " + input + " is not a number, please input a num");
        System.exit(0);
    }
    public static void inputNotNYError(String input) {
        System.out.println("Error: " + input + " is not a expeted value, please input Y or N");
        System.exit(0);
    }
    public static void UnknownAlignerError(String input) {
        System.out.println("Error: " + input + " , please input bwasw or minimap2");
        System.exit(0);
    }
    public static void BedError(int count) {
        System.out.println("Error: " + ".bed columns num is "+count+", .bed file need 7 columns: chr, start, end, readsName, mapq, strand, cigar");
        System.exit(0);
    }
}
