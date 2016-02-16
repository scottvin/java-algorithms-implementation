package com.jwetherell.algorithms.data_structures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.gephi.graph.api.Configuration;
import org.gephi.graph.api.Node;
import org.gephi.graph.impl.EdgeImpl;
import org.gephi.graph.impl.EdgeStore;
import org.gephi.graph.impl.GraphModelImpl;
import org.gephi.graph.impl.GraphStore;
import org.gephi.graph.impl.NodeImpl;
import org.gephi.graph.impl.NodeStore;

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
public class Graph<T extends Comparable<T>> extends GraphModelImpl  implements Comparable<Graph<T>> {
	private Collection<Vertex<T>> vertices = null;
	private Collection<Edge<T>> edges = null;

    public enum TYPE {
        DIRECTED, UNDIRECTED
    }

    /** Defaulted to undirected */
    private TYPE type = TYPE.UNDIRECTED;

    public Graph() { 
    	this(TYPE.UNDIRECTED);
    }

    public Graph(TYPE type) {
    	super(configuration());
        this.type = type;
    }

    private static Configuration configuration() {
		Configuration configuration2 = new Configuration();
		configuration2.setEdgeIdType(Integer.class);
		configuration2.setNodeIdType(Integer.class);
		configuration2.setEdgeWeightType(Double.class);
		return configuration2;
	}

	/** Deep copies **/
    public static <T extends Comparable<T>> Graph<T> copyGraph(Graph<T> g) {
    	Graph<T> graph = new Graph<T>(g.getType());

        // Copy the vertices 
        for (Vertex<T> fmOld : g.getAllVertices()){
        	Vertex<T> fmNew = graph.newVertex(fmOld.getValue(), fmOld.getWeight());
            // Copy the edges
            for (Edge<T> e : fmOld.getEdges()) {
            	Vertex<T> toOld = e.getToVertex();
				Vertex<T> toNew = graph.newVertex(toOld.getValue(), toOld.getWeight());
				graph.newEdge(Edge.id(fmNew, toNew), graph.getStore(), fmNew, toNew, e.getType(), e.getWeight(), e.isDirected());
            }
        }
        return graph;
    }

	private Edge<T> newEdge(Object id, GraphStore store, Vertex<T> fm, Vertex<T> to, int type, double weight, boolean directed) {
		Edge<T> edge = new Edge<T>(id, this.getStore(), fm, to, type, weight, directed);
		this.addEdge(edge);
		fm.addEdge(edge);
		return edge;
	}

	private Vertex<T> newVertex(T value, double weight) {
		Vertex<T> v = this.getVertex(value);
		if(v == null){
			v = new Vertex<T>(this, value, weight);
			this.addVertex(v);
		}
		return v;
	}

    public TYPE getType() {
        return type;
    }

    public List<Vertex<T>> getAllVertices() {
        ArrayList<Vertex<T>> vertexList = new ArrayList<Vertex<T>>();
        NodeStore ns = this.getStore().getNodeStore();
        for (Node node : ns) {
			vertexList.add((Vertex<T>) node);        	
		}
		return vertexList;
    }

    public Vertex<T> getVertex(T value) {
		return (Vertex<T>) super.getStore().getNode(value);
	}

    public boolean hasVertex(T value) {
		return super.getStore().hasNode(value);
	}

    public boolean addVertex(Vertex<T> v) {
		return super.getStore().addNode(v);
	}

    public boolean addEdge(Edge<T> e) {
		return super.getStore().addEdge(e);
	}

    public boolean removeVertex(Vertex<T> v) {
		return super.getStore().removeNode(v);
	}

    public boolean removeEdge(Edge<T> e) {
		return super.getStore().removeEdge(e);
	}

	public List<Edge<T>> getAllEdges() {
        ArrayList<Edge<T>> edgeList = new ArrayList<Edge<T>>();
        EdgeStore es = this.getStore().getEdgeStore();
        for (org.gephi.graph.api.Edge edge : es) {
			edgeList.add((Edge<T>) edge);        	
		}
		return edgeList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
    	GraphStore gstore = this.getStore();
		HashCodeBuilder builder = new HashCodeBuilder()
    			.append(this.type)
    			.append(gstore.getNodeCount())
    			.append(gstore.getEdgeCount());
		for (Node n : gstore.getNodes())
			builder.append(n);
		for (org.gephi.graph.api.Edge e : gstore.getEdgeStore())
			builder.append(e);
		return builder.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		Graph<T> rhs = (Graph<T>) obj;
		GraphStore lstore = this.getStore();
		GraphStore rstore = rhs.getStore();
		EqualsBuilder builder = new EqualsBuilder()
				.append(this.getType(), rhs.getType())
				.append(lstore.getNodeCount(), rstore.getNodeCount())
				.append(lstore.getEdgeCount(), rstore.getEdgeCount());
		
        Iterator<Node> iter1 = lstore.getNodes().iterator();
        Iterator<Node> iter2 = rstore.getNodes().iterator();
        while (iter1.hasNext() && iter2.hasNext()) {
            // Only checking the cost
      	  builder.append(iter1.next(), iter2.next());
        }

        Iterator<org.gephi.graph.api.Edge> ei1 = lstore.getEdges().iterator();
        Iterator<org.gephi.graph.api.Edge> ei2 = rstore.getEdges().iterator();
        while (ei1.hasNext() && ei2.hasNext()) {
            // Only checking the cost
      	  builder.append(ei1.next(), ei2.next());
        }

		return builder.build();
    }

    @Override
	public int compareTo(Graph<T> rhs) {
    	GraphStore rstore = rhs.getStore();
		GraphStore lstore = this.getStore();
		CompareToBuilder builder = new CompareToBuilder()
				.append(this.getType(), rhs.getType())
				.append(lstore.getNodeCount(), rstore.getNodeCount())
				.append(lstore.getEdgeCount(), rstore.getEdgeCount());
		
        Iterator<Node> iter1 = lstore.getNodes().iterator();
        Iterator<Node> iter2 = rstore.getNodes().iterator();
        while (iter1.hasNext() && iter2.hasNext()) {
            // Only checking the cost
      	  builder.append(iter1.next(), iter2.next());
        }

        Iterator<org.gephi.graph.api.Edge> ei1 = lstore.getEdges().iterator();
        Iterator<org.gephi.graph.api.Edge> ei2 = rstore.getEdges().iterator();
        while (ei1.hasNext() && ei2.hasNext()) {
            // Only checking the cost
      	  builder.append(ei1.next(), ei2.next());
        }

		return builder.build();
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    	builder.append("Vertices\n");
        GraphStore gstore = this.getStore();
		for (Node n : gstore.getNodes()){
        	builder.append(n);
        	builder.append("\n");
        }
    	builder.append("Edges\n");
        for (org.gephi.graph.api.Edge e : gstore.getEdges()){
            builder.append(e);
        	builder.append("\n");
        }
        return builder.build();
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
	    initiate(vertices, edges);
	}

	private void initiate(Collection<Vertex<T>> vertices, Collection<Edge<T>> edges) {
		this.edges = edges;
		this.vertices = vertices;
		
	}

	public Graph<T> populate() {
		
		GraphStore gstore = this.getStore();
	    gstore.clear();
		gstore.addAllNodes(vertices);
	    gstore.addAllEdges(edges);
	    for (Edge<T> e : edges) {
	        final Vertex<T> from = (Vertex<T>) gstore.getNode(e.getFromVertex().getId());
	        final Vertex<T> to = (Vertex<T>) gstore.getNode(e.getToVertex().getId());
	
	        if (!gstore.contains(from) || !gstore.contains(to))
	            continue;
	
	        from.addEdge(e);
	        if (this.type == TYPE.UNDIRECTED) {
	            Edge<T> reciprical = new Edge<T>(this, e.getCost(), to, from);
	            to.addEdge(reciprical);
	            this.addEdge(reciprical);
	        }
	    }
	    return this;
	}

	public static class Vertex<T extends Comparable<T>> extends NodeImpl implements Comparable<Vertex<T>> {
    	private GraphStore graphStore = new GraphStore();
        private double weight = 0;
        private List<Edge<T>> edges = new ArrayList<Edge<T>>();

        public Vertex(Graph<T> graph) {
        	this(graph, (T)null);
        }
        public Vertex(Graph<T> graph, T value) {
        	this(graph, value, 0);
        }

        public Vertex(Graph<T> graph, T value, double weight) {
        	super(value == null ? Integer.MAX_VALUE : value, graph.getStore());
            this.weight = weight;
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
			boolean first = true;
			for (Edge<T> e : this.getEdges()){
				if(first){
					builder.append("Edges=", e.getCost());
				}else{
					builder.append(e.getCost());
				}
				first = false;
			}
			return builder.build();
		}
		public GraphStore getGraphStore() {
			return graphStore;
		}
		public void setGraphStore(GraphStore graphStore) {
			this.graphStore = graphStore;
		}
    }

    public static class Edge<T extends Comparable<T>> extends EdgeImpl implements Comparable<Edge<T>> {
    	
    	GraphStore graphStore = new GraphStore();

		public Edge(Object id, GraphStore graphStore, Vertex<T> source, Vertex<T> target, int type, double weight, boolean directed) {
        	super(id, graphStore, source, target, type, weight, directed);
        	
        }
        
        public Edge(Graph<T> graph, double cost, Vertex<T> from, Vertex<T> to) {
        	this(id(from, to), graph.getStore(), from, to, 0, (double)cost, true);
        }

        private static <T extends Comparable<T>> Object id(Vertex<T> from, Vertex<T> to) {
			return new HashCodeBuilder()
					.append(from)
					.append(to)
					.build();
		}

        public GraphStore getGraphStore() {
			return graphStore;
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
            builder.append("FromVertex", this.getFromVertex().getValue())
                   .append("ToVertex", this.getToVertex().getValue())
                   .append("Cost", this.getCost());
            return builder.toString();
        }

		public void setGraphStore(GraphStore graphStore) {
			this.graphStore = graphStore;
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
			for (Edge<T> e : getPath()){
				builder.append(e);
	        	builder.append("\n");
			}
			return builder.build();
		}
    }

}