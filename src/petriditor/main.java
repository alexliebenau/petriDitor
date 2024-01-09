package propra;

import java.io.File;

import propra.view.GUI;

public class Petrinetz_3535266_Liebenau_Alexander {

	public static void main(String[] args) {

		System.setProperty("sun.java2d.uiScale", "1.0");

		// festlegen, dass der Swing Viewer verwendet werden soll
		System.setProperty("org.graphstream.ui", "swing");

		System.out.println("System info");
		System.out.println("------------------------------------");
		System.out.println("user.dir     = " + System.getProperty("user.dir"));
		System.out.println("java.version = " + System.getProperty("java.version"));
		System.out.println("java.home    = " + System.getProperty("java.home"));
		System.out.println();

		// Prüfen, ob das Beispiel-Verzeichnis gefunden wird
		File bsp = new File("../ProPra-WS23-Basis/Beispiele");
		System.out.println("Check if default directory exists");
		System.out.println("--------------------------------------");
		System.out.println("Default directory:: " + bsp);
		System.out.println("Exists?: " + bsp.exists());
		System.out.println();

		// Frame erzeugen
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new GUI("petriDitor");
			}
		});
	}
}