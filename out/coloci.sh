#!/bin/bash

jar=$1
$

java -cp /public/home/xyhuang/ChIADrop/script/CoLoci.jar FilterBarcode -i ${i}/hg19.all.frag.bed.gz -o ${i}/hg19.filter -n 1 -a 500 # 可以不做
java -cp /public/home/xyhuang/ChIADrop/script/CoLoci.jar MbedToBedpeBarcode -i ${i}/hg19.filter.all.frag.bed.gz -o ${i}/hg19.filter.barcode.bedpe.gz
zcat ${i}/hg19.filter.barcode.bedpe.gz | awk '\$1!=\$4 || \$5-\$3>=10000' | gzip > ${i}/hg19.filter.barcode.flt.bedpe.gz
java -cp /public/home/xyhuang/ChIADrop/script/CoLoci.jar SortBedpeHuge -i ${i}/hg19.filter.barcode.flt.bedpe.gz -o ${i}/hg19.filter.barcode.flt.sorted.bedpe.gz
java -cp /public/home/xyhuang/ChIADrop/script/CoLoci.jar BarcodeClusterCalling -i ${i}/hg19.filter.barcode.flt.sorted.bedpe.gz -o ${i}/hg19.filter.barcode.cluster.gz
java -cp /public/home/xyhuang/ChIADrop/script/CoLoci.jar PeakCallingPro -i ${i}/hg19.split.cluster.gz -o ${i}/hg19.filter.barcode.peak -n 3 -d 3000 -a 5 -b 3
awk '$21+0.0<0.01' ${i}/hg19.filter.barcode.peak.pvalue | awk '$6>$16*1.2 && $6>$11*1.2' | awk '{cen=int(($2+$3)/2);start=cen-500;end=cen+500;if(start<0){start=0;}print $1"\t"start"\t"end}' | awk -v OFS="\t" '{aa=NR; width=6; for(i=0;i<width-length(aa);++i) $4=$4"0"; $4="anchor"$4aa; print $0}' > ${i}/hg19.filter.barcode.peak.anchor
java -Xmx256G -cp /public/home/xyhuang/ChIADrop/script/CoLoci.jar Colocal -i ${i}/hg19.filter.barcode.peak.anchor -m ${i}/hg19.all.frag.bed.gz -o ${i}/hg19.${i}.barcode.coloci -c 3
java -Xmx64G -cp /public/home/xyhuang/ChIADrop/script/CoLoci.jar MonteCarloPvalue -i hg19.montecarlosim.pvalue -p hg19.montecarlosim.montecarlo.simulation -s test.txt.gz.coloci.colocalization.bed.gz -o hg19.final -n 100
java -Xmx64G -cp /public/home/xyhuang/ChIADrop/script/CoLoci.jar SimpleResult -i hg19.final.recal.pvalue.gz -o hg19.final.recal.pvalue.simple.gz
zcat hg19.final.recal.pvalue.simple.gz | awk '$4+0.0<=0.05' > hg19.final.recal.pvalue.simple.fdr.txt