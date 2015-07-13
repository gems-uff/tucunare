package teste;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import control.SaveFile;
import util.Connect;

public class DialogStatus extends JDialog{
	private static final long serialVersionUID = 6446402150733028773L;
	
	private static JProgressBar jProgressBar;
	private static JLabel jLabel;
	private static int max;
	private static JTextField txtOperaoFinalizada;
	private static JDialog dialogStatic;
	private JPanel panel_1;
	private JPanel panel_2;
	private static JButton btnSair;

	@SuppressWarnings("static-access")
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
		if (atual == max){
			jProgressBar.setVisible(false);
			jLabel.setText("Concluído. ("+atual+" de "+max+")");
			txtOperaoFinalizada.setVisible(true);
			btnSair.setVisible(true);
			System.out.println("Tempo em segundos do fim da recuperação dos dados: "+SaveFile.tempo);
			try {
				Connect.getInstance().close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.err.println("erro ao tentar finalizar a conexão com o banco de dados.");
			}
		}
		else
			jLabel.setText("Carregando... ("+atual+" de "+max+")");
		toPack();
	}
}
