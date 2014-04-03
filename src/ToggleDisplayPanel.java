import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

class ToggleDisplayPanel extends JPanel implements MouseListener {
	
	private static final long serialVersionUID = 7536138891723673370L;

	// Data members
	private PandemicGame d_game;
	boolean d_active;
	private Player d_player;
	private BufferedImage d_iconImage;
	private Color d_mouseEnterColor;
	private Color d_mouseExitColor;
	
	// Basic modifier methods
	public void enableToggling() { d_active = true; }
	public void disableToggling() { d_active = false; }

	public ToggleDisplayPanel(PandemicGame game, Player player, String path) {

		d_game = game;
		d_active = true;
		d_player = player;
		
		if (path != null) {
			try {
				d_iconImage = ImageIO.read(new File(path));
			}
			catch (IOException e) {

				// We cannot recover from an undefined icon file
				System.out.println("File Error: " + path + " could not be opened.");
				System.exit(1);
			}
		}
		else {
			d_iconImage = null;
		}

		d_mouseEnterColor = Color.WHITE;
		if (d_player != null) {
			d_mouseExitColor = d_player.color();
		}
		else {
			d_mouseExitColor = Color.GRAY;
		}
		setBackground(d_mouseExitColor);

		addMouseListener(this);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (d_iconImage != null) {
			g.drawImage(d_iconImage, 2, 2, null);
		}
	}
		
	public void mouseClicked(MouseEvent e) {

		if (d_active) {
			if (d_player != null) {
				d_game.toggleHandDisplay(d_player);
			}
			else {
				d_game.toggleDeckDisplay();
			}
		}
	}

	public void mousePressed(MouseEvent e) { }

	public void mouseReleased(MouseEvent e) { }

	public void mouseEntered(MouseEvent e) { 
		
		setBackground(d_mouseEnterColor);
	}

	public void mouseExited(MouseEvent e) {

		setBackground(d_mouseExitColor);
	}

}
