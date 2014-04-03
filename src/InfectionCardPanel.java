import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import static constants.PandemicConstants.*;

public class InfectionCardPanel extends JPanel {

	private static final long serialVersionUID = -3471466783662313776L;

	// Data members
	private City d_city;
	private boolean d_rotated;

	// Static variables
	private static BufferedImage s_infectCardImage;
	
	// Basic modifier methods
	public void setCity(City city) { d_city = city; }
	
	public InfectionCardPanel(boolean rotated) {

		d_city = null;
		d_rotated = rotated;
		setBackground(Color.WHITE);
		if (!rotated) {
			setSize(INFECT_CARD_WIDTH, INFECT_CARD_HEIGHT);
		}
		else {
			setSize(INFECT_CARD_HEIGHT, INFECT_CARD_WIDTH);
		}
	}
	
	public static void readInfectCardImage() {
		
		// Set up the infect card image
		try {
			s_infectCardImage = ImageIO.read(new File(INFECT_CARD_PATH));
		}
		catch (IOException e) {

			// We cannot recover from an undefined infect card file
			System.out.println("File Error: " + INFECT_CARD_PATH + " could not be opened.");
			System.exit(1);
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D)g;

		if (!d_rotated) {

			g2d.drawImage(s_infectCardImage, 1, 1, null);
			if (d_city != null) {
				g2d.setColor(Color.WHITE);
				g2d.fillRect(2, 53, INFECT_CARD_WIDTH - 15, 12);
				g2d.setColor(d_city.color());
				g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
				g2d.drawString(d_city.getName(), 6, 63);
			}
		}
		else {
			
			AffineTransform origRotation = g2d.getTransform();
			g2d.rotate(-Math.PI/2);
			
			g2d.drawImage(s_infectCardImage, -89, 1, null);
			if (d_city != null) {
				g2d.setColor(Color.WHITE);
				g2d.fillRect(-88, 53, INFECT_CARD_WIDTH - 15, 12);
				g2d.setColor(d_city.color());
				g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
				g2d.drawString(d_city.getName(), -83, 63);
			}
			g2d.setTransform(origRotation);
		}
	}
}
