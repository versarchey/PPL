# -*- coding: utf-8 -*-
import re
from . GenomeTrack import GenomeTrack
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import os
from math import *
from sklearn.cluster import AgglomerativeClustering, KMeans
from scipy.cluster.hierarchy import linkage, dendrogram, fcluster
from scipy.sparse import coo_matrix
import pysam # using tabix interface
import umap
from sklearn.decomposition import PCA
from sklearn import manifold
import seaborn as sns

def convert_to_hex(color_tuple, alpha=1):
    r, g, b, a = color_tuple
    hex_r = format(int(r * 255), '02x')
    hex_g = format(int(g * 255), '02x')
    hex_b = format(int(b * 255), '02x')
    hex_a = format(int(alpha * 255), '02x')
    hex_color = f"#{hex_r}{hex_g}{hex_b}{hex_a}"
    return hex_color

def dimensionality_reduction(self, data):
    reducer=None
    method = self.properties['ld_dimension_reduction_method']
    if method == 'umap':
        n_components=2
        n_neighbors=15
        nn_method="annoy"
        set_op_mix_ratio=1
        init='spectral'
        min_dist=0.1
        metric=self.properties['clustering_linkage_distance']
        reducer = umap.UMAP(n_components=n_components, 
                            n_neighbors=n_neighbors, 
                            # min_dist=min_dist,
                            metric=metric,
                            set_op_mix_ratio=set_op_mix_ratio,
                            init=init,
                            random_state=self.properties['random_seed'],
                            )
    elif method == 'tsne':
        # t-SNE
        n_components=2
        perplexity=30
        early_exaggeration=12
        learning_rate=200
        reducer = manifold.TSNE(n_components=n_components,
                            perplexity=perplexity,
                            early_exaggeration=early_exaggeration,
                            learning_rate=learning_rate,
                            random_state=self.properties['random_seed'],
                            )
    elif method == 'pca':
        # PCA
        n_components=2
        reducer = PCA(n_components=n_components,
                      random_state=self.properties['random_seed'],
                      )
    else:
        # print unexpected error
        raise ValueError(f"Unexpected dimensionality reduction method: {method}")
    
    embedding = reducer.fit_transform(data)

    return embedding

def dimensionality_reduction_plot(self, embedding, n_clusters=4, figsize=(8, 6)):
    figname=self.properties['ld_dimension_reduction_plot_file_name']
    ld_dimension_reduction_method = self.properties['ld_dimension_reduction_method'].upper()

    # 获取聚类结果
    labels=ld_clustering(self, embedding, n_clusters)

    # 将结果添加到输入数据的 DataFrame 中
    c1n=ld_dimension_reduction_method+"1"
    c2n=ld_dimension_reduction_method+"2"
    result_df = pd.DataFrame(embedding, columns=[c1n, c2n])
    result_df['Cluster'] = labels

    # 绘制散点图
    fig, ax_umap = plt.subplots(figsize = figsize)
    scatter = ax_umap.scatter(result_df[c1n], result_df[c2n], c=result_df['Cluster'], cmap='coolwarm', alpha=0.7)
    ax_umap.set_title(ld_dimension_reduction_method+' Clustering')
    ax_umap.set_xlabel(c1n)
    ax_umap.set_ylabel(c2n)
    fig.colorbar(scatter)
    fig.savefig(figname)
    plt.close(fig)

    return result_df

def ld_clustering(self, embedding, n_clusters):
    labels=""
    if self.properties['ld_clustering_method'] is "kmeans":
        kmeans = KMeans(n_clusters=n_clusters, 
                        random_state=self.properties['random_seed']
                        )
        labels = kmeans.fit_predict(embedding)
    return labels

def plot_heatmap(data, save_path):
    """
    绘制热图并保存到指定路径。

    参数:
    - data: 输入的数据，可以是 NumPy 数组或 Pandas DataFrame
    - save_path: 保存文件的路径
    """
    # 检查数据类型，如果是 DataFrame，获取其值
    if isinstance(data, pd.DataFrame):
        data = data.values

    fig, ax_heatmap = plt.subplots(figsize = (20, 30))

    # 绘制热图
    im = ax_heatmap.imshow(data, cmap='viridis', interpolation='nearest')

    # 添加颜色条
    colorbar = plt.colorbar(im, ax= ax_heatmap)
    colorbar.set_label('Color Scale')

    # 保存图形
    fig.savefig(save_path)
    plt.close(fig)
    


class TextTrack(GenomeTrack):
    Y_LIM=0
    SUPPORTED_ENDINGS = ['.clusters', '.cluster', '.gz', '.contacts.gz']  # this is used by make_tracks_file to guess the type of track based on file name
    TRACK_TYPE = 'multi-contacts'
    OPTIONS_TXT = """
height = 3
title =
text =
# x position of text in the plot (in bp)
x position =
"""
    DEFAULTS_PROPERTIES = {'bin_size':1000, 'height':10, 'plot_line':True,
                            "clustering":True , 'num_clusters': 4, 'clustering_bin_num':40, 'clustering_bin_size':10000,
                            "marker_size": 8, "line_width":2, 'alpha':0.7,
                            "clustering_linkage_method":"ward", "clustering_linkage_distance":"euclidean",
                            'clustering_linkage_plot':True, "clustering_linkage_plot_file_name":"linkage.png",
                            'clustering_matrix_plot':True, "clustering_matrix_plot_file_name":"matrix.png",
                            'num_clusters_readin': np.inf, 'num_clusters_plot':1000, 
                            'color_map': False, 'color':'#17A589', 'color_line':'#76D7C4',
                            'limit_regions':'','limit_regions_file':'', "only_plot_limited":False, "keep_single_fragments":False, 
                            'ld_dimension_reduction':False, 'ld_dimension_reduction_method':'umap',
                            'ld_clustering_method':'kmeans', 'ld_num_clusters':4,
                            'ld_dimension_reduction_plot':True, 'ld_dimension_reduction_plot_file_name':"dims_reduction.png", 
                            'kde_plot':True, 'kde_plot_only':False, 'kde_plot_cmap':'viridis', 'kde_plot_fill': False,
                            'plot_distr':False, 'random_seed':777,
                            }
    NECESSARY_PROPERTIES = ['file', ]
    SYNONYMOUS_PROPERTIES = {}
    POSSIBLE_PROPERTIES = {}
    BOOLEAN_PROPERTIES = ['clustering_linkage_plot', 'clustering_matrix_plot',
                          'ld_dimension_reduction_plot', 'plot_line',
                          'kde_plot', 'kde_plot_only', 'kde_plot_fill', 'plot_distr', 
        'ld_dimension_reduction', 'color_map', 'keep_single_fragments', "only_plot_limited", "clustering"]
    STRING_PROPERTIES = ['clustering_linkage_plot_file_name', 'clustering_matrix_plot_file_name',
        'title', 'file_type', 'file', 'color', 'ld_dimension_reduction_method','kde_plot_cmap', 
        'ld_dimension_reduction_plot_file_name','color_line', 'limit_regions', 'limit_regions_file',"clustering_linkage_method"]
    FLOAT_PROPERTIES = {'height': [0, np.inf], 'alpha': [0,1]}
    INTEGER_PROPERTIES = {'num_clusters':[1,100], 'bin_size':[100, np.inf], 'clustering_bin_size':[100, np.inf], 'clustering_bin_num':[0,np.inf],
                          'num_clusters_readin':[0,np.inf], 'num_clusters_plot':[0,np.inf], 
                          'marker_size':[1,np.inf],"line_width":[0,np.inf],
                          'ld_num_clusters':[2,20], 'random_seed':[-np.inf, np.inf],
                          }

    def load_clusters(self, file, chrom, region_start, region_end):
        print("INFO:reading data from "+file)
        """Returns a python dict { id : sequence } for the given .fasta file"""
        #读取clusters
        count = {}
        num = 0
        with open(os.path.realpath(file), 'r') as f:
            line = f.readline()
            while line:
                x = line.split()
                count[x[0]] = [j for j in x[1:]]
                line = f.readline()
                if (num+1)%self.properties['num_clusters_readin']==0: break
                num = num +1
        '''
        count:
            {'SRR19920179.1': ['chr8:93270376', 'chr8:34442757', 'chr8:93157576'],
            'SRR19920179.2': ['chrX:76587649', 'chrX:69963941', 'chrX:76568930'],...}
        '''
        Res = pd.DataFrame()
        index = 0
        for i in count:
            tmp = list(filter(lambda text: all([chrom + ":" in text]), count[i]))
            if(len(tmp) > 1):
                t = pd.DataFrame(data=tmp, columns=['position'])
                t.insert(column="index", loc=1, value=index)
                t.insert(column="ID", loc=1, value=i)
                Res = pd.concat([Res, t], ignore_index=True)
                index += 1
        tmp = Res['position'].str.split(':', expand=True)
        Res['chr'] = tmp.loc[:,0]
        Res['site'] = tmp.loc[:,1].astype(int)
        Res = Res[(Res['site'] > region_start) & (Res['site'] < region_end)]
        Res['siteBin'] = (Res['site']/self.properties['bin_size']).astype(int)*self.properties['bin_size']
        # Res = Res.sort_values(by="site")
        '''
        Res:
                        position                 ID  index   chr       site
            0      chr2:73417272      SRR19920179.9      0  chr2   73417272
            1      chr2:73358108      SRR19920179.9      0  chr2   73358108
            2      chr2:28064566     SRR19920179.11      1  chr2   28064566
            3      chr2:27967074     SRR19920179.11      1  chr2   27967074
            4      chr2:27562067     SRR19920179.11      1  chr2   27562067
            ...              ...                ...    ...   ...        ...
        '''
        return Res

    def load_clusters_optimized(self, file, chrom, region_start, region_end):
        print("INFO:reading data from "+file)
        """Returns a python dict { id : sequence } for the given .fasta file"""
        #读取clusters
        Res = pd.DataFrame()
        num = 0 # 读入的行数
        index = 0 #read_name的别称
        with open(os.path.realpath(file), 'r') as f:
            line = f.readline()
            while line:
                x = line.split()
                # count[x[0]] = [j for j in x[1:]]
                read_name = x[0]
                frags = [j for j in x[1:]]
                # 按染色体过滤
                tmp = list(filter(lambda text: all([chrom + ":" in text]), frags))
                if(len(tmp) > 1):
                    t = pd.DataFrame(data=tmp, columns=['position'])
                    fields = t['position'].str.split(':', expand=True)
                    t['chr'] = fields.loc[:,0]
                    t['site'] = fields.loc[:,1].astype(int)
                    # 按显示区间过滤
                    t = t[(t['site'] > region_start) & (t['site'] < region_end)]
                    if(len(t) > 1):
                        t.insert(column="index", loc=1, value=index)
                        t.insert(column="ID", loc=1, value=read_name)
                        Res = pd.concat([Res, t], ignore_index=True)
                    index += 1
                line = f.readline()
                if (num+1)%self.properties['num_clusters_readin']==0: break
                if (num+1)%200000==0: print("INFO:num_line: "+str(num))
                num = num +1
        Res['siteBin'] = (Res['site']/self.properties['bin_size']).astype(int)*self.properties['bin_size']
        '''
        Res:
                        position                 ID  index   chr       site
            0      chr2:73417272      SRR19920179.9      0  chr2   73417272
            1      chr2:73358108      SRR19920179.9      0  chr2   73358108
            2      chr2:28064566     SRR19920179.11      1  chr2   28064566
            3      chr2:27967074     SRR19920179.11      1  chr2   27967074
            4      chr2:27562067     SRR19920179.11      1  chr2   27562067
            ...              ...                ...    ...   ...        ...
        '''
        return Res
    
    def load_clusters_tabix(self, file, chrom, region_start, region_end):
        print("INFO:reading data from "+file)
        """Returns a python dict { id : sequence } for the given .fasta file"""
        #读取clusters
        tbx = pysam.TabixFile(file, index = file+".tbi")
        Res = pd.DataFrame()
        num = 0 # 读入的行数
        index = 0 #read_name的别称
        for row in tbx.fetch(chrom, region_start, region_end):
            x = row.split()
            # count[x[0]] = [j for j in x[1:]]
            chrom = x[0]
            site = int((int(x[1])+int(x[2]))/2)
            start = int(x[1])
            end = int(x[2])
            ID = x[3]
            index = -1
            position = chrom + ":" + str(site)
            t = pd.DataFrame(data=[[position, ID, index, chrom, site, start, end]], columns=['position',"ID","index", "chr", "site", "start", "end"])
            Res = pd.concat([Res, t], ignore_index=True)

            if (num+1)%self.properties['num_clusters_readin']==0: break
            # if (num+1)%1000000==0: break
            if (num+1)%200000==0: print("INFO:num_line: "+str(num))
            num = num +1
        # print(Res)
        '''
                               position                    ID  index   chr      site     start       end
                    0     chr9:36950997   SRR19920179.2534885     -1  chr9  36950997  36947498  36954497
                    1     chr9:36949208  SRR19920179.19645678     -1  chr9  36949208  36948486  36949931
                    2     chr9:36949391   SRR19920179.6983691     -1  chr9  36949391  36948490  36950293
                    3     chr9:36949211  SRR19920179.19296202     -1  chr9  36949211  36948490  36949933
                    4     chr9:36949389   SRR19920179.1493670     -1  chr9  36949389  36948493  36950286
                    ...             ...                   ...    ...   ...       ...       ...       ...
                    5200  chr9:37066508   SRR19920179.1251269     -1  chr9  37066508  37066137  37066880
                    5201  chr9:37066646  SRR19920179.14284065     -1  chr9  37066646  37066137  37067156
                    5202  chr9:37066508   SRR19920179.6117855     -1  chr9  37066508  37066137  37066879
                    5203  chr9:37066514  SRR19920179.12271814     -1  chr9  37066514  37066169  37066859
                    5204  chr9:37066566   SRR19920179.7727198     -1  chr9  37066566  37066253  37066880
        '''
        Res['siteBin'] = (Res['site']/self.properties['bin_size']).astype(int)*self.properties['bin_size']
        # Res['siteBin'] = (Res['site']/10000).astype(int)*10000

        # 选择与打标签
        ID_col=Res["ID"].sort_values()
        ID_list = None
        ID2Index = {}
        if (self.properties['keep_single_fragments']):
            ID_list = ID_col.unique() # 保留区间唯一片段
        else:
            valid_ID = ID_col.duplicated()
            ID_list = ID_col[valid_ID].unique() # 不保留
        # print(Res)
        Res=Res[Res['ID'].isin(ID_list)].sort_values(by="ID")
        index=0
        for index in range(len(ID_list)):
            ID2Index[ID_list[index]] = index
        Res['index'] = Res['ID'].map(ID2Index)

        return Res
    

    def plot_concatemers_bin(self, ax, Res):
        print("INFO:plotting concatemers")
        #Res = Res[(Res['site'] >= 100000000) & (Res['site'] <= 150000000)]
        # Res['scale_site'] = (Res['site'] - min(Res['site']))/ (max(Res['site']) - min(Res['site']))

        # label = [str(round(min(Res['site'])/1000000, 2))+"Mb", 
        #         str(round((max(Res['site'])+min(Res['site']))/2000000, 2))+"Mb",
        #         str(round(max(Res['site'])/1000000, 2))+"Mb"]

        cmap = plt.get_cmap('coolwarm')
        if self.properties['ld_dimension_reduction']:
            num = self.properties['ld_num_clusters']
        else:
            num = self.properties['num_clusters']
        colors = [convert_to_hex(cmap(i), self.properties['alpha']) for i in np.linspace(0, 1, num)]
        self.properties['color'] = self.properties['color'] + format(int(255*self.properties['alpha']), '02x')
        self.properties['color_line'] = self.properties['color_line'] + format(int(255*self.properties['alpha']), '02x')
        count = 0
        #根据每条read作图
        print(Res['index'].unique())
        for index_value in Res['index'].unique():
            #取得相关的所有frags
            subset = Res[Res['index'] == index_value]
            subset['index'] = count
            color = None
            line_color = None
            if self.properties['color_map'] == True:
                color = colors[int(subset.iloc[0]["group_id"])]
                line_color = colors[int(subset.iloc[0]["group_id"])]
            else:
                color = self.properties['color']
                line_color = self.properties['color_line']
            if not self.properties['plot_line']:
                line_color="#00000000"
            # ax.plot(subset['scale_site'], subset['index'], marker='o', linestyle='-', linewidth=1, markersize=2, color=color, alpha=0.5)
            ax.plot(subset['siteBin'] + self.properties['bin_size']/2, subset['index'], marker='o', linestyle='-', 
                    linewidth=self.properties['line_width'], 
                    markersize=self.properties['marker_size'], 
                    markerfacecolor=color,
                    markeredgecolor="#00000000", 
                    color=line_color,
                    )
            count += 1
            if (count+1)%self.properties['num_clusters_plot']==0: break
        # plt.xticks([0, 0.5, 1], label)
        # plt.yticks([],[])
        # plt.savefig('./test.png', dpi=300)
            
    
    def plot_concatemers_bin_with_kde(self, ax, Res):
        print("INFO:plotting concatemers with KDE")
        
        cmap = plt.get_cmap('coolwarm')
        if self.properties['ld_dimension_reduction']:
            num = self.properties['ld_num_clusters']
        else:
            num = self.properties['num_clusters']
        colors = [convert_to_hex(cmap(i), self.properties['alpha']) for i in np.linspace(0, 1, num)]
        self.properties['color'] = self.properties['color'] + format(int(255*self.properties['alpha']), '02x')
        self.properties['color_line'] = self.properties['color_line'] + format(int(255*self.properties['alpha']), '02x')
        count = 0
        
        # 根据每条read作图
        print(Res['index'].unique())
        for index_value in Res['index'].unique():
            # 取得相关的所有frags
            Res.loc[Res['index'] == index_value,'siteRow']=count
            subset = Res[Res['index'] == index_value]
            subset['index'] = count
            color = None
            line_color = None
            if self.properties['color_map'] == True:
                color = colors[int(subset.iloc[0]["group_id"])]
                line_color = colors[int(subset.iloc[0]["group_id"])]
            else:
                color = self.properties['color']
                line_color = self.properties['color_line']
            if not self.properties['plot_line']:
                line_color="#00000000"

            if not self.properties['kde_plot_only']:
                # 画散点图
                ax.plot(subset['siteBin'] + self.properties['bin_size']/2, subset['index'], marker='o', linestyle='-', 
                        linewidth=self.properties['line_width'], 
                        markersize=self.properties['marker_size'], 
                        markerfacecolor=color,
                        markeredgecolor="#00000000", 
                        color=line_color,
                        )
            count += 1
            if (count+1) % self.properties['num_clusters_plot'] == 0: 
                break
            
        if self.properties['kde_plot']:
            # 添加Kernel Density Estimation
            # sns.kdeplot(subset['siteBin'] + self.properties['bin_size']/2, subset['index'], cmap='viridis', fill=True, thresh=0, levels=30, ax=ax)
            Res_reset = Res.reset_index(drop=True)
            # sns.kdeplot(data=Res_reset, x='siteBin', y='siteRow', cmap='viridis', fill=True, thresh=0, levels=30, ax=ax)
            sns.kdeplot(data=Res_reset, x='siteBin', y='siteRow', 
                        cmap=self.properties['kde_plot_cmap'], 
                        fill=self.properties['kde_plot_fill'], 
                        thresh=0, levels=30, ax=ax)
            
    def plot_concatemers_distr(self, ax, Res):
        print("INFO:plotting concatemers with KDE")
        
        cmap = plt.get_cmap('coolwarm')
        if self.properties['ld_dimension_reduction']:
            num = self.properties['ld_num_clusters']
        else:
            num = self.properties['num_clusters']
        colors = [convert_to_hex(cmap(i), self.properties['alpha']) for i in np.linspace(0, 1, num)]
        self.properties['color'] = self.properties['color'] + format(int(255*self.properties['alpha']), '02x')
        self.properties['color_line'] = self.properties['color_line'] + format(int(255*self.properties['alpha']), '02x')
        count = 0
        
        # 根据每条read作图
        print(Res['index'].unique())
        for index_value in Res['index'].unique():
            # 取得相关的所有frags
            Res.loc[Res['index'] == index_value,'siteRow']=count
            count += 1
            if (count+1) % self.properties['num_clusters_plot'] == 0: 
                break
            
        if self.properties['plot_distr']:
            # 添加Kernel Density Estimation
            # sns.kdeplot(subset['siteBin'] + self.properties['bin_size']/2, subset['index'], cmap='viridis', fill=True, thresh=0, levels=30, ax=ax)
            Res_reset = Res.reset_index(drop=True)
            # sns.kdeplot(data=Res_reset, x='siteBin', y='siteRow', cmap='viridis', fill=True, thresh=0, levels=30, ax=ax)
            # sns.kdeplot(data=Res, x="siteBin", ax=ax, fill=True, color='red', label='KDE')
            sns.histplot(data=Res, x="siteBin", ax=ax, 
                        # fill=True, 
                        color='red', 
                        kde=True)
        # 需要将改该值改为ax的y轴上线 self.Y_LIM=len(height)
        y_limits = ax.get_ylim()
        # x_limits = ax.get_xlim()
        # print(y_limits)
        # print(x_limits)
        self.Y_LIM=y_limits[1]
        


    def clusters2vectors(self, clusters, region_start, region_end):
        print("INFO: converting cluster to verctors")
        len_vec = int((region_end - region_start)/self.properties['clustering_bin_size']) + 1
        vec_indexes = []
        vec_set = np.zeros(shape=(0,len_vec))
        Res = pd.DataFrame()
        for index_value in clusters['index'].unique():
            #取得相关的所有frags
            vec_indexes.append(index_value)
            subset = clusters[clusters['index'] == index_value]
            vec = np.zeros(shape=(1,len_vec))
            for i in subset['siteBin']:
                vec[0, int((i-region_start)/self.properties['clustering_bin_size'])] = 1
            vec_set=np.concatenate((vec_set, vec), axis = 0)
        # print("vec_indexes",vec_indexes)
        # print("vec_set",vec_set)
        return vec_indexes, vec_set

    # 根据限制区间过滤reads
    def filter_limited_regions(self, clusters):
        # 读入限制的区间
        regions = pd.DataFrame(columns=["chr", "start","end"])
        if self.properties['limit_regions_file'] != '' :
            # 从bed文件中读入
            with open(self.properties['limit_regions_file'], "rt") as file:
                for region in [i.strip() for i in file.readlines()]:
                    fields = region.split()[:3]
                    t = pd.DataFrame(data=[[i for i in fields]], columns=["chr", "start","end"])
                    regions = pd.concat([regions, t])
                regions["start"] = regions["start"].astype(int)
                regions["end"] = regions["end"].astype(int)
        else :
            #从字符串中读入
            for region in self.properties['limit_regions'].split(";"):
                fields = re.split("[:-]", region)
                t = pd.DataFrame(data=[[i for i in fields]], columns=["chr", "start","end"])
                regions = pd.concat([regions, t])
            regions["start"] = regions["start"].astype(int)
            regions["end"] = regions["end"].astype(int)
        print("limited regions: ", regions)
        # 
        frags_in_regions = pd.DataFrame(columns=clusters.columns.values.tolist()) # 新的容器
        for index, row in clusters.iterrows():
            isInRegion = False
            for index2, region in regions.iterrows():
                # 若与任何一个区间重叠，则保留
                if (row['chr'] == region['chr']) and ((row['start'] - region["end"]) * (row['end'] - region["start"]) < 0):
                # if (row['chr'] == region['chr']) and (row['site'] > region["start"] and row['site'] < region["end"]):
                    isInRegion = True
                    break
            if isInRegion:
                frags_in_regions = frags_in_regions.append(row)
        # print(frags_in_regions)
        if (self.properties['only_plot_limited']):
            if not self.properties['keep_single_fragments']:
                ID_col=frags_in_regions["ID"].sort_values()
                ID_list = None
                ID2Index = {}
                valid_ID = ID_col.duplicated()
                ID_list = ID_col[valid_ID].unique()
                frags_in_regions = frags_in_regions[frags_in_regions['ID'].isin(ID_list)]
            return frags_in_regions
        indexes_reserved = frags_in_regions["index"].unique()
        # print(len(indexes_reserved))
        new_clusters = clusters.loc[indexes_reserved]
        print(len(new_clusters))
        return new_clusters

    def plot(self, ax, chrom, region_start, region_end):
        # print("INFO: plot region:" + chrom + ":" + str(region_start) + "-" + str(region_end))
        """
        This example simply plots the given title at a fixed
        location in the axis. The chrom, region_start and region_end
        variables are not used.
        Args:
            ax: matplotlib axis to plot
            chrom_region: chromosome name
            start_region: start coordinate of genomic position
            end_region: end coordinate
        """
        # print text at position x = self.properties['x position'] and y = 0.5 (center of the plot)
        # ax.text(self.properties['x_position'], 0.5, self.properties['text'])
        # ax.text(region_start+100, 0.5, self.properties['text'])

        # load clusters file
        # clusters = self.load_clusters(self.properties['file'], chrom, region_start, region_end)
        # clusters = self.load_clusters_gpt(self.properties['file'], chrom, region_start, region_end)
        # clusters = self.load_clusters_optimized(self.properties['file'], chrom, region_start, region_end)
        clusters = self.load_clusters_tabix(self.properties['file'], chrom, region_start, region_end)
        clusters.index=clusters["index"]    #设置index
        print("INFO: num of clusters read in:", len(clusters["index"].unique()))

        if self.properties['limit_regions'] != '' or self.properties['limit_regions_file'] != '' :
            clusters = self.filter_limited_regions(clusters)
            print("INFO: num of clusters in limited region:", len(clusters["index"].unique()))
        if(len(clusters) <= 0):
            print("WARNING: No Records meeting standards")
            exit()

        # print(clusters)

        # only plot fragments histgram
        if self.properties['plot_distr']:
            self.plot_concatemers_distr(ax, clusters)
            return

        # clustering 
        if self.properties["clustering"]:
            if self.properties["plot_distr"]:
                self.properties["keep_single_fragments"]=True

            if self.properties["clustering_bin_num"] != 0:
                self.properties["clustering_bin_size"] = (region_end-region_start)/self.properties["clustering_bin_num"]
            # 2vectors
            vec_indexes, vec_set = self.clusters2vectors(clusters, region_start, region_end)
            self.Y_LIM=len(vec_indexes)
            # self.properties["x_lim"]=0
            # self.properties["y_lim"]=len(vec_indexes)

            sorted_order = '';
            # hierarchical clustering
            if not self.properties['ld_dimension_reduction']:
                # clustering = AgglomerativeClustering().fit(vec_set)
                linkage_matrix = linkage(vec_set, 
                                         method=self.properties['clustering_linkage_method'], 
                                         metric=self.properties['clustering_linkage_distance'], 
                                         optimal_ordering = True)
                sorted_order = dendrogram(linkage_matrix, no_plot=True)['leaves']
                
                #plot linkage and heatmap
                if self.properties['clustering_linkage_plot']:
                    fig, ax_hc = plt.subplots(figsize = (30, 12))
                    dendrogram(linkage_matrix, ax=ax_hc)
                    fig.savefig(self.properties['clustering_linkage_plot_file_name'])
                    plt.close(fig)
                if self.properties['clustering_matrix_plot']:
                    vec_set_sorted = vec_set[sorted_order] # heatplot
                    plot_heatmap(vec_set_sorted, self.properties['clustering_matrix_plot_file_name'])

                # 使用 fcluster 获取指定聚类数量
                num_clusters = self.properties['num_clusters']  # 指定聚类的数量
                cluster_sorted_order = fcluster(linkage_matrix, num_clusters, criterion='maxclust')-1
                 # sorting by leaves node
                leaves = sorted_order
                vecs_original_sort = np.dstack((
                    np.array(vec_indexes), 
                    np.array(range(len(vec_indexes))), 
                    cluster_sorted_order
                    ))[0].astype(int) 
                vecs_leaves_sort = np.dstack(( 
                    np.array(leaves), 
                    np.zeros(len(leaves))
                    ))[0].astype(int) 
                # print(vecs_original_sort)
                # print(vecs_leaves_sort)
                for i in range(vecs_leaves_sort.shape[0]):
                    leave_index = vecs_leaves_sort[i,0]
                    limiter = vecs_original_sort[:,1]==leave_index
                    vecs_leaves_sort[i,1] = vecs_original_sort[limiter,0]
                    try :
                        clusters.loc[clusters.index == vecs_leaves_sort[i,1], "group_id"] = vecs_original_sort[limiter,2][0]
                    except ValueError:
                        print(clusters.loc[clusters.index == vecs_leaves_sort[i,1]])
                        print("vecs_original_sort[limiter,2]", vecs_original_sort[limiter,2])
                clusters_sort_by_leaves = clusters.loc[vecs_leaves_sort[:,1]]
                clusters = clusters_sort_by_leaves
                # print(clusters)
                # print("INFO: concatemers_num: "+ str(vecs_leaves_sort.shape[0]))
                #            position                      ID index   chr      site     start       end   siteBin  group_id
                # 2865  chr9:37080587   SRR19920179.8902452.0  2865  chr9  37080587  37079542  37081633  37080500       1.0
                # 2865  chr9:36935847   SRR19920179.8902452.0  2865  chr9  36935847  36935439  36936255  36935500       1.0
                # 2865  chr9:37030723   SRR19920179.8902452.0  2865  chr9  37030723  37030518  37030928  37030500       1.0
                # 2737  chr9:37038011   SRR19920179.8233490.0  2737  chr9  37038011  37036870  37039153  37038000       1.0
                # 2737  chr9:37080207   SRR19920179.8233490.0  2737  chr9  37080207  37079419  37080996  37080000       1.0
                # ...             ...                     ...   ...   ...       ...       ...       ...       ...       ...
                # [301 rows x 9 columns]

            else :
                # low dim
                ## dimensionality_reduction
                ld_result = dimensionality_reduction(self, vec_set)

                ld_result = dimensionality_reduction_plot(self, ld_result, n_clusters=self.properties['ld_num_clusters'])

                ld_result["index"] = np.array(vec_indexes)
                # ld_result = ld_result.sort_values(by='Cluster')
                # print("ld_result", ld_result)
                #          PCA1      PCA2  Cluster  index
                # 47   0.927038  0.358055        0   1115
                # 126  0.340825  0.032454        0   2756
                # 34   0.927038  0.358055        0    908
                # 125  0.805772  0.331632        0   2737
                # 38   0.707053 -0.151995        0    951
                # ..        ...       ...      ...    ...
                # 40   0.089167  0.966179        3   1042
                # 86   0.089167  0.966179        3   1857
                # 6    0.276882  1.012033        3    295
                # 61   0.089167  0.966179        3   1417
                # 97   0.089167  0.966179        3   2076
                # [142 rows x 4 columns]
                # print(clusters)
                #            position                      ID index   chr      site     start       end   siteBin
                # 4     chr9:36935677  SRR19920179.10014335.0     4  chr9  36935677  36935440  36935915  36935500
                # 4     chr9:36936405  SRR19920179.10014335.0     4  chr9  36936405  36936241  36936570  36936000
                # 35    chr9:37027517  SRR19920179.10273126.0    35  chr9  37027517  37027083  37027952  37027500
                # 35    chr9:36995348  SRR19920179.10273126.0    35  chr9  36995348  36994231  36996466  36995000
                # 70    chr9:37010820  SRR19920179.10501390.0    70  chr9  37010820  37010729  37010911  37010500
                # ...             ...                     ...   ...   ...       ...       ...       ...       ...
                # 3005  chr9:37034243   SRR19920179.9757942.0  3005  chr9  37034243  37034194  37034293  37034000
                # 3005  chr9:37011341   SRR19920179.9757942.0  3005  chr9  37011341  37011048  37011634  37011000
                # 3039  chr9:36996063   SRR19920179.9976533.0  3039  chr9  36996063  36995658  36996468  36996000
                # 3039  chr9:37011298   SRR19920179.9976533.0  3039  chr9  37011298  37011031  37011566  37011000
                # 3039  chr9:37011939   SRR19920179.9976533.0  3039  chr9  37011939  37011631  37012247  37011500
                # [301 rows x 8 columns]
                for index, row in ld_result.iterrows():
                    limiter = clusters['index'] == row['index']
                    clusters.loc[limiter, 'group_id'] = row['Cluster']
                clusters = clusters.sort_values(by=['group_id','index'])
                print(clusters)
                print(clusters.loc[clusters['index']==1686])
                # print(clusters)

        else :
            self.Y_LIM=len(clusters["index"].unique())

        # plot concatemers
        # self.plot_concatemers_bin(ax, clusters)
        self.plot_concatemers_bin_with_kde(ax, clusters)

    def plot_label(self, label_ax, width_dpi, h_align='left'):
        if h_align == 'left':
            label_ax.text(0.05, 0.5, self.properties['title'],
                          horizontalalignment='left', size='large',
                          verticalalignment='center',
                          transform=label_ax.transAxes,
                          wrap=True)
        elif h_align == 'right':
            txt = label_ax.text(1, 0.5, self.properties['title'],
                                horizontalalignment='right', size='large',
                                verticalalignment='center',
                                transform=label_ax.transAxes,
                                wrap=True)
            # To be able to wrap to the left:
            txt._get_wrap_line_width = lambda: width_dpi
        else:
            txt = label_ax.text(0.5, 0.5, self.properties['title'],
                                horizontalalignment='center', size='large',
                                verticalalignment='center',
                                transform=label_ax.transAxes,
                                wrap=True)
            # To be able to wrap to the left:
            txt._get_wrap_line_width = lambda: width_dpi

    def plot_y_axis(self, ax, plot_axis):
        # if not self.properties['plot_hist']:
        if True:
            print(self.Y_LIM)
            ax.set_yticks(np.arange(0, self.Y_LIM, self.Y_LIM/3))

            """
            Plot the scale of the y axis with respect to the plot_axis
            Args:
                ax: axis to use to plot the scale
                plot_axis: the reference axis to get the max and min.

            Returns:

            """

            def value_to_str(value):
                # given a numeric value, returns a
                # string that removes unneeded decimal places
                if value % 1 == 0:
                    str_value = str(int(value))
                else:
                    str_value = f"{value:.1f}"
                return str_value

            ymin, ymax = 0, 1
            # get the position of 0 in the transAxes scale
            y_at_zero = (0 - ymin) / (ymax - ymin)

            ymax_str = value_to_str(self.Y_LIM)
            ymin_str = '0'


            # plot something that looks like this:
            # ymax ┐
            #      │
            #      │
            #    0 ┘

            # the coordinate system used is the ax.transAxes (lower left corner (0,0), upper right corner (1,1)
            # this way is easier to adjust the positions such that the lines are plotted complete
            # and not only half of the width of the line.
            x_pos = [0, 0.5, 0.5, 0]
            y_pos = [y_at_zero, y_at_zero, ymax, ymax]
            ax.plot(x_pos, y_pos, color='black', linewidth=1, transform=ax.transAxes)
            ax.text(-0.2, y_at_zero, ymin_str, verticalalignment='bottom', horizontalalignment='right', transform=ax.transAxes)
            ax.text(-0.2, ymax, ymax_str, verticalalignment='top', horizontalalignment='right', transform=ax.transAxes)
            ax.patch.set_visible(False)
        

    
