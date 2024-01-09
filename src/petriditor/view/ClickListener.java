package propra.view;

import org.graphstream.ui.view.ViewerListener;

public abstract class ClickListener implements ViewerListener {

	/**
	 * This only implements very basic functionality to implement the ViewerListener interface.
	 */
	public ClickListener() {
	}

	@Override
	public void viewClosed(String viewName) {
		System.out.println("ClickListener - viewClosed: " + viewName);
	}

	@Override
	public void buttonPushed(String id) {
		System.out.println("ClickListener - buttonPushed: " + id);
		this.clickNodeInGraph(id);
	}

	@Override
	public void buttonReleased(String id) {
		System.out.println("ClickListener - buttonReleased: " + id);
	}

	@Override
	public void mouseOver(String id) {
		System.out.println("ClickListener - mouseOver: " + id);

	}

	@Override
	public void mouseLeft(String id) {
		System.out.println("ClickListener - mouseLeft: " + id);
	}

	public void clickNodeInGraph(String id) {
		// override when subclassing
		System.out.println(String.format("ClickListener - clickNodeInGraph: Node %s clicked.", id));
	}
}