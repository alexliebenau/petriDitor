package propra.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import propra.model.Petrinet;
import propra.model.Place;
import propra.model.ReachabilityGraph;
import propra.model.Transition;
import propra.model.Vertex;
import propra.pnml.pnmlReader;
import propra.view.ClickListener;
import propra.view.ClickListenerPetriGraph;
import propra.view.ClickListenerReachabilityGraph;
import propra.view.Frame;


/**
 * The Controller class serves as the main control unit for the application, handling the interaction
 * between the model and the view components. It manages the Petrinet and graph visualizations,
 * user interactions, and returns the panels of the graphs to the GUI for display.
 * The GUI in returns sends commands to open and manipulate Petrinets and their reachability graphs.
 */
public class Controller {
	/**
	 * The current Petrinet
	 */
	private Petrinet Pnet;

	/**
	 * The current reachability graoh of the Petrinet
	 */
	private ReachabilityGraph Rgraph;

	/**
	 * The visualisation of the current Petrinet
	 */
	private PetrinetVisualisation PNvisual;

	/**
	 * The visualisation of the current reachability graph
	 */
	private ReachabilityGraphVisualisation RGvisual;

	/**
	 * Stack for undo operation
	 */
	private Stack<Tuple<Petrinet, ReachabilityGraph>> UndoStack;

	/**
	 * Stack for redo operation
	 */
	private Stack<Tuple<Petrinet, ReachabilityGraph>> RedoStack;

	/**
	 * Apolication window
	 */
	private Frame Frame;

	/**
	 * ID of the currently selected place in Petrinet
	 */
	private String Highlight;

	/**
	 * These are the two graph panels that the GUI can access.
	 */
	public ViewPanel Ppanel, Rpanel;

	/**
	 *  ClickListeners for each graph respectively.
	 */
	public ClickListener ClickListenerPG, ClickListenerRG;


    /**
     * Constructor for Controller. Initializes the controller and resets the undo/redo stacks.
     */
	public Controller() {
		System.out.print("Controller initialized\n");
		this.resetStack();
	}

    /**
     * Sets the main application frame.
     *
     * @param frame The main application frame.
     */
	public void setFrame(Frame frame) {
		this.Frame = frame;
	}

    /**
     * Sets ClickListener for the Petrinet visualisation.
     *
     * @param clpg ClickListener for the Petrinet visualisation.
     */
	public void setClickListener(ClickListenerPetriGraph clpg) {
		this.ClickListenerPG = clpg;
	}

    /**
     * Sets ClickListener for the Reachability Graph visualisation.
     *
     * @param clrg ClickListener for the  Reachability Graph visualisation.
     */
	public void setClickListener(ClickListenerReachabilityGraph clrg) {
		this.ClickListenerRG = clrg;
	}

    /**
     * Initializes the GraphStream Viewer for the provided graph.
     *
     * @param graph The graph to be visualized.
     * @param autoLayout Specifies whether to enable automatic layout.
     * @return A SwingViewer instance for the specified graph.
     */
	private SwingViewer initViewer(Graph graph, Boolean autoLayout) {
		SwingViewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		if (autoLayout) {
			viewer.enableAutoLayout();
		} else {
			viewer.disableAutoLayout();
		}

		return viewer;
	}

    /**
     * Initializes the viewer panel and configures mouse interactions.
     *
     * @param clickListener The listener for mouse click events.
     * @param viewer The GraphStream viewer associated with the panel.
     * @return The initialized ViewPanel with configured listeners.
     */
	private ViewPanel initViewerPanel(ClickListener clickListener, Viewer viewer) {
        ViewPanel panel = (ViewPanel) viewer.addDefaultView(false);
		ViewerPipe viewerPipe = viewer.newViewerPipe();
		viewerPipe.addViewerListener(clickListener);
		panel.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent me) {
				System.out.println("Controller/ClickListener - mousePressed: " + me);
				viewerPipe.pump();
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				System.out.println("Controller/ClickListener - mouseReleased: " + me);
				viewerPipe.pump();
			}
		});

		ViewPanel enclosedPanel = panel; // please excuse this ugly hack
		panel.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				double zoomLevel = enclosedPanel.getCamera().getViewPercent();
				if (e.getWheelRotation() == -1) {
					zoomLevel -= 0.1;
					if (zoomLevel < 0.1) {
						zoomLevel = 0.1;
					}
				}
				if (e.getWheelRotation() == 1) {
					zoomLevel += 0.1;
				}
				enclosedPanel.getCamera().setViewPercent(zoomLevel);
			}
		});
		return panel;
	}

    /**
     * Initializes the visualizations for the Petrinet and the Reachability graph.
     */
	private void initGraphs() {
		this.PNvisual = new PetrinetVisualisation(this.Pnet);
		this.RGvisual = new ReachabilityGraphVisualisation(this.Rgraph);
	}

    /**
     * Initializes the panels of the visualizations for the Petrinet and the Reachability graph.
     * The GUI is then able to access these.
     */
	public void initPanelGraph() {
		this.Ppanel = this.initViewerPanel(this.ClickListenerPG, this.initViewer(this.PNvisual, false));
        this.Rpanel = this.initViewerPanel(this.ClickListenerRG, this.initViewer(this.RGvisual, true));
	}

	/**
	 * Loads a file and handles the visualisation of both the Petrinet and Reachbility graph for the GUI.
	 * @param pnmlFile The file that stores the Petrinet to be loaded
	 * @throws NoSuchElementException If an error occurs during file parsing.
	 */
	public void displayGraphs(File pnmlFile) throws NoSuchElementException {
		try {
			this.loadPetrinet(pnmlFile);
			this.initGraphs();
			this.initPanelGraph();
		} catch (NoSuchElementException e) {
			throw e;
		}
	}

	/**
	 * Resets the visualisation.
	 */
	public void disposeGraphs() {
		this.Rgraph = null;
		this.PNvisual = null;
		this.RGvisual = null;
		this.Ppanel = null;
		this.Rpanel = null;
	}

	private void pushToStack(Stack<Tuple<Petrinet, ReachabilityGraph>> stack) {
		Tuple<Petrinet, ReachabilityGraph> state = new Tuple<>(this.Pnet.clone(), this.Rgraph.clone());
		stack.push(state);
	}

	private void getFromStack(Stack<Tuple<Petrinet, ReachabilityGraph>> stack) {
		Tuple<Petrinet, ReachabilityGraph> state = stack.pop();
		this.Pnet = state.e1;
		this.Rgraph = state.e2;
		if (this.PNvisual != null) {
			this.PNvisual.reset(this.Pnet);
		}
		if (this.RGvisual != null) {
			this.RGvisual.reset(this.Rgraph);
		}
	}

    /**
     * Saves the current state of the Petrinet.
     * This method captures the current marking of the Petrinet and
     * pushes it onto the undo stack for future reference.
     */
	public void saveState() {
		System.out.println("Controller - saveState: Saving current state: " + this.Pnet.getMarkingString());
		this.pushToStack(this.UndoStack);
	}

    /**
     * Performs an undo operation.
     * This method restores the Petrinet to its most recently saved state.
     * If the undo stack is empty, it displays a warning message.
     */
	public void undo() {
		if (!this.UndoStack.isEmpty()) {
			this.pushToStack(this.RedoStack);
			this.getFromStack(this.UndoStack);
			System.out.println("Controller - undo: Setting current state: " + this.Pnet.getMarkingString());
			this.Frame.update();
		} else {
			this.Frame.printMessage("Warning: Nothing to undo.");
		}
	}

    /**
     * Performs a redo operation.
     * This method restores the state of the Petrinet to the state
     * prior to the last undo operation.
     * If the redo stack is empty, it displays a warning message.
     */
	public void redo() {
		if (!this.RedoStack.isEmpty()) {
			this.pushToStack(this.UndoStack);
			this.getFromStack(this.RedoStack);
			System.out.println("Controller - redo: Setting current state: " + this.Pnet.getMarkingString());
			this.Frame.update();
		} else {
			this.Frame.printMessage("Warning: Nothing to redo.");
		}
	}

	private void resetStack() {
		this.UndoStack = new Stack<>();
		this.RedoStack = new Stack<>();
	}

    /**
     * Loads a Petrinet from a PNML file and initializes its visualization.
     *
     * @param pnmlFile The PNML file to load the Petrinet from.
     * @throws NoSuchElementException If an error occurs during file parsing.
     */
	private void loadPetrinet(File pnmlFile) throws NoSuchElementException {
		this.resetStack();
		pnmlReader parser = new pnmlReader(pnmlFile);
		parser.initParser();
		try {
			this.Pnet = parser.parsePNML();
			this.Rgraph = new ReachabilityGraph(new Vertex(this.Pnet.getMarking()));
			System.out.println(String.format("Controller - loadPetrinet: Loaded following petrinet from file\n\t%s%s\n",
						pnmlFile.toString(), this.Pnet.toString()));
		} catch (NoSuchElementException e) {
			throw e;
		}
	}

	/**
     * Checks if a given ID corresponds to a place in the Petrinet.
     *
     * @param id The ID to check.
     * @return Boolean indicating whether the ID corresponds to a place.
     */
	public Boolean isPlace(String id) {
		return this.Pnet.Places.containsKey(id);
	}

    /**
     * Checks if a given ID corresponds to a transition in the Petrinet.
     *
     * @param id The ID to check.
     * @return Boolean indicating whether the ID corresponds to a transition.
     */
	public Boolean isTransition(String id) {
		return this.Pnet.Transitions.containsKey(id);
	}

    /**
     * Fires a transition in the Petrinet.
     *
     * If the transition is ready, it updates the marking of the Petrinet
     * and reflects these changes in the reachability graph and visualizations.
     *
     * @param id The ID of the transition to be fired.
     */
	public void fire(String id) {
		Transition t = this.Pnet.Transitions.get(id);
		System.out.println("Controller - fire: Checking out transition " + id);
		if (t.isReady()) {
			Vertex before = new Vertex(this.Pnet.getMarking());
			t.fire();
			Vertex after = new Vertex(this.Pnet.getMarking(), id);
			this.Rgraph.addArc(before, after);
			if (this.PNvisual != null) {
				this.PNvisual.updateGraph(this.Pnet);
			}
			if (this.RGvisual != null) {
				this.RGvisual.updateGraph(this.Rgraph);
				this.RGvisual.enableHighlight(before, after);
			}
		}
	}

	/**
	 * Check if the reachability graph is bounded.
	 * If it is, it prints a message in the GUI for the user.
	 */
	public void checkBounded() {
		if (!this.Rgraph.isBounded()) {
			this.Frame.printMessage("The reachability graph is unbounded!");
		}
	}

    /**
     * Manages tokens for a highlighted place in the Petrinet.
     *
     * If a place is selected, this method adds or removes a token
     * based on which Button is selected in the GUI and updates the
     * Petrinet state and its visual representation.
     *
     * @param add Boolean indicating whether to add (true) or remove (false) a token.
     */
	public void manageToken(Boolean add) {
		if (this.PNvisual.Highlight != null) {
			Place p = this.Pnet.Places.get(this.PNvisual.Highlight);

			if (add) {
				p.Tokens++;
			} else {
				if (p.Tokens > 0) {
					p.Tokens--;
				} else {
					this.Frame.printMessage(String.format("Warning: Cannot remove Token from Place %s because it is already empty.", this.Highlight));
				}
			}

			this.PNvisual.updateGraph(this.Pnet);
			this.resetReachability();
			this.Frame.setStatePanel(true);
			this.Pnet.InitialMarking = this.Pnet.getMarking();
		}
	}

	private void explore(Stack<Tuple<LinkedList<Integer>, String>> stack, Set<Tuple<LinkedList<Integer>, String>> visited, Integer counter) {
		for (Map.Entry<String, Transition> entry : this.Pnet.Transitions.entrySet()) {
			String id = entry.getKey();
			Transition t = entry.getValue();
			if (t.isReady()) {
				Tuple<LinkedList<Integer>, String> next = new Tuple<>(this.Pnet.getMarking(), id);
				if (!visited.contains(next)) {
					visited.add(next);
					stack.push(next);
					counter++;
					System.out.println(String.format("Controller - explore: State %s and Transition %s added to stack  [%d]", next.e1, next.e2, counter));
				} else {
					System.out.println(String.format("Controller - explore: State %s and Transition %s already checked out.", this.Pnet.getMarking(), id));

				}
			}
		}
	}

    /**
     * Analyzes the Petrinet in the given PNML file to determine its properties.
     *
     * @param pnmlFile The PNML file to be analyzed.
     * @return An array containing information about the Petrinet properties.
     *         Index 0: Filename
     *         Index 1: "Yes" if bounded, "No" otherwise
     *         Index 2: The count of nodes in the reachability graph or an empty string if unbounded
     *         Index 3: M' value if unbounded or an empty string if bounded
     *         Index 4: M value if unbounded or an empty string if bounded
     * @throws NoSuchElementException If there is an issue with the file or during analysis.
     */
	public String[] analyzeFile(File pnmlFile) throws NoSuchElementException {
		String[] parsedInfo = new String[5];
		parsedInfo[0] = pnmlFile.getName();
		try {
			this.loadPetrinet(pnmlFile);
			Set<Tuple<LinkedList<Integer>, String>> visited = new HashSet<>();
			Stack<Tuple<LinkedList<Integer>, String>> stateStack = new Stack<>();
			Integer counter = 0;
			this.explore(stateStack, visited, counter);
			Boolean bounded = this.Rgraph.isBounded();

			while (!stateStack.isEmpty()) {
				Tuple<LinkedList<Integer>, String> set = stateStack.pop();
				counter--;
				System.out.println(String.format("Controller - analyzeFile: State %s and Transition %s taken from stack  [%d]", set.e1, set.e2, counter));
				this.Pnet.updateMarking(set.e1);
				this.fire(set.e2);
				this.explore(stateStack, visited, counter);
				bounded = this.Rgraph.isBounded();
				if (!bounded) break;
			}

			if (bounded) {
				parsedInfo[1] = "Yes"; // is bounded as String
				parsedInfo[2] = this.Rgraph.getCount(); // amount of nodes
				parsedInfo[3] = "";
				parsedInfo[4] = "";
			} else {
				parsedInfo[1] = "No"; // is bounded as String
				parsedInfo[2] = this.Rgraph.getPath(); // path
				parsedInfo[3] = this.Rgraph.getM() + ","; // m
				parsedInfo[4] = this.Rgraph.getMDash(); // m'
			}
			System.out.println("Controller - analyzeFiles: " + Arrays.toString(parsedInfo));
			return parsedInfo;
		} catch (NoSuchElementException e) {
			throw e;
		}
	}

    /**
     * Toggles highlighting of a specific node in the Petrinet graph.
     *
     * @param id The identifier of the node to toggle highlighting for.
     */
	public void toggleHighlight(String id) {
		this.PNvisual.toggleHighlight(id);
	}

    /**
     * Resets the Petrinet to its initial marking.
     */
	public void resetToInitial() {
		this.Frame.printMessage(String.format("Resetting to initial marking: %s", this.Pnet.InitialMarking.toString()));
		this.Pnet.updateMarking(this.Pnet.InitialMarking);
		this.PNvisual.updateGraph(this.Pnet);
		this.Ppanel = this.initViewerPanel(this.ClickListenerPG, this.initViewer(this.PNvisual, false));
		this.Frame.update();
	}

    /**
     * Resets the reachability graph to its initial state.
     */
	public void resetReachability() {
//		this.Frame.printMessage("Resetting Reachability Graph");
		this.Rgraph = new ReachabilityGraph(new Vertex(this.Pnet.getMarking()));
		this.RGvisual.reset(this.Rgraph);
        this.Rpanel = this.initViewerPanel(this.ClickListenerRG, this.initViewer(this.RGvisual, true));
        this.Frame.update();
	}

    /**
     * Sets the marking of the Petrinet to the provided marking.
     *
     * @param marking The marking to set for the Petrinet.
     */
	public void setMarking(LinkedList<Integer> marking) {
		this.Frame.printMessage("Setting marking to " + marking.toString());
		this.Pnet.updateMarking(marking);
		this.PNvisual.updateGraph(this.Pnet);
		this.Ppanel = this.initViewerPanel(this.ClickListenerPG, this.initViewer(this.PNvisual, false));
		this.Frame.update();
	}
}

/**
 * A utility class for creating a two-element Tuple.
 * I'm just used to working with them and really missed them in Java.
 * Also I ran out of mobile data to Google which native datatype I could use
 * instead, so this was the quicker solution to my problem.
 *
 * @param <A> The type of the first object in the tuple.
 * @param <B> The type of the second object in the tuple.
 */
class Tuple<A, B> {
	A e1;
	B e2;

	public Tuple(A e1, B e2) {
		this.e1 = e1;
		this.e2 = e2;
	}

	@Override
	public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tuple tuple = (Tuple) obj;
        return (this.e1.equals(tuple.e1) && this.e2.equals(tuple.e2));
	}

	@Override
    public int hashCode() {
        return this.e1.hashCode() + this.e2.hashCode();
    }
}
