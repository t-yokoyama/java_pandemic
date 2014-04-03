import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.JPanel;

import static constants.PandemicConstants.*;

class DeckDisplayPanel extends JPanel {
	
	private static final long serialVersionUID = -8378824194416255266L;

	private InfectionCardPanel [] d_discardPilePanels;
	private ArrayList<PlayerCard> d_playerDeck;
	private ArrayList<City> d_infectionDeck;
	private ArrayList<City> d_discardPile;
	
	public DeckDisplayPanel(ArrayList<PlayerCard> playerDeck, ArrayList<City> infectionDeck, ArrayList<City> discardPile) {
		
		d_playerDeck = playerDeck;
		d_infectionDeck = infectionDeck;
		d_discardPile = discardPile;
		setLayout(null);
		setBackground(new Color(255, 255, 255, 50));

		PlayerCardPanel playerDeckPanel = new PlayerCardPanel(null);
		playerDeckPanel.setLocation(40, 20);
		add(playerDeckPanel);

		InfectionCardPanel infectDeckPanel = new InfectionCardPanel(true);
		infectDeckPanel.setLocation(160, 20);
		add(infectDeckPanel);

		d_discardPilePanels = new InfectionCardPanel[MAX_INFECT_DISCARD_PILE];
		for (int i = 0; i < MAX_INFECT_DISCARD_PILE; i++) {
			d_discardPilePanels[i] = new InfectionCardPanel(true);
			d_discardPilePanels[i].setLocation(280 + (i * 25), 20);
			add(d_discardPilePanels[i]);
		}
		
		updateDiscardPileDisplay();
	}
	
	public void updateDiscardPileDisplay() {
		
		int discardPileSize = d_discardPile.size();
		for (int i = 0; i < discardPileSize; i++) {
			City city = d_discardPile.get(i);
			d_discardPilePanels[i].setCity(city);
			d_discardPilePanels[i].setVisible(true);
		}
		for (int i = discardPileSize; i < MAX_INFECT_DISCARD_PILE; i++) {
			d_discardPilePanels[i].setVisible(false);
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D)g;

		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));

		g2d.drawString(Integer.toString(d_playerDeck.size()) + " cards left", 40, 20 + PLAYER_CARD_HEIGHT + 10);
		g2d.drawString(Integer.toString(d_infectionDeck.size()) + " cards left", 160, 20 + INFECT_CARD_WIDTH + 10);
		g2d.drawString(Integer.toString(d_discardPile.size()) + " cards", 280, 20 + INFECT_CARD_WIDTH + 10);
		
		AffineTransform origRotation = g2d.getTransform();
		g2d.rotate(-Math.PI/2);
		g2d.drawString("Player Deck", -105, 35);
		g2d.drawString("Infection Deck", -105, 155);
		g2d.drawString("Infection Discard", -105, 275);
		g2d.setTransform(origRotation);
	}

}	
