package propra.controller;

import java.io.Serializable;
import java.util.NoSuchElementException;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;


/**
 * Abstract class for graph visualisation.
 */
abstract class Graph extends MultiGraph implements Serializable {
	private static final long serialVersionUID = -6235654585737631507L;

    /**
     * SpriteManager instance
     */
	protected SpriteManager SpriteMan;

    /**
     * Highlight and Initial nodes
     */
	protected String Highlight, Initial;

    /**
     * Constructor for Graph.
     */
	public Graph() {
		super("Petrinet");
		this.setAttribute("ui.style", "padding:5px;");
		this.SpriteMan = new SpriteManager(this);

		// set visualization quality
		this.setAttribute("ui.quality");
		this.setAttribute("ui.antialias");
		System.out.println("Graph - new Graph initialized.");
	}

    /**
     * Returns the style for highlighting a node.
     * @return The style for highlighting a node.
     */
	protected abstract String getStyleHighlight();

    /**
     * Returns the style for the initial node.
     * @return The style for the initial node.
     */
	protected abstract String getStyleInitial();

    /**
     * Returns the style for highlighting the initial node.
     * @return The style for highlighting the initial node.
     */
	protected abstract String getStyleInitialHighlight();

    /**
     * Returns the normal style for a node.
     * @return The normal style for a node.
     */
	protected abstract String getStyleNormal();

    /**
     * Sets the highlight for a node based on its ID and enable/disable flag.
     * @param id The ID of the node.
     * @param enable True to enable the highlight, false to disable.
     */
	protected abstract void setHighlight(String id, Boolean enable);

    /**
     * Sets the style for a node identified by its ID and whether it is supposed to be highlighted.
     * @param id The ID of the node.
     * @param isHighlight True if the node is highlighted, otherwise false.
     */
	protected void setNodeStyle(String id, Boolean isHighlight) {
		System.out.println(String.format("Graph - setNodeStyle: Setting Highlight on Node %s to %b",
				id, isHighlight));
		Node node;
		try {
			node = this.getNode(id);
		} catch (NoSuchElementException e) {
			throw new IllegalArgumentException(e);
		}
		boolean isInitial = id == this.Initial;
		if (isHighlight && isInitial) {
			node.setAttribute("ui.style", this.getStyleInitialHighlight());
		} else if (isHighlight && !isInitial) {
			node.setAttribute("ui.style", this.getStyleHighlight());
		} else if (!isHighlight && isInitial) {
			node.setAttribute("ui.style", this.getStyleInitial());
		} else if (!isHighlight && !isInitial) {
			node.setAttribute("ui.style", this.getStyleNormal());
		}
	}

    /**
     * Toggles the highlight for the node based with the given ID.
     * @param id The ID of the node.
     */
	protected void toggleHighlight(String id) {
		if (this.Highlight != null) {
			// deactivate highlight on current highlighted node
			this.setHighlight(this.Highlight, false);
		}
		if (this.Highlight == id) {
			// remove Highlight setting
			this.Highlight = null;
		} else {
			// set new Highlight
			this.Highlight = id;
			this.setHighlight(id, true);
		}
	}

    /**
     * Adds an arc between nodes.
     * @param fromID ID of the source node.
     * @param toID ID of the target node.
     * @param transID ID of the transition associated with the arc.
     */
	protected void addArc(String fromID, String toID, String transID) {
		String arcID = fromID + "-" + toID + ":" + transID;
		if (!this.edgeMap.containsKey(arcID)) {
			System.out.println(String.format("Graph - addArc: Adding Edge from %s to %s", fromID, toID));
			Node from = this.getNode(fromID);
			Node to = this.getNode(toID);

			Edge edge = this.addEdge(arcID, from, to, true);
			edge.setAttribute("ui.style", styleEdge);

			Sprite s = this.SpriteMan.addSprite(arcID + "_label");
			s.attachToEdge(arcID);
			s.setPosition(0.5);
			s.setAttribute("ui.label", "[" + transID + "]");
			s.setAttribute("ui.style", styleEdgeLabel);
		}
		System.out.println(String.format("Graph - addArc: Edge from %s to %s already found. No duplicate added.", fromID, toID));
	}

    /**
     * Displays information about the graph (node and edge count).
     * @return Information about the graph.
     */
	public String displayInfo() {
		return String.format("MultiGraph with %d nodes and %d edges.\n",
				this.nodeCount, this.edgeCount);
	}

    /**
     * Resets the graph by removing all nodes and edges.
     */
	protected void reset() {
		System.out.println("Graph - reset: Resetting graph. Adios amigo!");
		this.Highlight = null;
		while (this.getEdgeCount() > 0) {
			Edge edge = this.getEdge(0);
			this.SpriteMan.removeSprite(edge.getId() + "_label");
			this.removeEdge(edge);
		}

		while (this.getNodeCount() > 0) {
			Node node = this.getNode(0);
			this.SpriteMan.removeSprite(node.getId() + "_label");
			this.removeNode(node);
			}
	}

	/**
	 *  Style for edges
	 */
	protected static final String styleEdge = ""
		    + "fill-color: #003366;"
		    + "text-color: #003366;"
		    + "arrow-shape: arrow;"
		    + "arrow-size: 9px, 6px;"
		    + "text-size: 12;"
		    + "text-color: purple;"
		    + "z-index: 0;";

	/**
	 *  Style for node labels
	 */
	protected static final String styleNodeLabel = ""
			+ "stroke-mode: plain;"
			+ "stroke-width: 1px;"
			+ "stroke-color: black;"
			+ "text-background-mode: rounded-box;"
			+ "text-background-color: yellow;"
			+ "text-padding: 5px,5px;"
			+ "text-alignment: right;"
		    + "text-size: 12;"
		    + "text-alignment: center;"
		    + "text-offset: 0,35px;"
		    + "z-index: 1;";

	/**
	 * Style for edge labels
	 */
	protected static final String styleEdgeLabel = ""
			+ "shape:rounded-box;"
			+ "fill-color: rgba(0,0,0,0);"
			+ "text-background-mode: rounded-box;"
			+ "text-background-color: grey;"
			+ "text-padding: 5px,5px;"
		    + "text-size: 12;"
		    + "text-alignment: center;"
		    + "z-index: 1;";
}
