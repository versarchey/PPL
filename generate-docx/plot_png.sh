

prefix=$1
result_dir=$2
virtual_fragments=$3

mkdir -p figs

python funcs/plot_mapq.py -c $result_dir/$prefix/$prefix.contacts -o figs/$prefix.mpq.png
python funcs/plot_frags_reads_len.py -c $result_dir/$prefix/$prefix.contacts -o1 figs/$prefix.r1.png -o2 figs/$prefix.r2.png
python funcs/plot_frags_len.py -c $result_dir/$prefix/$prefix.contacts -o1 figs/$prefix.f1.png -o2 figs/$prefix.f2.png -f $virtual_fragments
python funcs/plot_dims.py -d $result_dir/$prefix/$prefix.dd -o1 figs/$prefix.d1.png -o2 figs/$prefix.d2.png
python funcs/plot_boundaries_dist.py -bc $result_dir/$prefix/$prefix.bc -bcc $result_dir/$prefix/$prefix.bc.evenByChr -o1 figs/$prefix.bc1.png -o2 figs/$prefix.bc2.png
python funcs/plot_penalty.py -p $result_dir/$prefix/$prefix.penalty.distribution -o1 figs/$prefix.p1.png -o2 figs/$prefix.p2.png
python funcs/plot_VPCs_dist.py -iis $result_dir/$prefix/$prefix.ii -o1 figs/$prefix.ii1.png -o2 figs/$prefix.ii2.png
