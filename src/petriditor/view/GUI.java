package propra.view;

import propra.controller.Controller;

/**
 * Main class for GUI.
 */
public class GUI {
	private Controller ctrl;
	private Frame WindowFrame;
	private Listener ActionListener;

	/**
	 * The constructor for the GUI.
	 * It initialized all important contents in the right order.
	 * @param name Name of the Frame to be displayed
	 */
	public GUI(String name) {
		this.ctrl = new Controller();
		this.WindowFrame = new Frame(name, this.ctrl);
		this.ActionListener = new Listener(this.WindowFrame, this.ctrl);
		ClickListenerPetriGraph clpg = new ClickListenerPetriGraph(this.ctrl);
		ClickListenerReachabilityGraph clrg = new ClickListenerReachabilityGraph(this.ctrl);
	}
}
