
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import static constants.PandemicConstants.*;

public class DiscoveredCuresPanel extends JPanel {

	private static final long serialVersionUID = 156779797787180697L;

	// Data members
	private InfectionManager d_infectionManager;
	private BufferedImage d_noCureImage;
	private BufferedImage [] d_cureImage;

	public DiscoveredCuresPanel(InfectionManager infectionManager) {
		
		d_infectionManager = infectionManager;
		setLayout(null);
		setSize(CURE_ICON_SIZE * NUM_DISEASES, CURE_ICON_SIZE);
		d_cureImage = new BufferedImage[NUM_DISEASES];

		for (int i = 0; i < NUM_DISEASES; i++) {
			try {
				d_cureImage[i] = ImageIO.read(new File(CURE_ICON_PATH[i]));
			}
			catch (IOException e) {

				// We cannot recover from an undefined icon file
				System.out.println("File Error: " + CURE_ICON_PATH[i] + " could not be opened.");
				System.exit(1);
			}
		}
		
		try {
			d_noCureImage = ImageIO.read(new File(NOCURE_ICON_PATH));
		}
		catch (IOException e) {

			// We cannot recover from an undefined icon file
			System.out.println("File Error: " + NOCURE_ICON_PATH + " could not be opened.");
			System.exit(1);
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		for (int i = 0; i < NUM_DISEASES; i++) {
			if (d_infectionManager.isCureFound(i)) {
				g.drawImage(d_cureImage[i], CURE_ICON_SIZE * i, 0, null);
			}
			else {
				g.drawImage(d_noCureImage, CURE_ICON_SIZE * i, 0, null);
			}
		}
	}
}
