import java.util.ArrayList;

import javax.swing.JPanel;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;

import static constants.PandemicConstants.*;

public class MapPanel extends JPanel {

	private static final long serialVersionUID = -3427209514413358607L;
	
	// Data members
	private BufferedImage d_bufferedMap;
	private ArrayList<Player> d_playerList;
	private ArrayList<City> d_cityList;
	private ArrayList<CityPanel> d_cityPanelList;
	
	public MapPanel(PandemicGame game, ArrayList<City> cityList, ArrayList<Player> playerList) {
		
		try {
			d_bufferedMap = ImageIO.read(new File(WORLD_MAP_PATH));
		}
		catch (IOException e) {

			// We cannot recover from an undefined map file
			System.out.println("File Error: " + WORLD_MAP_PATH + " could not be opened.");
			System.exit(1);
		}
		
		setLayout(null);

		d_cityList = cityList;
		d_playerList = playerList;

		d_cityPanelList = new ArrayList<CityPanel>();
		for (int i = 0; i < cityList.size(); i++) {
			CityPanel cityPanel = new CityPanel(game, cityList.get(i));
			d_cityPanelList.add(cityPanel);
			this.add(cityPanel);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(d_bufferedMap, 0, 0, null);

		Graphics2D g2d = (Graphics2D)g;
		
		// Draw web of links first
		g2d.setColor(Color.WHITE);
		for (int i = 0; i < d_cityList.size(); i++) {
			City city = d_cityList.get(i);
			for (int j = 0; j < city.getNeighbors().size(); j++) {
				drawConnection(city, city.getNeighbors().get(j), g2d);
			}
		}

		// Draw the players next so they render over top of the connection web
		for (int i = 0; i < d_playerList.size(); i++) {
			Player player = d_playerList.get(i);
			drawPlayer(player, g2d);
		}

		// Write the city names next so they render over top of the web and players
		g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		for (int i = 0; i < d_cityList.size(); i++) {
			City city = d_cityList.get(i);
			g2d.setColor(city.color());
			g2d.drawString(city.getName(), city.getX(), city.getY() + CITY_NAME_OFFSET);
		}
		
		// The actual cities are rendered on top of everything because they have been added
		// to theMapPanel as separate individual CityPanels
	}
	
	private void drawPlayer(Player player, Graphics2D g2d) {
		
		int x = player.getCity().getX();
		int y = player.getCity().getY();
		
		switch (player.getIndex()) {
		
		case 0:
			// Draw player 0 in the upper left quadrant of the city
			g2d.setColor(Color.BLACK);
			g2d.fillRect(x - PLAYER_SIZE - 1, y - PLAYER_SIZE - 1, PLAYER_SIZE + 2, PLAYER_SIZE + 2);
			g2d.setColor(player.color());
			g2d.fillRect(x - PLAYER_SIZE, y - PLAYER_SIZE, PLAYER_SIZE, PLAYER_SIZE);
			break;
		case 1:
			// Draw player 1 in the upper right quadrant of the city
			g2d.setColor(Color.BLACK);
			g2d.fillRect(x, y - PLAYER_SIZE - 1, PLAYER_SIZE + 2, PLAYER_SIZE + 2);
			g2d.setColor(player.color());
			g2d.fillRect(x + 1, y - PLAYER_SIZE, PLAYER_SIZE, PLAYER_SIZE);
			break;
		case 2:
			// Draw player 2 in the lower left quadrant of the city
			g2d.setColor(Color.BLACK);
			g2d.fillRect(x - PLAYER_SIZE - 1, y, PLAYER_SIZE + 2, PLAYER_SIZE + 2);
			g2d.setColor(player.color());
			g2d.fillRect(x - PLAYER_SIZE, y + 1, PLAYER_SIZE, PLAYER_SIZE);
			break;
		case 3:
			// Draw player 3 in the lower right quadrant of the city
			g2d.setColor(Color.BLACK);
			g2d.fillRect(x, y, PLAYER_SIZE + 2, PLAYER_SIZE + 2);
			g2d.setColor(player.color());
			g2d.fillRect(x + 1, y + 1, PLAYER_SIZE, PLAYER_SIZE);
			break;
		}
	}
	
	private void drawConnection(City city1, City city2, Graphics2D g2d) {

		int x1 = city1.getX();
		int y1 = city1.getY();
		int x2 = city2.getX();
		int y2 = city2.getY();

		// Normally, we would unnecessarily draw each connection twice because each connection is registered for
		// both cities that it links, and this method is called from each city's perspective. We choose to ignore
		// the half of the calls where city1 is east of city2 (x1 > x2) because we actually need x2 to be > x1 in
		// the 'wrap around' case below.
		if (x1 > x2)
			return;
		
		// If the cities are on opposite ends of the map, draw a 'wrap around' line in two segments that go
		// off the edges of the map
		if (x2 - x1 > MAP_WIDTH / 2) {

			g2d.drawLine(x1, y1, x2 - MAP_WIDTH, y2);
			g2d.drawLine(x1 + MAP_WIDTH, y1, x2, y2);
		}
		else {	// Otherwise, just draw the standard interpolating line
			g2d.drawLine(x1, y1, x2, y2);
		}
	}
	
	public void enableCitySelection(City city) {
	
		int cityIndex = city.getIndex();
		d_cityPanelList.get(cityIndex).enableSelection();			
	}

	public void disableCitySelection() {
		
		for (int i = 0; i < d_cityPanelList.size(); i++) {
			d_cityPanelList.get(i).disableSelection();			
		}
	}
}
