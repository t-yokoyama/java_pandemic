import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import static constants.PandemicConstants.*;
	
public class InstructionPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = 8330551848453305957L;

	// Data members
	private BufferedImage d_instructionImage;
	
	public InstructionPanel() {

		// Set up the instruction image
		try {
			d_instructionImage = ImageIO.read(new File(INSTRUCTION_DISPLAY_PATH));
		}
		catch (IOException e) {

			// We cannot recover from an undefined instruction image file
			System.out.println("File Error: " + INSTRUCTION_DISPLAY_PATH + " could not be opened.");
			System.exit(1);
		}

		setLayout(null);
		setSize(MENU_WIDTH + MAP_WIDTH, MAP_HEIGHT);		
		addMouseListener(this);

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(d_instructionImage, 0, 0, null);
	}

	public void mouseClicked(MouseEvent e) {
		
		setVisible(false);
	}
	
	public void mousePressed(MouseEvent e) { }

	public void mouseReleased(MouseEvent e) { }

	public void mouseEntered(MouseEvent e) { }

	public void mouseExited(MouseEvent e) { }

}
