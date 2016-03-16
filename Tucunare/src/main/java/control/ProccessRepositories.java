package control;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import model.Settings;
import teste.DialogStatus;
import util.Connect;
import view.RetrievePullRequest;

public class ProccessRepositories {
	private int totalPR;
	private Settings settings;
	private List<String> selectedRepositories;
	private String file;
	private static int threadAtual = 0;
	private RetrievePullRequest retrievePR;
	private DialogStatus ds;
	private List <String> repos;

	public ProccessRepositories (RetrievePullRequest retrievePR, List<String> selectedRepositories, Settings settings, String file) {
		this.retrievePR = retrievePR;
		this.file = file;
		this.settings = settings;
		this.selectedRepositories = selectedRepositories;

		try {
			startProcessing();
		} catch (UnknownHostException e) {
			System.err.println("Erro ao iniciar o processamento dos repositórios.\n"+e.getMessage());
		}
	}

	private void startProcessing() throws UnknownHostException {
		totalPR = 0;
		SaveFile.setCancelProcessing(false);

		iniciaThreads(selectedRepositories, settings);
		if (selectedRepositories.size()>0){
			showStatusWindow();
		}
	}

	public int retrieveAmountOfPRs(){
		int totalPRs =0;
		repos = new ArrayList<String>();
		List <String> owners = new ArrayList<String>();

		for (String repository : selectedRepositories) {
			String[] aux = repository.split("/");
			owners.add(aux[0]);
			repos.add(aux[1]);
		}

		DB db;
		try {
			db = Connect.getInstance().getDB("ghtorrent");
			DBCollection dbcPullRequest = db.getCollection("pull_requests");
			BasicDBObject fields = new BasicDBObject();
			fields.put("state", 1);
			fields.put("closed_at", 1);
			fields.put("owner", 1);
			fields.put("repo", 1);
			fields.put("user", 1);
			fields.put("number", 1);
			
			for (int i=0; i < owners.size(); i++){
				String owner = owners.get(i);
				String repo = repos.get(i);
				
				BasicDBObject query = new BasicDBObject("repo",repo);
				query.append("owner", owner);

				if (settings.getPrType() == 1)
					query.append("state", "open");
				if (settings.getPrType() == 2)
					query.append("state", "closed");

				query.append("closed_at", new BasicDBObject("$ne", null));
				query.append("closed_at", new BasicDBObject("$ne", "null"));
				query.append("closed_at", new BasicDBObject("$ne", ""));

				DBCursor cursor = dbcPullRequest.find(query, fields);
				cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);

				try{
					for (DBObject dbObject : cursor) {
						String user = ((BasicDBObject)dbObject.get("user")).get("login").toString();
						String closed_by = Issues.getClosedbyPull((Integer) dbObject.get("number"), repo,owner);

						if(!closed_by.equals("")){
							if(dbObject!=null && !closed_by.equals(user))
								totalPRs++;
						}
					}
				}catch(IllegalStateException ise){
//					Connect.resetConnection();
//					threadAtual=0;
//					SaveFile.setFinalizedThreads(0);
//					SaveFile.setTempo("");
//					RetrievePullRequest.setSelectedRepositories(new ArrayList<String>());
//					RetrievePullRequest.setSettings(new Settings());
//					DialogStatus.setCurrentPR(0);
//					DialogStatus.setThreads(0);
//					DialogStatus.setTotalPullRequests(0);
//					DialogStatus.setTotalRepositories(0);
				}
			} 
		}
		catch (UnknownHostException e) {
			System.err.println("Erro ao tentar contar os PRs dos repositórios.");
		}
		return totalPRs;
	}

	private void iniciaThreads(List<String> selectedRepositories,
			Settings settings) throws UnknownHostException {

		totalPR = retrieveAmountOfPRs();
		//caso sejam selecionados no máximo 3 repositórios, realiza a recuperação de todos ao mesmo tempo.
		if (selectedRepositories.size() <=3){
			for (int i=0; i<selectedRepositories.size() ; i++){
				String repository = selectedRepositories.get(i); 
				String[] aux = repository.split("/");
				new Thread(new SaveFile(this, aux[0], aux[1], file, settings), "Thread-"+repository).start();	
			}
		}else
			for (int i=0; i<3 ; i++){
				String repository = selectedRepositories.get(i); 
				String[] aux = repository.split("/");
				new Thread(new SaveFile(this, aux[0], aux[1], file, settings), "Thread-"+repository).start();	
			}
	}


	public void iniciaThreads(int finalizedThreads){
		if (selectedRepositories.size() > 3 && finalizedThreads < selectedRepositories.size() ){
			try {
				threadAtual++;
				String[] aux = selectedRepositories.get(threadAtual).split("/");
				new Thread(new SaveFile(this, aux[0], aux[1], file, settings), "Thread-"+selectedRepositories.get(threadAtual)).start();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}

	private void showStatusWindow() {
		ds = new DialogStatus(retrievePR.getJFrame(), selectedRepositories.size(), totalPR, file, repos);
		ds.setLocationRelativeTo(retrievePR.getJFrame());
		ds.setModal(true);
		ds.setVisible(true);
	}

	public void setMessageOfTextArea(String s){
		retrievePR.setMessageOfTextArea(s);
	}
	
	public static void setThreadAtual(int i){
		threadAtual = i;
	}
}
