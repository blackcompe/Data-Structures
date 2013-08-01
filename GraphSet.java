import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * A generic directed graph that doesn't allow duplicate elements and performs
 * topological sorting to detect cycles. All primitive operations are done in
 * linear time. Sorting is done in polynomial time at most.
 * 
 * @author Ryan Beckett
 */
public class GraphSet<T> {

    private HashSet<Vertex<T>> graph = new HashSet<Vertex<T>>();

    /**
     * Add a new element to the graph.
     * 
     * @param elem
     *            The element to be added to the graph.
     * @return <code>true</code> if the graph was changed as a result of this
     *         operation, otherwise <code>false</code>.
     */
    public boolean add(T elem) {
        if (get(elem) != null)
            return false;
        return graph.add(new Vertex<T>(elem));
    }

    /**
     * Create an edge between two elements. <code>src</code> and
     * <code>dest<code> must exist.
     * 
     * @param src
     *            The source vertex.
     * @param dest
     *            The destination vertex.
     * @return <code>true</code> if the graph was changed as a result of this
     *         operation, otherwise <code>false</code>.
     */
    public boolean newEdge(T src, T dest) {
        Vertex<T> srcVertex = get(src);
        Vertex<T> destVertex = get(dest);
        if (srcVertex == null || destVertex == null)
            return false;
        for (Vertex<T> vertex : graph)
            if (vertex.elem.equals(src))
                return vertex.newNeighbor(destVertex);
        return false;
    }

    /**
     * Remove an edge between two elements. <code>src</code> and
     * <code>dest<code> must exist.
     * 
     * @param src
     *            The source vertex.
     * @param dest
     *            The destination vertex.
     * @return <code>true</code> if the graph was changed as a result of this
     *         operation, otherwise <code>false</code>.
     */
    public boolean removeEdge(T src, T dest) {
        Vertex<T> srcVertex = get(src);
        Vertex<T> destVertex = get(dest);
        if (srcVertex == null || destVertex == null)
            return false;
        for (Vertex<T> vertex : graph)
            if (vertex.elem.equals(src))
                return vertex.removeNeighbor(destVertex);
        return false;
    }

    private Vertex<T> get(T elem) {
        for (Vertex<T> vertex : graph)
            if (vertex.elem.equals(elem))
                return vertex;
        return null;
    }

    /**
     * Remove the specified element from the graph.
     * 
     * @param elem
     *            The element to be removed from the graph.
     * @return <code>true</code> if the graph was changed as a result of this
     *         operation, otherwise <code>false</code>.
     */
    public boolean remove(T elem) {
        Vertex<T> removeVertex = get(elem);
        if (removeVertex == null)
            return false;
        for (Vertex<T> vertex : graph)
            vertex.removeNeighbor(removeVertex);
        return graph.remove(removeVertex);
    }

    /**
     * Return a string representation of the graph.
     * 
     * @return A string representation of the graph.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Vertex<T> vertex : graph) {
            sb.append(vertex.elem + "->" + vertex.neighbors.toString() + "\n");
        }
        return sb.toString();
    };

    /**
     * Performs a topological sort on the graph.
     * 
     * @return A list of elements in topological order or <code>null</code> if
     *         the graph has a cycle.
     */
    @SuppressWarnings("unchecked")
    public List<T> sort() {
        List<Vertex<T>> sortedVertices = new ArrayList<Vertex<T>>();
        Set<Vertex<T>> noSrcVertices = findVerticesWithNoSrcEdges();
        while (noSrcVertices.size() > 0) {
            Vertex<T> vertex = (Vertex<T>) noSrcVertices.toArray()[0];
            noSrcVertices.remove(vertex);
            sortedVertices.add(vertex);
            Object[] neighbors = vertex.neighbors.toArray();
            for (int i = 0; i < neighbors.length; i++) {
                Vertex<T> neighbor = (Vertex<T>) neighbors[i];
                removeEdge(vertex.elem, neighbor.elem);
                if (!hasSrcEdges(neighbor))
                    noSrcVertices.add(neighbor);
            }
        }
        if (graphHasEdges())
            return null;
        else {
            ArrayList<T> sortedElems = new ArrayList<T>();
            for (Vertex<T> vertex : sortedVertices)
                sortedElems.add(vertex.elem);
            return sortedElems;
        }

    }

    /**
     * Check whether the graph has edges.
     */
    private boolean graphHasEdges() {
        for (Vertex<T> vertex : graph)
            if (vertex.neighbors.size() > 0)
                return true;
        return false;
    }

    /**
     * Build a list of vertices without incoming edges.
     */
    private Set<Vertex<T>> findVerticesWithNoSrcEdges() {
        LinkedHashSet<Vertex<T>> noSrcElems = new LinkedHashSet<Vertex<T>>();
        for (Vertex<T> vertex : graph)
            if (!hasSrcEdges(vertex))
                noSrcElems.add(vertex);
        return noSrcElems;
    }

    /**
     * Check whether the vertex has an incoming edge.
     */
    private boolean hasSrcEdges(Vertex<T> vertex) {
        for (Vertex<T> currVertex : graph)
            if (currVertex.isNeighbor(vertex))
                return true;
        return false;
    }

    private static class Vertex<E> {

        E elem;
        HashSet<Vertex<E>> neighbors = new HashSet<Vertex<E>>();

        Vertex(E elem) {
            this.elem = elem;
        }

        boolean newNeighbor(Vertex<E> vertex) {
            return neighbors.add(vertex);
        }

        boolean isNeighbor(Vertex<E> vertex) {
            return neighbors.contains(vertex);
        }

        boolean removeNeighbor(Vertex<E> vertex) {
            return neighbors.remove(vertex);
        }

        @SuppressWarnings("unchecked")
        public boolean equals(Object o) {
            Vertex<E> other = (Vertex<E>) o;
            return elem.equals(other.elem);
        }

        public String toString() {
            return elem.toString();
        }
    }

    public static void main(String[] args) {

        GraphSet<Integer> graph = new GraphSet<Integer>();
        graph.add(1);
        graph.add(2);
        graph.add(3);
        graph.add(4);
        graph.add(5);
        graph.add(6);

        graph.newEdge(1, 2);
        graph.newEdge(1, 4);
        graph.newEdge(4, 2);
        graph.newEdge(5, 4);
        graph.newEdge(3, 5);
        graph.newEdge(3, 6);

        System.out.println(graph);

        // create cycle and fail sort
        // graph.newEdge(6, 6);

        System.out.println(graph.sort());
    }
}
