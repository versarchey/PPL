#plot mapq
import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt
import argparse
import warnings

print("Ploting the distribution of multi-way contacts by dimension...")

# Suppress all warnings
warnings.filterwarnings("ignore")

#读取参数
parser = argparse.ArgumentParser(description='Plot MAPQ.')
#-> required arguments
parser.add_argument('-c', type=str, dest='contactsFileName', required=True, help='path to contacts file')
#-> optional arguments
parser.add_argument('-o', type=str, dest='FigName', default='./Fig.svg', help='path to output figure (By default, ./Fig.svg)')
args = parser.parse_args()

#转换参数
# contactsFileName = "./sample.contacts"
# FigName = "./mapq_dis.svg"
contactsFileName = args.contactsFileName
FigName = args.FigName

data = []
with open(contactsFileName,"r") as dataReader:
    line = dataReader.readline()
    while line is not None and line != "":
        try:
            data.append(int(line.split("\t")[7].split(":")[0]))
        except:
            print(line)
        line = dataReader.readline()

data = np.array(data)
# sns.displot(data=data, kind="ecdf",log_scale=True)
y_ecdf = sns.ecdfplot(data=data, color="red")
# plt.xscale('log')  # 设置x轴为对数刻度
plt.xticks(np.arange(0, 61, 10))
plt.yticks(np.arange(0, 1.1, 0.1))
plt.xlim(-5, 65)
plt.xlabel("Mapping Quality")
plt.title("Distribution of MAPQ (density curve)")
plt.grid(True, linestyle='--', alpha=1)
plt.savefig(FigName)
# g.title("Empirical Cumulative Distribution Function (ECDF) for Sepal Length")