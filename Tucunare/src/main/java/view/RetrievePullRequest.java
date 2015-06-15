package view;

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
//	private JLabel jLabelFile;
	private JTextField jFilePath;
	private JTextField jTextRepo;
//	private JTextField jTextFile;
	private JButton jButtonSave;
	private JButton jButtonStop;
	private JButton jButtonFile;
	
	RetrievePullRequest(){
		jFrame = new JFrame("Retrieve Pull Requests from MongoDB");
		jFrame.setBounds(100, 200, 300, 400);
		jPanel = new JPanel();
		jFrame.add(jPanel);
		jLabelRepo = new JLabel("Nome do repositório:");
		jTextRepo = new JTextField(20);
		
//		jLabelFile = new JLabel("File:");
//		jTextFile = new JTextField(20);
		
		jButtonFile = new JButton("File...");
		
		jButtonSave = new JButton("Save");
		
//		jButtonStop = new JButton("Exit");
		
		jPanel.add(jLabelRepo);
		jPanel.add(jTextRepo);
//		jPanel.add(jLabelFile);
//		jPanel.add(jTextFile);
		jButtonFile.addActionListener(this);
		jPanel.add(jButtonFile);
		
		jButtonSave.addActionListener(this);
		jPanel.add(jButtonSave);
		
//		jButtonStop.addActionListener(this);
//		jPanel.add(jButtonStop);
		jFilePath = new JTextField(20);
		jFilePath.setEditable(false);
		jPanel.add(jFilePath);
		jFrame.pack();
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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
