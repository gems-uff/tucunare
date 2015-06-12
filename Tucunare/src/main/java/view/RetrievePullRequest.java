package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import control.PullRequests;

public class RetrievePullRequest implements ActionListener{
	private JPanel jPanel;
	private JFrame jFrame;
	private JLabel jLabel;
	private JTextField jText;
	private JButton jButton;
	RetrievePullRequest(){
		jFrame = new JFrame("Retrieve Pull Requests from MongoDB");
		jFrame.setBounds(100, 200, 300, 400);
		jPanel = new JPanel();
		jFrame.add(jPanel);
		jLabel = new JLabel("Nome do repositório");
		jText = new JTextField(20);
		jButton = new JButton("Salvar");
		jButton.addActionListener(this);
		jPanel.add(jLabel);
		jPanel.add(jText);
		jPanel.add(jButton);
		jFrame.pack();
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	public void actionPerformed(ActionEvent evt){
		String repo = jText.getText();
		PullRequests pull = new PullRequests();
		try {
			pull.saveFile(repo);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(jFrame, "Fim");
	}
	
	public static void main(String[] args) throws UnknownHostException {
		new RetrievePullRequest();
		
	}

}
