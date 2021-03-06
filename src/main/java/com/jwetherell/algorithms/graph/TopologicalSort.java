package com.jwetherell.algorithms.graph;

import java.util.ArrayList;
import java.util.List;

import com.jwetherell.algorithms.data_structures.Graph;

/**
 * In computer science, a topological sort (sometimes abbreviated topsort or
 * toposort) or topological ordering of a directed graph is a linear ordering of
 * its vertices such that, for every edge uv, u comes before v in the ordering.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class TopologicalSort<T extends Comparable<T>> {

	public TopologicalSort() { }

    /**
     * Performs a topological sort on a directed graph. Returns NULL if a cycle is detected.
     * 
     * Note: This should NOT change the state of the graph parameter.
     * 
     * @param graph
     * @return Sorted List of Vertices or NULL if graph has a cycle
     */
    public final List<Graph.Vertex<T>> sort(Graph<T> graph) {
        if (graph == null)
            throw new IllegalArgumentException("Graph is NULL.");

        if (graph.getType() != Graph.TYPE.DIRECTED)
            throw new IllegalArgumentException("Cannot perform a topological sort on a non-directed graph. graph type = "+graph.getType());

        // clone to prevent changes the graph parameter's state
        final Graph<T> clone = /*Graph.copyGraph*/(graph);
        final List<Graph.Vertex<T>> sorted = new ArrayList<Graph.Vertex<T>>();
        final List<Graph.Vertex<T>> noOutgoing = new ArrayList<Graph.Vertex<T>>();

        final List<Graph.Edge<T>> edges = new ArrayList<Graph.Edge<T>>();
        edges.addAll(clone.getAllEdges());

        // Find all the vertices which have no outgoing edges
        for (Graph.Vertex<T> v : clone.getAllVertices()) {
            if (v.getEdges().size() == 0)
                noOutgoing.add(v);
        }

        // While we still have vertices which have no outgoing edges 
        while (noOutgoing.size() > 0) {
            final Graph.Vertex<T> current = noOutgoing.remove(0);
            sorted.add(current);

            // Go thru each edge, if it goes to the current vertex then remove it.
            int i = 0;
            while (i < edges.size()) {
                final Graph.Edge<T> e = edges.get(i);
                final Graph.Vertex<T> from = e.getFromVertex();
                final Graph.Vertex<T> to = e.getToVertex();
                // Found an edge to the current vertex, remove it.
                if (to.equals(current)) {
                    edges.remove(e);
                    // Remove the reciprocal edge
                    from.getEdges().remove(e);
                } else {
                    i++;
                }
                // Removed all edges from 'from' vertex, add it to the onOutgoing list
                if (from.getEdges().size() == 0)
                    noOutgoing.add(from);
            }
        }
        // If we have processed all connected vertices and there are edges remaining, graph has multiple connected components.
        if (edges.size() > 0)
            return null;
        return sorted;
    }
}
