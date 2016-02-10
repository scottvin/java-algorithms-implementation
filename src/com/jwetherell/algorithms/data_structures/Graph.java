package com.jwetherell.algorithms.data_structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.gephi.graph.impl.EdgeImpl;
import org.gephi.graph.impl.GraphStore;
import org.gephi.graph.impl.NodeImpl;

/**
 * Graph. Could be directed or undirected depending on the TYPE enum. A graph is
 * an abstract representation of a set of objects where some pairs of the
 * objects are connected by links.
 * 
 * http://en.wikipedia.org/wiki/Graph_(mathematics)
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
@SuppressWarnings("unchecked")
public class Graph<T extends Comparable<T>> {

    private List<Vertex<T>> allVertices = new ArrayList<Vertex<T>>();
    private List<Edge<T>> allEdges = new ArrayList<Edge<T>>();

    public enum TYPE {
        DIRECTED, UNDIRECTED
    }

    /** Defaulted to undirected */
    private TYPE type = TYPE.UNDIRECTED;

    public Graph() { }

    public Graph(TYPE type) {
        this.type = type;
    }

    /** Deep copies **/
    public Graph(Graph<T> g) {
        type = g.getType();

        // Copy the vertices which also copies the edges
        for (Vertex<T> v : g.getVertices())
            this.allVertices.add(new Vertex<T>(v));

        for (Vertex<T> v : this.getVertices()) {
            for (Edge<T> e : v.getEdges()) {
                this.allEdges.add(e);
            }
        }
    }

    /**
     * Creates a Graph from the vertices and edges. This defaults to an undirected Graph
     * 
     * NOTE: Duplicate vertices and edges ARE allowed.
     * NOTE: Copies the vertex and edge objects but does NOT store the Collection parameters itself.
     * 
     * @param vertices Collection of vertices
     * @param edges Collection of edges
     */
    public Graph(Collection<Vertex<T>> vertices, Collection<Edge<T>> edges) {
        this(TYPE.UNDIRECTED, vertices, edges);
    }

    /**
     * Creates a Graph from the vertices and edges.
     * 
     * NOTE: Duplicate vertices and edges ARE allowed.
     * NOTE: Copies the vertex and edge objects but does NOT store the Collection parameters itself.
     * 
     * @param vertices Collection of vertices
     * @param edges Collection of edges
     */
    public Graph(TYPE type, Collection<Vertex<T>> vertices, Collection<Edge<T>> edges) {
        this(type);

        this.allVertices.addAll(vertices);
        this.allEdges.addAll(edges);

        for (Edge<T> e : edges) {
            final Vertex<T> from = e.getFromVertex();
            final Vertex<T> to = e.getToVertex();

            if (!this.allVertices.contains(from) || !this.allVertices.contains(to))
                continue;

            from.addEdge(e);
            if (this.type == TYPE.UNDIRECTED) {
                Edge<T> reciprical = new Edge<T>(e.getCost(), to, from);
                to.addEdge(reciprical);
                this.allEdges.add(reciprical);
            }
        }
    }

    public TYPE getType() {
        return type;
    }

    public List<Vertex<T>> getVertices() {
        return allVertices;
    }

    public List<Edge<T>> getEdges() {
        return allEdges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int code = this.type.hashCode() + this.allVertices.size() + this.allEdges.size();
        for (Vertex<T> v : allVertices)
            code *= v.hashCode();
        for (Edge<T> e : allEdges)
            code *= e.hashCode();
        return 31 * code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object g1) {
        if (!(g1 instanceof Graph))
            return false;

        final Graph<T> g = (Graph<T>) g1;

        final boolean typeEquals = this.type == g.type;
        if (!typeEquals)
            return false;

        final boolean verticesSizeEquals = this.allVertices.size() == g.allVertices.size();
        if (!verticesSizeEquals)
            return false;

        final boolean edgesSizeEquals = this.allEdges.size() == g.allEdges.size();
        if (!edgesSizeEquals)
            return false;

        // Vertices can contain duplicates and appear in different order but both arrays should contain the same elements
        final Object[] ov1 = this.allVertices.toArray();
        Arrays.sort(ov1);
        final Object[] ov2 = g.allVertices.toArray();
        Arrays.sort(ov2);
        for (int i=0; i<ov1.length; i++) {
            final Vertex<T> v1 = (Vertex<T>) ov1[i];
            final Vertex<T> v2 = (Vertex<T>) ov2[i];
            if (!v1.equals(v2))
                return false;
        }

        // Edges can contain duplicates and appear in different order but both arrays should contain the same elements
        final Object[] oe1 = this.allEdges.toArray();
        Arrays.sort(oe1);
        final Object[] oe2 = g.allEdges.toArray();
        Arrays.sort(oe2);
        for (int i=0; i<oe1.length; i++) {
            final Edge<T> e1 = (Edge<T>) oe1[i];
            final Edge<T> e2 = (Edge<T>) oe2[i];
            if (!e1.equals(e2))
                return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (Vertex<T> v : allVertices)
            builder.append(v.toString());
        return builder.toString();
    }

    public static class Vertex<T extends Comparable<T>> extends NodeImpl implements Comparable<Vertex<T>> {

        private double weight = 0;
        private List<Edge<T>> edges = new ArrayList<Edge<T>>();

        public Vertex() {
        	this((T)null);
        }
        public Vertex(T value) {
        	this(null, value, 0);
        }

        public Vertex(T value, double weight) {
        	this(null, value, weight);
        }
        public Vertex(GraphStore graphStore, T value, double weight) {
        	super(value == null ? Integer.MAX_VALUE : value, graphStore);
            this.weight = weight;
        }

        /** Deep copies the edges along with the value and weight **/
        public Vertex(Vertex<T> vertex) {
            this(vertex.getGraphStore(), vertex.getValue(), vertex.weight);

            this.edges.addAll(vertex.edges);
        }

        public T getValue() {
            return (T) getId();
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public void addEdge(Edge<T> e) {
            edges.add(e);
        }

        public List<Edge<T>> getEdges() {
            return edges;
        }

        public Edge<T> getEdge(Vertex<T> v) {
            for (Edge<T> e : this.getEdges()) {
                if (e.getToVertex().equals(v))
                    return e;
            }
            return null;
        }

        public boolean pathTo(Vertex<T> v) {
            for (Edge<T> e : this.getEdges()) {
                if (e.getToVertex().equals(v))
                    return true;
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
        	T value = this.getValue();
			return new HashCodeBuilder()
        			.append(value == null ? Integer.MAX_VALUE : value)
        			.append(this.getWeight())
        			.append(this.getEdges().size())
        			.build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
        	 if (obj == null) { return false; }
        	 if (obj == this) { return true; }
        	 if (obj.getClass() != getClass()) {
        	 return false;
        	 }
        	 Vertex<T> rhs = (Vertex<T>) obj;
        	EqualsBuilder builder = new EqualsBuilder()
        			.append(this.getWeight(), rhs.getWeight())
        			.append(this.getEdges().size(), rhs.getEdges().size())
        			.append(this.getValue(), rhs.getValue());
        	
          final Iterator<Edge<T>> iter1 = this.getEdges().iterator();
          final Iterator<Edge<T>> iter2 = rhs.getEdges().iterator();
          while (iter1.hasNext() && iter2.hasNext()) {
              // Only checking the cost
        	  builder.append(iter1.next().getCost(), iter2.next().getCost());
          }
			return builder.build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(Vertex<T> rhs) {
        	CompareToBuilder builder = new CompareToBuilder()
        			.append(this.getValue(), rhs.getValue())
        			.append(this.getWeight(), rhs.getWeight())
        			.append(this.getEdges().size(), rhs.getEdges().size());
			
            final Iterator<Edge<T>> iter1 = this.getEdges().iterator();
            final Iterator<Edge<T>> iter2 = rhs.getEdges().iterator();
            while (iter1.hasNext() && iter2.hasNext()) {
                // Only checking the cost
          	  builder.append(iter1.next().getCost(), iter2.next().getCost());
            }
        	
        	return builder.build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
		public String toString() {
			final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
					.append("Value", this.getValue())
					.append("Weight", this.getWeight());
			for (Edge<T> e : this.getEdges())
				builder.append(e);
			return builder.build();
		}
    }

    public static class Edge<T extends Comparable<T>> extends EdgeImpl implements Comparable<Edge<T>> {


        public Edge(Object id, GraphStore graphStore, Vertex<T> source, Vertex<T> target, int type, double weight, boolean directed) {
        	super(id, graphStore, source, target, type, weight, directed);
        }
        
        public Edge(double cost, Vertex<T> from, Vertex<T> to) {
        	super(id(from, to), null, from, to, 0, (double)cost, true);
        }

        private static <T extends Comparable<T>> Object id(Vertex<T> from, Vertex<T> to) {
			return new HashCodeBuilder()
					.append(from)
					.append(to)
					.build();
		}

		public Edge(Edge<T> e) {
            this(e.getCost(), e.getFromVertex(), e.getToVertex());
        }

        public double getCost() {
            return this.getWeight();
        }

        public void setCost(double cost) {
            this.setWeight(cost);
        }

        public Vertex<T> getFromVertex() {
            return (Vertex<T>) getSource();
        }

        public Vertex<T> getToVertex() {
            return (Vertex<T>) getTarget();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
        	return new HashCodeBuilder()
        			.append(this.getCost())
        			.append(this.getFromVertex())
        			.append(this.getToVertex())
        			.build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
       	 if (obj == null) { return false; }
       	 if (obj == this) { return true; }
       	 if (obj.getClass() != getClass()) {
       	 return false;
       	 }
       	Edge<T> rhs = (Edge<T>) obj;
        	return new EqualsBuilder()
        			.append(this.getCost(), rhs.getCost())
        			.append(this.getFromVertex(), rhs.getFromVertex())
        			.append(this.getToVertex(), rhs.getToVertex())
        			.build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(Edge<T> rhs) {
        	return new CompareToBuilder()
        			.append(this.getCost(), rhs.getCost())
        			.append(this.getFromVertex(), rhs.getFromVertex())
        			.append(this.getToVertex(), rhs.getToVertex())
        			.build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
            builder.append("FromVertex", this.getFromVertex())
                   .append("ToVertex", this.getToVertex())
                   .append("Cost", this.getCost()).append("\n");
            return builder.toString();
        }
    }

    public static class CostVertexPair<T extends Comparable<T>> implements Comparable<CostVertexPair<T>> {

        private double cost = Integer.MAX_VALUE;
        private Vertex<T> vertex = null;

        public CostVertexPair(double cost, Vertex<T> vertex) {
            if (vertex == null)
                throw (new NullPointerException("vertex cannot be NULL."));

            this.cost = cost;
            this.vertex = vertex;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public Vertex<T> getVertex() {
            return vertex;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
        	Vertex<T> vtx = this.getVertex();
			return new HashCodeBuilder()
        			.append(this.getCost())
        			.append(vtx == null ? 1 : vtx)
        			.build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
          	 if (obj == null) { return false; }
           	 if (obj == this) { return true; }
           	 if (obj.getClass() != getClass()) {
           	 return false;
           	 }
           	CostVertexPair<T> rhs = (CostVertexPair<T>) obj;
        	return new EqualsBuilder()
        			.append(this.getCost(), rhs.getCost())
        			.append(this.getVertex(), rhs.getVertex())
        			.build();
        	
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(CostVertexPair<T> rhs) {
        	return new CompareToBuilder()
        			.append(this.getCost(), rhs.getCost())
        			.append(this.getVertex(), rhs.getVertex())
        			.build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
			return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        			.append(this.getCost())
        			.append(this.getVertex())
        			.build();
        }
    }

    public static class CostPathPair<T extends Comparable<T>> implements Comparable<CostPathPair<T>>{

        private double cost = 0;
        private List<Edge<T>> path = null;

        public CostPathPair(double cost, List<Edge<T>> path) {
            if (path == null)
                throw (new NullPointerException("path cannot be NULL."));

            this.cost = cost;
            this.path = path;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public List<Edge<T>> getPath() {
            return path;
        }

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return new HashCodeBuilder()
					.append(this.getCost())
					.append(this.getPath().size())
					.build();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object obj) {
			 if (obj == null) { return false; }
			 if (obj == this) { return true; }
			 if (obj.getClass() != getClass()) {
			 return false;
			 }
			 CostPathPair<T> rhs = (CostPathPair<T>) obj;
			EqualsBuilder builder = new EqualsBuilder()
					.append(this.getCost(), rhs.getCost())
					.append(this.getPath().size(), rhs.getPath().size());
			
		  final Iterator<Edge<T>> iter1 = this.getPath().iterator();
		  final Iterator<Edge<T>> iter2 = rhs.getPath().iterator();
		  while (iter1.hasNext() && iter2.hasNext()) {
		      // Only checking the cost
			  builder.append(iter1.next().getCost(), iter2.next().getCost());
		  }
			return builder.build();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(CostPathPair<T> rhs) {
			CompareToBuilder builder = new CompareToBuilder()
					.append(this.getCost(), rhs.getCost())
					.append(this.getPath().size(), rhs.getPath().size());
			
		    final Iterator<Edge<T>> iter1 = this.getPath().iterator();
		    final Iterator<Edge<T>> iter2 = rhs.getPath().iterator();
		    while (iter1.hasNext() && iter2.hasNext()) {
		        // Only checking the cost
		  	  builder.append(iter1.next().getCost(), iter2.next().getCost());
		    }
			
			return builder.build();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
					.append("Cost", this.getCost());
			for (Edge<T> e : getPath())
				builder.append(e);
			return builder.build();
		}
    }
}