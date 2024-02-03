# Enhanced pipeline for extracting multi-contact

## Introduction
Pore-C experiments produce reads that are long, fragmented and noisy. Such reads are also known as FLNRs (fragmented long noisy reads).This characteristic of FLNRs is a result of the high error rate of nanopore sequencing and the characteristics of neighbor linking experiments.

<img src="https://github.com/versarchey/PPL-Toolbox/blob/main/Extract/figs/reads.png" width="90%">


<img src="https://github.com/versarchey/PPL-Toolbox/blob/main/Extract/figs/collision.png" width="60%">

## Method


### Mapping records classification
#### Classification and Tag definition:

<img src="https://github.com/versarchey/PPL-Toolbox/blob/main/Extract/figs/classification.png" width="85%">

| Tag               | Meaning                                                                                                        |
|-------------------|----------------------------------------------------------------------------------------------------------------|
| passed            | Valid records are fragments of multi-way contacts.                                                             |
| low MAPQ          | Records with a low mapping quality score.                                                                      |
| singleton         | Single record cannot construct a multi-way contact.                                                            |
| multi-mappings    | Mistake mappings conflict with other confident records in position relationship on the same read.            |
| adjacent contacts | Contacts with two records assigned to adjacent restriction fragments. (disposing the lower MAPQ record)      |
| close contacts    | Contacts with two records separating from less than 1000 bp in genomic distance. (disposing the lower MAPQ record) |
| isolated contacts | Records which have no other records from the same reads within 1mb genomic distance.                           |
| unmapped          | Records of reads cannot be mapped to any region of reference.                                                   |
| unconfident       | Records whose boundaries are inconsistent with identified sites (>20bp) of restriction enzymes.    

### Best fragments collection selection
We define a rule for calculating the penalty for a collection of records. In this rule, we define three different types of penalties: gap penalty, overlap penalty, and mismatch penalty. The gap penalty is the length of the gap between two records on a read segment, while the overlap penalty is the length of the overlap between two records. The mismatch penalty is calculated based on the cigar sequence of the matching record, which means the length of the current record in the read segment minus the number of true matches, so when a record in the read segment perfectly matches the genome, the matching penalty should be 0. The total penalty should be in the range of 0 to the length of the read segment, and therefore the total penalty divided by the length of the read segment gives the normalized total penalty. In the Data Quality Assessment module, this is a standardized total penalty score. These three penalties are also used as important indicators of data quality and comparison quality in the Data Quality Assessment module .

<img src="https://github.com/versarchey/PPL-Toolbox/blob/main/Extract/figs/penalty.png" width="100%">

Now, the problem of filtering the mapping records is transformed into the problem of finding a subset of all records in a read that has the lowest penalty. The problem can be solved using the idea of dynamic programming: first, in order to facilitate the calculation of the gap penalties at the beginning and the end, we need to additionally define the START node record and the END node record; after that, we sort the records according to their starting positions, and then calculate the sub-penalties between all the nodes and the nodes of the backward order according to the penalty calculation method to construct the initial penalty matrix; according to the definition of the penalty, it will not be negative, and thus we apply Dijskra's algorithm to this matrix to obtain the lowest penalty. According to the definition of penalty, the penalty will not be negative, so we apply Dijskra algorithm to this matrix to obtain the minimum penalty path from the START node to the END node, and the records on this path are the minimum penalty set.

<img src="https://github.com/versarchey/PPL-Toolbox/blob/main/Extract/figs/computing.png" width="60%">

### Gaps extraction & Re-mapping

In order to improve data utilization, regions of reads marked as unconfident and other regions of reads not matched to the genome (gaps) are not immediately discarded. After obtaining the initial valid multiple interactions, PPL extracts the reads that have not been aligned to the genome on gaps (>50bp) and generates a new fastq file in the output directory to store these extracted gaps sequences (please see Figure 13). These gaps sequences are re-aligned back to the genome, and be classified again. Finally, the two parts of the record are merged to obtain all valid interactions.

<img src="https://github.com/versarchey/PPL-Toolbox/blob/main/Extract/figs/gaps.png" width="80%">

### "Splited" for linker sequence used experiments (beta)
Excepted to the foreward mapping directly pipeline, we developed a spliting-reads-based mapping pipeline at the same time. But it express low perforemance on the Pore-C in our test (only 60% accuracy), so it is not recommended. But in the future, it probably will be used on the linker-based experiments, because linker sequence is generally longer than restriction motif, which is better to be identified by Smith-Waterman algorithm. 

#### Splitting reads and Mapping(SM) mode 
Depending on the experiment type, the uses different methods to split reads from restriction-enzyme-based (like in-situ Hi-C) and linker-based experiments (like ChIA-PET) respectively. For linke-based experiments, running on "splited" mode may reach higher mapping accuracy.

    # To restriction-enzyme-based experiment (Unrecommended)
    ## Splitting reads
    java -cp PPL.jar utils.SplitByRes \
        ../data/SRR11589402/SRR11589402_1.fastq.gz \
        ../data/SRR11589402/SRR11589402_1.splited.fastq.gz \
        nlaiii

    ## Mapping split reads and filtering results
    java -jar ../PPL.jar \
        --fastq ../data/SRR11589402/SRR11589402_1.splited.fastq.gz \
        --ligation_type res \
        --genomefile ../hg38_bwaindex/hg38.chr.fa \ 
        --GENOME_INDEX ../hg38_bwaindex/hg38.chr.fa \
        --resRemove Y \
        --output ./ \
        --prefix SRR11589402_split \
        --skipmap N \
        --start_step 2 \
        --restrictionsiteFile ../hg38_bwaindex/hg38.chr.nlaiii.bed \
        --splitReads Y \
        --thread 12

    # For linker-based experiment (Testing)
    
    # Merge contacts fragments results (only for splited mode)
    java -cp ${jar_path} utils.ContactMerge <contacts> <outputFile> <cutoff1> <cutoff2>
