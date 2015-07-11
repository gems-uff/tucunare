package teste;

import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DialogStatus extends JDialog{
	private JProgressBar jProgressBar;
	private static JLabel jLabel;
	private static int max;
	private static JTextField txtOperaoFinalizada;
	private static JFrame frameStatic;
	private static JDialog dialogStatic;
	private JPanel panel_1;
	private JPanel panel_2;
	private static JButton btnSair;
	
	public DialogStatus(JFrame frame, int max){
		super(frame);
		dialogStatic = this;
		this.max = max;
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		jLabel = new JLabel("Carregando... (0 de "+max+")");
		panel.add(jLabel);
		jProgressBar = new JProgressBar();
		panel.add(jProgressBar);
		jProgressBar.setIndeterminate(true);
		
		panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		txtOperaoFinalizada = new JTextField();
		panel_1.add(txtOperaoFinalizada);
		txtOperaoFinalizada.setHorizontalAlignment(SwingConstants.CENTER);
		txtOperaoFinalizada.setText("Operação finalizada.");
		txtOperaoFinalizada.setEditable(false);
		txtOperaoFinalizada.setVisible(false);
		txtOperaoFinalizada.setColumns(10);
		
		panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnSair = new JButton("Sair");
		btnSair.setVisible(false);
		btnSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		panel_2.add(btnSair);
		this.pack();
	}
	
	public static void toPack(){
		dialogStatic.pack();
	}
	
	public static void setjLabel(int atual){
		System.out.println("DialogStatus - setjLabel");
		jLabel.setText("Carregando... ("+atual+" de "+max+")");
		txtOperaoFinalizada.setVisible(true);
		btnSair.setVisible(true);
		toPack();
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		DialogStatus window = new DialogStatus(frame,1);
		window.show();
	}
}
