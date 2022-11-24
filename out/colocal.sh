#!/bin/bash

jar=$1
mbed=$2
dir=$3
i=$dir
mbed2=$3

width=1000
peak_dis=1500

if [ ! -d $dir  ]; then
    mkdir "$dir"
fi

#java -cp $jar FilterBarcode -i $mbed -o ${i}/hg19.filter -n 1 -a 500 # 可以不做
echo "mbed2bedpe"
java -cp $jar MbedToBedpeBarcode -i $mbed -o ${i}/hg19.filter.barcode.bedpe.gz
echo

echo "bedpe filter"
zcat ${i}/hg19.filter.barcode.bedpe.gz | awk '$1!=$4 || $5-$3>=10000' | gzip > ${i}/hg19.filter.barcode.flt.bedpe.gz
echo

echo "sort bedpe"
java -cp $jar SortBedpeHuge -i ${i}/hg19.filter.barcode.flt.bedpe.gz -o ${i}/hg19.filter.barcode.flt.sorted.bedpe.gz
echo

echo "cluster"
java -cp $jar BarcodeClusterCalling -i ${i}/hg19.filter.barcode.flt.sorted.bedpe.gz -o ${i}/hg19.filter.barcode.cluster.gz
echo

echo "callpeak"
java -cp $jar PeakCallingPro -i ${i}/hg19.filter.barcode.cluster.gz -o ${i}/hg19.filter.barcode.peak -n 3 -d $peak_dis -a 5 -b 3
echo

echo "peak2anchor"
awk '$21+0.0<0.01' ${i}/hg19.filter.barcode.peak.pvalue | awk '$6>$16*1.2 && $6>$11*1.2' | awk -v width=$width '{cen=int(($2+$3)/2);start=cen-width;end=cen+width;if(start<0){start=0;}print $1"\t"start"\t"end}' | awk -v OFS="\t" '{aa=NR; width=6; for(i=0;i<width-length(aa);++i) $4=$4"0"; $4="anchor"$4aa; print $0}' > ${i}/hg19.filter.barcode.peak.anchor
echo

echo "coloci"
java -Xmx256G -cp $jar Colocal -i ${i}/hg19.filter.barcode.peak.anchor -m $mbed2 -o ${i}/hg19.barcode.coloci -c 3
echo

#java -Xmx64G -cp $jar MonteCarloPvalue -i hg19.montecarlosim.pvalue -p hg19.montecarlosim.montecarlo.simulation -s test.txt.gz.coloci.colocalization.bed.gz -o hg19.final -n 100
#java -Xmx64G -cp $jar SimpleResult -i hg19.final.recal.pvalue.gz -o hg19.final.recal.pvalue.simple.gz
#zcat hg19.final.recal.pvalue.simple.gz | awk '$4+0.0<=0.05' > hg19.final.recal.pvalue.simple.fdr.txt

#zcat hg19.coloci.colocalization.bed.ms.gz | awk -v OFS='\t' 'ARGIND==1{a[$4]=$1":"$2"-"$3}ARGIND==2{for(i=1;i<=$1;i++){$(1+i)=$(1+i)":"a[$(1+i)]}print $0}' hg19.filter.barcode.peak.anchor -