package propra.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import propra.controller.Controller;

/**
 * Listener class handling various actions and events in the GUI.
 */
public class Listener implements ActionListener, ItemListener {

	private static final long serialVersionUID = 8200710908947497338L;
	private Frame Frame;
	private Controller ctrl;
	private File SelectedFile;
	private String Path;
	private LinkedList<File> files;

    /**
     * Constructor for the Listener class.
     *
     * @param frame    The application main frame
     * @param ctrl     The application controller
     */
	public Listener(Frame frame, Controller ctrl) {
		this.Frame = frame;
		this.ctrl = ctrl;
		this.Path = "resources/petrinets/";
		this.readDir();

		// Set Action Listener for menu
		this.Frame.Info.addActionListener(this);
		this.Frame.Undo.addActionListener(this);
		this.Frame.Redo.addActionListener(this);
		this.Frame.Open.addActionListener(this);
		this.Frame.Close.addActionListener(this);
		this.Frame.Quit.addActionListener(this);
		this.Frame.Reload.addActionListener(this);
		this.Frame.MultipleFiles.addActionListener(this);

		// Set Action Listener for toolbar
		this.Frame.NextFile.addActionListener(this);
		this.Frame.PrevFile.addActionListener(this);
		this.Frame.ResetGraph.addActionListener(this);
		this.Frame.DeleteGraph.addActionListener(this);
		this.Frame.Analyze.addActionListener(this);

		// set Item Listener for Toggle Buttons
		this.Frame.AddToken.addItemListener(this);
		this.Frame.RemoveToken.addItemListener(this);
	}

    /**
     * Handles state change events from toggle buttons.
     *
     * @param e The ItemEvent instance.
     */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (ItemEvent.SELECTED == e.getStateChange()) {
			this.ctrl.saveState();
			this.ctrl.manageToken(this.Frame.AddToken.isSelected());
			this.Frame.Buttongroup.clearSelection();
		}
	}

    /**
     * Handles action events, like button clicks and menu item selections.
     *
     * @param e The ActionEvent instance.
     */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (propra.view.Frame.INFO.equals(e.getActionCommand())) {

	        JDialog InfoDialog = new JDialog();
	        int h = 1000;
	        int w = 750;
	        InfoDialog.setSize(w,h);
	        InfoDialog.setBounds((this.Frame.getLocation().x + this.Frame.getWidth()/2) - w/2 ,
	        		(this.Frame.getLocation().y + this.Frame.getHeight()/2) - h/2, w, h);
	        InfoDialog.setModal(true);
	        InfoDialog.getContentPane().setBackground(Color.LIGHT_GRAY);

	        JTextArea textArea = new JTextArea("FernUni Hagen Programmierpraktikum WS23/34\nAbgabe von Alex Liebenau\n"
	        		+ "\nBitte mit 1,0 bewerten habe mir echt Mühe gegeben!\n\n");

	        textArea.setMargin(new Insets(20, 20, 20, 20));
	        textArea.setEditable(false);

	        for(Object propName : System.getProperties().keySet()) {
	          String property = propName + " = " + System.getProperty((String)propName);
	          textArea.append(property + "\n");
	        }

	        JScrollPane scrollPane = new JScrollPane(textArea);
	        scrollPane.setPreferredSize(new Dimension(950, 700));

	        InfoDialog.add(scrollPane);
	        InfoDialog.pack();
	        InfoDialog.setVisible(true);
		}

		if (propra.view.Frame.UNDO.equals(e.getActionCommand())) {
			this.ctrl.undo();
		}

		if (propra.view.Frame.REDO.equals(e.getActionCommand())) {
			this.ctrl.redo();
		}

		if (propra.view.Frame.OPEN.equals(e.getActionCommand())) {
			JFileChooser fileChooser = this.getFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                this.SelectedFile = fileChooser.getSelectedFile();
                String filePath = this.SelectedFile.getAbsolutePath();
                this.updatePath(this.SelectedFile.getParent());
                if (filePath.endsWith(".pnml")) {
                    // Load the selected file
                    this.Frame.printMessage("Loading file: " + filePath);
                    try {
	                    this.ctrl.displayGraphs(this.SelectedFile);
	                    this.Frame.getGraphPanel("File: " + this.SelectedFile.getName());
                    } catch (NoSuchElementException err) {
                    	this.Frame.printMessage("Error: " + err.getMessage());
                    }
                } else {
                    // Show an error dialog for incorrect file format
                    JOptionPane.showMessageDialog(null,
                            "Please select a .pnml file.",
                            "Incorrect File Format",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
		}

		if (propra.view.Frame.MULTIPLE.equals(e.getActionCommand())) {
			this.Frame.getInitPanel();
			JFileChooser fileChooser = this.getFileChooser();
		    fileChooser.setMultiSelectionEnabled(true);
		    int returnValue = fileChooser.showOpenDialog(null);
		    if (returnValue == JFileChooser.APPROVE_OPTION) {
		        File[] selectedFiles = fileChooser.getSelectedFiles();
		        Integer counter = 1;
		        LinkedList<File> files = new LinkedList<>();
		        for (File selectedFile : selectedFiles) {
	                this.updatePath(selectedFile.getParent());
		            String filePath = selectedFile.getAbsolutePath();
		            if (filePath.endsWith(".pnml")) {
		                files.add(selectedFile);
		            } else {
		                this.Frame.printMessage(String.format("WARNING - Ignoring file %d of %d because of incorrect format of file: ",
		                		counter, selectedFiles.length) + filePath);
		            }
		            counter++;
		        }
		        Collections.sort(files);
		        this.Frame.printMessage(String.format("Processing %d files...", selectedFiles.length));
		        try (Formatter formatter = new Formatter()) {
					int widthFilename = 66;
					int widthBounded = 10;
					int widthPath = 35;
					int widthState = 15;
					String template = "%-" + widthFilename + "s | %-" + widthBounded + "s | %-" + widthPath + "s %-" + widthState + "s %-" + widthState + "s%n";
					formatter.format("Done.\n", "");
					formatter.format(template, "", "", "Nodes / Edges", "", "");
					formatter.format(template, "Filename", "bounded", "Path length; m, m'", "", "");
					formatter.format("-".repeat(widthFilename + 1) + "|" + "-".repeat(widthBounded + 2) + "|" + "-".repeat(widthPath + 2*widthState) + "\n");
					counter = 1;
					for (File file : files) {
						try {
							String[] result = this.ctrl.analyzeFile(file);
							formatter.format(template, result[0], result[1], result[2], result[3], result[4]);
						} catch (NoSuchElementException err) {
							this.Frame.printMessage(String.format("File %d: Error while loading file %s. %s Ignoring file, check terminal for debug info.", counter, file.getName(), err.getMessage()));
						}
						counter++;
					}
					this.Frame.printMessage(formatter.toString());
				}
		    }
		}

		if (propra.view.Frame.ANALYZE.equals(e.getActionCommand())) {
			this.ctrl.saveState();
			try {
				String[] result = this.ctrl.analyzeFile(this.SelectedFile);
				String isBoundedString;
				Boolean bounded;
				if (result[1].equals("Yes")) {
					isBoundedString = "bounded";
					bounded = true;
				} else {
					isBoundedString = "not bounded";
					bounded = false;
				}

		        JDialog InfoDialog = new JDialog();
		        int h = 100;
		        int w = 500;
		        InfoDialog.setSize(w,h);
		        InfoDialog.setBounds((this.Frame.getLocation().x + this.Frame.getWidth()/2) - w/2 ,
		        		(this.Frame.getLocation().y + this.Frame.getHeight()/2) - h/2, w, h);
		        InfoDialog.setModal(true);
		        InfoDialog.getContentPane().setBackground(Color.LIGHT_GRAY);

		        JTextArea textArea = new JTextArea(String.format("The reachability graph is %s.", isBoundedString));

		        textArea.setMargin(new Insets(20, 20, 20, 20));
		        textArea.setEditable(false);

		        InfoDialog.add(textArea);
		        InfoDialog.pack();
		        InfoDialog.setVisible(true);

		        String message;
		        if (bounded) {
		        	message = String.format("[Nodes / Edges]: %s", result[2]);
		        } else {
		        	message = String.format("# Nodes:(Path): %s \t m, m': %s %s", result[2], result[3], result[4]);
		        }

		        this.Frame.printMessage(String.format("The reachability graph is %s. %s", isBoundedString, message));
			} catch (NoSuchElementException err) {
            	this.Frame.printMessage("Error: " + err.getMessage());
			}
		}

		if (propra.view.Frame.CLOSE.equals(e.getActionCommand())) {
			if (this.SelectedFile != null) {
				this.Frame.getInitPanel();
				this.Frame.printMessage("File closed.");
				this.SelectedFile = null;
			} else {
				this.Frame.printMessage("Nothing to close. Load file(s) first.");
			}
		}

		if (propra.view.Frame.QUIT.equals(e.getActionCommand())) {
			this.Frame.dispose();
		}

		if (propra.view.Frame.RELOAD.equals(e.getActionCommand())) {
			if (this.SelectedFile != null) {
				String filePath = this.SelectedFile.getAbsolutePath();
	            this.Frame.printMessage("Reloading file: " + filePath);
	            try {
	            	this.ctrl.displayGraphs(this.SelectedFile);
	            	this.Frame.getGraphPanel("File: " + this.SelectedFile.getName());
	            } catch (NoSuchElementException err) {
                	this.Frame.printMessage("Error: " + err.getMessage());
	            }
			} else {
	             // Show an error dialog for missing file
                JOptionPane.showMessageDialog(null,
                        "Please select a .pnml file first.",
                        "No file loaded",
                        JOptionPane.ERROR_MESSAGE);
                this.Frame.printMessage("Nothing to reload. Load file(s) first.");
			}
		}

		if (propra.view.Frame.PREVFILE.equals(e.getActionCommand())) {
			int index = this.files.indexOf(this.SelectedFile);
			try {
				this.SelectedFile = this.files.get(index - 1);
                this.Frame.printMessage("Loading file: " + this.SelectedFile.getAbsolutePath());
                try {
                	this.ctrl.displayGraphs(this.SelectedFile);
                	this.Frame.getGraphPanel("File: " + this.SelectedFile.getName());
                } catch (NoSuchElementException err) {
                	this.Frame.printMessage("Error: " + err.getMessage());
	            }
			} catch (IndexOutOfBoundsException exc) {
				this.Frame.printMessage("Warning: Cannot load previous file, the loaded file is already first in the current directory.");
			}
		}

		if (propra.view.Frame.NEXTFILE.equals(e.getActionCommand())) {
			int index = this.files.indexOf(this.SelectedFile);
			try {
				this.SelectedFile = this.files.get(index + 1);
                this.Frame.printMessage("Loading file: " + this.SelectedFile.getAbsolutePath());
                try {
                	this.ctrl.displayGraphs(this.SelectedFile);
                	this.Frame.getGraphPanel("File: " + this.SelectedFile.getName());
                } catch (NoSuchElementException err) {
                	this.Frame.printMessage("Error: " + err.getMessage());
	            }
			} catch (IndexOutOfBoundsException exc) {
				this.Frame.printMessage("Warning: Cannot load next file, the loaded file is already last in the current directory.");
			}
		}

		if (propra.view.Frame.RESETGRAPH.equals(e.getActionCommand())) {
			this.ctrl.saveState();
			this.ctrl.resetToInitial();
		}

		if (propra.view.Frame.DELETEGRAPH.equals(e.getActionCommand())) {

			this.ctrl.saveState();
			this.ctrl.resetReachability();
		}
	}

	private JFileChooser getFileChooser() {
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setCurrentDirectory(new File(this.Path));
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("PNML Files", "pnml");
	    fileChooser.setFileFilter(filter);
	    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    return fileChooser;
	}

	private void updatePath(String path) {
		if (!this.Path.equals(path)) {
			this.Path = path;
			this.readDir();
			System.out.print("Viewer - updatePath: Updated file chooser default directory to: " + path + "\n");
		}
	}

	private void readDir() {
		File dir = new File(this.Path);
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.getName().endsWith(".pnml")) {
					return true;
				}
				return false;
			}
		};
		List<File> fileList = Arrays.asList(dir.listFiles(filter));
		Collections.sort(fileList);
		this.files = new LinkedList<>(fileList);
	}

}
