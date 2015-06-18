package view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import control.PullRequests;

public class RetrievePullRequest implements ActionListener{
	private JPanel jPanel;
	private JFrame jFrame;
	private JLabel jLabelRepo;
	private JTextField jFilePath;
	private JTextField jTextRepo;
//	private JTextField jTextFile;
	private JButton jButtonSave;
	private JButton jButtonStop;
	private JButton jButtonFile;
	private JLabel jLabelPulls;
	
	RetrievePullRequest() throws UnknownHostException{
		jFrame = new JFrame("Retrieve Pull Requests from MongoDB");
		jFrame.setBounds(100, 200, 300, 400);
		jPanel = new JPanel();
		GridLayout grid = new GridLayout(0,2);
		jPanel.setLayout(grid);
		jFrame.add(jPanel);
		jLabelRepo = new JLabel("Nome do repositório:");
		jTextRepo = new JTextField(10);
		jButtonFile = new JButton("File...");
		jButtonSave = new JButton("Save");
		jPanel.add(jLabelRepo);
		jPanel.add(jTextRepo);

		jButtonFile.addActionListener(this);
		jPanel.add(jButtonFile);
		
		jButtonSave.addActionListener(this);
		jPanel.add(jButtonSave);
		
		jFilePath = new JTextField(10);
		jFilePath.setEditable(false);
		jPanel.add(jFilePath);
		jLabelPulls = new JLabel("");
		jPanel.add(jLabelPulls);
		
		jFrame.pack();
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLocationRelativeTo(null);
	}
	public void actionPerformed(ActionEvent evt){
		String file="";
		if(evt.getSource()==jButtonSave){
			String repo = jTextRepo.getText();
			PullRequests pull = new PullRequests();
			try {
				file = jFilePath.getText();
				pull.saveFile(repo, file);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		
		if(evt.getSource()==jButtonFile){
			JFileChooser fileChooser = new JFileChooser("D:\\files"); 
	        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	        int i= fileChooser.showSaveDialog(null);
	        if (i==1){
	        	jFilePath.setText("");
	        	return;
	        } else {
	        	jFilePath.setText(fileChooser.getSelectedFile().toString());
	            return;
	        }
		}
		
		if(evt.getSource()==jButtonStop){
			System.exit(0);
		}
		
		JOptionPane.showMessageDialog(jFrame, "Fim");
	}
	
	public static void main(String[] args) throws UnknownHostException {
		new RetrievePullRequest();
		
	}

}
