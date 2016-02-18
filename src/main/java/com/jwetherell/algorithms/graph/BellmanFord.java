package com.jwetherell.algorithms.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jwetherell.algorithms.data_structures.Graph;
import com.jwetherell.algorithms.data_structures.Graph.Vertex;

/**
 * Bellman-Ford's shortest path. Works on both negative and positive weighted
 * edges. Also detects negative weight cycles. Returns a tuple of total cost of
 * shortest path and the path.
 * 
 * Worst case: O(|V| |E|)
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class BellmanFord<T extends Comparable<T>> {

    public BellmanFord() { }

    /**
     * Get shortest path for all vertices
     */
    public Map<Graph.Vertex<T>, Graph.CostPathPair<T>> getShortestPaths(Graph<T> graph, Graph.Vertex<T> start) {
        final Map<Graph.Vertex<T>, List<Graph.Edge<T>>> paths = new HashMap<Graph.Vertex<T>, List<Graph.Edge<T>>>();
        final Map<Graph.Vertex<T>, Graph.CostVertexPair<T>> costs = new HashMap<Graph.Vertex<T>, Graph.CostVertexPair<T>>();

        getShortestPath(graph, start, paths, costs);

        final Map<Graph.Vertex<T>, Graph.CostPathPair<T>> map = new HashMap<Graph.Vertex<T>, Graph.CostPathPair<T>>();
        for (Graph.CostVertexPair<T> pair : costs.values()) {
            final double cost = pair.getCost();
            final Graph.Vertex<T> vertex = pair.getVertex();
            final List<Graph.Edge<T>> path = paths.get(vertex);
            map.put(vertex, new Graph.CostPathPair<T>(cost, path));
        }
        return map;
    }

    /**
     * Get shortest path to from 'start' to 'end' vertices
     */
    public Graph.CostPathPair<T> getShortestPath(Graph<T> graph, Graph.Vertex<T> start, Graph.Vertex<T> end) {
        if (graph == null)
            throw (new NullPointerException("Graph must be non-NULL."));

        final Map<Graph.Vertex<T>, List<Graph.Edge<T>>> paths = new HashMap<Graph.Vertex<T>, List<Graph.Edge<T>>>();
        final Map<Graph.Vertex<T>, Graph.CostVertexPair<T>> costs = new HashMap<Graph.Vertex<T>, Graph.CostVertexPair<T>>();
        return getShortestPath(graph, start, end, paths, costs);
    }

    private Graph.CostPathPair<T> getShortestPath(Graph<T> graph, 
                                                               Graph.Vertex<T> start, Graph.Vertex<T> end,
                                                               Map<Graph.Vertex<T>, List<Graph.Edge<T>>> paths,
                                                               Map<Graph.Vertex<T>, Graph.CostVertexPair<T>> costs) {
        if (end == null)
            throw (new NullPointerException("end must be non-NULL."));

        getShortestPath(graph, start, paths, costs);

        final Graph.CostVertexPair<T> pair = costs.get(end);
        final List<Graph.Edge<T>> list = paths.get(end);
        return (new Graph.CostPathPair<T>(pair.getCost(), list));
    }

    private void getShortestPath(Graph<T> graph, 
                                        Graph.Vertex<T> start,
                                        Map<Graph.Vertex<T>, List<Graph.Edge<T>>> paths,
                                        Map<Graph.Vertex<T>, Graph.CostVertexPair<T>> costs) {
        if (graph == null)
            throw (new NullPointerException("Graph must be non-NULL."));
        if (start == null)
            throw (new NullPointerException("start must be non-NULL."));

        List<Vertex<T>> allVertices = graph.getAllVertices();
		for (Graph.Vertex<T> v : allVertices)
            paths.put(v, new ArrayList<Graph.Edge<T>>());

        // All vertices are INFINITY unless it's the start vertices
        for (Graph.Vertex<T> v : allVertices)
            if (v.equals(start))
                costs.put(v, new Graph.CostVertexPair<T>(0, v));
            else
                costs.put(v, new Graph.CostVertexPair<T>(Integer.MAX_VALUE, v));

        boolean negativeCycleCheck = false;
        for (int i = 0; i < allVertices.size(); i++) {
            // If it's the last vertices, perform a negative weight cycle check.
            // The graph should be finished by the size()-1 time through this loop.
            if (i == (allVertices.size() - 1))
                negativeCycleCheck = true;

            // Compute costs to all vertices
            for (Graph.Edge<T> e : graph.getAllEdges()) {
                final Graph.CostVertexPair<T> pair = costs.get(e.getToVertex());
                final Graph.CostVertexPair<T> lowestCostToThisVertex = costs.get(e.getFromVertex());

                // If the cost of the from vertex is MAX_VALUE then treat as INIFINITY.
                if (lowestCostToThisVertex.getCost() == Integer.MAX_VALUE)
                    continue;

                final double cost = lowestCostToThisVertex.getCost() + e.getCost();
                if (cost < pair.getCost()) {
                    // Found a shorter path to a reachable vertex
                    pair.setCost(cost);

                    if (negativeCycleCheck) {
                        // Uhh ohh... negative weight cycle
                        throw new IllegalArgumentException("Graph contains a negative weight cycle.");
                    }

                    final List<Graph.Edge<T>> list = paths.get(e.getToVertex());
                    list.clear();
                    list.addAll(paths.get(e.getFromVertex()));
                    list.add(e);
                }
            }
        }
    }
}
