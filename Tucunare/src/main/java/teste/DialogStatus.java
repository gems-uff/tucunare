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
	private static JLabel jLabelRepositories;
	private static int totalRepositories;
	private static int totalPullRequests;
	private static int currentPR=0;
	private static JTextField txtOperaoFinalizada;
	private JPanel panel_1;
	private JPanel panel_2;
	private static JButton btnSair;
	private static JFrame jFrameStatic;
	private JPanel panel_3;
	private static JLabel lblPullRequests;

	@SuppressWarnings("static-access")
	public DialogStatus(JFrame frame, int totalRepositories, int totalPullRequests){
		super(frame);
		jDialogStatic = this;
		jFrameStatic = frame;
		this.pack();
		this.totalRepositories = totalRepositories;
		this.totalPullRequests = totalPullRequests;
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		getContentPane().add(panel);
		jLabelRepositories = new JLabel("Repositórios... (0 de "+totalRepositories+")");
		panel.add(jLabelRepositories);
		
		panel_3 = new JPanel();
		panel.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		lblPullRequests = new JLabel("Pull Requests: 0 de "+totalPullRequests);
		lblPullRequests.setHorizontalAlignment(SwingConstants.CENTER);
		panel_3.add(lblPullRequests, BorderLayout.NORTH);
		jProgressBar = new JProgressBar();
		panel_3.add(jProgressBar);
		jProgressBar.setMaximum(0);
		jProgressBar.setMaximum(100);
		jProgressBar.setStringPainted(true);//Faz aparecer o valor em porcentagem  
		jProgressBar.setValue(0);
		//jProgressBar.setIndeterminate(true);

		panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));

		txtOperaoFinalizada = new JTextField("Processando dados.");
		panel_1.add(txtOperaoFinalizada);
		txtOperaoFinalizada.setHorizontalAlignment(SwingConstants.CENTER);
		txtOperaoFinalizada.setEditable(false);
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
		jDialogStatic.setTitle("Processamento de Repositórios.");
		this.pack();
	}

	public static void setThreads(int atual){
		if (atual == totalRepositories){
			//jProgressBar.setVisible(false);
			txtOperaoFinalizada.setText("Operação finalizada.");
			jLabelRepositories.setText("Repositórios processados: "+atual+" de "+totalRepositories+".");
			txtOperaoFinalizada.setVisible(true);
			btnSair.setVisible(true);
			System.out.println("Tempo em segundos do fim da recuperação dos dados: "+SaveFile.getTempo());
			try {
				Connect.getInstance().close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.err.println("erro ao tentar finalizar a conexão com o banco de dados.");
			}
			jDialogStatic.setLocationRelativeTo(jFrameStatic);
			jDialogStatic.pack();
			
		}else{
			jDialogStatic.pack();
			jLabelRepositories.setText("Repositório(s)... ("+atual+" de "+totalRepositories+")");
			jDialogStatic.setLocationRelativeTo(jFrameStatic);
						
		}

	}

	public static void addsPullRequests(){
		currentPR++;
		lblPullRequests.setText("Pull Requests: "+currentPR+" de "+totalPullRequests);
		System.out.println("TotalPR: "+totalPullRequests);
		int x = (100*currentPR)/totalPullRequests;
		
		jProgressBar.setValue(x);
		jDialogStatic.setLocationRelativeTo(jFrameStatic);		
		jDialogStatic.pack();
	}

	public static void setTotalRepositories(int totalRepositories) {
		DialogStatus.totalRepositories = totalRepositories;
	}

	public static void setTotalPullRequests(int totalPullRequests) {
		DialogStatus.totalPullRequests = totalPullRequests;
	}

	public static void setCurrentPR(int currentPR) {
		DialogStatus.currentPR = currentPR;
	}
}
