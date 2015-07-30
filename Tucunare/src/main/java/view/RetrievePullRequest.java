package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import teste.DialogStatus;
import control.PullRequests;
import control.SaveFile;

import javax.swing.JTextField;
import javax.swing.BoxLayout;

public class RetrievePullRequest implements ActionListener, ItemListener, ListSelectionListener {

	private Settings settings;
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JPanel centerPanel;
	private JPanel centerPanelNorth;
	private JPanel centerPanelMid;
	private JPanel centerPanelMidIn;
	private JPanel panelCommByFiles;
	private JPanel panelAuthorMoreCommitsPR;
	private JPanel centerPanelBotAuthorPR;

	private JFrame jFrame;
	private JLabel jLabelCommByFiles;
	private JLabel jLabelAuthorMoreComm;

	private JTextField jTxtFePath;
	private JTextField jTextRepo;
	private JTextField jTxtAuthorMoreCommitsPR;
	private JTextField jTxtCommByFiles;

	private JTextArea jTextArea;

	private JButton jButtonSave;
	private JButton jButtonStop;
	private JButton jButtonFile;
	private JButton jButtonSelectAll;
	private JButton jButtonDeselectAll;

	private JCheckBox jcbAllRepoData;

	private JCheckBox jcbNumPR;
	private JCheckBox jcbStatePR;
	private JCheckBox jcbTitlePR;
	private JCheckBox jcbShaHeadBasePR;
	private JCheckBox jcbDatesPR;
	private JCheckBox jcbClosedMergedByPR;
	private JCheckBox jcbLifetimePR;
	private JCheckBox jcbAssigneePR;

	private JCheckBox jcbCommentsPR;
	private JCheckBox jcbCommitsPR;
	private JCheckBox jcbAuthorMoreCommitsPR;
	private JCheckBox jcbCommitsByFilesPR;
	private JCheckBox jcbChangedFilesPR;
	private JCheckBox jcbFilesPR;
	private JCheckBox jcbRootDirectoryPR;
	private JCheckBox jcbModifiedLinesPR;
	private JTextField jtxtRepoContributors;

	private JCheckBox jcbUser;
	private JCheckBox jcbAgeUser;
	private JCheckBox jcbUserType;
	private JCheckBox jcbUserPulls;
	private JCheckBox jcbUserAverages;
	private JCheckBox jcbUserFollowers;
	private JCheckBox jcbUserFollowing;
	private JCheckBox jcbUserLocation;

	private JButton jButtonRepositories;

	private JList<String> repositoryList;
	private List<String> selectedRepositories = new ArrayList<String>();
	public static int totalPullRequests=0;
	//Menu
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnHelp;
	private JMenuItem mntmSobre;
	private JMenuItem mntmLoadConfiguration;
	private JMenuItem mntmSave;
	private JMenuItem mntmExit;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JPanel panel;
	private JComboBox<String> jcbPRType;
	private JLabel lblPrType;
	private JPanel panel_1;
	private JLabel jlbMonthAgo;
	private JCheckBox jcbContributors;
	private JPanel panel_2;
	private JPanel panel_3;
	private JPanel panel_4;
	private JCheckBox jcbAllPRData;
	private JCheckBox jcbAllAuthorData;
	private JPanel panel_7;
	private JPanel panel_5;
	private JPanel panel_6;
	private JPanel panel_8;
	private JPanel panel_9;
	private JPanel panel_10;
	private JPanel panel_11;
	private JPanel panel_12;

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
			boolean aux = jcbCommitsByFilesPR.isSelected();
			jLabelCommByFiles.setVisible(aux);
			jTxtCommByFiles.setVisible(aux);
		}else
			if (e.getSource() == jcbAuthorMoreCommitsPR){
				jLabelAuthorMoreComm.setVisible(jcbAuthorMoreCommitsPR.isSelected());
				jTxtAuthorMoreCommitsPR.setVisible(jcbAuthorMoreCommitsPR.isSelected());
			}else
				if (e.getSource() == jcbCommitsPR){
					boolean aux = jcbCommitsPR.isSelected();
					jcbFilesPR.setEnabled(aux);
					jcbCommitsByFilesPR.setEnabled(aux);
					jcbAuthorMoreCommitsPR.setEnabled(aux);

					jcbFilesPR.setSelected(aux);
					jcbCommitsByFilesPR.setSelected(aux); 
					jcbAuthorMoreCommitsPR.setSelected(aux);
				}else
					if (e.getSource() == jcbContributors){						
						if (jtxtRepoContributors==null)
							jtxtRepoContributors = new JTextField();
						if (jlbMonthAgo == null)
							jlbMonthAgo = new JLabel("Months ago:");
						jlbMonthAgo.setVisible(jcbContributors.isSelected());
						jtxtRepoContributors.setVisible(jcbContributors.isSelected());

					}else
						if (e.getSource() == jcbUserPulls){
							jcbUserAverages.setSelected(jcbUserPulls.isSelected());
						}else
							if (e.getSource() == jcbAllPRData){
								loadPRComponents(jcbAllPRData.isSelected());
							}else
								if (e.getSource() == jcbAllAuthorData){
									loadAuthorComponents(jcbAllAuthorData.isSelected());
								}
	}

	public void actionPerformed(ActionEvent evt) {
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
						totalPullRequests=0;
						settings = getSelectedFields();
						for (String repository : selectedRepositories) {
							entrou = true;
							totalPullRequests += PullRequests.getPulls(repository, settings.getPrType());
							new Thread(new SaveFile(repository, file, settings), "Thread-"+repository).start();	
						}
						if (entrou){
							showStatusWindow();

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
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
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


	private void showStatusWindow() {
		DialogStatus ds = new DialogStatus(jFrame, selectedRepositories.size() ,totalPullRequests);
		ds.setLocationRelativeTo(jFrame);
		ds.setModal(true);
		ds.setVisible(true);
	}

	public void addTopPanel(){
		topPanel = new JPanel();

		jButtonSelectAll = new JButton("Select all");

		jButtonDeselectAll = new JButton("Deselect all");

		panel_7 = new JPanel();
		topPanel.add(panel_7);
		panel_7.setLayout(new BorderLayout(3, 0));

		panel_5 = new JPanel();
		panel_7.add(panel_5, BorderLayout.WEST);
		panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.Y_AXIS));

		panel_9 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_9.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_5.add(panel_9);

		jButtonRepositories = new JButton("Repositories:  ");
		panel_9.add(jButtonRepositories);
		jButtonRepositories.setHorizontalAlignment(SwingConstants.LEFT);
		jButtonRepositories.addActionListener(this);

		panel_8 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_8.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel_5.add(panel_8);

		jButtonFile = new JButton("File directory:");
		panel_8.add(jButtonFile);

		jButtonFile.addActionListener(this);

		panel_6 = new JPanel();
		panel_7.add(panel_6, BorderLayout.CENTER);
		panel_6.setLayout(new BoxLayout(panel_6, BoxLayout.Y_AXIS));

		panel_11 = new JPanel();
		panel_6.add(panel_11);
		jTextRepo = new JTextField(20);
		panel_11.add(jTextRepo);
		jTextRepo.setEditable(false);

		panel_10 = new JPanel();
		panel_6.add(panel_10);

		jTxtFePath = new JTextField(20);
		panel_10.add(jTxtFePath);
		jTxtFePath.setEditable(false);

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
		jcbAllRepoData = new JCheckBox("all");

		centerPanelNorth.add(jcbAllRepoData);

		panel_1 = new JPanel();
		centerPanelNorth.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		jcbContributors = new JCheckBox("contributors");
		jcbContributors.addItemListener(this);
		jcbContributors.setSelected(true);
		panel_1.add(jcbContributors, BorderLayout.NORTH);

		jlbMonthAgo = new JLabel("Months ago:");
		panel_1.add(jlbMonthAgo, BorderLayout.WEST);

		panel_2 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_2.getLayout();
		flowLayout_1.setVgap(0);
		flowLayout_1.setHgap(0);
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel_1.add(panel_2, BorderLayout.CENTER);

		jtxtRepoContributors = new JTextField();
		jtxtRepoContributors.setText("2");
		jtxtRepoContributors.setColumns(5);

		panel_2.add(jtxtRepoContributors);
		centerPanelNorth.add(new JLabel());

		//Painel de dados do Pull Request
		centerPanelMid = new JPanel(new BorderLayout());
		centerPanelMid.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pull Request: ", 
				TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));
		centerPanelMidIn = new JPanel(new GridLayout(0,4));
		jcbNumPR = new JCheckBox("number");

		jcbStatePR = new JCheckBox("state");
		jcbClosedMergedByPR = new JCheckBox("closed and merged by");
		jcbAssigneePR = new JCheckBox("assignee");
		jcbCommentsPR = new JCheckBox("comments");
		jcbCommitsPR = new JCheckBox("commits");
		jcbCommitsPR.addItemListener(this);
		jcbCommitsByFilesPR = new JCheckBox("commits by files");
		jcbCommitsByFilesPR.addItemListener(this);
		jcbModifiedLinesPR = new JCheckBox("modified lines");

		jcbAllPRData = new JCheckBox("all");
		jcbAllPRData.setSelected(true);
		jcbAllPRData.addItemListener(this);
		centerPanelMidIn.add(jcbAllPRData);

		panel = new JPanel();
		panel.setToolTipText("choose the type of pull request to be recovered. All = open & closed.");
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setVgap(10);
		flowLayout.setAlignment(FlowLayout.LEFT);
		flowLayout.setAlignOnBaseline(true);
		centerPanelMidIn.add(panel);

		lblPrType = new JLabel("PR type: ");
		panel.add(lblPrType);

		jcbPRType = new JComboBox<String>();
		jcbPRType.setModel(new DefaultComboBoxModel<String>(new String[] {"all", "open", "closed"}));
		jcbPRType.setSelectedIndex(0);

		panel.add(jcbPRType);
		centerPanelMidIn.add(jcbNumPR);
		centerPanelMidIn.add(jcbStatePR);
		jcbTitlePR = new JCheckBox("title");
		centerPanelMidIn.add(jcbTitlePR);
		jcbShaHeadBasePR = new JCheckBox("shas (head e base)");
		centerPanelMidIn.add(jcbShaHeadBasePR);

		//JLabel labelDates = new JLabel("PR dates: ");
		jcbDatesPR = new JCheckBox("dates");
		centerPanelMidIn.add(jcbDatesPR);
		centerPanelMidIn.add(jcbClosedMergedByPR);
		jcbLifetimePR = new JCheckBox("lifetime");
		centerPanelMidIn.add(jcbLifetimePR);
		centerPanelMidIn.add(jcbAssigneePR);
		centerPanelMidIn.add(jcbCommentsPR);
		centerPanelMidIn.add(jcbCommitsPR);
		jcbAuthorMoreCommitsPR = new JCheckBox("author more commits");
		jcbAuthorMoreCommitsPR.addItemListener(this);

		panelAuthorMoreCommitsPR = new JPanel(new BorderLayout());
		jLabelAuthorMoreComm = new JLabel(" Day(s):  ");

		panelAuthorMoreCommitsPR.add(jcbAuthorMoreCommitsPR, BorderLayout.NORTH);
		panelAuthorMoreCommitsPR.add(jLabelAuthorMoreComm, BorderLayout.WEST);

		centerPanelMidIn.add(panelAuthorMoreCommitsPR);

		panel_4 = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) panel_4.getLayout();
		flowLayout_3.setVgap(0);
		flowLayout_3.setHgap(0);
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		panelAuthorMoreCommitsPR.add(panel_4, BorderLayout.CENTER);
		jTxtAuthorMoreCommitsPR = new JTextField("10");
		jTxtAuthorMoreCommitsPR.setColumns(5);
		panel_4.add(jTxtAuthorMoreCommitsPR);
		jTxtAuthorMoreCommitsPR.setToolTipText("Number in former days the date of creation of the PR to retrieve the author of more commits on the PR.");
		jTxtAuthorMoreCommitsPR.setHorizontalAlignment(SwingConstants.LEFT);

		panelCommByFiles = new JPanel(new BorderLayout());
		jLabelCommByFiles = new JLabel(" Day(s):  ");
		panelCommByFiles.add(jcbCommitsByFilesPR, BorderLayout.NORTH);
		panelCommByFiles.add(jLabelCommByFiles, BorderLayout.WEST);

		centerPanelMidIn.add(panelCommByFiles);

		panel_3 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_3.getLayout();
		flowLayout_2.setVgap(0);
		flowLayout_2.setHgap(0);
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		panelCommByFiles.add(panel_3, BorderLayout.CENTER);
		jTxtCommByFiles = new JTextField("10");
		panel_3.add(jTxtCommByFiles);
		jTxtCommByFiles.setColumns(5);
		jTxtCommByFiles.setToolTipText("Number of days prior to the date of creation of the PR to recover the amount of commits by PR files.");
		jcbChangedFilesPR = new JCheckBox("changed files");
		centerPanelMidIn.add(jcbChangedFilesPR);
		jcbFilesPR = new JCheckBox("files");
		centerPanelMidIn.add(jcbFilesPR);

		jcbRootDirectoryPR = new JCheckBox("root directory");
		jcbRootDirectoryPR.setSelected(true);
		centerPanelMidIn.add(jcbRootDirectoryPR);
		centerPanelMidIn.add(jcbModifiedLinesPR);


		//TitledBorder title = BorderFactory.createTitledBorder( BorderFactory.createEmptyBorder(), "PR dates: ");

		centerPanelBotAuthorPR = new JPanel(new GridLayout(0,4));
		centerPanelBotAuthorPR.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Author Data: ", 
				TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));
		jcbAgeUser = new JCheckBox("age user");
		jcbUserFollowers = new JCheckBox("followers");
		jcbUserFollowing = new JCheckBox("following");
		jcbUserLocation = new JCheckBox("location");

		jcbAllAuthorData = new JCheckBox("all");
		jcbAllAuthorData.setSelected(true);
		jcbAllAuthorData.addItemListener(this);
		centerPanelBotAuthorPR.add(jcbAllAuthorData);

		jcbUser = new JCheckBox("user");
		jcbUser.setSelected(true);
		centerPanelBotAuthorPR.add(jcbUser);


		//centerPanelBotauthorPR.add(labelAuthorData);
		centerPanelBotAuthorPR.add(jcbAgeUser);
		jcbUserType = new JCheckBox("type");
		centerPanelBotAuthorPR.add(jcbUserType);
		//centerPanelBotauthorPR.setBorder(title);

		//JLabel labelAuthorData = new JLabel("Author data: ");
		jcbUserPulls = new JCheckBox("pulls user");
		jcbUserPulls.addItemListener(this);
		centerPanelBotAuthorPR.add(jcbUserPulls);
		jcbUserAverages = new JCheckBox("averages");
		centerPanelBotAuthorPR.add(jcbUserAverages);
		centerPanelBotAuthorPR.add(jcbUserFollowers);
		centerPanelBotAuthorPR.add(jcbUserFollowing);
		centerPanelBotAuthorPR.add(jcbUserLocation );

		JPanel centerPanelMidBot = new JPanel(new BorderLayout());
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

		panel_12 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_12.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		bottomPanel.add(panel_12, BorderLayout.NORTH);

		jButtonSave = new JButton("Save");
		panel_12.add(jButtonSave);

		jButtonSave.addActionListener(this);
		bottomPanel.add(jTextArea);
		bottomPanel.setEnabled(false);
		jFrame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	}

	private void loadComponents(boolean value){
		jcbAllRepoData.setSelected(value);
		jcbContributors.setSelected(value);
		loadPRComponents(value);
		loadAuthorComponents(value);
	}

	public void loadPRComponents(boolean value){
		jcbAllPRData.setSelected(value);
		jcbNumPR.setSelected(value);
		jcbStatePR.setSelected(value);
		jcbTitlePR.setSelected(value);
		jcbShaHeadBasePR.setSelected(value);
		jcbDatesPR.setSelected(value);
		jcbClosedMergedByPR.setSelected(value);
		jcbLifetimePR.setSelected(value);
		jcbAssigneePR.setSelected(value);
		jcbCommentsPR.setSelected(value);
		jcbCommitsPR.setSelected(value);
		jcbAuthorMoreCommitsPR.setSelected(value);
		jcbCommitsByFilesPR.setSelected(value);
		jcbChangedFilesPR.setSelected(value);
		jcbFilesPR.setSelected(value);
		jcbRootDirectoryPR.setSelected(value);
		jcbModifiedLinesPR.setSelected(value);

		jcbNumPR.setEnabled(!value);
		jcbStatePR.setEnabled(!value);
		jcbTitlePR.setEnabled(!value);
		jcbShaHeadBasePR.setEnabled(!value);
		jcbDatesPR.setEnabled(!value);
		jcbClosedMergedByPR.setEnabled(!value);
		jcbLifetimePR.setEnabled(!value);
		jcbAssigneePR.setEnabled(!value);
		jcbCommentsPR.setEnabled(!value);
		jcbCommitsPR.setEnabled(!value);
		jcbAuthorMoreCommitsPR.setEnabled(!value);
		jcbCommitsByFilesPR.setEnabled(!value);
		jcbChangedFilesPR.setEnabled(!value);
		jcbFilesPR.setEnabled(!value);
		jcbRootDirectoryPR.setEnabled(!value);
		jcbModifiedLinesPR.setEnabled(!value);

	}

	public void loadAuthorComponents(boolean value){
		jcbAllAuthorData.setSelected(value);
		jcbUser.setSelected(value);
		jcbAgeUser.setSelected(value);
		jcbUserType.setSelected(value);
		jcbUserPulls.setSelected(value);
		jcbUserAverages.setSelected(value);
		jcbUserFollowers.setSelected(value);
		jcbUserFollowing.setSelected(value);
		jcbUserLocation.setSelected(value);

		jcbUser.setEnabled(!value);
		jcbAgeUser.setEnabled(!value);
		jcbUserType.setEnabled(!value);
		jcbUserPulls.setEnabled(!value);
		jcbUserAverages.setEnabled(!value);
		jcbUserFollowers.setEnabled(!value);
		jcbUserFollowing.setEnabled(!value);
		jcbUserLocation.setEnabled(!value);

	}

	private Settings getSelectedFields(){
		Settings s = null;
		JSONObject jo = new JSONObject();
		try{
			//dados do repositório
			jo.put("repo", jcbAllRepoData.isSelected());
			//dados core do PR
			jo.put("number", jcbNumPR.isSelected());
			jo.put("state", jcbStatePR.isSelected());
			jo.put("title", jcbTitlePR.isSelected());
			jo.put("shas", jcbShaHeadBasePR.isSelected());
			jo.put("dates", jcbDatesPR.isSelected());
			jo.put("closedmergedby", jcbClosedMergedByPR.isSelected());
			jo.put("lifetime", jcbLifetimePR.isSelected());
			jo.put("assignee", jcbAssigneePR.isSelected());

			//dados files PR
			jo.put("comments", jcbCommentsPR.isSelected());
			jo.put("commits", jcbCommitsPR.isSelected());
			jo.put("commitsdays", getDaysValues());
			jo.put("changedfiles", jcbChangedFilesPR.isSelected());
			jo.put("files", jcbFilesPR.isSelected());
			jo.put("dirfinal", jcbRootDirectoryPR.isSelected());
			jo.put("modifiedlines", jcbModifiedLinesPR.isSelected());

			//dados user
			jo.put("user", jcbUser.isSelected());
			jo.put("age", jcbAgeUser.isSelected());
			jo.put("type", jcbUserType.isSelected());
			jo.put("pullsuser", jcbUserPulls.isSelected());
			jo.put("averages", jcbUserAverages.isSelected());
			jo.put("followers", jcbUserFollowers.isSelected());
			jo.put("following", jcbUserFollowing.isSelected());
			jo.put("location", jcbUserLocation.isSelected());
			jo.put("prtype", jcbPRType.getSelectedIndex());
			jo.put("contributors", jcbContributors.isSelected());

			s = new Settings(jo);
			System.out.println("Valid settings: "+s.tryParseValues());
			//System.out.println("Settings: "+s);
		}catch(JSONException je){
			s = new Settings();
			System.err.println("erro.");
		}

		return s;
	}

	private List <Integer> getDaysValues() throws NumberFormatException {
		List <Integer> result = new ArrayList<Integer>();
		if (jcbAuthorMoreCommitsPR.isSelected())
			result.add(Integer.parseInt(jTxtAuthorMoreCommitsPR.getText()));
		else
			result.add(-1);

		if (jcbCommitsByFilesPR.isSelected())
			result.add(Integer.parseInt(jTxtCommByFiles.getText()));
		else
			result.add(-1);

		if (jcbContributors.isSelected())
			result.add(Integer.parseInt(jtxtRepoContributors.getText()));
		else
			result.add(-1);

		return result;
	}

	private boolean hasCheckBoxSelected(){
		List<Component[]> ls = new ArrayList<Component[]>();
		ls.add(centerPanelNorth.getComponents());
		ls.add(centerPanelMidIn.getComponents());
		ls.add(panelCommByFiles.getComponents());
		ls.add(panelAuthorMoreCommitsPR.getComponents());
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


	public static void main(String[] args) {
		RetrievePullRequest window;
		try {
			window = new RetrievePullRequest();
			window.jFrame.setLocationRelativeTo(null); 
			window.jFrame.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Servidor de banco de dados não encontrado.","Erro",1);
			System.err.println(e.getMessage());
		}
	}

}
