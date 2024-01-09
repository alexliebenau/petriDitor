package propra.view;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Helper class of a JPanel to display a picture.
 * Is used for the background of the initial Panel.
 */
public class ImagePanel extends JPanel {

	  private static final long serialVersionUID = 3335891599363377018L;
	  private BufferedImage image;

	  protected ImagePanel() throws IOException {
	    try {
	      image = ImageIO.read(new File("resources/fuh_logo.png"));
	    } catch (IOException ex) {
	      	throw ex;
	    }
	  }

	  @Override
	  protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
        FontMetrics metrics = g.getFontMetrics();
        String text = "Programmierpraktikum Wintersemester 2023/24";
        String text2 = "Lösung von Alex Liebenau (q3535266)";
        int textWidth = metrics.stringWidth(text);
        int x = (getWidth() - textWidth) / 2;
        int y = getHeight() - 20;
        int y2 = getHeight() - 35;
        g.setColor(Color.BLACK);
        g.drawString(text2, x, y);
        g.drawString(text, x, y2);
	  }

	}