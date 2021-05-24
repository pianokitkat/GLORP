package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class TitlePanel extends JPanel{
	private final static int WIDTH = 285;
	private final static int HEIGHT = 90;
	private JLabel myTitleLabel;
	private GameIcon myTitleIcon = new GameIcon("src/icons/title.png");
	
	public TitlePanel() {
		super();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.LIGHT_GRAY);
		
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		setBackground(Color.darkGray);
		myTitleIcon.resize(300, 150);
		myTitleLabel = new JLabel(myTitleIcon);
		add(myTitleLabel, BorderLayout.CENTER);
	}
	
	@Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    }

}
