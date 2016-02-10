package com.jwetherell.algorithms.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import com.jwetherell.algorithms.data_structures.Graph;

/**
 * Dijkstra's shortest path. Only works on non-negative path weights. Returns a
 * tuple of total cost of shortest path and the path.
 * 
 * Worst case: O(|E| + |V| log |V|)
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Dijkstra<T extends Comparable<T>> {

	public Dijkstra() { }

    public Map<Graph.Vertex<T>, Graph.CostPathPair<T>> getShortestPaths(Graph<T> graph, Graph.Vertex<T> start) {
        final Map<Graph.Vertex<T>, List<Graph.Edge<T>>> paths = new HashMap<Graph.Vertex<T>, List<Graph.Edge<T>>>();
        final Map<Graph.Vertex<T>, Graph.CostVertexPair<T>> costs = new HashMap<Graph.Vertex<T>, Graph.CostVertexPair<T>>();

        getShortestPath(graph, start, null, paths, costs);

        final Map<Graph.Vertex<T>, Graph.CostPathPair<T>> map = new HashMap<Graph.Vertex<T>, Graph.CostPathPair<T>>();
        for (Graph.CostVertexPair<T> pair : costs.values()) {
        	double cost = pair.getCost();
            Graph.Vertex<T> vertex = pair.getVertex();
            List<Graph.Edge<T>> path = paths.get(vertex);
            map.put(vertex, new Graph.CostPathPair<T>(cost, path));
        }
        return map;
    }

    public Graph.CostPathPair<T> getShortestPath(Graph<T> graph, Graph.Vertex<T> start, Graph.Vertex<T> end) {
        if (graph == null)
            throw (new NullPointerException("Graph must be non-NULL."));

        // Dijkstra's algorithm only works on positive cost graphs
        final boolean hasNegativeEdge = checkForNegativeEdges(graph.getVertices());
        if (hasNegativeEdge)
            throw (new IllegalArgumentException("Negative cost Edges are not allowed."));

        final Map<Graph.Vertex<T>, List<Graph.Edge<T>>> paths = new HashMap<Graph.Vertex<T>, List<Graph.Edge<T>>>();
        final Map<Graph.Vertex<T>, Graph.CostVertexPair<T>> costs = new HashMap<Graph.Vertex<T>, Graph.CostVertexPair<T>>();
        return getShortestPath(graph, start, end, paths, costs);
    }

    private Graph.CostPathPair<T> getShortestPath(Graph<T> graph, 
                                                              Graph.Vertex<T> start, Graph.Vertex<T> end,
                                                              Map<Graph.Vertex<T>, List<Graph.Edge<T>>> paths,
                                                              Map<Graph.Vertex<T>, Graph.CostVertexPair<T>> costs) {
        if (graph == null)
            throw (new NullPointerException("Graph must be non-NULL."));
        if (start == null)
            throw (new NullPointerException("start must be non-NULL."));

        // Dijkstra's algorithm only works on positive cost graphs
        boolean hasNegativeEdge = checkForNegativeEdges(graph.getVertices());
        if (hasNegativeEdge)
            throw (new IllegalArgumentException("Negative cost Edges are not allowed."));

        for (Graph.Vertex<T> v : graph.getVertices())
            paths.put(v, new ArrayList<Graph.Edge<T>>());

        for (Graph.Vertex<T> v : graph.getVertices()) {
            if (v.equals(start))
                costs.put(v, new Graph.CostVertexPair<T>(0, v));
            else
                costs.put(v, new Graph.CostVertexPair<T>(Integer.MAX_VALUE, v));
        }

        final Queue<Graph.CostVertexPair<T>> unvisited = new PriorityQueue<Graph.CostVertexPair<T>>();
        unvisited.add(costs.get(start));

        while (!unvisited.isEmpty()) {
            final Graph.CostVertexPair<T> pair = unvisited.remove();
            final Graph.Vertex<T> vertex = pair.getVertex();

            // Compute costs from current vertex to all reachable vertices which haven't been visited
            for (Graph.Edge<T> e : vertex.getEdges()) {
                final Graph.CostVertexPair<T> toPair = costs.get(e.getToVertex()); // O(1)
                final Graph.CostVertexPair<T> lowestCostToThisVertex = costs.get(vertex); // O(1)
                final double cost = lowestCostToThisVertex.getCost() + e.getCost();
                if (toPair.getCost() == Integer.MAX_VALUE) {
                    // Haven't seen this vertex yet

                    // Need to remove the pair and re-insert, so the priority queue keeps it's invariants
                    unvisited.remove(toPair); // O(n)
                    toPair.setCost(cost);
                    unvisited.add(toPair); // O(log n)

                    // Update the paths
                    List<Graph.Edge<T>> set = paths.get(e.getToVertex()); // O(log n)
                    set.addAll(paths.get(e.getFromVertex())); // O(log n)
                    set.add(e);
                } else if (cost < toPair.getCost()) {
                    // Found a shorter path to a reachable vertex

                    // Need to remove the pair and re-insert, so the priority queue keeps it's invariants
                    unvisited.remove(toPair); // O(n)
                    toPair.setCost(cost);
                    unvisited.add(toPair); // O(log n)

                    // Update the paths
                    List<Graph.Edge<T>> set = paths.get(e.getToVertex()); // O(log n)
                    set.clear();
                    set.addAll(paths.get(e.getFromVertex())); // O(log n)
                    set.add(e);
                }
            }

            // Termination conditions
            if (end != null && vertex.equals(end)) {
                // We are looking for shortest path to a specific vertex, we found it.
                break;
            }
        }

        if (end != null) {
            final Graph.CostVertexPair<T> pair = costs.get(end);
            final List<Graph.Edge<T>> set = paths.get(end);
            return (new Graph.CostPathPair<T>(pair.getCost(), set));
        }
        return null;
    }

    private boolean checkForNegativeEdges(Collection<Graph.Vertex<T>> vertitices) {
        for (Graph.Vertex<T> v : vertitices) {
            for (Graph.Edge<T> e : v.getEdges()) {
                if (e.getCost() < 0)
                    return true;
            }
        }
        return false;
    }
}
