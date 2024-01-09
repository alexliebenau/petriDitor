package propra.view;

import java.util.LinkedList;

import propra.controller.Controller;

/**
 * ClickListenerReachabilityGraph handles click events in a reachability graph.
 * It extends the ClickListener class and works with the Controller to update
 * the system state based on the node selected in the reachability graph.
 */
public class ClickListenerReachabilityGraph extends ClickListener {
	private Controller ctrl;

    /**
     * Constructs a ClickListenerReachabilityGraph instance.
     *
     * @param ctrl The controller to be used for handling click events.
     */
	public ClickListenerReachabilityGraph(Controller ctrl) {
		super();
		this.ctrl = ctrl;
		this.ctrl.setClickListener(this);
	}

    /**
     * Handles click events on a node in the reachability graph.
     * Saves the current state for undo/redo purposes and updates the system to the state represented
     * by the clicked node.
     *
     * @param id The identifier of the node that was clicked.
     */
	@Override
	public void clickNodeInGraph(String id) {
		this.ctrl.saveState();
		System.out.println("ClickListenerRecachabilityGraph - clickNodeInGraph: Clicked Node " + id);
		this.ctrl.setMarking(convertStateToLinkedList(id));
	}

	private static LinkedList<Integer> convertStateToLinkedList(String input) {
		LinkedList<Integer> result = new LinkedList<>();
		// Remove the leading '(' and trailing ')' characters
		String content = input.substring(1, input.length() - 1);

		// Split the tokens of the places using the '|' delimiter
		String[] tokens = content.split("\\|");

		// Convert tokens to integers and add to the linked list
		for (String token : tokens) {
			result.add(Integer.parseInt(token));
	    }

	    return result;
	}
}