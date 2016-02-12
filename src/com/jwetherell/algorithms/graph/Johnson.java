package com.jwetherell.algorithms.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jwetherell.algorithms.data_structures.Graph;
import com.jwetherell.algorithms.data_structures.Graph.Edge;
import com.jwetherell.algorithms.data_structures.Graph.Vertex;

/**
 * Johnson's algorithm is a way to find the shortest paths between all pairs of
 * vertices in a sparse directed graph. It allows some of the edge weights to be
 * negative numbers, but no negative-weight cycles may exist.
 * 
 * Worst case: O(V^2 log V + VE)
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Johnson<T extends Comparable<T>> {

	public Johnson() { }

    public Map<Graph.Vertex<T>, Map<Graph.Vertex<T>, List<Graph.Edge<T>>>> getAllPairsShortestPaths(Graph<T> g) {
        if (g == null)
            throw (new NullPointerException("Graph must be non-NULL."));

        // First, a new node 'connector' is added to the graph, connected by zero-weight edges to each of the other nodes.
        final Graph<T> graph = Graph.copyGraph(g);
        final Graph.Vertex<T> connector = new Graph.Vertex<T>();
		
        graph.addVertex(connector);
        
        // Add the connector Vertex to all edges.
        List<Vertex<T>> allVertices = graph.getAllVertices();
		List<Edge<T>> allEdges = graph.getAllEdges();
		for (Graph.Vertex<T> v : allVertices) {
			if(!connector.equals(v)){
	            final Graph.Edge<T> edge = new Graph.Edge<T>(0, connector, v);
	            connector.addEdge(edge);
	            graph.addEdge(edge);
			}
        }

        // Second, the Bellman–Ford algorithm is used, starting from the new vertex 'connector', to find for each vertex 'v'
        // the minimum weight h(v) of a path from 'connector' to 'v'. If this step detects a negative cycle, the algorithm is terminated.
        final Map<Graph.Vertex<T>, Graph.CostPathPair<T>> costs = new BellmanFord<T>().getShortestPaths(graph, connector);

        // Next the edges of the original graph are re-weighted using the values computed by the Bellman–Ford algorithm: an edge 
        // from u to v, having length w(u,v), is given the new length w(u,v) + h(u) − h(v).
        for (Graph.Edge<T> e : allEdges) {
            final double weight = e.getCost();
            final Graph.Vertex<T> u = e.getFromVertex();
            final Graph.Vertex<T> v = e.getToVertex();

            // Don't worry about the connector
            if (u.equals(connector) || v.equals(connector)) 
                continue;

            // Adjust the costs
            final double uCost = costs.get(u).getCost();
            final double vCost = costs.get(v).getCost();
            final double newWeight = weight + uCost - vCost;
            e.setCost(newWeight);
        }

        // Finally, 'connector' is removed, and Dijkstra's algorithm is used to find the shortest paths from each node (s) to every 
        // other vertex in the re-weighted graph.
        for (Graph.Edge<T> e : connector.getEdges()) {
            graph.removeEdge(e);
        }
        graph.removeVertex(connector);

        final Map<Graph.Vertex<T>, Map<Graph.Vertex<T>, List<Graph.Edge<T>>>> allShortestPaths = new HashMap<Graph.Vertex<T>, Map<Graph.Vertex<T>, List<Graph.Edge<T>>>>();
        for (Graph.Vertex<T> v : allVertices) {
            final Map<Graph.Vertex<T>, Graph.CostPathPair<T>> costPaths = new Dijkstra<T>().getShortestPaths(graph, v);
            final Map<Graph.Vertex<T>, List<Graph.Edge<T>>> paths = new HashMap<Graph.Vertex<T>, List<Graph.Edge<T>>>();
            for (Graph.Vertex<T> v2 : costPaths.keySet()) {
                final Graph.CostPathPair<T> pair = costPaths.get(v2);
                paths.put(v2, pair.getPath());
            }
            allShortestPaths.put(v, paths);
        }
        return allShortestPaths;
    }
}
