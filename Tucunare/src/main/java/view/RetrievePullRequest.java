package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import control.PullRequests;

public class RetrievePullRequest implements ActionListener, ItemListener{
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JPanel centerPanel;
	private JPanel centerPanelNorth;
	private JPanel centerPanelMid;
	private JPanel centerPanelMidIn;
	private JPanel panelContributor;
	private JPanel panelCommByFiles;
	private JPanel panelAuthorMoreCommitsPR;
	private JPanel centerPanelMidDates;
	private JPanel centerPanelBotAuthorPR;

	private JFrame jFrame;

	private JLabel jLabelRepo;
	private JLabel lbContributor;
	private JLabel lbCommByFiles;
	private JLabel lbAuthorMoreComm;

	private JTextField jFilePath;
	private JTextField jTextRepo;
	private JTextField txtAuthorMoreCommitsPR;
	private JTextField txtContributor;
	private JTextField txtCommByFiles;

	private JTextArea logArea;

	private JButton jButtonSave;
	private JButton jButtonStop;
	private JButton jButtonFile;
	private JButton jButtonSelectAll;
	private JButton jButtonDeselectAll;

	private JCheckBox cbRepoCreatedDate;
	private JCheckBox cbRepoOthers;
	private JCheckBox cbRepo;
	private JCheckBox cbOwner;
	private JCheckBox cbAgeUser;
	private JCheckBox cbUserFollowers;
	private JCheckBox cbUserFollowing;
	private JCheckBox cbWatchRepo;
	private JCheckBox cbFollowContributors;
	private JCheckBox cbUserLocation;
	private JCheckBox cbTotalPRUser;
	private JCheckBox cbAcceptanceUser;
	private JCheckBox cbCreatedAtPR;
	private JCheckBox cbClosedAtPR;
	private JCheckBox cbMergedAtPR;
	private JCheckBox cbIdPR;
	private JCheckBox cbAuthorPR;
	private JCheckBox cbNumPR;
	private JCheckBox cbContributorsPR;
	private JCheckBox cbStatePR;
	private JCheckBox cbLifetimePR;
	private JCheckBox cbClosedByPR;
	private JCheckBox cbMergedByPR;
	private JCheckBox cbShaHeadBasePR;
	private JCheckBox cbAssigneePR;
	private JCheckBox cbCommentsPR;
	private JCheckBox cbCommitsPR;
	private JCheckBox cbFilesPR;
	private JCheckBox cbCommitsByFilesPR;
	private JCheckBox cbAuthorMoreCommitsPR;
	private JCheckBox cbLinesAddedPR;
	private JCheckBox cbLinesDeletedPR;
	private JCheckBox cbChangedFilePR;
	private JCheckBox cbDeveloperType;
	private JCheckBox cbTitlePR;

	RetrievePullRequest(){
		jFrame = new JFrame("Retrieve Pull Requests from MongoDB");
		jFrame.setBounds(100, 100, 1000, 480);
		addTopPanel();
		addCenterPanel();
		addBottomPanel();
		loadComponents(true);
		jFrame.pack();
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == cbCommitsByFilesPR){
			if (cbCommitsByFilesPR.isSelected()){
				lbCommByFiles.setVisible(true);
				txtCommByFiles.setVisible(true);
			}else{
				txtCommByFiles.setVisible(false);
				lbCommByFiles.setVisible(false);
			}
		}else
			if (e.getSource() == cbContributorsPR){
				if (cbContributorsPR.isSelected()){
					lbContributor.setVisible(true);
					txtContributor.setVisible(true);
				}else{
					lbContributor.setVisible(false);
					txtContributor.setVisible(false);
				}
			}else 
				if (e.getSource() == cbAuthorMoreCommitsPR){
					if (cbAuthorMoreCommitsPR.isSelected()){
						lbAuthorMoreComm.setVisible(true);
						txtAuthorMoreCommitsPR.setVisible(true);
					}else{
						lbAuthorMoreComm.setVisible(false);
						txtAuthorMoreCommitsPR.setVisible(false);
					}
				}else
					if (e.getSource() == cbCommitsPR){
						if (cbCommitsPR.isSelected()){
							cbFilesPR.setEnabled(true);
							cbCommitsByFilesPR.setEnabled(true);
							cbAuthorMoreCommitsPR.setEnabled(true);

							cbFilesPR.setSelected(true);
							cbCommitsByFilesPR.setSelected(true); 
							cbAuthorMoreCommitsPR.setSelected(true);

						}else{
							cbFilesPR.setEnabled(false);
							cbCommitsByFilesPR.setEnabled(false);
							cbAuthorMoreCommitsPR.setEnabled(false);

							cbFilesPR.setSelected(false);
							cbCommitsByFilesPR.setSelected(false); 
							cbAuthorMoreCommitsPR.setSelected(false);
						}
					}else 
						if (e.getSource() == cbAuthorPR){
							if (cbAuthorPR.isSelected())
								loadAuthorData(true);
							else
								loadAuthorData(false);
						}
	}

	private void loadAuthorData(boolean value) {
		Component[] ls = centerPanelBotAuthorPR.getComponents();
		for (Component comp : ls) {
			if (comp instanceof JCheckBox){
				JCheckBox temp =(JCheckBox) comp; 
				temp.setSelected(value);
				temp.setEnabled(value);
			}
		}
	}
	public void actionPerformed(ActionEvent evt) {
		getSelectedFields();
		String file="";
		if(evt.getSource()==jButtonSave){
			if (jTextRepo==null || jTextRepo.equals("") || jTextRepo.getText().length()<1){
				logArea.setText("Entre com um repositório válido.");
				return;
			}
			if (jFilePath == null || jFilePath.equals("") || jFilePath.getText().length()<1){
				logArea.setText("Escolha um diretório para armazenar o arquivo.");
				return;
			}
			else{
				String repo = jTextRepo.getText();
				PullRequests pull = new PullRequests();
				file = jFilePath.getText();
				logArea.setText("Caminho: "+file+"\n"+repo);
				try {
					pull.saveFile(repo, file, getSelectedFields(), getTxtFields());
					JOptionPane.showMessageDialog(jFrame, "Fim");
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "Entre com valores válidos nos campos de dias");
				}
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
		} else 
			if (evt.getSource()==jButtonSelectAll)
				loadComponents(true);
			else
				if (evt.getSource() == jButtonDeselectAll)
					loadComponents(false);

		if(evt.getSource()==jButtonStop){
			System.exit(0);
		}
	}

	public void addTopPanel(){
		topPanel = new JPanel();

		jLabelRepo = new JLabel("Repository name:");
		jTextRepo = new JTextField(20);
		jTextRepo.setText("angular");

		jButtonFile = new JButton("File...");

		jButtonSave = new JButton("Save");

		jButtonSelectAll = new JButton("Select all");

		jButtonDeselectAll = new JButton("Deselect all");

		topPanel.add(jLabelRepo);
		topPanel.add(jTextRepo);

		jButtonFile.addActionListener(this);
		topPanel.add(jButtonSave);

		jButtonSave.addActionListener(this);
		topPanel.add(jButtonFile);

		jFilePath = new JTextField(20);
		jFilePath.setEditable(false);
		topPanel.add(jFilePath);

		jButtonSelectAll.addActionListener(this);
		topPanel.add(jButtonSelectAll);

		jButtonDeselectAll.addActionListener(this);
		topPanel.add(jButtonDeselectAll);



		jFrame.add(topPanel, BorderLayout.NORTH);

	}

	public void addCenterPanel(){
		centerPanel = new JPanel(new BorderLayout()); 
		centerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Selecione os dados a serem recuperados pela ferramenta: ", 
				TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));

		//Painel de dados do RepositÃ³rio
		centerPanelNorth = new JPanel(new GridLayout(1,4));
		centerPanelNorth.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Repository: ", 
				TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));
		cbRepo = new JCheckBox("repository");
		cbRepoCreatedDate = new JCheckBox("created date");
		cbRepoOthers = new JCheckBox("others");
		cbOwner = new JCheckBox("owner");

		centerPanelNorth.add(cbRepo);
		centerPanelNorth.add(cbOwner);
		centerPanelNorth.add(cbRepoCreatedDate);
		centerPanelNorth.add(cbRepoOthers);
		centerPanelNorth.add(new JLabel());

		//Painel de dados do Pull Request
		centerPanelMid = new JPanel(new BorderLayout());
		centerPanelMid.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pull Request: ", 
				TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));
		centerPanelMidIn = new JPanel(new GridLayout(4,5));

		cbIdPR = new JCheckBox("id");
		cbAuthorPR = new JCheckBox("author");
		cbAuthorPR.addItemListener(this);
		cbNumPR = new JCheckBox("number");
		cbContributorsPR = new JCheckBox("contributors");
		cbContributorsPR.addItemListener(this);

		cbStatePR = new JCheckBox("state");
		cbLifetimePR = new JCheckBox("lifetime");
		cbClosedByPR = new JCheckBox("closed by");
		cbMergedByPR = new JCheckBox("merged by");
		cbShaHeadBasePR = new JCheckBox("sha (head e base)");
		cbAssigneePR = new JCheckBox("assignee");
		cbCommentsPR = new JCheckBox("comments");
		cbCommitsPR = new JCheckBox("commits");
		cbCommitsPR.addItemListener(this);
		cbFilesPR = new JCheckBox("files");
		cbCommitsByFilesPR = new JCheckBox("commits by files");
		cbCommitsByFilesPR.addItemListener(this);
		cbAuthorMoreCommitsPR = new JCheckBox("author more commits");
		cbAuthorMoreCommitsPR.addItemListener(this);
		cbLinesAddedPR = new JCheckBox("lines added");
		cbLinesDeletedPR = new JCheckBox("deleted lines");
		cbChangedFilePR = new JCheckBox("changed files");
		cbTitlePR = new JCheckBox("title");

		centerPanelMidIn.add(cbIdPR);
		centerPanelMidIn.add(cbAuthorPR);
		centerPanelMidIn.add(cbNumPR);

		panelContributor = new JPanel(new BorderLayout());
		txtContributor = new JTextField("10");
		lbContributor = new JLabel(" Dia(s):  ");

		panelContributor.add(cbContributorsPR, BorderLayout.NORTH);
		panelContributor.add(lbContributor, BorderLayout.WEST);
		panelContributor.add(txtContributor);

		centerPanelMidIn.add(panelContributor);
		centerPanelMidIn.add(cbStatePR);
		centerPanelMidIn.add(cbLifetimePR);
		centerPanelMidIn.add(cbClosedByPR);
		centerPanelMidIn.add(cbMergedByPR);
		centerPanelMidIn.add(cbShaHeadBasePR);
		centerPanelMidIn.add(cbAssigneePR);
		centerPanelMidIn.add(cbCommentsPR);
		centerPanelMidIn.add(cbCommitsPR);
		centerPanelMidIn.add(cbFilesPR);

		panelCommByFiles = new JPanel(new BorderLayout());
		txtCommByFiles = new JTextField("10");
		lbCommByFiles = new JLabel(" Dia(s):  ");
		panelCommByFiles.add(cbCommitsByFilesPR, BorderLayout.NORTH);
		panelCommByFiles.add(lbCommByFiles, BorderLayout.WEST);
		panelCommByFiles.add(txtCommByFiles);

		centerPanelMidIn.add(panelCommByFiles);

		panelAuthorMoreCommitsPR = new JPanel(new BorderLayout());
		txtAuthorMoreCommitsPR = new JTextField("10");
		lbAuthorMoreComm = new JLabel(" Dia(s):  ");

		panelAuthorMoreCommitsPR.add(cbAuthorMoreCommitsPR, BorderLayout.NORTH);
		panelAuthorMoreCommitsPR.add(lbAuthorMoreComm, BorderLayout.WEST);
		panelAuthorMoreCommitsPR.add(txtAuthorMoreCommitsPR);

		centerPanelMidIn.add(panelAuthorMoreCommitsPR);
		centerPanelMidIn.add(cbLinesAddedPR);
		centerPanelMidIn.add(cbLinesDeletedPR);
		centerPanelMidIn.add(cbChangedFilePR);
		centerPanelMidIn.add(cbTitlePR);


		TitledBorder title = BorderFactory.createTitledBorder( BorderFactory.createEmptyBorder(), "PR dates: ");
		//TitledBorder title = BorderFactory.createTitledBorder( null, null);
		//title.setTitlePosition(TitledBorder.TOP);

		centerPanelMidDates = new JPanel(new GridLayout());
		centerPanelMidDates.setBorder(title);

		//JLabel labelDates = new JLabel("PR dates: ");
		cbCreatedAtPR = new JCheckBox("created at");
		cbClosedAtPR = new JCheckBox("closed at");
		cbMergedAtPR = new JCheckBox("merged at");

		//centerPanelMidDates.add(labelDates);
		centerPanelMidDates.add(cbCreatedAtPR);
		centerPanelMidDates.add(cbClosedAtPR);
		centerPanelMidDates.add(cbMergedAtPR);
		centerPanelMidDates.add(new JLabel());
		centerPanelMidDates.add(new JLabel());

		TitledBorder titleAuthorData = BorderFactory.createTitledBorder( BorderFactory.createEmptyBorder(), "Author data: ");
		centerPanelBotAuthorPR = new JPanel(new GridLayout(2,5));
		centerPanelBotAuthorPR.setBorder(titleAuthorData);
		//centerPanelBotauthorPR.setBorder(title);

		//JLabel labelAuthorData = new JLabel("Author data: ");
		cbTotalPRUser = new JCheckBox("total PR by user");
		cbDeveloperType = new JCheckBox("type");
		cbAgeUser = new JCheckBox("age user");
		cbUserFollowers = new JCheckBox("followers");
		cbUserFollowing = new JCheckBox("following");
		cbWatchRepo = new JCheckBox("watch repo");
		cbUserLocation = new JCheckBox("follow contributors");
		cbFollowContributors = new JCheckBox("location");
		cbAcceptanceUser = new JCheckBox("acceptance average");


		//centerPanelBotauthorPR.add(labelAuthorData);
		centerPanelBotAuthorPR.add(cbAgeUser);
		centerPanelBotAuthorPR.add(cbUserFollowers);
		centerPanelBotAuthorPR.add(cbUserFollowing);
		centerPanelBotAuthorPR.add(cbWatchRepo);
		centerPanelBotAuthorPR.add(cbFollowContributors );
		centerPanelBotAuthorPR.add(cbUserLocation);
		centerPanelBotAuthorPR.add(cbTotalPRUser);
		centerPanelBotAuthorPR.add(cbDeveloperType);
		centerPanelBotAuthorPR.add(cbAcceptanceUser);

		JPanel centerPanelMidBot = new JPanel(new BorderLayout());

		centerPanelMidBot.add(centerPanelMidDates, BorderLayout.NORTH);
		centerPanelMidBot.add(centerPanelBotAuthorPR, BorderLayout.SOUTH);

		centerPanelMid.add(centerPanelMidBot, BorderLayout.SOUTH);
		centerPanelMid.add(centerPanelMidIn);
		centerPanel.add(centerPanelMid, BorderLayout.CENTER);
		centerPanel.add(centerPanelNorth, BorderLayout.NORTH);
		jFrame.add(centerPanel, BorderLayout.CENTER);
	}

	private void addBottomPanel() {
		logArea = new JTextArea();
		bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(logArea);
		bottomPanel.setEnabled(false);
		jFrame.add(bottomPanel, BorderLayout.SOUTH);
	}

	private void loadComponents(boolean value){
		cbAgeUser.setSelected(value);
		cbUserFollowers.setSelected(value);
		cbUserFollowing.setSelected(value);
		cbFollowContributors.setSelected(value);
		cbUserLocation.setSelected(value);
		cbWatchRepo.setSelected(value);
		cbTotalPRUser.setSelected(value);
		cbAcceptanceUser.setSelected(value);
		cbCreatedAtPR.setSelected(value);
		cbClosedAtPR.setSelected(value);
		cbMergedAtPR.setSelected(value);
		cbIdPR.setSelected(value);
		cbAuthorPR.setSelected(value);
		cbNumPR.setSelected(value);
		cbContributorsPR.setSelected(value);
		cbStatePR.setSelected(value);
		cbLifetimePR.setSelected(value);
		cbClosedByPR.setSelected(value);
		cbMergedByPR.setSelected(value);
		cbShaHeadBasePR.setSelected(value);
		cbAssigneePR.setSelected(value);
		cbCommentsPR.setSelected(value);
		cbCommitsPR.setSelected(value);
		cbFilesPR.setSelected(value);
		cbCommitsByFilesPR.setSelected(value);
		cbAuthorMoreCommitsPR.setSelected(value);
		cbLinesAddedPR.setSelected(value);
		cbLinesDeletedPR.setSelected(value);
		cbChangedFilePR.setSelected(value);
		cbTitlePR.setSelected(value);
		cbRepo.setSelected(value);
		cbOwner.setSelected(value);
		cbRepoCreatedDate.setSelected(value);
		cbRepoOthers.setSelected(value);
		cbDeveloperType.setSelected(value);

	}

	private List<String> getSelectedFields(){
		List<String> result = new ArrayList<String>();
		Component[] ls0 = centerPanelNorth.getComponents();
		Component[] ls1 = centerPanelMidIn.getComponents();
		Component[] ls2 = panelContributor.getComponents();
		Component[] ls3 = panelCommByFiles.getComponents();
		Component[] ls4 = panelAuthorMoreCommitsPR.getComponents();
		Component[] ls5 = centerPanelMidDates.getComponents();
		Component[] ls6 = centerPanelBotAuthorPR.getComponents();

		for (Component comp : ls0) {
			if (comp instanceof JCheckBox && ((JCheckBox)comp).isSelected())
				result.add(((JCheckBox) comp).getText());
		}
		for (Component comp : ls1) {
			if (comp instanceof JCheckBox && ((JCheckBox)comp).isSelected())
				result.add(((JCheckBox) comp).getText());
		}
		for (Component comp : ls2) {
			if (comp instanceof JCheckBox && ((JCheckBox)comp).isSelected())
				result.add(((JCheckBox) comp).getText());
		}
		for (Component comp : ls3) {
			if (comp instanceof JCheckBox && ((JCheckBox)comp).isSelected())
				result.add(((JCheckBox) comp).getText());
		}
		for (Component comp : ls4) {
			if (comp instanceof JCheckBox && ((JCheckBox)comp).isSelected())
				result.add(((JCheckBox) comp).getText());
		}
		for (Component comp : ls5) {
			if (comp instanceof JCheckBox && ((JCheckBox)comp).isSelected())
				result.add(((JCheckBox) comp).getText());
		}
		for (Component comp : ls6) {
			if (comp instanceof JCheckBox && ((JCheckBox)comp).isSelected())
				result.add(((JCheckBox) comp).getText());
		}

		return result;
	}

	private Map<String, Integer> getTxtFields() throws NumberFormatException {
		Map<String, Integer> result = new HashMap<String, Integer>();
		if (cbContributorsPR.isSelected())
			result.put("contributorMonths", Integer.parseInt(txtContributor.getText()));		

		if (cbAuthorMoreCommitsPR.isSelected())
			result.put("authorMoreCommDays", Integer.parseInt(txtAuthorMoreCommitsPR.getText()));
		
		if (cbCommitsByFilesPR.isSelected())
			result.put("commByFilesDays", Integer.parseInt(txtCommByFiles.getText()));
		
		return result;
	}

	public static void main(String[] args) throws UnknownHostException {
		new RetrievePullRequest();

	}



}
