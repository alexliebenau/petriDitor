package propra.model;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Represents a vertex in a graph, specifically for use in a reachability graph
 * of a Petri net. This class contains the state of the Petri net at this vertex
 * and optionally the transition used to reach this state.
 *
 * Hint: State and marking are the same. I just like to call a marking state. Feels more comfortable.
 */
public class Vertex implements Serializable {
	private static final long serialVersionUID = 8122808594182892360L;

    /**
     *  Represents the state of the Petri net at this vertex.
     *  Each integer in the list represents the number of tokens in a place of the net.
     */
    public LinkedList<Integer> State;

    /**
     *  Identifies the transition used to reach this vertex/state from the previous one.
     */
    public String ReachedFrom;

    /**
     * Constructs a Vertex with the specified state.
     *
     * @param state The state of the Petri net at this vertex.
     */
	public Vertex(LinkedList<Integer> state) {
		this.State = state;
	}

    /**
     * Constructs a Vertex with the specified state and the ID of the transition
     * used to reach this state.
     *
     * @param state   The state of the Petri net at this vertex.
     * @param fromID  The ID of the transition used to reach this state.
     */
	public Vertex(LinkedList<Integer> state, String fromID) {
		this.State = state;
		this.ReachedFrom = fromID;
	}

    /**
     * Provides a string representation of the Vertex, showing its state.
     *
     * @return A string representation of the vertex's state.
     */
    @Override
	public String toString() {
	    StringBuilder result = new StringBuilder("(");
	    for (Integer number : this.State) {
	        result.append(number).append("|");
	    }
	    // Remove the trailing '|' if the list isn't empty
	    if (!this.State.isEmpty()) {
	        result.deleteCharAt(result.length() - 1);
	    }
	    result.append(")");
	    return result.toString();
    }

    /**
     * Compares this vertex to another object for equality. Two vertices are considered
     * equal if they have the same state.
     *
     * @param obj The object to be compared with this vertex.
     * @return true if the specified object is equal to this vertex, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vertex vertex = (Vertex) obj;
        return this.State.equals(vertex.State);
    }

    /**
     * Generates a hash code for this vertex. The hash code is based on the vertex's state.
     *
     * @return A hash code value for this vertex.
     */
    @Override
    public int hashCode() {
        return this.State.hashCode();
    }

}
