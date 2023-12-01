#plot inter-intra proportion
import pandas as pd
from io import StringIO
import plotly.express as px
import plotly.io as pio
import argparse
import warnings

print("Ploting the distribution of VPCs (Virtual Parwise Contacts) distance...")

# Suppress all warnings
warnings.filterwarnings("ignore")

#读取参数
parser = argparse.ArgumentParser(description='Plot boundary check.')
#-> required arguments
parser.add_argument('-iis', type=str, dest='contact_dist_stats', required=True, help='path to contacts distance stats file')
#-> optional arguments
parser.add_argument('-o1', type=str, dest='FigName1', default='./Fig1.svg', help='path to output figure1 (proportion, By default, ./Fig1.svg)')
parser.add_argument('-o2', type=str, dest='FigName2', default='./Fig2.svg', help='path to output figure2 (distribution, By default, ./Fig2.svg)')
args = parser.parse_args()

# contact_dist_stats="./test_data/SRR11589404_10w.fq/SRR11589404_10w.fq.ii"
# FigName1 = "./ii_prop.svg"
# FigName2 = "./ii_count.svg"

contact_dist_stats=args.contact_dist_stats
FigName1 = args.FigName1
FigName2 = args.FigName2


# 读取文本文件并处理每一行的开头和结尾空白字符
with open(contact_dist_stats, 'r') as file:
    lines = file.readlines()
    lines = [line.strip() for line in lines]

# 使用Pandas读取处理后的数据
data = '\n'.join(lines)
df = pd.read_csv(StringIO(data), sep='\t')
df["group"][7]="[22,)"
df['sum']=df['intra short-range(<=20kb)'] + df['intra long-range(>20kb)'] + df['inter']

df_normalized_by_group = df.copy()

df_normalized_by_group['intra short-range(<=20kb)'] = df_normalized_by_group.apply(
    lambda row: 0 if row['sum'] == 0 else round(row['intra short-range(<=20kb)'] / row['sum'], 2),
    axis=1
)
df_normalized_by_group['intra long-range(>20kb)'] = df_normalized_by_group.apply(
    lambda row: 0 if row['sum'] == 0 else round(row['intra long-range(>20kb)'] / row['sum'], 2),
    axis=1
)
df_normalized_by_group['inter'] = df_normalized_by_group.apply(
    lambda row: 0 if row['sum'] == 0 else round(row['inter'] / row['sum'], 2),
    axis=1
)

# 显示DataFrame
print(df)

colors = {"intra short-range(<=20kb)": "#00cc96", "intra long-range(>20kb)": "#EB89B5", "inter": "#330C73"}

fig1 = px.bar(df_normalized_by_group, x="group", y=["intra short-range(<=20kb)", "intra long-range(>20kb)", "inter"], 
             text="value", color_discrete_map=colors,
             title="Distribution of VPCs distance by dimension group (VPCs number)")
fig1.update_layout(
    width=1000,  # 设置宽度
    height=600,  # 设置高度
)
fig1.update_xaxes(title_text='Contact Fragments')
fig1.update_yaxes(title_text='Proportion')
fig1.update_layout(
    paper_bgcolor='rgba(0,0,0,0)',
    plot_bgcolor='rgba(0,0,0,0)'
)

fig2 = px.bar(df, x="group", y=["intra short-range(<=20kb)", "intra long-range(>20kb)", "inter"], 
             text="value", color_discrete_map=colors,
             title="Distribution of VPCs distance by dimension group (proportion)")
fig2.update_layout(
    width=1000,  # 设置宽度
    height=600,  # 设置高度
)
fig2.update_xaxes(title_text='Contact Fragments')
fig2.update_yaxes(title_text='VPCs Number')
fig2.update_layout(
    paper_bgcolor='rgba(0,0,0,0)',
    plot_bgcolor='rgba(0,0,0,0)'
)

# fig1.show()
# fig2.show()

pio.write_image(fig1, FigName1)
pio.write_image(fig2, FigName2)