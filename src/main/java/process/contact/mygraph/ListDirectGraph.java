package process.contact.mygraph;


import java.io.Console;
import java.util.*;

/**
 * 链表实现的有向图
 *
 * 邻接链表（Adjacency List）实现的有向图
 *
 * @author binbin.hou
 * @since 0.0.2
 */
public class ListDirectGraph<V> implements IDirectGraph<V> {

    /**
     * 节点链表
     * @since 0.0.2
     */
    public List<GraphNode<V>> nodeList;
    public List<Edge<V>> edgeList;

    /**
     * 初始化有向图
     * @since 0.0.2
     */
    public ListDirectGraph() {
        this.nodeList = new ArrayList<GraphNode<V>>();
        this.edgeList = new ArrayList<Edge<V>>();
    }

    @Override
    public void addVertex(V v) {
        GraphNode<V> node = new GraphNode<>(v);

        // 直接加入到集合中
        this.nodeList.add(node);
    }

    @Override
    public V getVertex(int index) {
        return nodeList.get(index).getVertex();
    }

    @Override
    public void addEdge(Edge<V> edge) {
        //1. 新增一条边，直接遍历列表。
        // 如果存在这条的起始节点，则将这条边加入。
        // 如果不存在，则直接报错即可。
        edgeList.add(edge);
        for(GraphNode<V> graphNode : nodeList) {
            V from = edge.getFrom();
            V vertex = graphNode.getVertex();

            // 起始节点在开头
            if(from.equals(vertex)) {
                graphNode.getEdgeSet().add(edge);
            }
        }
    }

    @Override
    public Edge<V> getEdge(int from, int to) {
        // 获取开始和结束的顶点
        V toVertex = getVertex(from);

        // 获取节点
        GraphNode<V> fromNode = nodeList.get(from);
        // 获取对应结束顶点的边
        return fromNode.get(toVertex);
    }

    /**
     * 获取图节点
     * @param edge 边
     * @return 图节点
     */
    private GraphNode<V> getGraphNode(final Edge<V> edge) {
        for(GraphNode<V> node : nodeList) {
            final V from = edge.getFrom();

            if(node.getVertex().equals(from)) {
                return node;
            }
        }

        return null;
    }

    /**
     * 获取对应的图节点
     * @param vertex 顶点
     * @return  图节点
     * @since 0.0.2
     */
    private GraphNode<V> getGraphNode(final V vertex) {
        for(GraphNode<V> node : nodeList) {
            if(vertex.equals(node.getVertex())) {
                return node;
            }
        }
        return null;
    }

    public List<V> BellmanFord(V source, V destination)
    {
        int VertexCount = this.nodeList.size();
        // distSet[i] will hold the shortest distance from source to i
//        int[] distSet = new int[VertexCount];
        Map<V,Double> distSet = new HashMap<V,Double>();
        Map<V,List<V>> paths = new HashMap<V,List<V>>();

        // Step 1: Initialize distances from source to all other vertices as INFINITE
        for (GraphNode<V> node :
                this.nodeList) {
            V key = node.getVertex();
            distSet.put(key, Double.MAX_VALUE);
            List<V> path = new ArrayList<V>();
//            path.add(source);
            paths.put(key, path);
        }
        distSet.put(source, 0.0);
        paths.get(source).add(source);

        // Step 2: Relax all edges |V| - 1 times. A simple shortest path from source
        // to any other vertex can have at-most |V| - 1 edges
        for (int i = 1; i <= VertexCount - 1; i++)
        {
            for(Edge<V> edge : edgeList)
            {
                V u = edge.getFrom();
                V v = edge.getTo();
                double weight = edge.getWeight();

                if (distSet.get(u) != Double.MAX_VALUE && distSet.get(u) + weight < distSet.get(v)){
                    distSet.put(v, distSet.get(u) + weight);
                    paths.get(v).clear();
                    paths.get(v).addAll(paths.get(u));
                    paths.get(v).add(v);
                }
            }
        }

        // Step 3: check for negative-weight cycles.  The above step guarantees
        // shortest distances if graph doesn't contain negative weight cycle.
        // If we get a shorter path, then there is a cycle.
//        for(Edge<V> edge : edgeList){
//            V u = edge.getFrom();
//            V v = edge.getTo();
//            double weight = edge.getWeight();
//        }
//        foreach (var edge in _adjacentEdges.Values.SelectMany(e => e))
//        {
//            int u = edge.Begin;
//            int v = edge.End;
//            int weight = edge.Weight;
//
//            if (distSet[u] != int.MaxValue
//                    && distSet[u] + weight < distSet[v])
//            {
//                Console.WriteLine("Graph contains negative weight cycle.");
//            }
//        }
        return paths.get(destination);
    }

}