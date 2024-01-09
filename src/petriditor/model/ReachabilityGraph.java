package propra.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * This class provides the functionality to build a Reachability Graph for a Petrinet.
 * It can furthermore check if a graph is bounded, and if so, retrieve the Path of a
 * partial reachability graph as well as m and m'.
 */
public class ReachabilityGraph implements Serializable {
	private static final long serialVersionUID = 2286119872773994640L;

    /**
     * Adjacency list representation of the reachability graph, mapping vertices to their adjacent vertices.
     */
	public Map<Vertex, LinkedList<Vertex>> adjacentList;

    /**
     * Initial vertex of the reachability graph.
     */
	public Vertex Initial;

    /**
     * Array storing the unbounded states (m and m').
     */
	public Vertex[] UnboundedStates;

    /**
     * List storing the path traversed in the reachability graph.
     */
	private LinkedList<String> Path;


    /**
     * Constructs a reachability graph with the given initial vertex.
     *
     * @param initial The initial vertex of the reachability graph.
     */
	public ReachabilityGraph(Vertex initial) {
		this.UnboundedStates = new Vertex[2];
		this.adjacentList = new HashMap<>();
		this.addVertex(initial);
		this.Initial = initial;
		this.Path = new LinkedList<>();
	}

    /**
     * Creates a deep clone of this ReachabilityGraph instance.
     *
     * @return A new ReachabilityGraph object that is a deep clone of this instance.
     */
	@Override
	public ReachabilityGraph clone() {
		// return a clone using serialization
		try {
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ObjectOutputStream out = new ObjectOutputStream(baos);
		    out.writeObject(this);
		    out.flush();
		    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		    ObjectInputStream in = new ObjectInputStream(bais);
		    return (ReachabilityGraph) in.readObject();
	    } catch (IOException | ClassNotFoundException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

    /**
     * Adds a vertex to the reachability graph. If the vertex already exists, it's not duplicated.
     *
     * @param vertex The vertex to be added.
     */
	public void addVertex(Vertex vertex) {
		if (!this.adjacentList.containsKey(vertex)) {
			System.out.println("ReachabilityGraph - addVertex: Added Vertex  " + vertex.toString());
			this.adjacentList.put(vertex, new LinkedList<>());
		} else {
			System.out.println(String.format("ReachabilityGraph - addVertex: Vertex %s already found. No duplicate added.", vertex.toString()));
		}
	}

    /**
     * Adds an arc between two vertices in the graph, also adding the 'to' vertex if it's not already present.
     *
     * @param from The source vertex of the arc.
     * @param to   The destination vertex of the arc.
     */
	public void addArc(Vertex from, Vertex to) {
		this.addVertex(to);
		System.out.println(String.format("ReachabilityGraph - addArc: Adding Arc from %s to %s along Transition %s",
				from.toString(), to.toString(), to.ReachedFrom));

		LinkedList<Vertex> reachableStates = this.adjacentList.get(from);
		if (!reachableStates.contains(to)) {
			reachableStates.add(to);
		}
		this.Path.add(to.ReachedFrom);
	}

    /**
     * Sets the initial vertex of the reachability graph.
     *
     * @param initial The vertex to be set as initial.
     */
	public void setInitial(Vertex initial) {
		System.out.println("ReachabilityGraph - setInitital: Setting Vertex  " + initial.toString() + " as initial.");
		if (!this.adjacentList.containsKey(initial)) {
			this.addVertex(initial);
		}
		this.Initial = initial;
	}

	private Integer getVertexCount() {
		return this.adjacentList.size();
	}

	private Integer getArcCount() {
		int count = 0;
		for (Map.Entry<Vertex, LinkedList<Vertex>> entry : this.adjacentList.entrySet()) {
			count += entry.getValue().size();
		}
		return count;
	}

    /**
     * Provides a formatted string representing the counts of vertices and arcs in the graph.
     *
     * @return A string in the format "vertices / arcs".
     */
	public String getCount() {
		return String.format("%d / %d", this.getVertexCount(), this.getArcCount());
	}

    /**
     * Determines if the graph is bounded.
     *
     * @return true if the graph is bounded, false otherwise.
     */
	public Boolean isBounded() {
		for (Vertex v1 : this.adjacentList.keySet()) {
			for (Vertex v2 : this.adjacentList.keySet()) {
				if (isCandidate(v1, v2)) {
//					if (pathExists(v1, v2)) {
					if (this.pathExists(v1, v2)) {
						System.out.println("ReachabilityGraph - isBounded: false");
						// store m and m'
						this.UnboundedStates[0] = v1;
						this.UnboundedStates[1] = v2;
						return false;
					}
				}
			}
		}
		System.out.println("ReachabilityGraph - isBounded: true");
		return true;
	}

    /**
     * Checks if two vertices are candidates for unboundedness in the graph.
     *
     * @param v1 The first vertex.
     * @param v2 The second vertex.
     * @return true if the vertices are candidates for unboundedness, false otherwise.
     */
	private Boolean isCandidate(Vertex v1, Vertex v2) {
		LinkedList<Integer> state1 = v1.State;
		LinkedList<Integer> state2 = v2.State;
		int isBigger = 0;
		int isEqual = 0;
		for (int i = 0; i < state1.size(); i++) { // iterate through each place
			int diff = state2.get(i) - state1.get(i);
			switch (diff) {
			case 0:
				isEqual++;
				break;

			case 1:
				isEqual++;
				isBigger++;
				break;

			default:
				break;
			}
		}
		if (isEqual == state1.size() && isBigger > 0) {
			System.out.println("ReachabilityGraph - isCandidate: Found candidates: " + v1.toString() + " & " + v2.toString());
			return true;
		}
		return false;
	}

    /**
     * Determines if there exists a path between two vertices in the graph.
     */
	private Boolean pathExists(Vertex source, Vertex destination) {
    	Set<Vertex> path = new HashSet<>();
    	// call recursive depth search
    	if (hasPath(source, destination, path)) {
    		System.out.println("ReachabilityGraph - pathExists: Path found between: " + source.toString() + " and " + destination.toString());
    		return true;
    	} else {
    		return false;
    	}
	}

    /**
     * Helper method to determine if there is a path between two vertices using Depth-First Search.
     */
	private Boolean hasPath(Vertex source, Vertex destination, Set<Vertex> path) {
    	if (path.contains(source)) {
            return false;
        }

        path.add(source);

        if (source.equals(destination)) {
        	System.out.println("ReachabilityGraph - hasPath: Path found for candidates: " + source.toString() + " & " + destination.toString());
            return true;
        }

        for (Vertex neighbor : this.adjacentList.get(source)) {
            if (this.hasPath(neighbor, destination, path)) {
            	System.out.println("ReachabilityGraph - hasPath: Path found for candidates: " + source.toString() + " & " + destination.toString());
                return true;
            }
        }
        return false;
	}

    /**
     * Finds the shortest path between two vertices using Dijkstra's algorithm.
     *
     * This method calculates the shortest path in a graph from the source vertex
     * to the destination vertex. It uses a breadth-first search approach and keeps
     * track of predecessors to reconstruct the path once the destination is reached.
     *
     * @param source      The starting vertex of the path.
     * @param destination The destination vertex of the path.
     * @return A list of vertices representing the shortest path from source to destination.
     *         Returns an empty list if no path is found.
     */
    public List<Vertex> shortestPath(Vertex source, Vertex destination) {
        HashMap<Vertex, Vertex> predecessors = new HashMap<>();
        Stack<Vertex> stack = new Stack<>();
        HashSet<Vertex> visited = new HashSet<>();
        LinkedList<Vertex> path = new LinkedList<>();

        stack.push(source);
        visited.add(source);

        while (!stack.isEmpty()) {
            Vertex current = stack.pop(); // get first element of list and remove it

            for (Vertex neighbor : this.adjacentList.get(current)) {
                if (!visited.contains(neighbor)) {
                    predecessors.put(neighbor, current);
                    if (neighbor.equals(destination)) {
                        // Reconstruct the path from destination to source
                        for (Vertex at = destination; at != null; at = predecessors.get(at)) {
                            path.add(at);
                        }
                        Collections.reverse(path);
                        path.remove(0); // removes the null of the initial vertex's ReachedFrom
                        return path;
                    }
                    visited.add(neighbor);
                    stack.push(neighbor);
                }
            }
        }

        // No path found
        return Collections.emptyList();
    }

    /**
     * Generates a string representation of the path traversed from the initial vertex to m'.
     *
     * @return A string representing the path.
     */
    public String getPath() {
    	List<Vertex> path = this.shortestPath(this.Initial, this.UnboundedStates[1]);
    	StringBuilder pathBuilder = new StringBuilder();
		pathBuilder.append(path.size() + ":(");
		for (Vertex v : path) {
			pathBuilder.append(v.ReachedFrom + ",");
		}
		pathBuilder.deleteCharAt(pathBuilder.length() - 1); // remove trailing comma
		pathBuilder.append(");");
		return pathBuilder.toString();
    }

    /**
     * Gets the string representation of the unbounded state 'm'.
     *
     * @return A string representing the unbounded state 'm'.
     */
    public String getM() {
    	return this.UnboundedStates[0].toString();
    }

    /**
     * Gets the string representation of the unbounded state 'm'' (m dash).
     *
     * @return A string representing the unbounded state 'm''.
     */
    public String getMDash() {
    	return this.UnboundedStates[1].toString();
    }
}