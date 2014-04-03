
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import static constants.PandemicConstants.*;

public class CityPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = 4006723571078577617L;

	// Data members
	private PandemicGame d_game;
	private City d_city;
	private boolean d_enabled;
	private boolean d_hover;
	
	// Basic accessor methods
	public City getCity() { return d_city; }

	// Basic modifier methods
	public void enableSelection() { d_enabled = true; }
	public void disableSelection() { d_enabled = false; }

	public CityPanel(PandemicGame game, City city) {

		d_game = game;
		d_city = city;
		d_enabled = false;
		d_hover = false;

		setBackground(new Color(0, 0, 0, 0));
		setLocation(d_city.getX() - CITY_DOT_RADIUS - 2, d_city.getY() - CITY_DOT_RADIUS - 2);
		setSize(2 * (CITY_DOT_RADIUS + 2), 2 * (CITY_DOT_RADIUS + 2));
		addMouseListener(this);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D)g;

		// Render border
		g2d.setColor(d_city.color());
		g2d.fillOval(0,
				 	 0,
				 	 (CITY_DOT_RADIUS + 2) * 2,
				 	 (CITY_DOT_RADIUS + 2) * 2);

		// Render city dot
		if (d_hover) {
			g2d.setColor(CITY_SELECT_COLOR);
		}
		else if (d_city.hasResearchStation()) {
			g2d.setColor(CITY_RESEARCH_STATION_COLOR);
		}
		else {
			g2d.setColor(CITY_DEFAULT_COLOR);
		}
		g2d.fillOval(2,
					 2,
					 CITY_DOT_RADIUS * 2,
					 CITY_DOT_RADIUS * 2);
		
		// Render infection
		g2d.setColor(d_city.color());
		for (int i = 0; i < 3; i++) {
			if (d_city.getInfectLevel() > i) {
				g2d.fillRect(2 + CITY_DOT_RADIUS / 2,
							 1 + 3 * CITY_DOT_RADIUS / 2 - 3 * i,
							 CITY_DOT_RADIUS + 1,
							 2);
			}
		}
	}

	public void mouseClicked(MouseEvent e) { 
		
		if (d_enabled) {
			d_game.citySelected(d_city);
			d_hover = false;
		}
	}

	public void mousePressed(MouseEvent e) { }

	public void mouseReleased(MouseEvent e) { }

	public void mouseEntered(MouseEvent e) {

		if (d_enabled) {
			d_hover = true;
			d_game.getMapPanel().repaint();
		}
	}

	public void mouseExited(MouseEvent e) {

		if (d_enabled) {
			d_hover = false;
			d_game.getMapPanel().repaint();
		}
	}

}
