# Haplo-tagging contacts and create so-called Haplo by Phased SNPs (only for diploid organism) 

## METHOD
### Pipeline
![image](https://github.com/versarchey/PPL/blob/main/HaploTag/figs/haplotag.png)
### Imputation Methods
![image](https://github.com/versarchey/PPL/blob/main/HaploTag/figs/imputation.png)
Our implemented methods referto the Chen et.al researchers' work and results.
> Chen, Y., Lin, Z.-B., Wang, S.-K., Wu, B., Niu, L.-J., Zhong, J.-Y., Sun, Y.-M., Bai, X., Liu, L.-R., Xie, W., Luo, R., Hou, C., Luo, F., & Xiao, C.-L. (2023). High-resolution diploid 3D genome reconstruction using Pore-C data. bioRxiv, 2023.2008.2029.555243. https://doi.org/10.1101/2023.08.29.555243 




## USAGE
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
        # optional
        --sampleName <STRING> 
            # if not specified, the first sample in .vcf will be used.
        --baseq <INTEGER:0-93;DEFAULT:0>
            # baseq threshold used to filter out low sequencing quality results.
        --mapq <INTEGER:0-60;DEFAULT:1>
            # mapq threshold used to filter out low mapping quality results.
        --ratio <FLOAT:0-1;DEFAULT:0.7>
            # ratio cutoff used to filter out fragments with unsufficient snps evidence
        --flankLen <INTEGER:0-100;DEFAULT:3>
            # minimum flank sequence length besides with SNPs. Bases on the flank sequence must be able to match genome totally.

    #3. Deduce the unphased frag by 3 inputation methods
    java –cp $jar_file utils.GenotypingImputation
        # required
        -i <.contacts.withTag> 
        -v <PhasedRecords.vcf> 
        -o <.contacts.withTag.imputed>
        # optional
        --step <INTEGER:1,2 or 3>
            # imputation methods used.
        --dist <INTEGER:1-infinity>
            # used to location method. The unphased frags will be deduced if its genomic distance with another phased frag whithin the threshold.
        --ratio <FLOAT:0-1>
            # Ratio Cutoff in Imputation by dominant rule

