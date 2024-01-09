package propra.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import propra.controller.Controller;


/**
 * Frame class representing the main window of the application.
 */
public class Frame extends JFrame {

	private static final long serialVersionUID = -5889798258170436748L;
	private JLabel state;
	private JMenuBar menu;
	private JMenu File, Help;
	private JToolBar Toolbar;
	protected ButtonGroup Buttongroup;
	protected JMenuItem Info, Open, Undo, Redo, Close, Reload, MultipleFiles, Quit;
	protected JButton NextFile, PrevFile, ResetGraph, DeleteGraph, Analyze;
	protected JToggleButton AddToken, RemoveToken;
	protected JRootPane rootPane;

	private JPanel mainPanel;
	private JTextArea textOut;
	private Controller ctrl;

	protected static final String FILE = "File";
	protected static final String HELP = "Help";
	protected static final String INFO = "Info";
	protected static final String OPEN = "Open file...";
	protected static final String UNDO = "Undo";
	protected static final String REDO = "Redo";
	protected static final String CLOSE = "Close";
	protected static final String QUIT = "Quit";
	protected static final String RELOAD = "Reload";
	protected static final String MULTIPLE = "Open multiple files..";
	protected static final String NEXTFILE = "Next File";
	protected static final String PREVFILE = "Prev File";
	protected static final String ADDTOKEN = "Add Token";
	protected static final String REMOVETOKEN = "Remove Token";
	protected static final String RESETGRAPH = "Reset Petrinet";
	protected static final String DELETEGRAPH = "Delete Reachability Graph";
	protected static final String ANALYZE = "Analyze Reachability";


    /**
     * Constructor for the Frame class.
     *
     * @param name       The name/title of the frame.
     * @param controller The controller associated with the frame.
     */
	public Frame(String name, Controller controller) {
		super(name);
		this.ctrl = controller;
		this.ctrl.setFrame(this);
		this.setLayout(new BorderLayout());

		// Get menu bar

		this.menu = new JMenuBar();
		this.File = new JMenu(FILE);
		this.Help = new JMenu(HELP);
		this.Info = new JMenuItem(INFO, new ImageIcon("resources/icons/info.png"));
		this.Open = new JMenuItem(OPEN, new ImageIcon("resources/icons/open.png"));
		this.Undo = new JMenuItem(UNDO, new ImageIcon("resources/icons/undo.png"));
		this.Redo = new JMenuItem(REDO, new ImageIcon("resources/icons/redo.png"));
		this.Close = new JMenuItem(CLOSE, new ImageIcon("resources/icons/close.png"));
		this.Quit = new JMenuItem(QUIT, new ImageIcon("resources/icons/quit.png"));
		this.Reload = new JMenuItem(RELOAD, new ImageIcon("resources/icons/reload.png"));
		this.MultipleFiles = new JMenuItem(MULTIPLE, new ImageIcon("resources/icons/multiple.png"));

		// Set menu order
		this.menu.add(this.File);
		this.menu.add(this.Help);
		this.File.add(this.Undo);
		this.File.add(this.Redo);
		this.File.add(this.Open);
		this.File.add(this.MultipleFiles);
		this.File.add(this.Reload);
		this.File.add(this.Close);
		this.File.add(this.Quit);
		this.Help.add(this.Info);

		this.setJMenuBar(this.menu);

		//  Get Toolbar
		this.Toolbar = new JToolBar();
		this.PrevFile = new JButton(PREVFILE, new ImageIcon("resources/icons/prev.png"));
		this.NextFile = new JButton(NEXTFILE, new ImageIcon("resources/icons/next.png"));

		this.AddToken = new JToggleButton(ADDTOKEN, new ImageIcon("resources/icons/add.png"));
		this.RemoveToken = new JToggleButton(REMOVETOKEN, new ImageIcon("resources/icons/remove.png"));
		this.ResetGraph = new JButton(RESETGRAPH, new ImageIcon("resources/icons/reset.png"));
		this.DeleteGraph = new JButton(DELETEGRAPH, new ImageIcon("resources/icons/delete.png"));
		this.Analyze = new JButton(ANALYZE, new ImageIcon("resources/icons/analyze.png"));

		this.Toolbar.add(this.PrevFile);
		this.Toolbar.add(this.NextFile);
		this.Toolbar.addSeparator();

		this.Buttongroup = new ButtonGroup();
		this.Buttongroup.add(this.AddToken);
		this.Buttongroup.add(this.RemoveToken);
		this.Toolbar.add(this.AddToken);
		this.Toolbar.add(this.RemoveToken);

		this.Toolbar.addSeparator();
		this.Toolbar.add(this.ResetGraph);
		this.Toolbar.add(this.DeleteGraph);
		this.Toolbar.add(this.Analyze);

		// set RootPane for keyboard shortcuts
        this.rootPane = getRootPane();

		// Initiate main panel

		this.mainPanel = new JPanel(new BorderLayout());
		this.add(this.mainPanel, BorderLayout.CENTER);

        this.textOut = new JTextArea();
        this.textOut.setEditable(false);
        this.textOut.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        this.state = new JLabel();
        this.mainPanel.add(this.state, BorderLayout.SOUTH);

		double height = 0.7;
		double ratio = 1.6;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int h = (int) (screenSize.height * height);
		int w = (int) (h * ratio);
	    setBounds((screenSize.width - w) / 2, (screenSize.height - h) / 2, w, h);

	    this.getInitPanel();
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);

        this.printMessage("Initialization successeful. Load petrinet to proceed!");
	}

    /**
     * Updates the frame's main panel.
     */
	public void update() {
		this.mainPanel.revalidate();
		this.mainPanel.repaint();
	}

	private void updateView(JComponent content, String statusString) {
		this.mainPanel.removeAll();
		this.state = new JLabel("   " + statusString);
		this.mainPanel.add(this.state, BorderLayout.SOUTH);

		JScrollPane scrollPane = new JScrollPane(this.textOut);
        JSplitPane textSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, content, scrollPane);
        textSplit.setResizeWeight(0.75);
        this.mainPanel.add(textSplit, BorderLayout.CENTER);
		this.mainPanel.revalidate();
		this.mainPanel.repaint();
	}

    /**
     * Initializes the initial panel of the frame.
     */
	protected void getInitPanel() {
		this.ctrl.disposeGraphs();
		JPanel logoPanel;
		try {
			logoPanel = new ImagePanel();
		} catch (IOException e) {
			e.printStackTrace();
			logoPanel = new JPanel(new BorderLayout());
			}
		this.updateView(logoPanel, "java.version = " + System.getProperty("java.version")
    		+ "  |  user.dir = " + System.getProperty("user.dir"));
	}

    /**
     * Sets up the panel for graph-related functionalities.
     *
     * @param status The status message to display (usually the file name).
     */
	protected void getGraphPanel(String status) {
		this.ctrl.initPanelGraph();
        JPanel jpnlPG = new JPanel(new BorderLayout());
		jpnlPG.add(BorderLayout.CENTER, this.ctrl.Ppanel);
        JPanel jpnlRG = new JPanel(new BorderLayout());
		jpnlRG.add(BorderLayout.CENTER, this.ctrl.Rpanel);

        JSplitPane graphSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jpnlPG, jpnlRG);
        graphSplit.setResizeWeight(0.5);
        this.updateView(graphSplit, status + "\t\t\t\t        Modified: No");
		this.mainPanel.add(this.Toolbar, BorderLayout.NORTH);
		this.setStatePanel(false);
	}

    /**
     * Prints a message to the text area. A time stamp will be included for each message.
     *
     * @param message The message to be printed.
     */
	public void printMessage(String message) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss");
		this.textOut.append(String.format("[%s] %s\n", java.time.LocalDateTime.now().format(formatter), message));
	}

	public void setStatePanel(Boolean modified) {
		String currentState = this.state.getText();
		if (currentState.contains("Modified: No") && modified) {
			String newState = currentState.replace("Modified: No", "Modified: Yes");
			this.state.setText(newState);
		} else if (currentState.contains("Modified: Yes") && !modified) {
			String newState = currentState.replace("Modified: Yes", "Modified: No");
			this.state.setText(newState);
		}
	}
}
