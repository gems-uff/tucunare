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
import javax.swing.JOptionPane;
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
	private static JButton btnVisualizar;
	private static JButton btnCancelar;

	@SuppressWarnings("static-access")
	public DialogStatus(JFrame frame, int totalRepositories, int totalPullRequests){
		super(frame);
		jDialogStatic = this;
		jFrameStatic = frame;
		this.setSize(250, 130);
		this.totalRepositories = totalRepositories;
		this.totalPullRequests = totalPullRequests;
		getContentPane().setLayout(new BorderLayout(0, 0));

		panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));

		txtOperaoFinalizada = new JTextField("Concluídos: 0 de "+totalRepositories);
		panel_1.add(txtOperaoFinalizada, BorderLayout.NORTH);
		txtOperaoFinalizada.setHorizontalAlignment(SwingConstants.CENTER);
		txtOperaoFinalizada.setEditable(false);
		txtOperaoFinalizada.setColumns(10);
		
		panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.CENTER);
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
		
		btnVisualizar = new JButton("Visualizar arquivo");
		btnVisualizar.setVisible(false);
		
		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (arg0.getSource() == btnCancelar){
					int i = JOptionPane.showConfirmDialog(null, "Você realmente deseja cancelar o processamento?", "Atenção:", JOptionPane.YES_NO_OPTION);
					if (i!=1){
						SaveFile.setCancelProcessing();
						dispose();
					}
				}
			}
		});
		panel_2.add(btnCancelar);
		panel_2.add(btnVisualizar);
		panel_2.add(btnSair);
		jDialogStatic.setTitle("Processamento de Repositórios.");
	}

	public static void setThreads(int atual){
		if (atual == totalRepositories){
			txtOperaoFinalizada.setText("Concluídos: "+ atual +" de "+totalRepositories);
			btnVisualizar.setVisible(true);
			btnSair.setVisible(true);
			btnCancelar.setVisible(false);
			try {
				Connect.getInstance().close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.err.println("erro ao tentar finalizar a conexão com o banco de dados.");
			}
			jDialogStatic.setLocationRelativeTo(jFrameStatic);
			
		}else{
			txtOperaoFinalizada.setText("Concluídos: "+ atual +" de "+totalRepositories);
			jDialogStatic.setLocationRelativeTo(jFrameStatic);				
		}
	}

	public static void addsPullRequests(){
		currentPR++;
		lblPullRequests.setText("Pull Requests: "+currentPR+" de "+totalPullRequests);
		int x = (100*currentPR)/totalPullRequests;
		
		jProgressBar.setValue(x);
		jDialogStatic.setLocationRelativeTo(jFrameStatic);		
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
