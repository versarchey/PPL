#!/bin/bash

dir=$1
prefix=$2

cat ${1}/${2}.disRemove ${1}/${2}.lowQuality ${1}/${2}.resRemove ${1}/${2}.notOnShortestPath ${1}/${2}.singleton|sort -k4,4 > ${1}/${2}.clear