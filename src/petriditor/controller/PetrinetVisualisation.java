package propra.controller;

import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

import propra.model.Petrinet;
import propra.model.Place;
import propra.model.Transition;

/**
 * Class for the visualization of a Petrinet.
 */
public class PetrinetVisualisation extends Graph implements Serializable {
	private static final long serialVersionUID = -8411350415602820770L;

    /**
     * Empty constructor for a graph.
     */
	public PetrinetVisualisation() {
		super();
		System.out.print("PetrinetVisualisation - Empty PetrinetVisualisation created.\n");
	}

    /**
     * Constructor for a graph with a Petrinet.
     * @param petrinet The Petrinet to be visualized.
     */
	public PetrinetVisualisation(Petrinet petrinet) {
		super();
		this.parsePetrinet(petrinet);
		System.out.print(String.format("PetrinetVisualisation - Created new Graph. It's a %s", this.displayInfo()));
	}

	/**
	 * Parses a given Petrinet and constructs its visual representation.
	 * @param petrinet The Petrinet to be parsed and visualized.
	 */
	private void parsePetrinet(Petrinet petrinet) {
		// set nodes for places
		for (Map.Entry<String, Place> entry : petrinet.Places.entrySet()) {
			String id = entry.getKey();
			Place place = entry.getValue();

			if (petrinet.InitialPlace == place) {
				this.addPlace(id, place, true);
				this.Initial = id;
			} else {
				this.addPlace(id, place, false);
			}
			this.setNodeStyle(id, false);
		}

		// set nodes for transitions
		for (Map.Entry<String, Transition> entry : petrinet.Transitions.entrySet()) {
			String id = entry.getKey();
			Transition transition = entry.getValue();
			this.addTransition(id, transition);
		}

		// set edges
		for (Map.Entry<String, Transition> entry : petrinet.Transitions.entrySet()) {
			String transitionID = entry.getKey();
			Transition transition = entry.getValue();

			for (Map.Entry<String, Place> e : transition.Pre.entrySet()) {
				String arcID = e.getKey();
				Place place = e.getValue();
				String placeID = petrinet.getID(place);
				this.addArc(placeID, transitionID, arcID);
			}

			for (Map.Entry<String, Place> e : transition.Post.entrySet()) {
				String arcID = e.getKey();
				Place place = e.getValue();
				String placeID = petrinet.getID(place);
				this.addArc(transitionID, placeID, arcID);
			}
		}

		this.updateGraph(petrinet);
	}

	/**
	 * Adds a place to the Petrinet visualization.
	 * @param id The identifier of the place.
	 * @param place The place object to be added.
	 * @param isInitial Boolean indicating if the place is an initial place.
	 */
	private void addPlace(String id, Place place, Boolean isInitial) {
		System.out.println(String.format("PetrinetVisualisation - addPlace: Adding Node %s", id));
		Node node = this.addNode(id);
		node.setAttribute("xy", place.Position[0], place.Position[1]);
		Sprite s = this.SpriteMan.addSprite(id + "_label");
		s.attachToNode(id);
		s.setAttribute("ui.style", styleNodeLabel);
	}

	/**
	 * Adds a transition to the Petrinet visualization.
	 * @param id The identifier of the transition.
	 * @param transition The transition object to be added.
	 */
	private void addTransition(String id, Transition transition) {
		System.out.println(String.format("PetrinetVisualisation - addTransition: Adding Node %s", id));
		Node node = this.addNode(id);
		node.setAttribute("xy", transition.Position[0], transition.Position[1]);

		Sprite s = this.SpriteMan.addSprite(id + "_label");
		s.attachToNode(id);
		s.setAttribute("ui.label", String.format("[%s] %s", id, transition.Name));
		s.setAttribute("ui.style", styleNodeLabel);
	}

	/**
	 * Sets the style of a transition node based on its activation status.
	 * @param id The identifier of the transition node.
	 * @param isActive Boolean indicating whether the transition is active.
	 */
	private void setTransitionStyle(String id, Boolean isActive) {
		System.out.println(String.format("PetrinetVisualisation - set TransitionStyle: Setting activation for Node %s to %b", id, isActive));
		Node node;
		try {
			node = this.getNode(id);
		} catch (NoSuchElementException e) {
			throw new IllegalArgumentException(e);
		}
		if (isActive) {
			node.setAttribute("ui.style", styleTransitionActive);
		} else {
			node.setAttribute("ui.style", styleTransition);
		}
	}

	/**
	 * Updates the visual representation of the current Petrinet.
	 * @param petrinet The current Petrinet.
	 */
	public void updateGraph(Petrinet petrinet) {
		for (Map.Entry<String, Place> entry : petrinet.Places.entrySet()) {
			String id = entry.getKey();
			Place p = entry.getValue();
			Node n = this.getNode(id);

			String tokens;
			if (p.Tokens > 9) {
				tokens = ">9";
			} else if (p.Tokens > 0) {
				tokens = p.Tokens.toString();
			} else {
				tokens = "";
			}
			n.setAttribute("ui.label", tokens);

			Sprite s = this.SpriteMan.getSprite(id + "_label");
			s.setAttribute("ui.label", String.format("[%s] %s <%d>", id, p.Name, p.Tokens));
		}


		for (Map.Entry<String, Transition> entry : petrinet.Transitions.entrySet()) {
			String id = entry.getKey();
			Transition t = entry.getValue();
			this.setTransitionStyle(id, t.isReady());
		}
	}

	/**
	 * Resets the visual representation of the Petrinet and reparses the given Petrinet.
	 * @param petrinet The Petrinet to be reset and reparsed.
	 */
	public void reset(Petrinet petrinet) {
		this.reset();
		this.parsePetrinet(petrinet);
	}

    /**
     * Sets the highlight for a node based on its ID and enable/disable flag.
     * @param id The ID of the node.
     * @param enable True to enable the highlight, false to disable.
     */
	@Override
	protected void setHighlight(String id, Boolean enable) {
		this.setNodeStyle(id, enable);
	}

    /**
     * Returns the style for the normal node.
     * @return The style for the normal node.
     */
	@Override
	protected String getStyleNormal() {
		return stylePlace;
	}

    /**
     * Returns the style for the initial node.
     * @return The style for the initial node.
     */
	@Override
	protected String getStyleInitial() {
		return stylePlace;
	}

    /**
     * Returns the style for highlighting a node.
     * @return The style for highlighting a node.
     */
	@Override
	protected String getStyleHighlight() {
		return stylePlaceHighlight;
	}

    /**
     * Returns the style for highlighting the initial node.
     * @return The style for highlighting the initial node.
     */
	@Override
	protected String getStyleInitialHighlight() {
		return stylePlaceHighlight;
	}

	private static final String stylePlace = ""
			+ "size: 40px;"
			+ "fill-color: #99ccff;"
			+ "text-color: #003366;"
			+ "text-size: 18;"
			+ "text-offset: 0,-5;"
			+ "stroke-mode: plain;"
			+ "stroke-color: #003366;"
			+ "stroke-width: 1px;"
			+ "z-index: 2;";

	private static final String stylePlaceHighlight = ""
			+ "size: 50px;"
			+ "fill-color: #99ccff;"
			+ "text-color: #003366;"
			+ "text-size: 21;"
			+ "text-style: bold;"
			+ "text-offset: 0,-5;"
			+ "stroke-mode: plain;"
			+ "stroke-color: red;"
			+ "stroke-width: 3px;"
			+ "z-index: 2;";

	private static final String stylePlaceInitial = ""
			+ "size: 40px;"
			+ "fill-color: #99ffa5;"
			+ "text-color: #24ab34;"
			+ "text-size: 18;"
			+ "text-offset: 0,-5;"
			+ "stroke-mode: plain;"
			+ "stroke-color:#24ab34;"
			+ "stroke-width: 1px;"
			+ "z-index: 2;";

	private static final String stylePlaceInitialHighlight = ""
			+ "size: 50px;"
			+ "fill-color: #99ffa5;"
			+ "text-color: #24ab34;"
			+ "text-size: 21;"
			+ "text-offset: 0,-5;"
			+ "stroke-mode: plain;"
			+ "stroke-color: red;"
			+ "stroke-width: 3px;"
			+ "z-index: 2;";

	private static final String styleTransition = ""
			+ "shape: box;"
			+ "size: 30px,30px;"
			+ "fill-color: #d6ebff;"
			+ "stroke-mode: plain;"
			+ "stroke-color: #003366;"
			+ "stroke-width: 1px;";

	private static final String styleTransitionActive = ""
			+ "shape: box;"
			+ "size: 30px, 30px;"
			+ "fill-color: #fff7d6;"
			+ "stroke-mode: plain;"
			+ "stroke-color: #d1b130;"
			+ "stroke-width: 1px;";
}
