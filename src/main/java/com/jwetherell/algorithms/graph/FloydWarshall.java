package com.jwetherell.algorithms.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jwetherell.algorithms.data_structures.Graph;

/**
 * Floyd–Warshall algorithm is a graph analysis algorithm for finding shortest
 * paths in a weighted graph (with positive or negative edge weights).
 * 
 * Worst case: O(V^3)
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class FloydWarshall<T extends Comparable<T>> {

	public FloydWarshall() { }

    public Map<Graph.Vertex<T>, Map<Graph.Vertex<T>, Double>> getAllPairsShortestPaths(Graph<T> graph) {
        if (graph == null)
            throw (new NullPointerException("Graph must be non-NULL."));

        final List<Graph.Vertex<T>> vertices = graph.getAllVertices();

        final double[][] sums = new double[vertices.size()][vertices.size()];
        for (int i = 0; i < sums.length; i++) {
            for (int j = 0; j < sums[i].length; j++) {
                sums[i][j] = Integer.MAX_VALUE;
            }
        }

        final List<Graph.Edge<T>> edges = graph.getAllEdges();
        for (Graph.Edge<T> e : edges) {
            final int indexOfFrom = vertices.indexOf(e.getFromVertex());
            final int indexOfTo = vertices.indexOf(e.getToVertex());
            sums[indexOfFrom][indexOfTo] = e.getCost();
        }

        for (int k = 0; k < vertices.size(); k++) {
            for (int i = 0; i < vertices.size(); i++) {
                for (int j = 0; j < vertices.size(); j++) {
                    if (i == j) {
                        sums[i][j] = 0;
                    } else {
                        final double ijCost = sums[i][j];
                        final double ikCost = sums[i][k];
                        final double kjCost = sums[k][j];
                        final double summed = (ikCost != Integer.MAX_VALUE && 
                                            kjCost != Integer.MAX_VALUE) ? 
                                                   (ikCost + kjCost)
                                               : 
                                                   Integer.MAX_VALUE;
                        if (ijCost > summed)
                            sums[i][j] = summed;
                    }
                }
            }
        }

        final Map<Graph.Vertex<T>, Map<Graph.Vertex<T>, Double>> allShortestPaths = new HashMap<Graph.Vertex<T>, Map<Graph.Vertex<T>, Double>>();
        for (int i = 0; i < sums.length; i++) {
            for (int j = 0; j < sums[i].length; j++) {
                final Graph.Vertex<T> from = vertices.get(i);
                final Graph.Vertex<T> to = vertices.get(j);

                Map<Graph.Vertex<T>, Double> map = allShortestPaths.get(from);
                if (map == null)
                    map = new HashMap<Graph.Vertex<T>, Double>();

                final double cost = sums[i][j];
                if (cost != Integer.MAX_VALUE)
                    map.put(to, cost);
                allShortestPaths.put(from, map);
            }
        }
        return allShortestPaths;
    }
}
