package propra.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import propra.model.ReachabilityGraph;
import propra.model.Vertex;


/**
 * A class for visualizing a reachability graph.
 */
public class ReachabilityGraphVisualisation extends Graph implements Serializable {
	private static final long serialVersionUID = -2957263116758292738L;

    /**
     * Constructs an empty ReachabilityGraphVisualisation instance.
     */
	public ReachabilityGraphVisualisation() {
		super();
		System.out.print("ReachabilityGraphVisualisation - Empty ReachabilityGraphVisualisation created.\n");
	}

    /**
     * Constructs a ReachabilityGraphVisualisation instance and parses the provided reachability graph.
     *
     * @param graph The reachability graph to visualize.
     */
	public ReachabilityGraphVisualisation(ReachabilityGraph graph) {
		super();
		this.parseGraph(graph);
		System.out.print(String.format("ReachabilityGraphVisualisation - Created new Graph. It's a %s", this.displayInfo()));
	}

    /**
     * Parses the given reachability graph and constructs its visual representation.
     *
     * @param graph The reachability graph to be parsed.
     */
	private void parseGraph(ReachabilityGraph graph) {

		for (Map.Entry<Vertex, LinkedList<Vertex>> entry : graph.adjacentList.entrySet()) {
			Vertex v = entry.getKey();
			LinkedList<Vertex> adjList = entry.getValue();
			if (v.equals(graph.Initial)) {
				this.addState(v, true);
			} else {
				this.addState(v,  false);
			}
			for (Vertex v2 : adjList) {
				if (v2.equals(graph.Initial)) {
					this.addState(v2, true);
				} else {
					this.addState(v2,  false);
				}
				this.addArc(v.toString(), v2.toString(), v2.ReachedFrom);
			}
		}
	}

    /**
     * Resets the visual representation of the graph and reparses the provided reachability graph.
     *
     * @param graph The reachability graph to reset and parse.
     */
	public void reset(ReachabilityGraph graph) {
		this.reset();
		this.parseGraph(graph);
	}

    /**
     * Updates the visual representation of the reachability graph.
     *
     * @param graph The reachability graph to be updated.
     */
	public void updateGraph(ReachabilityGraph graph) {
		this.parseGraph(graph);
	}

	private void addState(Vertex v, Boolean isInitial) {
		String nodeID = v.toString();
		Node node;
		if (!this.nodeMap.containsKey(nodeID)) {
			System.out.println(String.format("ReachabilityGraphVisualisation - addState: Adding Node %s", v.toString()));
			node = this.addNode(nodeID);
			node.setAttribute("ui.label", nodeID);
		} else {
			System.out.print(String.format("ReachabilityGraphVisualisation - addState: Node %s already existing. No duplicate will be added.\n", nodeID));
		}
		this.setStateStyle(nodeID, isInitial);
	}

	private void setStateStyle(String id, Boolean isInitial) {
		System.out.println(String.format("ReachabilityGraphVisualisation - setStateStyle: Setting initial for Node %s to %b", id, isInitial));
		Node node;
		try {
			node = this.getNode(id);
		} catch (NoSuchElementException e) {
			throw new IllegalArgumentException(e);
		}
		if (isInitial) {
			node.setAttribute("ui.style", styleStateInitial);
		} else {
			node.setAttribute("ui.style", styleState);
		}
	}

    /**
     * Enables highlighting for a transition between two states and the destination vertex.
     *
     * @param from The originating vertex.
     * @param to The destination vertex.
     */
	public void enableHighlight(Vertex from, Vertex to) {
		String arcID = from.toString() + "-" + to.toString() + ":" + to.ReachedFrom;
		this.toggleHighlight(arcID + "#$$$#" + to.toString());
	}

    /**
     * Sets the highlight status of an edge and node in the graph.
     *
     * @param id The identifier of the edge and node.
     * @param enable Boolean indicating whether to enable or disable highlighting.
     */
	@Override
	protected void setHighlight(String id, Boolean enable) {
        String[] substrings = id.split("#\\$\\$\\$#");
        String transID = substrings[0];
        String stateID = substrings[1];
        System.out.println(String.format("ReachabilityGraphVisualisation - setHighlight: Setting Highlight on Edge %s and Node %s to %b",
        		transID, stateID, enable));
		this.setNodeStyle(stateID, enable);
		Edge e = this.getEdge(transID);
		if (enable) {
			e.setAttribute("ui.style", styleEdgeHighlight);
		} else {
			e.setAttribute("ui.style", styleEdge);
		}
	}

    /**
     * Returns the normal style for a node.
     * @return The normal style for a node.
     */
	@Override
	protected String getStyleNormal() {
		return styleState;
	}

    /**
     * Returns the style for the initial node.
     * @return The style for the initial node.
     */
	@Override
	protected String getStyleInitial() {
		return styleStateInitial;
	}

    /**
     * Returns the style for highlighting a node.
     * @return The style for highlighting a node.
     */
	@Override
	protected String getStyleHighlight() {
		return styleStateHighlight;
	}

    /**
     * Returns the style for highlighting the initial node.
     * @return The style for highlighting the initial node.
     */
	@Override
	protected String getStyleInitialHighlight() {
		return styleStateInitialHighlight;
	}

	private static final String styleState = ""
			+ "shape: box;"
			+ "size: 60px,20px;"
			+ "size-mode: fit;"
			+ "fill-color: #99ccff;"
			+ "text-background-color:#99ccff;"
			+ "text-background-mode:rounded-box;"
			+ "text-color: #003366;"
			+ "text-size: 18;"
			+ "text-offset: 0,-5;"
			+ "text-padding:5px,5px;"
			+ "stroke-mode: plain;"
			+ "stroke-color: #003366;"
			+ "stroke-width: 1px;";

	private static final String styleStateInitial = ""
			+ "shape: box;"
			+ "size: 60px,20px;"
			+ "size-mode: fit;"
			+ "fill-color: #99ffa5;"
			+ "text-background-mode:rounded-box;"
			+ "text-background-color:#99ffa5;"
			+ "text-color: #24ab34;"
			+ "text-size: 18;"
			+ "text-offset: 0,-5;"
			+ "text-padding:5px,5px;"
			+ "stroke-mode: plain;"
			+ "stroke-color: #24ab34;"
			+ "stroke-width: 1px;";

	private static final String styleStateHighlight = ""
			+ "shape: box;"
			+ "size: 60px,20px;"
			+ "size-mode: fit;"
			+ "fill-color: #99ccff;"
			+ "text-background-color: red;"
			+ "text-background-mode:rounded-box;"
			+ "text-color: #003366;"
			+ "text-size: 18;"
			+ "text-offset: 0,-5;"
			+ "text-padding:5px,5px;"
			+ "stroke-mode: plain;"
			+ "stroke-color: red;"
			+ "stroke-width: 3px;";

	private static final String styleStateInitialHighlight = ""
			+ "shape: box;"
			+ "size: 60px,20px;"
			+ "size-mode: fit;"
			+ "fill-color: #99ffa5;"
			+ "text-background-mode:rounded-box;"
			+ "text-background-color:#99ffa5;"
			+ "text-color: #24ab34;"
			+ "text-size: 18;"
			+ "text-offset: 0,-5;"
			+ "text-padding:5px,5px;"
			+ "stroke-mode: plain;"
			+ "stroke-color: red;"
			+ "stroke-width: 3px;";

	protected static final String styleEdgeHighlight = ""
		    + "fill-color: red;"
		    + "text-color: red;"
		    + "arrow-shape: arrow;"
		    + "arrow-size: 9px, 6px;"
		    + "text-size: 12;"
		    + "z-index: 0;";
}
