#plot reads length
import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd
from adjustText import adjust_text
import argparse
import warnings

print("Ploting the distribution of the length of reads...")

# Suppress all warnings
warnings.filterwarnings("ignore")

#读取参数
parser = argparse.ArgumentParser(description='Plot MAPQ.')
#-> required arguments
parser.add_argument('-c', type=str, dest='contactsFileName', required=True, help='path to contacts file')
parser.add_argument('-f', type=str, dest='fragmentFileName', required=True, help='path to virtual fragments file')
#-> optional arguments
parser.add_argument('-o1', type=str, dest='FigName1', default='./Fig1.svg', help='path to output figure1 (distribution, By default, ./Fig1.svg)')
parser.add_argument('-o2', type=str, dest='FigName2', default='./Fig2.svg', help='path to output figure2 (cumulative distribution, By default, ./Fig2.svg)')
args = parser.parse_args()

# contactsFileName = "./sample.contacts"
# fragmentFileName = "./dpnii.bed"
# FigName1 = "./len_reads_dens.svg"
# FigName2 = "./len_reads_cum.svg"
contactsFileName = args.contactsFileName
fragmentFileName =args.fragmentFileName
FigName1 = args.FigName1
FigName2 = args.FigName2

data_r = {}
data_v = []
with open(contactsFileName,"r") as dataReader:
    line = dataReader.readline()
    while line is not None and line != "":
        try:
            data_r[line.split("\t")[3]]=int(line.split("\t")[6])
        except:
            print(line)
        line = dataReader.readline()
data_r=list(data_r.values())
hue=["Sequencing read"]*len(data_r)

#读取模拟酶切的文件
with open(fragmentFileName,"r") as dataReader:
    line = dataReader.readline()
    while line is not None and line != "":
        try:
            data_v.append(int(line.split("\t")[3]))
            hue.append("Virtual fragment")
        except:
            print(line)
        line = dataReader.readline()

data_r=np.array(data_r)
data_v=np.array(data_v)
q1_r=int(np.quantile(data_r,.25))
q1_v=int(np.quantile(data_v,.25))
q3_r=int(np.quantile(data_r,.75))
q3_v=int(np.quantile(data_v,.75))
mid_r=int(np.quantile(data_r,.5))
mid_v=int(np.quantile(data_v,.5))
data=np.append(data_r,data_v)

data=np.array(data)+1

df = pd.DataFrame({'data': data,
                   'Group': hue})

#生成palette
start_color = np.array([1.0, 1.0, 0.0])  # 黄色
end_color = np.array([0.5, 0.0, 0.5])    # 紫色
# 定义调色板中的颜色数量
num_colors = 2
# 生成渐变颜色列表
colors = ["#BE66D9", "#2A1419"]

#计算x轴的坐标
xticks = []
for i in range(1, 7):
    if 10**i > data.max():
        xticks.append(data.max())
        break
    else:
        xticks.append(10**i)
# print(xticks)

#plot density
f, ax = plt.subplots(figsize=(6, 5))

kk=sns.histplot(data=df, x="data", hue="Group", element="step", log_scale=True, palette=colors, 
             stat="density", common_norm=False,)
# y_ecdf = sns.kdeplot(data=df, x='Dist between mapping boundary and right site')
# plt.xscale('log')  # 设置x轴为对数刻度
# plt.xticks(np.arange(0, data.max(), 1000))
plt.xticks(xticks)
plt.xlim(0, )
plt.xlabel("Read Length")
plt.title("Distribution of reads length")
# plt.grid(True, linestyle='--', alpha=1)
plt.savefig(FigName1)

f, ax = plt.subplots(figsize=(6, 5))

kk=sns.histplot(data=df, x="data", hue="Group", element="step", log_scale=True, palette=colors,
             cumulative=True, stat="density", common_norm=False,)
plt.xlim(0, )
plt.xlabel("Read Length")
plt.xticks(xticks)
plt.title("Cumulative distribution of reads length")
# plt.grid(True, linestyle='--', alpha=1)

#中位线
plt.axhline(y=.25, linestyle='-.', c="#6549F5")
plt.axhline(y=.5,  linestyle='-.', c="#49F5A6")
plt.axhline(y=.75,  linestyle='-.', c="#F56049")
# plt.axvline(x=mid_v, ymax=.5, linestyle='--', c="black")
# plt.axvline(x=mid_r, ymax=.5, linestyle='--', c="black")
text0=plt.text(1,0.51,va="bottom",s="mid")
text1=plt.text(mid_r,0.51,va="bottom",s=mid_r)
text2=plt.text(mid_v,0.51,va="bottom",s=mid_v)

text3=plt.text(1,0.26,va="bottom",s="lower")
text4=plt.text(q1_r,0.26,va="bottom",s=q1_r)
text5=plt.text(q1_v,0.26,va="bottom",s=q1_v)

text6=plt.text(1,0.76,va="bottom",s="higher")
text7=plt.text(q3_r,0.76,va="bottom",s=q3_r)
text8=plt.text(q3_v,0.76,va="bottom",s=q3_v)
texts=[text0,text1,text2,text3,text4,text5,text6,text7,text8]
adjust_text(texts, ax=ax, expand_points=(1, 1))

plt.savefig(FigName2)
