#plot dimension

import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd
import argparse
import warnings

print("Ploting Mapping quality...")

# Suppress all warnings
warnings.filterwarnings("ignore")

#读取参数
parser = argparse.ArgumentParser(description='Plot MAPQ.')
#-> required arguments
parser.add_argument('-d', type=str, dest='dimensionStatsFile', required=True, help='path to dimension stats file')
#-> optional arguments
parser.add_argument('-o1', type=str, dest='FigName1', default='./Fig1.svg', help='path to output figure1 (merged by groups, By default, ./Fig1.svg)')
parser.add_argument('-o2', type=str, dest='FigName2', default='./Fig2.svg', help='path to output figure2 (non-merged, By default, ./Fig2.svg)')
args = parser.parse_args()

# dimensionStatsFile = "./sample.dd"
# FigName1 = "./dimension_dis_g.svg"
# FigName2 = "./dimension_dis_a.svg"
dimensionStatsFile = args.dimensionStatsFile
FigName1 = args.FigName1
FigName2 = args.FigName2

data=np.loadtxt(dimensionStatsFile)
dimensions=data[:,0]
count=data[:,1]

conditions = {
    "1": (data[:, 0] >= 1) ,
    "2": (data[:, 0] >= 2) ,
    "3": (data[:, 0] >= 3) ,
    "4": (data[:, 0] >= 4) ,
    "5~6": (data[:, 0] >= 5) & (data[:, 0] <= 6),
    "7~11": (data[:, 0] >= 7) & (data[:, 0] <= 11),
    "12~21": (data[:, 0] >= 12) & (data[:, 0] <= 21),
    "22~49": (data[:, 0] >= 22) & (data[:, 0] <= 49),
    ">50": (data[:, 0] >= 50) }

new_data={}
for i in conditions.keys():
    new_data[i] = data[conditions[i]][:,1].sum()
new_dimentions=np.array(list(new_data.keys()))
new_count=np.array(list(new_data.values()))

f, ax = plt.subplots(figsize=(6, 4))
sns.scatterplot(x=new_dimentions, y=new_count/new_count.sum(), 
                color="#A668D5", marker="o", 
                linewidth=2, edgecolor="#A668D5",
                ax=ax,zorder=5)
sns.lineplot(x=new_dimentions, y=new_count/new_count.sum(),
             color="#FFCD00", linewidth=2,
             ax=ax)

plt.grid(True, linestyle='--', alpha=1)
# p.add(so.Line(marker="o", edgecolor="w"), so.Agg(), linestyle=None)
plt.xlabel("Contact Fragments")
plt.ylabel("Proportion of reads")
plt.title("Distribution of multi-contacts captured (grouped)")

plt.savefig(FigName1)

f, ax = plt.subplots(figsize=(6, 4))
sns.lineplot(x=data[:,0], y=data[:,1]/data[:,1].sum(), color="#A668D5")
# sns.scatterplot(x=data[:,0], y=data[:,1]/data[:,1].sum(), )
plt.xlabel("Contact Fragments")
plt.ylabel("Proportion of reads")
plt.title("Distribution of multi-contacts captured")

plt.savefig(FigName2)
