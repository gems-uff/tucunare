package view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

public class LoadingWindow{

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		new LoadingWindow();

	}

	public LoadingWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Counting Pull Requests");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JProgressBar pb = new JProgressBar();
		pb.setIndeterminate(true);
		
		JLabel lb = new JLabel("Counting pull requests...");
		lb.setHorizontalAlignment(SwingConstants.CENTER);
		
		frame.setUndecorated(true);
		
		BorderLayout bl_panel = new BorderLayout();
		JPanel panel = new JPanel(bl_panel);
		panel.setBorder(new LineBorder(new Color(102, 102, 102)));
		panel.add(lb, BorderLayout.NORTH);
		panel.add(pb, BorderLayout.CENTER);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);

	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

}
