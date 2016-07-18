package control;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import model.Settings;
import util.Connect;
import view.DialogStatus;
import view.RetrievePullRequest;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class ProccessRepositories implements Runnable {
	private int totalPR;
	private Settings settings;
	private List<String> selectedRepositories;
	private String file;
	private RetrievePullRequest retrievePR;
	private DialogStatus ds;
	private List <String> repos;

	public ProccessRepositories (RetrievePullRequest retrievePR, List<String> selectedRepositories, 
			Settings settings, String file) {
		this.retrievePR = retrievePR;
		this.file = file;
		this.settings = settings;
		this.selectedRepositories = selectedRepositories;
	}

	@Override
	public void run() {
		totalPR = 0;
		SaveFile.setCancelProcessing(false);
		try {
			iniciaThreads(selectedRepositories, settings);
			if (selectedRepositories.size()>0){
				showStatusWindow();
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
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
					System.err.println(ise.getMessage());
				}
			} 
		}
		catch (UnknownHostException e) {
			System.err.println("Erro ao tentar contar os PRs dos repositórios.");
		}
		retrievePR.getLw().getFrame().setVisible(false);
		return totalPRs;
	}

	private void iniciaThreads(List<String> selectedRepositories,
			Settings settings) throws UnknownHostException {
		System.out.println("Lista de repositórios: ");
		for (int i = 0; i < selectedRepositories.size(); i++) {
			System.out.print(selectedRepositories.get(i)+", ");
		}
		System.out.println();
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


	public void iniciaThreads(){
		//Se a ferramenta for recuperar mais de 3 repositórios e
		// se as threads iniciadas ainda n somarem o número máximo.
		System.out.println("IniThreads: " + SaveFile.inicializedThreads );
		if (selectedRepositories.size() > 3 && SaveFile.inicializedThreads < selectedRepositories.size() ){
			try {
				String[] aux = selectedRepositories.get(SaveFile.inicializedThreads).split("/");
				new Thread(new SaveFile(this, aux[0], aux[1], file, settings), "Thread-"+selectedRepositories.get(SaveFile.inicializedThreads)).start();
			} catch (Exception e) {
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

}
