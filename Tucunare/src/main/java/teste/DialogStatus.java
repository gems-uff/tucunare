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

public class DialogStatus extends JDialog {
	private static final long serialVersionUID = 6446402150733028773L;

	private static JDialog jDialogStatic;
	private static JProgressBar jProgressBar;
	private static JLabel jLabel;
	private static int totalRepositories;
	private static int totalPullRequests;
	private static int currentPR;
	private static JTextField txtOperaoFinalizada;
	private JPanel panel_1;
	private JPanel panel_2;
	private static JButton btnSair;
	private static JFrame jFrameStatic;

	@SuppressWarnings("static-access")
	public DialogStatus(JFrame frame, int totalRepositories, int totalPullRequests){
		super(frame);
		jDialogStatic = this;
		jFrameStatic = frame;
		this.totalRepositories = totalRepositories;
		this.totalPullRequests = totalPullRequests;
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		getContentPane().add(panel);
		jLabel = new JLabel("Carregando... (0 de "+totalRepositories+")");
		panel.add(jLabel);
		jProgressBar = new JProgressBar();
		jProgressBar.setMaximum(0);
		jProgressBar.setMaximum(100);
		jProgressBar.setStringPainted(true);//Faz aparecer o valor em porcentagem  
		jProgressBar.setValue(0);
		panel.add(jProgressBar);
		//jProgressBar.setIndeterminate(true);

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

	public static void setThreads(int atual){
		if (atual == totalRepositories){
			jProgressBar.setVisible(false);
			jLabel.setText("Concluído. ("+atual+" de "+totalRepositories+")");
			txtOperaoFinalizada.setVisible(true);
			btnSair.setVisible(true);
			System.out.println("Tempo em segundos do fim da recuperação dos dados: "+SaveFile.tempo);
			try {
				Connect.getInstance().close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.err.println("erro ao tentar finalizar a conexão com o banco de dados.");
			}
			jDialogStatic.setLocationRelativeTo(jFrameStatic);		
			jDialogStatic.pack();
		}else
			jLabel.setText("Carregando... ("+atual+" de "+totalRepositories+")");

	}

	public static void addsPullRequests(){
		currentPR++;
		int x = (100*currentPR)/totalPullRequests;
		jProgressBar.setValue(x);
		jDialogStatic.setLocationRelativeTo(jFrameStatic);		
		jDialogStatic.pack();
	}
}
