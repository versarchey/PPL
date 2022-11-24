bed=$1
res=$2
ratio='0.5'
if [ ! -n $3 ]; then
      ratio=$3
fi

cat $bed|bedtools intersect -a - -b $res -F $ratio -wa -wb|awk -F '\t' -v OFS='\t' '$4"-"$5"-"$6==pre{fs=fs","$NF}$4"-"$5"-"$6!=pre{print pre_s,fs;fs=$NF;pre=$4"-"$5"-"$6;pre_s=$1"\t"$2"\t"$3"\t"$4"\t"$5"\t"$6"\t"$7"\t"$8"\t"$9"\t"$10}END{print pre_s,fs}'