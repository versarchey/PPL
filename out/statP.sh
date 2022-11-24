#!/bin/bash

monomers=${1}
stats=${2}
num=${3}

cat ${monomers}|awkx.sh 4|less|uniq -c|awk1.sh |awk -v num=${num} '{a[$1]++}END{for(i=2;i<=4;i++){print i"\t"a[i]} print "5to6\t"(a[5]+a[6]);for(i=7;i<=11;i++){sum1+=a[i]} print "7to11\t"sum1; for(i=12;i<=21;i++){sum2+=a[i]} print "12to21\t"sum2; for(i=22;i<=49;i++){sum3+=a[i]} print "22to49\t"sum3; for(i in a){if(int(i)>=50){sum4+=a[i];}} print ">=50\t"sum4;}'>${stats}