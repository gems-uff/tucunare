package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
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

public class RetrievePullRequest implements ActionListener, ItemListener, ListSelectionListener {

	private static Settings settings;
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JPanel centerPanel;
	private JPanel centerPanelNorth;
	private JPanel centerPanelSouth;
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

	private JCheckBox jcbAllCoreDevRecData;

	private JCheckBox follower_relation;
	private JCheckBox following_relation;
	private JCheckBox prior_evaluation;
	private JCheckBox recent_evaluation;
	private JCheckBox evaluate_pulls;
	private JCheckBox recent_pulls;
	private JCheckBox evaluate_time;
	private JCheckBox latest_time; 
	private JCheckBox first_time;
	private JCheckBox path_files;

	private JCheckBox jcbAllRepoData;

	private JCheckBox jcbNumPR;
	private JCheckBox jcbStatusPR;
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
	private static List<String> selectedRepositories = new ArrayList<String>();
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
	private static int threadAtual=0;
	private static String file="";
	private JCheckBox jcbPRParticipants;
	private JLabel lblNewLabel;
	private JCheckBox jcbRepoAcceptance;
	private JCheckBox jcbRepoWatchers;
	private JLabel label;
	private JCheckBox jcbPRId;
	


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
					if (e.getSource() == jcbUserPulls){
						boolean aux = jcbUserPulls.isSelected();
						jcbUserAverages.setEnabled(aux);
						jcbUserAverages.setSelected(aux);
					}else
						if (e.getSource() == jcbAllPRData)
							loadPRComponents(jcbAllPRData.isSelected());
						else
							if (e.getSource() == jcbAllAuthorData)
								loadAuthorComponents(jcbAllAuthorData.isSelected());
							else
								if (e.getSource() == jcbAllRepoData)
									loadAllRepoData(jcbAllRepoData.isSelected());
								else
									if (e.getSource() == jcbAllCoreDevRecData)
										loadCoreDevRecComponents(jcbAllCoreDevRecData.isSelected());

	}

	private void loadAllRepoData(boolean selected) {
		jcbRepoAcceptance.setSelected(selected);
		jcbRepoWatchers.setSelected(selected);
		
		jcbRepoAcceptance.setEnabled(!selected);
		jcbRepoWatchers.setEnabled(!selected);
		
	}

	public void actionPerformed(ActionEvent evt) {
		file="";
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
					
					file = jTxtFePath.getText();
					
					jTextArea.setText("Processando dados.");
					
					totalPullRequests=0;
					settings = getSelectedFields();
					
					iniciaThreads(selectedRepositories, settings);
					if (selectedRepositories.size()>0){
						showStatusWindow();

					}
					//} catch (UnknownHostException e) {
					//	e.printStackTrace();
					//}
					//catch (NumberFormatException e) {
					//	JOptionPane.showMessageDialog(null, "Entre com valores vÃ¡lidos nos campos de dias");
					//}
				}
			}else
				JOptionPane.showMessageDialog(null, "Escolha pelo menos uma informaÃ§ão para ser recuperada.");
		}
		else
			if(evt.getSource()==jButtonFile){
				JFileChooser fileChooser = new JFileChooser("D:\\ICMLA2015\\files"); 
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
									null, new JScrollPane(repositoryList), "SeleÃ§ão de repositórios:", JOptionPane.PLAIN_MESSAGE);
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


	private void iniciaThreads(List<String> selectedRepositories,
			Settings settings) {

		for (String repository : selectedRepositories) {
			try {
				String [] r = repository.split("/");
				totalPullRequests += PullRequests.getPulls(r[1], r[0], settings.getPrType());
			} catch (ArithmeticException e) {
				System.err.println("Erro ao iniciar a contagem de pull requests dos repositórios selecionados.");
			} catch(UnknownHostException e){
				System.err.println("Não foi possível encontrar o host ao iniciar as threads.");
			}
		}

		//caso sejam selecionados no máximo 3 repositórios, realiza a recuperação de todos ao mesmo tempo.
		if (selectedRepositories.size() <=3){
			for (String repository : selectedRepositories) {
				try {
					String[] aux = repository.split("/");
					new Thread(new SaveFile(aux[0], aux[1], file, settings), "Thread-"+repository).start();	
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}
		else
		{//Se forem selecionados mais de 3 repositórios, será iniciada a recuperação de 3, após isso ao final de cada thread uma nova é iniciada.
			threadAtual +=3; 
			for (int i=0; i<3; i++){
				try {
					String[] aux = selectedRepositories.get(i).split("/");
					new Thread(new SaveFile(aux[0], aux[1], file, settings), "Thread-"+aux[1]).start();	
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}

		}

	}

	public static void iniciaThreads(){
		if (threadAtual>3){
			try {
				String[] aux = selectedRepositories.get(threadAtual).split("/");
				new Thread(new SaveFile(aux[0], aux[1], file, settings), "Thread-"+selectedRepositories.get(threadAtual)).start();
				threadAtual++;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
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

		//Painel de elementos CoreDevRec
		centerPanelSouth = new JPanel(new GridLayout(3,4));
		centerPanelSouth.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CoreDevRec: ", 
				TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));

		jcbAllCoreDevRecData = new JCheckBox("All");
		jcbAllCoreDevRecData.setSelected(true);
		jcbAllCoreDevRecData.addItemListener(this);
		jcbAllCoreDevRecData.setToolTipText("Recupera todos os dados de CoreDevRec.");

		follower_relation = new JCheckBox("Follower Relation");
		following_relation = new JCheckBox("Following Relation");
		prior_evaluation = new JCheckBox("Prior Evaluation");
		recent_evaluation = new JCheckBox("Recent Evaluation");
		evaluate_pulls = new JCheckBox("Evaluate Pulls");
		recent_pulls = new JCheckBox("Recent Pulls");
		evaluate_time = new JCheckBox("Evaluate Time");
		latest_time = new JCheckBox("Latest Time");
		first_time = new JCheckBox("First Time");
		path_files = new JCheckBox("Path Files");

		centerPanelSouth.add(jcbAllCoreDevRecData);
		centerPanelSouth.add(follower_relation);
		centerPanelSouth.add(following_relation);
		centerPanelSouth.add(prior_evaluation);
		centerPanelSouth.add(recent_evaluation);
		centerPanelSouth.add(evaluate_time);
		centerPanelSouth.add(latest_time);
		centerPanelSouth.add(first_time);
		/**
		 *
		 * */

		//Painel de dados do RepositÃƒÂ³rio
		centerPanelNorth = new JPanel(new GridLayout(1,4));
		centerPanelNorth.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Repository: ", 
				TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));

		jcbAllRepoData = new JCheckBox("all"); 
		jcbAllRepoData.setToolTipText("Select all data repository to retrieve the repository, owner, dataRepo, acceptanceRepo and watchRepo.");
		jcbAllRepoData.addItemListener(this);

		centerPanelNorth.add(jcbAllRepoData);
		
		jcbRepoAcceptance = new JCheckBox("Repository Acceptance");
		centerPanelNorth.add(jcbRepoAcceptance);
		
		jcbRepoWatchers = new JCheckBox("Repository Watchers");
		centerPanelNorth.add(jcbRepoWatchers);
		centerPanelNorth.add(new JLabel());

		//Painel de dados do Pull Request
		centerPanelMid = new JPanel(new BorderLayout());
		centerPanelMid.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pull Request: ", 
				TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));
		centerPanelMidIn = new JPanel(new GridLayout(0,4));
		jcbNumPR = new JCheckBox("Number");
		jcbNumPR.setToolTipText("Retrieve the number of the pull requests.");

		jcbStatusPR = new JCheckBox("Status");
		jcbStatusPR.setToolTipText("Retrieve the status of the pull request (open, closed or merged).");
		jcbClosedMergedByPR = new JCheckBox("Closed and Merged by");
		jcbClosedMergedByPR.setToolTipText("Retrieves who closed and merged the pull request.");
		jcbAssigneePR = new JCheckBox("Assignee");
		jcbCommentsPR = new JCheckBox("Comments");
		jcbCommitsPR = new JCheckBox("Commits");
		jcbCommitsPR.addItemListener(this);
		jcbCommitsByFilesPR = new JCheckBox("Commits By Files");
		//jcbCommitsByFilesPR.setSelected(true);
		jcbCommitsByFilesPR.setToolTipText("Retrieves the number of commits on each file of the pull request.");
		jcbCommitsByFilesPR.addItemListener(this);
		jcbModifiedLinesPR = new JCheckBox("Modified Lines");
		jcbModifiedLinesPR.setToolTipText("Retrieves the number of additions and deletions of code lines.");

		jcbAllPRData = new JCheckBox("All");
		jcbAllPRData.setToolTipText("Select this to recover all the pull request data.");
		jcbAllPRData.setSelected(true);
		jcbAllPRData.addItemListener(this);
		centerPanelMidIn.add(jcbAllPRData);

		panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setVgap(10);
		flowLayout.setAlignment(FlowLayout.LEFT);
		flowLayout.setAlignOnBaseline(true);
		centerPanelMidIn.add(panel);

		lblPrType = new JLabel("PR type: ");
		panel.add(lblPrType);

		jcbPRType = new JComboBox<String>();
		jcbPRType.setToolTipText("Choose the state of pull requests to be recovered. All = open & closed.");
		jcbPRType.setModel(new DefaultComboBoxModel<String>(new String[] {"all", "open", "closed"}));
		jcbPRType.setSelectedIndex(0);

		panel.add(jcbPRType);
		
		jcbPRId = new JCheckBox("id");
		centerPanelMidIn.add(jcbPRId);
		centerPanelMidIn.add(jcbNumPR);
		centerPanelMidIn.add(jcbStatusPR);
		jcbTitlePR = new JCheckBox("Title");
		centerPanelMidIn.add(jcbTitlePR);
		jcbShaHeadBasePR = new JCheckBox("Shas (Head e Base)");
		jcbShaHeadBasePR.setToolTipText("Retrieves the sha head and the sha base of the pull request.");
		centerPanelMidIn.add(jcbShaHeadBasePR);

		//JLabel labelDates = new JLabel("PR dates: ");
		jcbDatesPR = new JCheckBox("Dates");
		jcbDatesPR.setToolTipText("Retrieves the created, closed and merged date of the pull request.");
		centerPanelMidIn.add(jcbDatesPR);
		centerPanelMidIn.add(jcbClosedMergedByPR);
		jcbLifetimePR = new JCheckBox("Lifetime");
		jcbLifetimePR.setToolTipText("Retrieves the lifetime of the pull request (in days, hours and minutes)");
		centerPanelMidIn.add(jcbLifetimePR);
		centerPanelMidIn.add(jcbAssigneePR);
		centerPanelMidIn.add(jcbCommentsPR);
		centerPanelMidIn.add(jcbCommitsPR);
		jcbAuthorMoreCommitsPR = new JCheckBox("Author More Commits");
		//jcbAuthorMoreCommitsPR.setSelected(true);
		jcbAuthorMoreCommitsPR.setToolTipText("Retrieves the author with more commits in the pull request.");
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
		jTxtAuthorMoreCommitsPR = new JTextField("7");
		jTxtAuthorMoreCommitsPR.setToolTipText("Enter the number of days before the first commit in the pull request to recover the author with more commits in the pull request.");
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
		jTxtCommByFiles = new JTextField("7");
		panel_3.add(jTxtCommByFiles);
		jTxtCommByFiles.setColumns(5);
		jTxtCommByFiles.setToolTipText("Number of days prior to the date of creation of the PR to recover the amount of commits by PR files.");
		jcbChangedFilesPR = new JCheckBox("Changed Files");
		jcbChangedFilesPR.setToolTipText("Retrieves the number of files modifieds in the pull request.");

		centerPanelMidIn.add(jcbChangedFilesPR);
		jcbFilesPR = new JCheckBox("Files");
		centerPanelMidIn.add(jcbFilesPR);

		jcbRootDirectoryPR = new JCheckBox("Root Directory");
		jcbRootDirectoryPR.setToolTipText("Retrieves the root directory of the pull request files.");

		centerPanelMidIn.add(jcbRootDirectoryPR);
		centerPanelMidIn.add(jcbModifiedLinesPR);

		centerPanelBotAuthorPR = new JPanel(new GridLayout(0,4));
		centerPanelBotAuthorPR.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Author Data: ", 
				TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));
		jcbAgeUser = new JCheckBox("Age User");
		jcbUserFollowers = new JCheckBox("Followers");
		jcbUserFollowing = new JCheckBox("Following");
		jcbUserLocation = new JCheckBox("Location");
		jcbUserLocation.setToolTipText("Retrieves the location of the author of the pull request.");

		jcbAllAuthorData = new JCheckBox("All");
		jcbAllAuthorData.setSelected(true);
		jcbAllAuthorData.addItemListener(this);
		centerPanelBotAuthorPR.add(jcbAllAuthorData);

		jcbUser = new JCheckBox("User");
		centerPanelBotAuthorPR.add(jcbUser);

		centerPanelBotAuthorPR.add(jcbAgeUser);
		jcbUserType = new JCheckBox("Type Old");
		jcbUserType.setToolTipText("Retrieves the type of the author of the pull request (core member or contributor)");
		centerPanelBotAuthorPR.add(jcbUserType);

		jcbUserPulls = new JCheckBox("Pulls User");
		jcbUserPulls.setToolTipText("Retrieves the number of pull requests submitted by the author, its open and closed pull requests.");
		jcbUserPulls.addItemListener(this);
		centerPanelBotAuthorPR.add(jcbUserPulls);
		jcbUserAverages = new JCheckBox("Averages");
		jcbUserAverages.setToolTipText("Retrieves the acceptance and rejections  average of pull requests submitted by the author.");
		centerPanelBotAuthorPR.add(jcbUserAverages);
		centerPanelBotAuthorPR.add(jcbUserFollowers);
		centerPanelBotAuthorPR.add(jcbUserFollowing);
		centerPanelBotAuthorPR.add(jcbUserLocation );

		JPanel centerPanelMidBot = new JPanel(new BorderLayout());
		centerPanelMidBot.add(centerPanelBotAuthorPR, BorderLayout.SOUTH);

		centerPanelMid.add(centerPanelMidBot, BorderLayout.SOUTH);
		centerPanelMid.add(centerPanelMidIn);

		jcbPRParticipants = new JCheckBox("Participants");
		centerPanelMidIn.add(jcbPRParticipants);
		centerPanel.add(centerPanelMid, BorderLayout.CENTER);
		centerPanel.add(centerPanelNorth, BorderLayout.NORTH);
		centerPanel.add(centerPanelSouth, BorderLayout.SOUTH);
		
		label = new JLabel("");
		centerPanelSouth.add(label);

		lblNewLabel = new JLabel("");
		centerPanelSouth.add(lblNewLabel);
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
		loadPRComponents(value);
		loadAuthorComponents(value);
		loadCoreDevRecComponents(value);
	}

	public void loadPRComponents(boolean value){
		jcbAllPRData.setSelected(value);
		jcbPRId.setSelected(value);
		jcbNumPR.setSelected(value);
		jcbStatusPR.setSelected(value);
		jcbTitlePR.setSelected(value);
		jcbShaHeadBasePR.setSelected(value);
		jcbDatesPR.setSelected(value);
		jcbClosedMergedByPR.setSelected(value);
		jcbLifetimePR.setSelected(value);
		jcbAssigneePR.setSelected(value);
		jcbCommentsPR.setSelected(value);
		jcbCommitsPR.setSelected(value);
		jcbPRParticipants.setSelected(value);
		if (value == true){
			jcbAuthorMoreCommitsPR.setSelected(value);
			jcbCommitsByFilesPR.setSelected(value);
			jcbFilesPR.setSelected(value);
		}
		jcbChangedFilesPR.setSelected(value);
		jcbRootDirectoryPR.setSelected(value);
		jcbModifiedLinesPR.setSelected(value);

		jcbPRId.setEnabled(!value);
		jcbNumPR.setEnabled(!value);
		jcbStatusPR.setEnabled(!value);
		jcbTitlePR.setEnabled(!value);
		jcbShaHeadBasePR.setEnabled(!value);
		jcbDatesPR.setEnabled(!value);
		jcbClosedMergedByPR.setEnabled(!value);
		jcbLifetimePR.setEnabled(!value);
		jcbAssigneePR.setEnabled(!value);
		jcbCommentsPR.setEnabled(!value);
		jcbCommitsPR.setEnabled(!value);
		jcbPRParticipants.setEnabled(!value);
		if (value == true){
			jcbAuthorMoreCommitsPR.setEnabled(!value);
			jcbCommitsByFilesPR.setEnabled(!value);
			jcbFilesPR.setEnabled(!value);
		}
		jcbChangedFilesPR.setEnabled(!value);
		jcbRootDirectoryPR.setEnabled(!value);
		jcbModifiedLinesPR.setEnabled(!value);

	}

	public void loadAuthorComponents(boolean value){
		jcbAllAuthorData.setSelected(value);
		jcbUser.setSelected(value);
		jcbAgeUser.setSelected(value);
		jcbUserType.setSelected(value);
		jcbUserPulls.setSelected(value);
		if (value == true){
			jcbUserAverages.setSelected(value);
			jcbUserAverages.setEnabled(!value);
		}
		jcbUserFollowers.setSelected(value);
		jcbUserFollowing.setSelected(value);
		jcbUserLocation.setSelected(value);
		jcbUser.setEnabled(!value);
		jcbAgeUser.setEnabled(!value);
		jcbUserType.setEnabled(!value);
		jcbUserPulls.setEnabled(!value);
		jcbUserFollowers.setEnabled(!value);
		jcbUserFollowing.setEnabled(!value);
		jcbUserLocation.setEnabled(!value);

	}

	public void loadCoreDevRecComponents(boolean value){
		follower_relation.setSelected(value);
		following_relation.setSelected(value);
		prior_evaluation.setSelected(value);
		recent_evaluation.setSelected(value);
		evaluate_pulls.setSelected(value);
		recent_pulls.setSelected(value);
		evaluate_time.setSelected(value);
		latest_time.setSelected(value);
		first_time.setSelected(value);
		path_files.setSelected(value);

		follower_relation.setEnabled(!value);
		following_relation.setEnabled(!value);
		prior_evaluation.setEnabled(!value);
		recent_evaluation.setEnabled(!value);
		evaluate_pulls.setEnabled(!value);
		recent_pulls.setEnabled(!value);
		evaluate_time.setEnabled(!value);
		latest_time.setEnabled(!value);
		first_time.setEnabled(!value);
		path_files.setEnabled(!value);
	}

	private Settings getSelectedFields(){
		Settings s = null;
		JSONObject jo = new JSONObject();
		try{
			//dados do repositório
			jo.put("allrepodata", jcbAllRepoData.isSelected());
			jo.put("repoacceptance", jcbRepoAcceptance.isSelected());
			jo.put("repowatchers", jcbRepoWatchers.isSelected());

			//dados core do PR
			jo.put("allprdata", jcbAllPRData.isSelected());
			jo.put("id", jcbPRId.isSelected());
			jo.put("number", jcbNumPR.isSelected());
			jo.put("status", jcbStatusPR.isSelected());
			jo.put("title", jcbTitlePR.isSelected());
			jo.put("shas", jcbShaHeadBasePR.isSelected());
			jo.put("dates", jcbDatesPR.isSelected());
			jo.put("closedmergedby", jcbClosedMergedByPR.isSelected());
			jo.put("lifetime", jcbLifetimePR.isSelected());
			jo.put("assignee", jcbAssigneePR.isSelected());
			jo.put("authormorecommits", jcbAuthorMoreCommitsPR.isSelected());
			jo.put("commitsbyfiles", jcbCommitsByFilesPR.isSelected());
			jo.put("participants", jcbPRParticipants.isSelected());

			//dados files PR
			jo.put("comments", jcbCommentsPR.isSelected());
			jo.put("commits", jcbCommitsPR.isSelected());
			jo.put("commitsdays", getDaysValues());
			jo.put("changedfiles", jcbChangedFilesPR.isSelected());
			jo.put("files", jcbFilesPR.isSelected());
			jo.put("dirfinal", jcbRootDirectoryPR.isSelected());
			jo.put("modifiedlines", jcbModifiedLinesPR.isSelected());

			//dados user
			jo.put("allauthordata", jcbAllAuthorData.isSelected());
			jo.put("user", jcbUser.isSelected());
			jo.put("age", jcbAgeUser.isSelected());
			jo.put("type", jcbUserType.isSelected());
			jo.put("pullsuser", jcbUserPulls.isSelected());
			jo.put("averages", jcbUserAverages.isSelected());
			jo.put("followers", jcbUserFollowers.isSelected());
			jo.put("following", jcbUserFollowing.isSelected());
			jo.put("location", jcbUserLocation.isSelected());
			jo.put("prtype", jcbPRType.getSelectedIndex());

			//dados CoreDevRec
			jo.put("allcoredevrecdata", jcbAllCoreDevRecData.isSelected());
			jo.put("followerrelation", follower_relation.isSelected());
			jo.put("followingrelation", following_relation.isSelected());
			jo.put("priorevaluation", prior_evaluation.isSelected());
			jo.put("recentevaluation", recent_evaluation.isSelected());
			jo.put("evaluatepulls", evaluate_pulls.isSelected());
			jo.put("recentpulls", recent_pulls.isSelected());
			jo.put("evaluatetime", evaluate_time.isSelected());
			jo.put("latesttime", latest_time.isSelected());
			jo.put("firsttime", first_time.isSelected());
			jo.put("pathfiles", path_files.isSelected());

			s = new Settings(jo);
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
