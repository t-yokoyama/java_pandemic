import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import static constants.PandemicConstants.*;

public class PlayerCardPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = 7522088032156313393L;

	// Data members
	private PandemicGame d_game;
	private PlayerCard d_card;
	boolean d_singleSelectEnabled;
	boolean d_multipleSelectEnabled;
	boolean d_selected;
	
	// Static variables
	private static BufferedImage s_playerCardImage;
	private static BufferedImage s_epidemicCardImage;

	// Basic accessor methods
	public PlayerCard getCard() { return d_card; }
	public boolean isSelected() { return d_selected; }
	
	// Basic modifier methods
	public void setCard(PlayerCard card) { d_card = card; }
	public void enableSingleSelection() { d_singleSelectEnabled = true; }
	public void disableSingleSelection() { d_singleSelectEnabled = false; }
	public void enableMultipleSelection() { d_multipleSelectEnabled = true; }
	public void disableMultipleSelection() { d_multipleSelectEnabled = false; }
	
	public PlayerCardPanel(PandemicGame game) {

		d_game = game;
		d_card = null;
		d_singleSelectEnabled = false;
		d_multipleSelectEnabled = false;
		d_selected = false;
		setBackground(Color.BLACK);
		setSize(PLAYER_CARD_WIDTH, PLAYER_CARD_HEIGHT);
		addMouseListener(this);
	}

	public static void readPlayerCardImages() {
		
		// Set up the player card image
		try {
			s_playerCardImage = ImageIO.read(new File(PLAYER_CARD_PATH));
		}
		catch (IOException e) {

			// We cannot recover from an undefined player card file
			System.out.println("File Error: " + PLAYER_CARD_PATH + " could not be opened.");
			System.exit(1);
		}

		// Set up the epidemic card image
		try {
			s_epidemicCardImage = ImageIO.read(new File(EPIDEMIC_CARD_PATH));
		}
		catch (IOException e) {

			// We cannot recover from an undefined epidemic card file
			System.out.println("File Error: " + EPIDEMIC_CARD_PATH + " could not be opened.");
			System.exit(1);
		}

	}
	
	public void reset() {

		d_singleSelectEnabled = false;
		d_multipleSelectEnabled = false;
		if (d_selected) {
			d_selected = false; 
			this.setLocation(this.getLocation().x, this.getLocation().y + 10);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (d_card != null && d_card.isEpidemic()) {
			g.drawImage(s_epidemicCardImage, 1, 1, null);
		}
		else {
			
			Graphics2D g2d = (Graphics2D)g;
			if (d_card != null) {
				g2d.setColor(d_card.getCity().color());
			}
			else {
				g2d.setColor(Color.WHITE);
			}
			g2d.fillRect(1, 1, PLAYER_CARD_WIDTH - 2, PLAYER_CARD_HEIGHT - 2);
			g2d.setColor(Color.WHITE);
			g2d.fillRect(5, 20, PLAYER_CARD_WIDTH - 10, PLAYER_CARD_HEIGHT - 25);

			g.drawImage(s_playerCardImage, 6, 21, null);
			
			g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
			if (d_card != null) {
				g2d.drawString(d_card.getCity().getName(), 4, 12);
			}
		}
	}
	
	public void mouseClicked(MouseEvent e) {

		if (d_singleSelectEnabled) {
			d_game.cardSelected(d_card);
		}
		else if (d_multipleSelectEnabled) {
			
			if (!d_selected) {
				d_selected = true;
				this.setLocation(this.getLocation().x, this.getLocation().y - 10);
				this.repaint();
			}
			else {
				d_selected = false; 
				this.setLocation(this.getLocation().x, this.getLocation().y + 10);
				this.repaint();
			}
		}
	}
	
	public void mousePressed(MouseEvent e) { }

	public void mouseReleased(MouseEvent e) { }

	public void mouseEntered(MouseEvent e) { }

	public void mouseExited(MouseEvent e) { }
}
