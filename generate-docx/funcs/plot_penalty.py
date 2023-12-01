#plot penalty

import numpy as np
import seaborn as sns
import pandas as pd
from adjustText import adjust_text
import matplotlib.pyplot as plt
import argparse
import warnings

print("Ploting the distribution of three penalty metrics...")

# Suppress all warnings
warnings.filterwarnings("ignore")

#读取参数
parser = argparse.ArgumentParser(description='Plot boundary check.')
#-> required arguments
parser.add_argument('-p', type=str, dest='penaltyFileName', required=True, help='path to penalty calculation log file')
#-> optional arguments
parser.add_argument('-o1', type=str, dest='FigName1', default='./Fig1.svg', help='path to output figure1 (cumulative distribution, By default, ./Fig1.svg)')
parser.add_argument('-o2', type=str, dest='FigName2', default='./Fig2.svg', help='path to output figure2 (violin plot, By default, ./Fig2.svg)')
args = parser.parse_args()

# penaltyFileName="./sample.penalty"
# FigName1 = "./penalty1.svg"
# FigName2 = "./penalty2.svg"
penaltyFileName = args.penaltyFileName
FigName1 = args.FigName1
FigName2 = args.FigName2

data_p=[]
data_op=[]
data_gp=[]
data_ap=[]
data_l=[]

with open(penaltyFileName, "r") as file1:
    data = np.loadtxt(file1)
    # data_dl = data[:,1] + np.random.rand()
    # data_dr = data[:,2] + np.random.rand()
    data_l = np.array(data[:,4])
    data_op = np.array(data[:,0])/data_l
    data_gp = np.array(data[:,1])/data_l
    data_ap = np.array(data[:,2])/data_l
    data_p = np.array(data[:,3])/data_l
    
data_op=np.array(data_op)
data_gp=np.array(data_gp)
data_ap=np.array(data_ap)

sum_op = data_op.sum()
sum_gp = data_gp.sum()
sum_ap = data_ap.sum()

q1_o=(np.quantile(data_op,.25))
q1_g=(np.quantile(data_gp,.25))
q1_a=(np.quantile(data_ap,.25))
q3_o=(np.quantile(data_op,.75))
q3_g=(np.quantile(data_gp,.75))
q3_a=(np.quantile(data_ap,.75))
mid_o=(np.quantile(data_op,.50))
mid_g=(np.quantile(data_gp,.50))
mid_a=(np.quantile(data_ap,.50))


# data=np.append(data_op,data_gp,data_ap)
data=np.concatenate((data_op, data_gp, data_ap))
hue=["Overlap"]*len(data_op)+["Gap"]*len(data_gp)+["Indel/Mismatch"]*len(data_ap)


# np.arange(0, max(data_dl.max(),data_dr.max()), 10)

df = pd.DataFrame({'Penalty': data,
                   'Type': hue})


colors = ["#D7E885", "#ECD387", "#9C7BC9"]

f, ax = plt.subplots(figsize=(6, 5))
sns.displot(data=df, x="Penalty", hue="Type",kind="ecdf",log_scale=True,palette=colors)

plt.xlabel("Normalized Penalty")
plt.title("Distribution of normalized penalties (stacking density)")



#中位线
plt.axhline(y=.25, linestyle='-.', c="#6549F5")
plt.axhline(y=.5,  linestyle='-.', c="#49F5A6")
plt.axhline(y=.75,  linestyle='-.', c="#F56049")
# plt.axvline(x=mid_v, ymax=.5, linestyle='--', c="black")
# plt.axvline(x=mid_r, ymax=.5, linestyle='--', c="black")
text0=plt.text(1,0.51,va="bottom",s="mid")
text1=plt.text(mid_o,0.51,va="bottom",s='{:.1f}%'.format(mid_o * 100))
text2=plt.text(mid_g,0.51,va="bottom",s='{:.1f}%'.format(mid_g * 100))
text3=plt.text(mid_a,0.51,va="bottom",s='{:.1f}%'.format(mid_a * 100))

text4=plt.text(1,0.26,va="bottom",s="lower")
text5=plt.text(q1_o,0.26,va="bottom",s='{:.1f}%'.format(q1_o * 100))
text6=plt.text(q1_g,0.26,va="bottom",s='{:.1f}%'.format(q1_g * 100))
text7=plt.text(q1_a,0.26,va="bottom",s='{:.1f}%'.format(q1_a * 100))

text8=plt.text(1,0.76,va="bottom",s="higher")
text9=plt.text(q3_o,0.76,va="bottom",s='{:.1f}%'.format(q3_o * 100))
text10=plt.text(q3_g,0.76,va="bottom",s='{:.1f}%'.format(q3_g * 100))
text11=plt.text(q3_a,0.76,va="bottom",s='{:.1f}%'.format(q3_a * 100))

texts=[text0,text1,text2,text3,text4,text5,text6,text7,text8,text9,text10,text11]
adjust_text(texts, ax=ax, expand_points=(1, 1))

# plt.ylabel("Proportion of reads")
plt.savefig(FigName1)

f, ax = plt.subplots(figsize=(6, 5))
sns.violinplot(data=df, x="Penalty", y="Type", palette=colors)
# plt.ylabel("Normalized Penalty")
plt.title("Distribution of normalized penalties (violin plot)")
plt.xscale('log')
plt.savefig(FigName2)

