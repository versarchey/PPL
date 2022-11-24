#!/bin/bash

monomers=${1}
stats=${2}
num=${3}

cat ${monomers}|awkx.sh 4|less|uniq -c|awk1.sh |awk -v num=${num} '$1<num{a[$1]++}$1>=num{a[num]++}END{for(i=2;i<=num;i++){print i"\t"a[i]}}'>${stats}