package com.jwetherell.algorithms.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import com.jwetherell.algorithms.data_structures.Graph;

/**
 * Prim's minimum spanning tree. Only works on undirected graphs. It finds a
 * subset of the edges that forms a tree that includes every vertex, where the
 * total weight of all the edges in the tree is minimized.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Prim<T extends Comparable<T>> {

	public Prim() { }

    public Graph.CostPathPair<T> getMinimumSpanningTree(Graph<T> graph, Graph.Vertex<T> start) {
        if (graph == null)
            throw (new NullPointerException("Graph must be non-NULL."));

        // Prim's algorithm only works on undirected graphs
        if (graph.getType() == Graph.TYPE.DIRECTED) 
            throw (new IllegalArgumentException("Undirected graphs only."));

        int cost = 0;

        final Set<Graph.Vertex<T>> unvisited = new HashSet<Graph.Vertex<T>>();
        unvisited.addAll(graph.getAllVertices());
        unvisited.remove(start); // O(1)

        final List<Graph.Edge<T>> path = new ArrayList<Graph.Edge<T>>();
        final Queue<Graph.Edge<T>> edgesAvailable = new PriorityQueue<Graph.Edge<T>>();

        Graph.Vertex<T> vertex = start;
        while (!unvisited.isEmpty()) {
            // Add all edges to unvisited vertices
            for (Graph.Edge<T> e : vertex.getEdges()) {
                if (unvisited.contains(e.getToVertex()))
                    edgesAvailable.add(e);
            }

            // Remove the lowest cost edge
            final Graph.Edge<T> e = edgesAvailable.remove();
            cost += e.getCost();
            path.add(e); // O(1)

            vertex = e.getToVertex();
            unvisited.remove(vertex); // O(1)
        }

        return (new Graph.CostPathPair<T>(cost, path));
    }
}
