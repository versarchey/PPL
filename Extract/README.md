# Enhanced pipeline for extracting multi-contact

## Introduction
Pore-C experiments produce reads that are long, fragmented and noisy. Such reads are also known as FLNRs (fragmented long noisy reads).This characteristic of FLNRs is a result of the high error rate of nanopore sequencing and the characteristics of neighbor linking experiments.

<img src="https://github.com/versarchey/PPL-Toolbox/blob/main/Extract/figs/reads.png" width="100%">

<img src="https://github.com/versarchey/PPL-Toolbox/blob/main/Extract/figs/collision.png" width="60%">

## Method

### Mapping records classification
<img src="https://github.com/versarchey/PPL-Toolbox/blob/main/Extract/figs/classification.png" width="70%">

### Best fragments collection selection
<img src="https://github.com/versarchey/PPL-Toolbox/blob/main/Extract/figs/penalty.png" width="100%">

<img src="https://github.com/versarchey/PPL-Toolbox/blob/main/Extract/figs/computing.png" width="60%">

### Gaps extraction & Re-mapping
<img src="https://github.com/versarchey/PPL-Toolbox/blob/main/Extract/figs/gaps.png" width="80%">
