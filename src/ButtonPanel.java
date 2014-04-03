import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import static constants.PandemicConstants.*;

class ButtonPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = -4111806977444452644L;
	
	public enum ButtonType { ACTION_DRIVE,
							 ACTION_DIRECTFLIGHT,
							 ACTION_CHARTERFLIGHT,
							 ACTION_SHUTTLEFLIGHT,
							 ACTION_TREATDISEASE,
							 ACTION_SHAREKNOWLEDGE,
							 ACTION_BUILDSTATION,
							 ACTION_DISCOVERCURE,
							 ACTION_PASS,
							 CARDS_SELECTED,
							 CANCEL_ACTION,
							 HELP,
							 PLAYER_1,
							 PLAYER_2,
							 PLAYER_3,
							 PLAYER_4,
							}

	private PandemicGame d_game;
	private ButtonType d_type;
	private boolean d_active;
	private Color d_mouseEnterColor;
	private Color d_mouseExitColor;
	
	public ButtonPanel(PandemicGame game, ButtonType type, String text) {

		d_game = game;
		d_type = type;
		JLabel label = new JLabel(text); 

		switch (d_type) {
		case ACTION_DRIVE:
		case ACTION_DIRECTFLIGHT:
		case ACTION_CHARTERFLIGHT:
		case ACTION_SHUTTLEFLIGHT:
		case ACTION_TREATDISEASE:
		case ACTION_SHAREKNOWLEDGE:
		case ACTION_BUILDSTATION:
		case ACTION_DISCOVERCURE:
		case ACTION_PASS:
		case HELP:
			label.setForeground(ACTION_BUTTON_FONT_COLOR);
			break;
		case CARDS_SELECTED:
		case CANCEL_ACTION:
			label.setForeground(SPECIAL_BUTTON_FONT_COLOR);
			break;
		case PLAYER_1:
		case PLAYER_2:
		case PLAYER_3:
		case PLAYER_4:
			// No text
			break;
		}

		add(label);
		addMouseListener(this);
		activate();
	}
	
	public void activate() {

		d_active = true;
		
		switch (d_type) {
		case ACTION_DRIVE:
		case ACTION_DIRECTFLIGHT:
		case ACTION_CHARTERFLIGHT:
		case ACTION_SHUTTLEFLIGHT:
		case ACTION_TREATDISEASE:
		case ACTION_SHAREKNOWLEDGE:
		case ACTION_BUILDSTATION:
		case ACTION_DISCOVERCURE:
		case ACTION_PASS:
		case HELP:
			d_mouseEnterColor = ACTION_BUTTON_MOUSE_ENTER_COLOR;
			d_mouseExitColor = ACTION_BUTTON_MOUSE_EXIT_COLOR;
			break;
		case CARDS_SELECTED:
		case CANCEL_ACTION:
			d_mouseEnterColor = SPECIAL_BUTTON_MOUSE_ENTER_COLOR;
			d_mouseExitColor = SPECIAL_BUTTON_MOUSE_EXIT_COLOR;
			break;
		case PLAYER_1:
			d_mouseEnterColor = Color.WHITE;
			d_mouseExitColor = PLAYER_COLOR[0];
			break;
		case PLAYER_2:
			d_mouseEnterColor = Color.WHITE;
			d_mouseExitColor = PLAYER_COLOR[1];
			break;
		case PLAYER_3:
			d_mouseEnterColor = Color.WHITE;
			d_mouseExitColor = PLAYER_COLOR[2];
			break;
		case PLAYER_4:
			d_mouseEnterColor = Color.WHITE;
			d_mouseExitColor = PLAYER_COLOR[3];
			break;
		}
		
		setBackground(d_mouseExitColor);
	}
	
	public void deactivate() {
		
		d_active = false;
		setBackground(BUTTON_DISABLED_COLOR);
	}
	
	public void mousePressed(MouseEvent e) { }

	public void mouseReleased(MouseEvent e) { }

	public void mouseEntered(MouseEvent e) {

		if (d_active) {
			setBackground(d_mouseEnterColor);
			d_game.describeActionButton(d_type);
		}
	}

	public void mouseExited(MouseEvent e) {

		if (d_active) {
			setBackground(d_mouseExitColor);
		}
	}

	public void mouseClicked(MouseEvent e) {
		
		if (d_active) {
			d_game.buttonSelected(d_type);
		}
	}
}
