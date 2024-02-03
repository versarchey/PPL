# PPL Toolbox
PPL (<b>P</b>ore-C <b>P</b>ip<b>L</b>ine) is a set of tools to process, evaluate and visualize the multi-way contacts experiment based on 3C and ONT long reads sequencing.

![image](https://github.com/versarchey/PPL/blob/main/logo.png)

## Figure summary <a name="figure-summary"></a>
![image](https://github.com/versarchey/PPL/blob/main/pipeline.png)

---

## Index <a name="index"></a>
- [Figure summary](#figure-summary)
- [Index](#index)
- [Install](#install)
  - [Dependencies](#dependencies)
- [Simple usage](#simple-usage)
  - [Extract contacts from .fastq](#extract-contacts-from-fastq)
    - [Step1. virtual restriction enzyme restriction](#step1-virtual-restriction-enzyme-restriction)
    - [Step2. Extract multi-contacts](#step2-extract-multi-contacts)
    - [Records tag types](#records-tag-types)
  - [Data quality evaluation](#data-quality-evaluation)
  - [Denoise multi-contacts by hypergraph filtering](#denoise-multi-contacts-by-hypergraph-filtering)
  - [Haplo-tagging contacts by phased SNPs (only for diploid organism until now)](#haplo-tagging-contacts-by-phased-snps-only-for-diploid-organism-until-now)
  - [Visualization class for multi-way contacts](#visualization-class-for-multi-way-contacts)
  - [Deduplicate PCR amplicons (Only for single-cell experiments using PCR, such as scNanoHi-C)](#deduplicate-pcr-amplicons-only-for-single-cell-experiments-using-pcr-such-as-scnanohi-c)
  - [Multi-way Contacts Annotation](#multi-way-contacts-annotation)
  - [Convert Format](#convert-format)
  - [Other Functions](#other-functions)
## [".contacts" file format](#contacts-file-format)



## Install <a name="install"></a>

### Dependencies <a name="dependencies"></a>
1. Java package
    1. ubuntu(>=18.04)/centos(>=6)
    1. jdk(>=1.8)
    1. minimap2
1. Visualization
    1. Our Pore-C track visualization is based on python, [click here and read further](https://github.com/versarchey/PPL-Toolbox/tree/main/visualization_Pore-C-track)。

## Simple usage <a name="simple-usage"></a>

### Extract contacts from .fastq <a name="extract-contacts-from-fastq"></a>
#### Step1. virtual restriction enzyme restriction <a name="step1-virtual-restriction-enzyme-restriction"></a>
The virtual fragments file will be generated on this step. The virtual fragments need to be generated on the firt step, because this information will be used on the further downstream process and data quality evaluation.

    # Usage
    java -cp PPL.jar utils.VirDigestTool 
        <reference genome, .fasta> 
        <identified site, e.g. ^GATC> 
        <result>

    # For example
    genome="/public/home/xxx/genome/hg38/hg38.fa"
    res_d="/public/home/xxx/genome/hg38/hg38.nlaiii.res.bed"
    java -cp PPL.jar utils.VirDigestTool \
        ${genome} \
        ./CATG^ \
        ${res_d}

#### Output format:
|Chr|Start|End|Length|ID|
| :---: | :---: | :---: | :---: | :---: |
|chr1|11159|12410|1251|24|

#### Several restriction enzyme names and identified motifs:
|Enzyme Name|            Motif|
| :---: | :---: |
|HindIII      |           A^AGCTT|
|MboI|^GATC|
|BglII|A^GATCT|
|DpnII       |            ^GATC|
|Sau3AI|^GATC|
|NlaIII         |         CATG^|
|Hinf1|G^ANTC|
|AluI|AG^CT|
|NcoI          |          C^CATGG|

#### Step2. Extract multi-contacts <a name="step2-extract-multi-contacts"></a>
PPL use a enhanced pipeline to extract multi-contacts from .fastq with high accuracy and sensitivity. 
Detailed introduction: [/Extract/README.md](https://github.com/versarchey/PPL-Toolbox/tree/main/Extract).

    # Some samples were listed blow, which can cover most

    ## Setting
    work_dir="/public/home/xxx/analysis/PPL_test"
    fq_dir="/public/home/xxx/data/pore-c"
    genome="/public/home/xxx/genome/hg38/hg38.fa"
    res_d="/public/home/xxx/genome/hg38/hg38.nlaiii.res.bed"
    id="SRR11589402"

    ## Simple usage
    java -jar ../PPL.jar \ 
        --ligation_type res \
        --genomefile $genome \
        --fastq ${fq_dir}/${id}/${id}_1.fastq.gz \
        --output ./ \
        --prefix $id \
        --start_step 2 \
        --restrictionsiteFile ${res_d} \
        --thread 12

    ## Complex usage, but it functions same as the simple one.
    java -jar ../PPL.jar --ligation_type res \
        --genomefile $genome \
        --fastq ${fq_dir}/${id}/${id}_1.fastq.gz \
        --splitReads N --resRemove N --disRemove N \
        --output ./ \
        --prefix $id \
        --skipmap N \
        --start_step 2 \
        --restrictionsiteFile ${res_d} \
        --thread 12 \
        --cutoffMapq 1 \
        --filter res
    
    ## Run from .bam (skip mapping)
    java -jar ../PPL.jar \ 
        --ligation_type res \
        --genomefile $genome \
        --fastq ${fq_dir}/${id}/${id}_1.fastq.gz \
        --output ./ \
        --skipmap Y \ # look the param
        --prefix $id \
        --start_step 2 \
        --restrictionsiteFile ${res_d} \
        --thread 12

    ## Run from 
    java -jar ../PPL.jar --ligation_type res \
        --genomefile $genome \
        --fastq ${fq_dir}/${id}/${id}_1.fastq.gz \
        --splitReads N --resRemove N --disRemove N \
        --output ./ \
        --prefix $id \
        --skipmap N \ 
        --start_step 2 \
        --stop_step 4 \
        --restrictionsiteFile ${res_d} \
        --thread 12 \
        --cutoffMapq 1 \
        --filter res

    ## Ban the "gaps extracting & re-mapping" (save much time, but make a few loss of sensitivity )
    java -jar ../PPL.jar \ 
        --ligation_type res \
        --genomefile $genome \
        --fastq ${fq_dir}/${id}/${id}_1.fastq.gz \
        --output ./ \
        --skipmap N \
        --prefix $id \
        --start_step 2 \
        --start_step 3 \ # look here, step4-7 is made for  
        --restrictionsiteFile ${res_d} \
        --thread 12

    # If you don't want to run on the default parameters,
    # please look for help by:
    java -jar PPL.jar --help

Until now, all mapping records have been classified and tagged.

#### Records tag types: <a name="records-tag-types"></a>
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
| unconfident       | Records whose boundaries are inconsistent with identified sites (>20bp) of restriction enzymes.               |

### Data quality evaluation <a name="data-quality-evaluation"></a>
We developed a .docx file based report frame, detailed Illustration & Usage: [/generate-docx/README.md](https://github.com/versarchey/PPL/tree/main/generate-docx). This chapter now only introduce several related functions in PPL package.

    # Output coverage ratio of each read.
    java -cp ${jar_path} utils.CoverageReads contacts> <coverage.results>
    
    ## --output: (length of all valid frags / length of all mapped reads) = 0.8871755749913668

    ## --outputfile content:
    ##    reads  length  lenValidMappings    ##    coverageRatio   regionValidMappings
    ##    SRR19920179.1   5836    5149    0.8822823851953393      [281..922),[1324..5832)  
    ##    ......
---
    # Boundary check for mapping site (Compute the distance between mapping site and closed virtual restriction site)
    java -cp ${jar_path} utils.BoundaryCheck <contacts> <RestrictionSite> <dis.results>
    
    ## --outputfile content (.bc):
    ##   mapIndex   avgDist shorterDist 
    ##    2       0       4
    ##    1       5       5
    ##    ......

    ## --outputfile content (.bc.evenByChr):
    ##    #chr    numFrags    numMapps    AvgDis  AvgDisMin
    ##    chr9    297448  141252  17      3
    ##    chr7    390397  181805  17      3
    ##    ......
---
    # Distribution of "ways" of multi-contacts
    java -cp ${jar_path} utils.StatDimensionDistribution <contactFile> <outFile>

    # Distribution of "ways" of multi-contacts in each bin
    java -cp ${jar_path} utils.StatsDistributionByBin <monomers> <prefixOutputFiles> <chromSizes> <binSize, 1000000>
---
    # Stats the inter-intra proportion
    java -cp ${jar_path} utils.StatIntraInter <contacts> <stats.out>
    java -cp ${jar_path} utils.StatIntraInter2 <pairs> <stats.out>
    
    ## --outputfile content :
    ##    group   intra short-range(<=20kb)       intra long-range(>20kb) inter
    ##    All     743371  5526092 2079079
    ##    [2, 2]  17685   72429   20721
    ##    [3, 3]  52842   261590  74746
    ##    [4, 4]  91556   524933  154049
    ##    [5, 6]  232155  1561497 497028
    ##    [7, 11] 303240  2600146 1006767
    ##    [12, 21]        45581   501516  319003
    ##    [22, 2147483647]        312     3981    6765

    # Stats the inter-intra TAD proportion
    java -cp ${jar_path} utils.StatByTAD <contacts> <domainfile(3 colmns)> <stats.out>

### Denoise multi-contacts by hypergraph filtering <a name="denoise-multi-contacts-by-hypergraph-filtering"></a>
Detailed Illustration & Usage: [/Denoise/README.md](https://github.com/versarchey/PPL-Toolbox/tree/main/Denoise/README.md)
#### Simple usage <a name="uguide"></a>
    java -cp PPL.jar utils.FilterHyper
        #required
        <Input file: multi-contacts>
        <Output file: multi-contacts filtered>
        <Chrom size file> <binSize, e.g. 1000000>
        #optional
        <Percentile cutoff, default:0.85> 
        <Range, e.g. chr1: 1000000-2000000>


### Haplo-tagging contacts by phased SNPs (only for diploid organism until now) <a name="haplo-tagging-contacts-by-phased-snps-only-for-diploid-organism-until-now"></a>

Detailed Illustration & Usage: [/HaploTag/README.md](https://github.com/versarchey/PPL-Toolbox/tree/main/HaploTag/README.md)
#### Simple usage <a name="uguide"></a>
    #1. Optional, make pre-genotyping quality control to select the basq and mapq cutoff.
    java -cp $jar_file utils.GenotypingQC
        -i <.bam> -v <PhasedSNP.vcf> -o <QCReport.txt>
        -l <num_line>
    
    #2. Haplo-tagging by SNPs
    java –cp $jar_file utils.GenotypingBySNP
        # required
        -i <.bam> 
            # input, alignment file
        -c <.conctact> 
            # input, multi-way contacts
        -v <.vcf> 
            # input, phased snps
        -o <.contacts.withTag>
            # output, phased multi-way contacts

    #3. Deduce the unphased frag by 3 inputation methods
    java –cp $jar_file utils.GenotypingImputation
        -i <.contacts.withTag> 
        -v <PhasedRecords.vcf> 
        -o <.contacts.withTag.imputed>
        
### Visualization class for multi-way contacts <a name="visualization-class-for-multi-way-contacts"></a>
Detailed Installation & Usage: [/visualization_Pore-C-track/README.md](https://github.com/versarchey/PPL-Toolbox/tree/main/visualization_Pore-C-track/README.md)

The visualization method is based on the pyGenomeTracks. You need to istall
it and copy the PoreTrack.py into pygenometracks/tracks folder. After that you can
add Pore-C track into your figure.

### Deduplicate PCR amplicons (Only for single-cell experiments using PCR, such as scNanoHi-C) <a name="deduplicate-pcr-amplicons-only-for-single-cell-experiments-using-pcr-such-as-scnanohi-c"></a>
Detailed Illustration & Usage: [/PCR_deduplication/README.md](https://github.com/versarchey/PPL/blob/main/PCR_deduplication/README.md)
#### Simple usage <a name="uguide"></a>
    java -cp ${jar_path} utils.DeDuplicateByFrag <contactFile> <contactFile.dedup> <statsFile>



### Multi-way Contacts Annotation <a name="multi-way-contacts-annotation"></a>
This is a bedtools-liked function, can annotate multi-contacts using multiple genomic region file in one run, such as genes, histone modification peaks .etc. Different to "bedtools overlap", this function use 2 different cutoff types (--overlaps) to ensure accuracy.

    Usage: 
        # Recommended
        java -cp $jar -cp PPL.jar utils.Contact2PCluster <.contacts file> <.pcluster file>
        java -cp $jar utils.AnnotatePClusters
            #required args
                -pc <pclusters>
                    #e.g. SRR19920179.54  chr6:44637890-44639773 … chr6:44498771-44499301
                -o <annotated pclusters>
                    # e.g. SRR19920179.54  chr6:44637890-44639773 … chr6:44498771-44499301 <anno info col>
                -a <annotation1,…>  
                    # e.g. chr9  85546539  85805188  -  AGTPBP1
                -at <col id,…>  
                    # e.g. 5
            #optional args
                --overlaps 	<FLOAT, (0,1)> or <INTEGER, [1,)>
                    ## when FLOAT, the cutoff is the length of two regions
                    ## when INTEGER, the cutoff is the length of two regions 
    Or:
        java -cp $jar utils.AnnotateContacts
            #required args
                -c <contacts file>
                -o <annotated contacts file>
                -a <annotation1,…>  
                -at <col id,…>  
            #optional args
                --overlaps

### Convert Format <a name="convert-format"></a>
    # Decompose multi-way contacts to 4DN pairs (4DN)
    java -cp ${jar_path} utils.Contact2Pairs <monomers> <pairs>

    # Decompose multi-way contacts to Matrix (hicpro)
    java -cp ${jar_path} utils.Contact2Matrix <chromSize> <monomers> <outFile> <binSize>

    # Convert multi-way contacts to Hic medium (juicer)
    java -cp ${jar_path} utils.Contact2HicMedium <monomers> <hic_medium>

    # Convert multi-way contacts to cluster (SPRITE)
    java -cp ${jar_path} utils.Contact2Cluster <monomers> <cluster>

    # Convert multi-way contacts to Mbed (coloci analysis)
    java -cp ${jar_path} utils.Contact2Mbed <monomers> <mbed>

### Other Functions <a name="other-functions"></a>
 
    # Splite reads by restriction motif (step0)
    java -cp ${jar_path} utils.SplitByRes <fastq> <fastq.splited> <ResSite>

    # Assign virtual fragments
    java -cp ${jar_path} utils.AssignFragment <monomers> <RestrictionSite> <outputFile> <cutoff>(eg. 20 or 0.5, default 20) <a/b>(a:1 fragment at least; b:0 possibly. default 1;)

    # Extract gaps sequence from reads by .contacts file
    java -cp ${jar_path} utils.ExtractGaps <original.fastq> <gaps.fastq> <coverageFile> <minLength>(default 50)

## ".contacts" file format <a name="contacts-file-format"></a>
| Col 列号 | Field 字段 |Description 含义|示例|
| :---: | :---: | --- |---|
|1|chr|染色体id(String)|chr1|
|2|start|基因组上的起始位置(Number)|1000000|
|3|end|基因组上的终止位置(Number)|1003000|
|4|readID|reads id(String)|SRR11589402.5|
|5|readStart|reads上的起始位置(Number)|0|
|6|readEnd|reads上的终止位置(Number)|2900|
|7|readLength|reads长度(Number)|4000|
|8|score|mapq&AS(String)|60:2600|
|9|strand|strand(String)|+|
|10|mappingId|mappings id(Number)|100|
|11|status|mappings status(String)|passed|
|12|fragmentAssigned|匹配到的酶切片段|chr8-100890013-100890075,6903240:chr8-100890075-100890232|

    #chr, start, end, readID, readStart, readEnd, readLength, mapq:AS, strand, mapping id, status, (optional)fragment information
    ## simple
    chr12   92770602        92770829        SRR11589402.5   1237    1467    1517    15:1012      -       0   passed  chr12-100890013-100890075

    ## phased
    chr12.phase1   92770602        92770829        SRR11589402.5   1237    1467    1517    15:1012      -       0   passed  chr12-100890013-100890075
    
