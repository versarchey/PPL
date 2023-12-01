#plot boundary check

import numpy as np
import seaborn as sns
import pandas as pd
import plotly.express as px
import plotly.graph_objects as go
import plotly.io as pio
import matplotlib.pyplot as plt
import argparse
import warnings

print("Ploting the distance between mapping boundaries and sites identfied by restriction enzyme...")

# Suppress all warnings
warnings.filterwarnings("ignore")

#读取参数
parser = argparse.ArgumentParser(description='Plot boundary check.')
#-> required arguments
parser.add_argument('-bc', type=str, dest='bcheckFileName', required=True, help='path to boundary check file')
parser.add_argument('-bcc', type=str, dest='bcheckFileChrName', required=True, help='path to boundary check by chromosome file')
#-> optional arguments
parser.add_argument('-o1', type=str, dest='FigName1', default='./Fig1.svg', help='path to output figure1 (distribution, By default, ./Fig1.svg)')
parser.add_argument('-o2', type=str, dest='FigName2', default='./Fig2.svg', help='path to output figure2 (cumulative distribution, By default, ./Fig2.svg)')
args = parser.parse_args()

# bcheckFileName="./sample.10000.bc"
# bcheckFileChrName="./sample.bc.chr"
# FigName1 = "./distance_cum.svg"
# FigName2 = "./distance_radar.svg"
bcheckFileName = args.bcheckFileName
bcheckFileChrName =args.bcheckFileChrName
FigName1 = args.FigName1
FigName2 = args.FigName2

data_dl=[]
data_dr=[]

data_chrName_low={}
data_chrName_all={}

with open(bcheckFileName, "r") as file1:
    data = np.loadtxt(file1)
    # data_dl = data[:,1] + np.random.rand()
    # data_dr = data[:,2] + np.random.rand()
    data_dl = data[:,1].astype(int)
    data_dr = data[:,2].astype(int)

with open(bcheckFileChrName, "r") as file2:
    line = file2.readline()
    while line is not None and line != "":
        try:
            # if(line.split("\t")[5]!="-1"):
            fields=line.split("\t")
            data_chrName_all[fields[0]]=int(fields[3])
            data_chrName_low[fields[0]]=int(fields[4])
        except:
            print(line)
        line = file2.readline()

cutoff=int(np.quantile(np.append(data_dl,data_dr),.95))

data_dl[data_dl>cutoff]=cutoff
data_dr[data_dr>cutoff]=cutoff
data_dl[data_dl<0]=50
data_dr[data_dr<0]=50

# np.arange(0, max(data_dl.max(),data_dr.max()), 10)

data=np.append(data_dl,data_dr)
hue=np.array(["left"]*len(data_dl)+["right"]*len(data_dr))

df = pd.DataFrame({'Dist between mapping boundary and right site': np.append(data_dl, data_dr),
                   'Side': np.array(["left"] * len(data_dl) + ["right"] * len(data_dr))})

colors = ["#FF9340", "#33CEC3"]

f, ax = plt.subplots(figsize=(6, 5))
# y_ecdf = sns.ecdfplot(data=data[:10000], hue=hue)
sns.histplot(data=df,  x='Dist between mapping boundary and right site', hue='Side', element="step",  palette=colors,
             cumulative=True, stat="density", common_norm=False,ax=ax)
# y_ecdf = sns.ecdfplot(data=df, x='Dist between mapping boundary and right site', hue='Side')
plt.xticks()
plt.yticks(np.arange(0, 1.1, 0.1))
plt.xlabel("Distance")
plt.title("Distribution of dist between mapping boundary and identified site")
# plt.grid(True, linestyle='--', alpha=1)
plt.savefig(FigName1)




df = pd.DataFrame({
    "all":list(data_chrName_all.values()),
    "low":list(data_chrName_low.values()),
    "chr":list(data_chrName_low.keys())})

df = df.sort_values(by='chr')

print(df["low"])

fig = go.Figure()

fig.add_trace(go.Barpolar(
    # r=list(data_chrName_low.values()),
    r=df["low"],
    # theta=list(data_chrName_low.keys()),
    theta=df["chr"],
    marker_line_color="black",opacity=0.8,marker_color="#FF9340",
    # fill='toself',
    name='Closer'
))
fig.add_trace(go.Barpolar(
    # r=list(data_chrName_all.values()),
    r=df["all"],
    # theta=list(data_chrName_low.keys()),
    theta=df["chr"],
    marker_line_color="black",opacity=0.8,marker_color="#33CEC3",
    # fill='toself',
    name='Both'
))

fig.update_layout(
    title="Average dist by Chr using two metrics"
)

# fig = px.line_polar(df, r='all', theta='chr', line_close=True)
pio.write_image(fig, FigName2)
