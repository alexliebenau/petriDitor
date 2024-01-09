package propra.view;

import propra.controller.Controller;

/**
 * ClickListenerPetriGraph handles click events in a Petrinet visualisation.
 * It extends the ClickListener class and integrates with the Controller
 * to perform specific actions based on the type of node clicked in the graph.
 */
public class ClickListenerPetriGraph extends ClickListener {
	private Controller ctrl;

    /**
     * Constructs a ClickListenerPetriGraph instance.
     *
     * @param ctrl The controller to be used for handling click events.
     */
	public ClickListenerPetriGraph(Controller ctrl) {
		super();
		this.ctrl = ctrl;
		this.ctrl.setClickListener(this);
	}

    /**
     * Handles click events on a node in the Petrinet graph.
     * Determines whether the clicked node is a place or a transition,
     * and performs the appropriate action.
     *
     * @param id The identifier of the node that was clicked.
     */
	@Override
	public void clickNodeInGraph(String id) {
		System.out.println("ClickListenerPetriGraph - clickNodeInGraph: " + id);
		// check if ID is Place or Transition
		if (this.ctrl.isPlace(id)) {
			this.ctrl.toggleHighlight(id);
		} else if (this.ctrl.isTransition(id)) {
			this.ctrl.saveState();
			this.ctrl.fire(id);
			this.ctrl.checkBounded();
		}
	}
}