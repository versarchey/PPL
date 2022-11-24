package process.contact.mygraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 完整的节点信息： * * （1）顶点 * （2）边 * * 二者的集合。 * * * @author binbin.hou * @since 0.0.2
 */
public class GraphNode<V> {
    /**
     * 顶点信息 * @since 0.0.2
     */
    private V vertex;
    /**
     * 以此顶点为起点的边的集合，是一个列表，列表的每一项是一条边 * * （1）使用集合，避免重复
     */
    private Set<Edge<V>> edgeSet;

    /**
     * 初始化一個節點 * @param vertex 頂點
     */
    public GraphNode(V vertex) {
        this.vertex = vertex;
        this.edgeSet = new HashSet<Edge<V>>();
    }

    /**
     * 新增一条边 * @param edge 边
     */
    public void add(final Edge<V> edge) {
        edgeSet.add(edge);
    }

    /**
     * 获取目标边 * @param to 目标边 * @return 边 * @since 0.0.2
     */
    public Edge<V> get(final V to) {
        for (Edge<V> edge : edgeSet) {
            V dest = edge.getTo();
            if (dest.equals(to)) {
                return edge;
            }
        }
        return null;
    }

    /**
     * 获取目标边 * @param to 目标边 * @return 边 * @since 0.0.2
     */
    public Edge<V> remove(final V to) {
        Iterator<Edge<V>> edgeIterable = edgeSet.iterator();
        while (edgeIterable.hasNext()) {
            Edge<V> next = edgeIterable.next();
            if (to.equals(next.getTo())) {
                edgeIterable.remove();
                return next;
            }
        }
        return null;
    }

    public V getVertex() {
        return vertex;
    }

    public Set<Edge<V>> getEdgeSet() {
        return edgeSet;
    }

    @Override
    public String toString() {
        return "GraphNode{" + "vertex=" + vertex + ", edgeSet=" + edgeSet + '}';
    }
}