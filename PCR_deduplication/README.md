# Deduplicate PCR redundacy in scNanoHi-C experiment 


## Command
    java -cp ${jar_path} utils.DeDuplicateByFrag \
    <contactFile> \         # arg1: (input) original .contacts file 
    <contactFile.dedup> \   # arg2: (output) deduped .contacts file
    <statsFile>             # arg3: stats file (col1:reads id, col2:reads covered by, col3:is amplicon, col4: amplicon type)

## Log
    # example
    All concatemers count:          437634
    Duplicate count: 317048 Ratio:  0.7244592513378759
    Identical Duplicate count:      224605      Ratio: 0.5132256634539364
    Included Duplicate count:       92443       Ratio: 0.21123358788393956

## Method
To preserve the high-order concatemers as much as possible, only one contact, which can not be included by the others, would be remained. Our Method illustration is belowed.
![image](https://github.com/versarchey/PPL/blob/main/PCR_deduplication/figs/dedup_illustration.svg)

## Visualization
### Citation
Test Data (GM12878 cell line) From:
> Li, W., Lu, J., Lu, P., Gao, Y., Bai, Y., Chen, K., Su, X., Li, M., Liu, J. e., Chen, Y., Wen, L., & Tang, F. (2023). scNanoHi-C: a single-cell long-read concatemer sequencing method to reveal high-order chromatin structures within individual cells. Nature methods. https://doi.org/10.1038/s41592-023-01978-w
        
         

### Coverage depth check
In order to visualize the effect of de-redundancy using PPL, we viewed the coverage of the anchors intervals before and after de-redundancy in the JBrowse2. Theoretically, the depth of coverage of each base on the genome after de-redundancy should not exceed 2 (number of chromosome sets in humans = 2).
![image](https://github.com/versarchey/PPL/blob/main/PCR_deduplication/figs/orginal_deduped2.svg)

### Duplication proportion
![image](https://github.com/versarchey/PPL/blob/main/PCR_deduplication/figs/proportion_change2.svg)

