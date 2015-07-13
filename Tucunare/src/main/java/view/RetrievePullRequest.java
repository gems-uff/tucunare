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
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import control.PullRequests;
import control.SaveFile;

import javax.swing.JProgressBar;

import teste.DialogStatus;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ButtonGroup;

public class RetrievePullRequest implements ActionListener, ItemListener, ListSelectionListener {

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
	private JLabel jLabelContributor;
	private JLabel jLabelCommByFiles;
	private JLabel jLabelAuthorMoreComm;

	private JTextField jTxtFePath;
	private JTextField jTextRepo;
	private JTextField jTxtAuthorMoreCommitsPR;
	private JTextField jTxtContributor;
	private JTextField jTxtCommByFiles;

	private JTextArea jTextArea;

	private JButton jButtonSave;
	private JButton jButtonStop;
	private JButton jButtonFile;
	private JButton jButtonSelectAll;
	private JButton jButtonDeselectAll;

	private JCheckBox jcbRepoCreatedDate;
	private JCheckBox jcbRepoOthers;
	private JCheckBox jcbRepo;
	private JCheckBox jcbOwner;
	private JCheckBox jcbAgeUser;
	private JCheckBox jcbUserFollowers;
	private JCheckBox jcbUserFollowing;
	private JCheckBox jcbWatchRepo;
	private JCheckBox jcbFollowContributors;
	private JCheckBox jcbUserLocation;
	private JCheckBox jcbTotalPRUser;
	private JCheckBox jcbAcceptanceUser;
	private JCheckBox jcbCreatedAtPR;
	private JCheckBox jcbClosedAtPR;
	private JCheckBox jcbMergedAtPR;
	private JCheckBox jcbIdPR;
	private JCheckBox jcbAuthorPR;
	private JCheckBox jcbNumPR;
	private JCheckBox jcbContributorsPR;
	private JCheckBox jcbStatePR;
	private JCheckBox jcbLifetimePR;
	private JCheckBox jcbClosedByPR;
	private JCheckBox jcbMergedByPR;
	private JCheckBox jcbShaHeadBasePR;
	private JCheckBox jcbAssigneePR;
	private JCheckBox jcbCommentsPR;
	private JCheckBox jcbCommitsPR;
	private JCheckBox jcbFilesPR;
	private JCheckBox jcbCommitsByFilesPR;
	private JCheckBox jcbAuthorMoreCommitsPR;
	private JCheckBox jcbLinesAddedPR;
	private JCheckBox jcbLinesDeletedPR;
	private JCheckBox jcbChangedFilePR;
	private JCheckBox jcbDeveloperType;
	private JCheckBox jcbTitlePR;
	private JButton jButtonRepositories;

	private JList<String> repositoryList;
	private List<String> selectedRepositories = new ArrayList<String>();
	public static int total=0;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnHelp;
	private JMenuItem mntmSobre;
	private JMenuItem mntmLoadConfiguration;
	private JMenuItem mntmSave;
	private JMenuItem mntmExit;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	public RetrievePullRequest() throws UnknownHostException{
		loadRepositories();
		jFrame = new JFrame("Retrieve Pull Requests from MongoDB");
		jFrame.setBounds(100, 100, 1000, 480);
		addTopPanel();
		addCenterPanel();
		addBottomPanel();
		loadComponents(true);
		jFrame.pack();
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		menuBar = new JMenuBar();
		jFrame.setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmLoadConfiguration = new JMenuItem("Load configuration");
		mnFile.add(mntmLoadConfiguration);
		
		mntmSave = new JMenuItem("Save");
		mnFile.add(mntmSave);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		buttonGroup.add(mntmExit);
		mnFile.add(mntmExit);
		
		mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		mntmSobre = new JMenuItem("Sobre");
		mntmSobre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Mostrar janela "Sobre".
			}
		});
		mnHelp.add(mntmSobre);
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == jcbCommitsByFilesPR){
			if (jcbCommitsByFilesPR.isSelected()){
				jLabelCommByFiles.setVisible(true);
				jTxtCommByFiles.setVisible(true);
			}else{
				jTxtCommByFiles.setVisible(false);
				jLabelCommByFiles.setVisible(false);
			}
		}else
			if (e.getSource() == jcbContributorsPR){
				if (jcbContributorsPR.isSelected()){
					jLabelContributor.setVisible(true);
					jTxtContributor.setVisible(true);
				}else{
					jLabelContributor.setVisible(false);
					jTxtContributor.setVisible(false);
				}
			}else 
				if (e.getSource() == jcbAuthorMoreCommitsPR){
					if (jcbAuthorMoreCommitsPR.isSelected()){
						jLabelAuthorMoreComm.setVisible(true);
						jTxtAuthorMoreCommitsPR.setVisible(true);
					}else{
						jLabelAuthorMoreComm.setVisible(false);
						jTxtAuthorMoreCommitsPR.setVisible(false);
					}
				}else
					if (e.getSource() == jcbCommitsPR){
						if (jcbCommitsPR.isSelected()){
							jcbFilesPR.setEnabled(true);
							jcbCommitsByFilesPR.setEnabled(true);
							jcbAuthorMoreCommitsPR.setEnabled(true);

							jcbFilesPR.setSelected(true);
							jcbCommitsByFilesPR.setSelected(true); 
							jcbAuthorMoreCommitsPR.setSelected(true);

						}else{
							jcbFilesPR.setEnabled(false);
							jcbCommitsByFilesPR.setEnabled(false);
							jcbAuthorMoreCommitsPR.setEnabled(false);

							jcbFilesPR.setSelected(false);
							jcbCommitsByFilesPR.setSelected(false); 
							jcbAuthorMoreCommitsPR.setSelected(false);
						}
					}else 
						if (e.getSource() == jcbAuthorPR){
							if (jcbAuthorPR.isSelected())
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
			if (hasCheckBoxSelected()){
				if (jTextRepo==null || jTextRepo.equals("") || jTextRepo.getText().length()<1){
					jTextArea.setText("Clique no botão \"Repositories\" e escolha pelo menos 1 repositório.\n\n\n");
					return;
				}
				if (jTxtFePath == null || jTxtFePath.equals("") || jTxtFePath.getText().length()<1){
					jTextArea.setText("Escolha um diretório para armazenar o arquivo.\n\n\n");
					return;
				}
				else{
					//PullRequests pull = new PullRequests();
					file = jTxtFePath.getText();
					try {
						jTextArea.setText("Processando dados.");
						boolean entrou=false;
						total=0;
						for (String repository : selectedRepositories) {
							entrou = true;
							total++;
							new Thread(new SaveFile(repository, file, getSelectedFields(), getTxtFields()), "Thread-"+repository).start();	
						}
						if (entrou){
							DialogStatus ds = new DialogStatus(jFrame, total);
							ds.setVisible(true);
							ds.setModal(true);
						}
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(null, "Entre com valores válidos nos campos de dias");
					}
				}
			}else
				JOptionPane.showMessageDialog(null, "Escolha pelo menos uma informação para ser recuperada.");
		}
		else
			if(evt.getSource()==jButtonFile){
				JFileChooser fileChooser = new JFileChooser("D:\\files"); 
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int i= fileChooser.showSaveDialog(null);
				if (i==1){
					jTxtFePath.setText("");
					return;
				} else {
					jTxtFePath.setText(fileChooser.getSelectedFile().toString());
					return;
				}
			} else 
				if (evt.getSource()==jButtonSelectAll)
					loadComponents(true);
				else
					if (evt.getSource() == jButtonDeselectAll)
						loadComponents(false);
					else
						if (evt.getSource() == jButtonRepositories){
							JOptionPane.showMessageDialog(
									null, new JScrollPane(repositoryList), "Seleção de repositórios:", JOptionPane.PLAIN_MESSAGE);
							String result = "";
							for (String s : selectedRepositories) {
								result += s+"; ";
							}
							jTextRepo.setText(result);
						}

		if(evt.getSource()==jButtonStop){
			System.exit(0);
		}	
	}


	public void addTopPanel(){
		topPanel = new JPanel();
		jTextRepo = new JTextField(20);
		jTextRepo.setEditable(false);

		jButtonFile = new JButton("File...");

		jButtonSave = new JButton("Save");

		jButtonSelectAll = new JButton("Select all");

		jButtonDeselectAll = new JButton("Deselect all");

		jButtonRepositories = new JButton("Repositories:");
		jButtonRepositories.addActionListener(this);
		topPanel.add(jButtonRepositories);
		topPanel.add(jTextRepo);

		jButtonFile.addActionListener(this);
		topPanel.add(jButtonSave);

		jButtonSave.addActionListener(this);
		topPanel.add(jButtonFile);

		jTxtFePath = new JTextField(20);
		jTxtFePath.setEditable(false);
		topPanel.add(jTxtFePath);

		jButtonSelectAll.addActionListener(this);
		topPanel.add(jButtonSelectAll);

		jButtonDeselectAll.addActionListener(this);
		topPanel.add(jButtonDeselectAll);



		jFrame.getContentPane().add(topPanel, BorderLayout.NORTH);

	}

	public void addCenterPanel(){
		centerPanel = new JPanel(new BorderLayout()); 
		centerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Selecione os dados a serem recuperados pela ferramenta: ", 
				TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));

		//Painel de dados do RepositÃ³rio
		centerPanelNorth = new JPanel(new GridLayout(1,4));
		centerPanelNorth.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Repository: ", 
				TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));
		jcbRepo = new JCheckBox("repository");
		jcbRepoCreatedDate = new JCheckBox("created date");
		jcbRepoOthers = new JCheckBox("others");
		jcbOwner = new JCheckBox("owner");

		centerPanelNorth.add(jcbRepo);
		centerPanelNorth.add(jcbOwner);
		centerPanelNorth.add(jcbRepoCreatedDate);
		centerPanelNorth.add(jcbRepoOthers);
		centerPanelNorth.add(new JLabel());

		//Painel de dados do Pull Request
		centerPanelMid = new JPanel(new BorderLayout());
		centerPanelMid.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pull Request: ", 
				TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));
		centerPanelMidIn = new JPanel(new GridLayout(4,5));

		jcbIdPR = new JCheckBox("id");
		jcbAuthorPR = new JCheckBox("author");
		jcbAuthorPR.addItemListener(this);
		jcbNumPR = new JCheckBox("number");
		jcbContributorsPR = new JCheckBox("contributors");
		jcbContributorsPR.addItemListener(this);

		jcbStatePR = new JCheckBox("state");
		jcbLifetimePR = new JCheckBox("lifetime");
		jcbClosedByPR = new JCheckBox("closed by");
		jcbMergedByPR = new JCheckBox("merged by");
		jcbShaHeadBasePR = new JCheckBox("sha (head e base)");
		jcbAssigneePR = new JCheckBox("assignee");
		jcbCommentsPR = new JCheckBox("comments");
		jcbCommitsPR = new JCheckBox("commits");
		jcbCommitsPR.addItemListener(this);
		jcbFilesPR = new JCheckBox("files");
		jcbCommitsByFilesPR = new JCheckBox("commits by files");
		jcbCommitsByFilesPR.addItemListener(this);
		jcbAuthorMoreCommitsPR = new JCheckBox("author more commits");
		jcbAuthorMoreCommitsPR.addItemListener(this);
		jcbLinesAddedPR = new JCheckBox("lines added");
		jcbLinesDeletedPR = new JCheckBox("deleted lines");
		jcbChangedFilePR = new JCheckBox("changed files");
		jcbTitlePR = new JCheckBox("title");

		centerPanelMidIn.add(jcbIdPR);
		centerPanelMidIn.add(jcbAuthorPR);
		centerPanelMidIn.add(jcbNumPR);

		panelContributor = new JPanel(new BorderLayout());
		jTxtContributor = new JTextField("10");
		jLabelContributor = new JLabel(" Dia(s):  ");

		panelContributor.add(jcbContributorsPR, BorderLayout.NORTH);
		panelContributor.add(jLabelContributor, BorderLayout.WEST);
		panelContributor.add(jTxtContributor);

		centerPanelMidIn.add(panelContributor);
		centerPanelMidIn.add(jcbStatePR);
		centerPanelMidIn.add(jcbLifetimePR);
		centerPanelMidIn.add(jcbClosedByPR);
		centerPanelMidIn.add(jcbMergedByPR);
		centerPanelMidIn.add(jcbShaHeadBasePR);
		centerPanelMidIn.add(jcbAssigneePR);
		centerPanelMidIn.add(jcbCommentsPR);
		centerPanelMidIn.add(jcbCommitsPR);
		centerPanelMidIn.add(jcbFilesPR);

		panelCommByFiles = new JPanel(new BorderLayout());
		jTxtCommByFiles = new JTextField("10");
		jLabelCommByFiles = new JLabel(" Dia(s):  ");
		panelCommByFiles.add(jcbCommitsByFilesPR, BorderLayout.NORTH);
		panelCommByFiles.add(jLabelCommByFiles, BorderLayout.WEST);
		panelCommByFiles.add(jTxtCommByFiles);

		centerPanelMidIn.add(panelCommByFiles);

		panelAuthorMoreCommitsPR = new JPanel(new BorderLayout());
		jTxtAuthorMoreCommitsPR = new JTextField("10");
		jLabelAuthorMoreComm = new JLabel(" Dia(s):  ");

		panelAuthorMoreCommitsPR.add(jcbAuthorMoreCommitsPR, BorderLayout.NORTH);
		panelAuthorMoreCommitsPR.add(jLabelAuthorMoreComm, BorderLayout.WEST);
		panelAuthorMoreCommitsPR.add(jTxtAuthorMoreCommitsPR);

		centerPanelMidIn.add(panelAuthorMoreCommitsPR);
		centerPanelMidIn.add(jcbLinesAddedPR);
		centerPanelMidIn.add(jcbLinesDeletedPR);
		centerPanelMidIn.add(jcbChangedFilePR);
		centerPanelMidIn.add(jcbTitlePR);


		TitledBorder title = BorderFactory.createTitledBorder( BorderFactory.createEmptyBorder(), "PR dates: ");
		//TitledBorder title = BorderFactory.createTitledBorder( null, null);
		//title.setTitlePosition(TitledBorder.TOP);

		centerPanelMidDates = new JPanel(new GridLayout());
		centerPanelMidDates.setBorder(title);

		//JLabel labelDates = new JLabel("PR dates: ");
		jcbCreatedAtPR = new JCheckBox("created at");
		jcbClosedAtPR = new JCheckBox("closed at");
		jcbMergedAtPR = new JCheckBox("merged at");

		//centerPanelMidDates.add(labelDates);
		centerPanelMidDates.add(jcbCreatedAtPR);
		centerPanelMidDates.add(jcbClosedAtPR);
		centerPanelMidDates.add(jcbMergedAtPR);
		
		centerPanelMidDates.add(new JLabel());
		centerPanelMidDates.add(new JLabel());

		TitledBorder titleAuthorData = BorderFactory.createTitledBorder( BorderFactory.createEmptyBorder(), "Author data: ");
		centerPanelBotAuthorPR = new JPanel(new GridLayout(2,5));
		centerPanelBotAuthorPR.setBorder(titleAuthorData);
		//centerPanelBotauthorPR.setBorder(title);

		//JLabel labelAuthorData = new JLabel("Author data: ");
		jcbTotalPRUser = new JCheckBox("total PR by user");
		jcbDeveloperType = new JCheckBox("type");
		jcbAgeUser = new JCheckBox("age user");
		jcbUserFollowers = new JCheckBox("followers");
		jcbUserFollowing = new JCheckBox("following");
		jcbWatchRepo = new JCheckBox("watch repo");
		jcbUserLocation = new JCheckBox("follow contributors");
		jcbFollowContributors = new JCheckBox("location");
		jcbAcceptanceUser = new JCheckBox("acceptance average");


		//centerPanelBotauthorPR.add(labelAuthorData);
		centerPanelBotAuthorPR.add(jcbAgeUser);
		centerPanelBotAuthorPR.add(jcbUserFollowers);
		centerPanelBotAuthorPR.add(jcbUserFollowing);
		centerPanelBotAuthorPR.add(jcbWatchRepo);
		centerPanelBotAuthorPR.add(jcbFollowContributors );
		centerPanelBotAuthorPR.add(jcbUserLocation);
		centerPanelBotAuthorPR.add(jcbTotalPRUser);
		centerPanelBotAuthorPR.add(jcbDeveloperType);
		centerPanelBotAuthorPR.add(jcbAcceptanceUser);

		JPanel centerPanelMidBot = new JPanel(new BorderLayout());

		centerPanelMidBot.add(centerPanelMidDates, BorderLayout.NORTH);
		centerPanelMidBot.add(centerPanelBotAuthorPR, BorderLayout.SOUTH);

		centerPanelMid.add(centerPanelMidBot, BorderLayout.SOUTH);
		centerPanelMid.add(centerPanelMidIn);
		centerPanel.add(centerPanelMid, BorderLayout.CENTER);
		centerPanel.add(centerPanelNorth, BorderLayout.NORTH);
		jFrame.getContentPane().add(centerPanel, BorderLayout.CENTER);
	}

	private void addBottomPanel() {
		jTextArea = new JTextArea();
		jTextArea.setEditable(false);
		jTextArea.setText("\n\n\n");
		bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(jTextArea);
		bottomPanel.setEnabled(false);
		jFrame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	}

	private void loadComponents(boolean value){
		jcbAgeUser.setSelected(value);
		jcbUserFollowers.setSelected(value);
		jcbUserFollowing.setSelected(value);
		jcbFollowContributors.setSelected(value);
		jcbUserLocation.setSelected(value);
		jcbWatchRepo.setSelected(value);
		jcbTotalPRUser.setSelected(value);
		jcbAcceptanceUser.setSelected(value);
		jcbCreatedAtPR.setSelected(value);
		jcbClosedAtPR.setSelected(value);
		jcbMergedAtPR.setSelected(value);
		jcbIdPR.setSelected(value);
		jcbAuthorPR.setSelected(value);
		jcbNumPR.setSelected(value);
		jcbContributorsPR.setSelected(value);
		jcbStatePR.setSelected(value);
		jcbLifetimePR.setSelected(value);
		jcbClosedByPR.setSelected(value);
		jcbMergedByPR.setSelected(value);
		jcbShaHeadBasePR.setSelected(value);
		jcbAssigneePR.setSelected(value);
		jcbCommentsPR.setSelected(value);
		jcbCommitsPR.setSelected(value);
		jcbFilesPR.setSelected(value);
		jcbCommitsByFilesPR.setSelected(value);
		jcbAuthorMoreCommitsPR.setSelected(value);
		jcbLinesAddedPR.setSelected(value);
		jcbLinesDeletedPR.setSelected(value);
		jcbChangedFilePR.setSelected(value);
		jcbTitlePR.setSelected(value);
		jcbRepo.setSelected(value);
		jcbOwner.setSelected(value);
		jcbRepoCreatedDate.setSelected(value);
		jcbRepoOthers.setSelected(value);
		jcbDeveloperType.setSelected(value);

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
		if (jcbContributorsPR.isSelected())
			result.put("contributorMonths", Integer.parseInt(jTxtContributor.getText()));		

		if (jcbAuthorMoreCommitsPR.isSelected())
			result.put("authorMoreCommDays", Integer.parseInt(jTxtAuthorMoreCommitsPR.getText()));

		if (jcbCommitsByFilesPR.isSelected())
			result.put("commByFilesDays", Integer.parseInt(jTxtCommByFiles.getText()));

		return result;
	}

	private boolean hasCheckBoxSelected(){
		List<Component[]> ls = new ArrayList<Component[]>();
		ls.add(centerPanelNorth.getComponents());
		ls.add(centerPanelMidIn.getComponents());
		ls.add(panelContributor.getComponents());
		ls.add(panelCommByFiles.getComponents());
		ls.add(panelAuthorMoreCommitsPR.getComponents());
		ls.add(centerPanelMidDates.getComponents());
		ls.add(centerPanelBotAuthorPR.getComponents());
		for (Component[] comp : ls) {
			for (Component component : comp) {
				if (component instanceof JCheckBox && ((JCheckBox) component).isSelected())
					return true;
			}
		}
		return false;
	}

	public void loadRepositories() throws UnknownHostException{
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		List<String> result = PullRequests.getAllRepos();
		for (String s : result) {
			listModel.addElement(s);
		}
		repositoryList = new JList<String>(listModel);
		repositoryList.addListSelectionListener(this);
	}

	public void valueChanged(ListSelectionEvent e) {
		final List<String> selectedValuesList = repositoryList.getSelectedValuesList();
		selectedRepositories = selectedValuesList;
	}

	public static void main(String[] args) throws UnknownHostException {
		RetrievePullRequest window = new RetrievePullRequest();
		window.jFrame.setVisible(true);
	}

}
