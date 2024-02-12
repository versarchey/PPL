# "Pore-C Tracks" Usage  

## Visulization Method
<img src="https://github.com/versarchey/PPL-Toolbox/blob/main/visualization_Pore-C-track/figs/pipeline.png" width=100%>

## Installation
    # step 1:
    
    ## install requirements
    pip install -r requirements.txt

    # step 2:

    ## amend this library path to yourselves
    path_to_your_python_lib=/home/test/anaconda3/envs/pygenometracks2/lib/python3.8/site-packages/pygenometracks/tracks/PoreCTrack.py
    
    ## copy the Pore-C track type to ${path_to_your_python_lib}
    cp PoreCTrack.py ${path_to_your_python_lib}/pygenometracks/tracks

## Usage

#### Several usual `.ini` examples and output:
reuslt: <br>
<img src="https://github.com/versarchey/PPL-Toolbox/blob/main/visualization_Pore-C-track/figs/sample01.png" width=100%>

properties file: <br>

    [x-axis]
    where = top

    [genes]
    file = /mnt/e/tmp/visualization/hg38.refGene.gtf.gz
    height = 7
    title = genes
    fontsize = 10
    file_type = bed
    gene_rows = 10

    [bigwig file atac]
    file_type = bigwig
    file = /mnt/e/tmp/visualization/case/gm12878_atac_ENCFF180ZAY.bigWig
    # height of the track in cm (optional value)
    height = 4
    title = chromatin accessibility
    [spacer]

    [bigwig file H3K27ac]
    file_type = bigwig
    file = /mnt/e/tmp/visualization/case/gm12878_chip-H3K27ac_ENCFF440GZA.bigWig
    # height of the track in cm (optional value)
    height = 4
    title = ChIP-Seq H3K27ac
    [spacer]

    [bigwig file H3K4me1]
    file_type = bigwig
    file = /mnt/e/tmp/visualization/case/gm12878_chip-H3K4me1_ENCFF190RZM.bigWig
    # height of the track in cm (optional value)
    height = 4
    title = ChIP-Seq H3K4me1
    [spacer]

    [hic matrix]
    file = /mnt/e/tmp/visualization/4DNFIXP4QG5B.mcool::/resolutions/2000
    #file = /mnt/e/tmp/visualization/hipore-c_146.mcool::/resolutions/5000
    title = interaction hic_matrix
    #colormap = bwr
    max_value = -4
    min_value = -7.5
    # depth is the maximum distance plotted in bp. In Hi-C tracks
    # the height of the track is calculated based on the depth such
    # that the matrix does not look deformed
    depth = 80000
    transform = log
    file_type = hic_matrix

    [spacer]

    [Pore-C track 0, fragments ditrbution, original]
    title = Pore-C track 0, fragments ditrbution, original
    plot_line = false
    alpha = 0.3
    file = /mnt/e/tmp/hyper_filter/chr9.1k.contacts.filtered.sorted.gz
    height = 3

    kde_plot = true
    kde_plot_cmap = viridis
    kde_plot_fill = false
    plot_distr = true

    [spacer]

    [Pore-C track 1, fragments ditrbution, limited by peaks]
    title = Pore-C track 1, fragments ditrbution, limited by peaks
    plot_line = false
    alpha = 0.3
    file = /mnt/e/tmp/hyper_filter/chr9.1k.contacts.filtered.sorted.gz
    height = 3

    limit_regions_file = /mnt/e/tmp/visualization/case/PAX5_related_intersected.bed

    kde_plot = true
    kde_plot_cmap = viridis
    kde_plot_fill = false
    plot_distr = true

    [spacer]

    [Pore-C track 2, fragments plot, non-processed]
    title = Pore-C track 2, fragments plot, non-processed
    file = /mnt/e/tmp/hyper_filter/chr9.1k.contacts.filtered.sorted.gz
    plot_line = false
    alpha = 0.3
    clustering = false

    marker_size = 6

    kde_plot = false
    kde_plot_cmap = viridis
    kde_plot_fill = false

    [spacer]

    [Pore-C track 3, fragments plot, hirarchical clustering only]
    title = Pore-C track 3, fragments plot, hirarchical clustering only
    plot_line = false
    alpha = 0.3
    file = /mnt/e/tmp/hyper_filter/chr9.1k.contacts.filtered.sorted.gz

    marker_size = 6

    kde_plot = false
    kde_plot_cmap = viridis
    kde_plot_fill = false

    [spacer]

    [Pore-C track 4, fragments plot, limited by regions only]
    title = Pore-C track 4, fragments plot, limited by regions only
    plot_line = true
    alpha = 0.55
    file = /mnt/e/tmp/hyper_filter/chr9.1k.contacts.filtered.sorted.gz
    clustering = false
    limit_regions_file = /mnt/e/tmp/visualization/case/PAX5_related_intersected.bed

    marker_size = 7

    kde_plot = false
    kde_plot_cmap = viridis
    kde_plot_fill = false

    only_plot_limited = true

    [spacer]

    [Pore-C track 5, high dimension (limited by regions + hirarchical clustering (40 bins))]
    title = Pore-C track 5, high dimension (limited by regions + hirarchical clustering (40 bins))
    file = /mnt/e/tmp/hyper_filter/chr9.1k.contacts.filtered.sorted.gz
    height = 10
    file_type = multi-contacts
    bin_size = 500

    ld_dimension_reduction = false
    ld_dimension_reduction_method = pca
    ld_clustering_method = kmeans
    ld_num_clusters = 4
    ld_dimension_reduction_plot = true
    ld_dimension_reduction_plot_file_name = dr_pca_test.png

    clustering = true
    clustering_linkage_method = ward
    clustering_linkage_distance = euclidean
    clustering_linkage_plot = true
    clustering_linkage_plot_file_name = linkage_test.png
    clustering_matrix_plot = true
    clustering_matrix_plot_file_name = matrix_test.png
    num_clusters = 4
    clustering_bin_num = 40
    clustering_bin_size = 10000
    num_clusters_readin = 3000000
    num_clusters_plot = 3000
    color_map = true
    color = #5281ffff
    color_line = #5281ffaa
    alpha = 0.55
    marker_size = 7
    line_width = 1
    limit_regions_file = /mnt/e/tmp/visualization/case/PAX5_related_intersected.bed
    # limit_regions = chr9:36993634-36994734;chr9:37027551-37028027;chr9:37027551-37028027
    #limit_regions = chr17:60135000-60138000;chr17:60076000-60080000;chr17:60137000-60142000
    #limit_regions = chr17:60136521-60137503;chr17:60077914-60078641;chr17:60140090-60140762
    only_plot_limited = true
    keep_single_fragments = false

    kde_plot = false
    kde_plot_cmap = viridis
    kde_plot_fill = false

    [spacer]

    [Pore-C track 6, low dimension (limited by regions + UMAP + KMeans)]
    title = Pore-C track 6, low dimension (limited by regions + UMAP + KMeans)
    file = /mnt/e/tmp/hyper_filter/chr9.1k.contacts.filtered.sorted.gz
    height = 10
    file_type = multi-contacts
    bin_size = 500

    ld_dimension_reduction = true
    ld_dimension_reduction_method = umap
    ld_clustering_method = kmeans
    ld_num_clusters = 4
    ld_dimension_reduction_plot = true
    ld_dimension_reduction_plot_file_name = dr_umap_test.png

    clustering = true
    clustering_linkage_method = ward
    clustering_linkage_distance = euclidean
    clustering_linkage_plot = true
    clustering_linkage_plot_file_name = linkage_test.png
    clustering_matrix_plot = true
    clustering_matrix_plot_file_name = matrix_test.png
    num_clusters = 4
    clustering_bin_num = 40
    clustering_bin_size = 10000
    num_clusters_readin = 3000000
    num_clusters_plot = 3000
    color_map = true
    color = #5281ffff
    color_line = #5281ffaa
    alpha = 0.55
    marker_size = 7
    line_width = 1
    limit_regions_file = /mnt/e/tmp/visualization/case/PAX5_related_intersected.bed
    # limit_regions = chr9:36993634-36994734;chr9:37027551-37028027;chr9:37027551-37028027
    #limit_regions = chr17:60135000-60138000;chr17:60076000-60080000;chr17:60137000-60142000
    #limit_regions = chr17:60136521-60137503;chr17:60077914-60078641;chr17:60140090-60140762
    only_plot_limited = true
    keep_single_fragments = false

    kde_plot = false
    kde_plot_cmap = viridis
    kde_plot_fill = false

    [spacer]

    [Pore-C track 7, kde plot, non-processed]
    title = Pore-C track 2, fragments plot, non-processed
    file = /mnt/e/tmp/hyper_filter/chr9.1k.contacts.filtered.sorted.gz
    plot_line = false
    alpha = 0.3
    clustering = false

    marker_size = 6

    kde_plot = true
    kde_plot_only = true
    kde_plot_cmap = coolwarm
    kde_plot_fill = true

    [spacer]

    [Pore-C track 8, kde plot, hirarchical clustering only]
    title = Pore-C track 3, fragments plot, hirarchical clustering only
    plot_line = false
    alpha = 0.3
    file = /mnt/e/tmp/hyper_filter/chr9.1k.contacts.filtered.sorted.gz

    marker_size = 6

    kde_plot = true
    kde_plot_only = true
    kde_plot_cmap = coolwarm
    kde_plot_fill = true

    [spacer]

    [Pore-C track 9, kde plot, limited by regions only]
    title = Pore-C track 4, fragments plot, limited by regions only
    plot_line = true
    alpha = 0.55
    file = /mnt/e/tmp/hyper_filter/chr9.1k.contacts.filtered.sorted.gz
    clustering = false
    limit_regions_file = /mnt/e/tmp/visualization/case/PAX5_related_intersected.bed

    marker_size = 7

    kde_plot = true
    kde_plot_only = true
    kde_plot_cmap = coolwarm
    kde_plot_fill = true

    only_plot_limited = true

    [spacer]

    [Pore-C track 10, kde plot, high dimension (limited by regions + hirarchical clustering (40 bins))]
    title = Pore-C track 5, high dimension (limited by regions + hirarchical clustering (40 bins))
    file = /mnt/e/tmp/hyper_filter/chr9.1k.contacts.filtered.sorted.gz
    height = 10
    file_type = multi-contacts
    bin_size = 500

    ld_dimension_reduction = false
    ld_dimension_reduction_method = pca
    ld_clustering_method = kmeans
    ld_num_clusters = 4
    ld_dimension_reduction_plot = true
    ld_dimension_reduction_plot_file_name = dr_pca_test.png

    clustering = true
    clustering_linkage_method = ward
    clustering_linkage_distance = euclidean
    clustering_linkage_plot = true
    clustering_linkage_plot_file_name = linkage_test.png
    clustering_matrix_plot = true
    clustering_matrix_plot_file_name = matrix_test.png
    num_clusters = 4
    clustering_bin_num = 40
    clustering_bin_size = 10000
    num_clusters_readin = 3000000
    num_clusters_plot = 3000
    color_map = true
    color = #5281ffff
    color_line = #5281ffaa
    alpha = 0.55
    marker_size = 7
    line_width = 1
    limit_regions_file = /mnt/e/tmp/visualization/case/PAX5_related_intersected.bed
    # limit_regions = chr9:36993634-36994734;chr9:37027551-37028027;chr9:37027551-37028027
    #limit_regions = chr17:60135000-60138000;chr17:60076000-60080000;chr17:60137000-60142000
    #limit_regions = chr17:60136521-60137503;chr17:60077914-60078641;chr17:60140090-60140762
    only_plot_limited = true
    keep_single_fragments = false

    kde_plot = true
    kde_plot_only = true
    kde_plot_cmap = coolwarm
    kde_plot_fill = true

    [spacer]

    [Pore-C track 11, kde plot, low dimension (limited by regions + UMAP + KMeans)]
    title = Pore-C track 6, low dimension (limited by regions + UMAP + KMeans)
    file = /mnt/e/tmp/hyper_filter/chr9.1k.contacts.filtered.sorted.gz
    height = 10
    file_type = multi-contacts
    bin_size = 500

    ld_dimension_reduction = true
    ld_dimension_reduction_method = umap
    ld_clustering_method = kmeans
    ld_num_clusters = 4
    ld_dimension_reduction_plot = true
    ld_dimension_reduction_plot_file_name = dr_umap_test.png

    clustering = true
    clustering_linkage_method = ward
    clustering_linkage_distance = euclidean
    clustering_linkage_plot = true
    clustering_linkage_plot_file_name = linkage_test.png
    clustering_matrix_plot = true
    clustering_matrix_plot_file_name = matrix_test.png
    num_clusters = 4
    clustering_bin_num = 40
    clustering_bin_size = 10000
    num_clusters_readin = 3000000
    num_clusters_plot = 3000
    color_map = true
    color = #5281ffff
    color_line = #5281ffaa
    alpha = 0.55
    marker_size = 7
    line_width = 1
    limit_regions_file = /mnt/e/tmp/visualization/case/PAX5_related_intersected.bed
    # limit_regions = chr9:36993634-36994734;chr9:37027551-37028027;chr9:37027551-37028027
    #limit_regions = chr17:60135000-60138000;chr17:60076000-60080000;chr17:60137000-60142000
    #limit_regions = chr17:60136521-60137503;chr17:60077914-60078641;chr17:60140090-60140762
    only_plot_limited = true
    keep_single_fragments = false

    kde_plot = true
    kde_plot_only = true
    kde_plot_cmap = coolwarm
    kde_plot_fill = true

    random_seed=7

    [spacer]

    [scalebar]
    title = scalebar test
    color = black
    height = 1
    file_type = scalebar


## Properties
<ul>
    <li>file_type<br>
        [STRING], [NECESSARY] <br>
        Use "multi-contacts" to enable Pore-C track module.
    </li>
    <li>file<br>
        [STRING], [NECESSARY] <br>
        Contacts file name (ended with .clusters, .cluster).
    </li>
    <li>height<br>
        [INTEGER] [DEFAULT:10]<br>
        Track height. 
    </li>
    <li>title<br>
        [STRING] <br>
        Track title.
    </li>
    <li>bin_size<br>
        [INTEGER] [DEFAULT:1000] [REGION:100-inf]<br>
        Resolution when plotting. e.g. "chr1:10126"->"chr1:10500".
    </li>
    <li>clustering<br>
        [BOOLEAN] [DEFAULT:true]<br>
        Hierarchical clustering.
    </li>
    <li>num_clusters<br>
        [INTEGER] [DEFAULT:4] [REGION:1-100]<br>
        The number of clusters found by the Hierarchical clustering algorithm.
    </li>
    <li>clustering_linkage_method<br>
        [STRING] [DEFAULT:ward] [REGION:[ward,single,complete,average,weighted,centroid,median]]<br>
        Hierarchical linkage distance computing method.
        Recommeded reference: (https://docs.scipy.org/doc/scipy/reference/generated/scipy.cluster.hierarchy.linkage.html#scipy.cluster.hierarchy.linkage).
    </li>
    <li>clustering_linkage_distance<br>
        [STRING] [DEFAULT:euclidean]<br>
        Pairwise distances between observations in n-dimensional space.
        ‘braycurtis’, ‘canberra’, ‘chebyshev’, ‘cityblock’, ‘correlation’, ‘cosine’, ‘dice’, ‘euclidean’, ‘hamming’, ‘jaccard’, ‘jensenshannon’, ‘kulczynski1’, ‘mahalanobis’, ‘matching’, ‘minkowski’, ‘rogerstanimoto’, ‘russellrao’, ‘seuclidean’, ‘sokalmichener’, ‘sokalsneath’, ‘sqeuclidean’, ‘yule’.
        Recommeded reference: (https://docs.scipy.org/doc/scipy/reference/generated/scipy.cluster.hierarchy.linkage.html#scipy.cluster.hierarchy.linkage).
    </li>
    <li>clustering_linkage_plot<br>
        [BOOLEAN] [DEFAULT:true]<br>
        Whether plot linkage tree to file.
    </li>
    <li>clustering_linkage_plot_file<br>
        [STRING] [DEFAULT:linkage.png]<br>
        Figure name of linkage tree.
    </li>
    <li>clustering_matrix_plot<br>
        [BOOLEAN] [DEFAULT:true]<br>
        Whether plot incidence matrix to file.
    </li>
    <li>clustering_matrix_plot_file<br>
        [STRING] [DEFAULT:matrix.png]<br>
        Figure name of incidence matrix.
    </li>
    <li>clustering_bin_num<br>
        [INTEGER] [DEFAULT:40] [REGION:0-inf]<br>
        Resolution when clustering.
        WARNING: Will cover "clustering_bin_size" setting.
    </li>
    <li>clustering_bin_size<br>
        [INTEGER] [DEFAULT:10000] [REGION:100-inf]<br>
        Resolution when clustering.
        WARNING: Function only when "clustering_bin_num" is set -1.
    </li>
    <li>num_clusters_readin<br>
        [INTEGER] [DEFAULT:inf] [REGION:0-inf]<br>
        Maximum input clusters nubmer.
    </li>
    <li>num_clusters_plot<br>
        [INTEGER] [DEFAULT:1000] [REGION:0-inf]<br>
        Maximum clusters nubmer on output figure.
    </li>
    <li>color_map<br>
        [BOOLEAN] [DEFAULT:False]<br>
        Whether color map need to be used to discriminate the contacts by way-num.
        WARNING: Must be used with setting "clustering=true".
    </li>
    <li>color<br>
        [STRING] [DEFAULT:#17A589]<br>
        Node(fragment) color.
    </li>
    <li>alpha<br>
        [float] [DEFAULT:0.7]<br>
        Node(fragment) color alpha.
    </li>
    <li>color_line<br>
        [STRING] [DEFAULT:#76D7C4]<br>
        Node(fragments) color.
    </li>
    <li>marker_size<br>
        [INTEGER] [DEFAULT:8]<br>
        Node(fragment) size on figure.
    </li>
    <li>line_width<br>
        [INTEGER] [DEFAULT:1]<br>
        Line(between fragments) width on figure.
    </li>
    <li>limit_regions<br>
        [STRING] [DEFAULT:]<br>
        Use several region to filter out noisied fragment to emphasize something. e.g. chr17:60135000-60138000;chr17:60076000-60080000;chr17:60176000-60180000
        WARNING: Function only when "limit_regions_file" param is not set.
    </li>
    <li>limit_regions_file<br>
        [STRING] [DEFAULT:]<br>
        Use several region saved in a .bed to filter out noisied fragment to emphasize something. 
        WARNING: Will cover "limit_regions" setting.
    </li>
    <li>keep_single_fragments<br>
        [BOOLEAN] [DEFAULT:False]<br>
        Whether plot single fragments on the present region?
    </li>
    <li>ld_dimension_reduction<br>
        [BOOLEAN] [DEFAULT:False]<br>
        Whether make dimension reduction for multi-contacts?
    </li>
    <li>ld_dimension_reduction_method<br>
        [STRING] [DEFAULT:umap]<br>
        Choose dimension reduction method from : pca，tsne or umap.
        WARNING: Function only when "ld_dimension_reduction" param is true.
    </li>
    <li>ld_clustering_method<br>
        [STRING] [DEFAULT:kmeans]<br>
        Choose clustering method after dimension reduction : hkmeans or kmeans.
        WARNING: Function only when "ld_dimension_reduction" param is true.
    </li>
    <li>ld_num_clusters<br>
        [INTEGER] [DEFAULT:4]<br>
        Clusters number.
        WARNING: Function only when "ld_dimension_reduction" param is true.
    </li>
    <li>ld_dimension_reduction_plot<br>
        [BOOLEAN] [DEFAULT:true]<br>
        Whether save figure.
        WARNING: Function only when "ld_dimension_reduction" param is true.
    </li>
    <li>ld_dimension_reduction_plot_file_name<br>
        [STRING] [DEFAULT:dims_reduction.png]<br>
        Figure saved name.
        WARNING: Function only when "ld_dimension_reduction" param is true.
    </li>
    <li>plot_line<br>
        [BOOLEAN] [DEFAULT:true]<br>
        Whether plot line.
    </li>
    <li>kde_plot<br>
        [BOOLEAN] [DEFAULT:true]<br>
        Whether plot univariate or bivariate distributions using kernel density estimation.
        A kernel density estimate (KDE) plot is a method for visualizing the distribution of observations in a dataset, analogous to a histogram. KDE represents the data using a continuous probability density curve in one or more dimensions (Please refer to https://seaborn.pydata.org/generated/seaborn.kdeplot.html#seaborn.kdeplot).
    </li>
    <li>kde_plot_only<br>
        [BOOLEAN] [DEFAULT:false]<br>
    </li>
    <li>kde_plot_cmap<br>
        [STRING] [DEFAULT:viridis]<br>
        WARNING: Function only when "ld_dimension_reduction" param is true.
    </li>
    <li>kde_plot_fill<br>
        [BOOLEAN] [DEFAULT:false]<br>
        WARNING: Function only when "ld_dimension_reduction" param is true.
    </li>
    <li>plot_distr<br>
        [BOOLEAN] [DEFAULT:false]<br>
        Plot histgram instead of scatter and kde.
        WARNING: 1. "bin_size" is the width of histgram. 2. "keep_single_fragments" will be set true.
    </li>
    <li>random_seed<br>
        [INTEGER] [DEFAULT:777]<br>
    </li>
</ul>
