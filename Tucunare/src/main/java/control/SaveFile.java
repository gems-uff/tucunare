package control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import model.Settings;
import util.Connect;
import view.DialogStatus;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class SaveFile implements Runnable {
	private String repo = ""; 
	private String owner = "";
	private String file;
	private static int finalizedThreads = 0;
	public static int inicializedThreads = 0;
	private Settings settings;
	private ProccessRepositories proccessRepos;
	private static boolean cancelProcessing = false;

	public SaveFile(ProccessRepositories proccessRepos, String owner, String repo, String file, Settings settings) throws UnknownHostException{
		this.proccessRepos = proccessRepos;
		this.repo = repo; 
		this.owner = owner;
		this.file = file;
		this.settings = settings;
	}

	public void run() {
		inicializedThreads++;
		long tempoInicial = System.currentTimeMillis(); 
		try {
			if (!retrieveData(settings)){
				System.err.println("Erro ao executar a thread "+Thread.currentThread().getName());
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName()+" finalizada em "+((System.currentTimeMillis() - tempoInicial)/1000)+" segundos.");
		DialogStatus.setThreads(finalizedThreads);
	}

	//Usar threads para recuperar os dados e salvar tudo o que foi armazenado na String em uma única escrita de arquivo.
	public boolean retrieveData(Settings settings) throws UnknownHostException {
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcPullRequest = db.getCollection("pull_requests");

		BasicDBObject query = new BasicDBObject("repo",repo);
		query.append("owner", owner);

		if (!settings.tryParseValues(repo, owner))
			proccessRepos.setMessageOfTextArea("Erro ao carregar as configurações. Todos os dados serão recuperados.");

		if (settings.getPrType() == 1)
			query.append("state", "open"); //Apenas pull requests abertos
		if (settings.getPrType() == 2)
			query.append("state", "closed"); //Apenas pull requests encerrados

		//mudar aqui a validação para utilizar a informação de closed_at
		query.append("closed_at", new BasicDBObject("$ne", null));
		query.append("closed_at", new BasicDBObject("$ne", "null"));
		query.append("closed_at", new BasicDBObject("$ne", ""));

		try{
			DBCursor cursor = dbcPullRequest.find(query);//.sort(new BasicDBObject("number", -1));
			cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);

			/**	
			 *  Início da Recuperação dos métodos. 
			 */
			
			Class<DataRecoveryMethods> drmClass = DataRecoveryMethods.class;
			List<Method> meth = new ArrayList<Method>();
			try {
				for (String aux: settings.getMethods())
					meth.add(drmClass.getMethod(aux));//recuperando os métodos.
			}catch(Exception e){
				System.err.println("Método "+e.getMessage()+" não encontrado.");
			}
			
			DataRecoveryMethods drm;  //instância da classe que contém os métodos.

			/** 
			 *  Fim da Recuperação dos métodos 
			 */

			//Escrevendo o cabeçalho do arquivo
			writeHeader(settings.getHeader());
			
			//String que armazena os dados de cada PR.
			StringBuilder result = new StringBuilder("");
			
			//O Loop abaixo percorre a coleção de PullRequests do repositório para recuperar as informações.
			for (DBObject dbObject : cursor) {
				
				String user = ((BasicDBObject)dbObject.get("user")).get("login").toString();
				String closed_by = Issues.getClosedbyPull((Integer) dbObject.get("number"), repo,owner);
				//Instrução para interromper o processamento das threads.
				if (cancelProcessing){
					saveFile(result);
					break;
				}
				
				if(!closed_by.equals("")){
					if(dbObject!=null && !closed_by.equals(user)){
						// Execução dos métodos.
						drm = new DataRecoveryMethods(dbcPullRequest, dbObject, settings);
						for (Method method : meth) {
							result.append(method.invoke(drm));
						}
						result.append("\r\n");
						DialogStatus.addsPullRequests();
					}
				}				
			}
			if (!cancelProcessing && !saveFile(result))
				System.err.println("Não foi possível salvar o arquivo.");
			finalizaThread();
			return true;
		}catch(Exception ioe){
			System.err.println("Exceção: "+ioe.getMessage());
			finalizaThread();
			return false;
		}
	}	

	private void finalizaThread() {
		finalizedThreads++;
		proccessRepos.iniciaThreads();		
	}

	public boolean saveFile(StringBuilder pullRequestData){
		File fileTemp = new File(file+File.separator+repo+".csv");
		FileWriter fw = null;

		try {
			fw = new FileWriter(fileTemp, true);
			fw.write(pullRequestData.toString());
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("erro na escrita do arquivo.");
			return false;
		}
		return true;
	}

	public boolean writeHeader(String header){
		File fileTemp = new File(file+File.separator+repo+".csv");

		FileWriter fw = null;

		try {
			fw = new FileWriter(fileTemp);
			header += getCoreDevRecHeader();
			fw.write(header);
			fw.write("\r\n");

		} catch (FileNotFoundException fnfe){
			JOptionPane.showMessageDialog(null, "Erro, o arquivo pode estar sendo utilizado por outro programa.");
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("erro na escrita do cabeçalho do arquivo.");
			return false;
		}
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("erro ao tentar fechar a escrita do arquivo (2).");
		}
		return true;
	}

	public String getCoreDevRecHeader() throws UnknownHostException{
		StringBuilder result = new StringBuilder(""); 

		if (settings.isPrior_evaluation()){
			ArrayList<String> prior_evalutionList = Commits.getCoreTeamPullList(repo, owner);
			for (int index = 0; index < prior_evalutionList.size(); index++)
				prior_evalutionList.set(index, "pe_"+prior_evalutionList.get(index));
			String pe = prior_evalutionList.toString();
			result.append(pe.substring(1, pe.length()-1).replaceAll(", ", ",")+",");
		}

		if (settings.isRecent_pulls()){
			ArrayList<String> recent_pullList = Commits.getCoreTeamPullList(repo, owner);
			for (int index = 0; index < recent_pullList.size(); index++)
				recent_pullList.set(index, "rp_"+recent_pullList.get(index));
			String rp = recent_pullList.toString();
			result.append(rp.substring(1, rp.length()-1).replaceAll(", ", ",")+",");
		}

		if (settings.isEvaluate_pulls()){
			ArrayList<String> evaluation_pullList = Commits.getCoreTeamPullList(repo, owner);
			for (int index = 0; index < evaluation_pullList.size(); index++)
				evaluation_pullList.set(index, "ep_"+evaluation_pullList.get(index));
			String ep = evaluation_pullList.toString();
			result.append(ep.substring(1, ep.length()-1).replaceAll(", ", ",")+",");
		}

		if (settings.isRecent_evaluation()){
			ArrayList<String> recent_evaluationList = Commits.getCoreTeamPullList(repo, owner);
			for (int index = 0; index < recent_evaluationList.size(); index++)
				recent_evaluationList.set(index, "re_"+recent_evaluationList.get(index));
			String re = recent_evaluationList.toString();
			result.append(re.substring(1, re.length()-1).replaceAll(", ", ",")+",");
		}

		if (settings.isEvaluate_time()){ 
			ArrayList<String> evaluate_timeList = Commits.getCoreTeamPullList(repo, owner);
			for (int index = 0; index < evaluate_timeList.size(); index++)
				evaluate_timeList.set(index, "et_"+evaluate_timeList.get(index));
			String et = evaluate_timeList.toString();
			result.append(et.substring(1, et.length()-1).replaceAll(", ", ",")+",");
		}

		if (settings.isLatest_time()){
			ArrayList<String> latest_timeList = Commits.getCoreTeamPullList(repo, owner);
			for (int index = 0; index < latest_timeList.size(); index++)
				latest_timeList.set(index, "lt_"+latest_timeList.get(index));
			String lt = latest_timeList.toString();
			result.append(lt.substring(1, lt.length()-1).replaceAll(", ", ",")+",");
		}

		if (settings.isFirst_time()){
			ArrayList<String> first_timeList = Commits.getCoreTeamPullList(repo, owner);
			for (int index = 0; index < first_timeList.size(); index++)
				first_timeList.set(index, "ft_"+first_timeList.get(index));
			String ft = first_timeList.toString();
			result.append(ft.substring(1, ft.length()-1).replaceAll(", ", ",")+",");
		}
		return result.toString();
	}
	
	public static void setCancelProcessing(boolean t){
		cancelProcessing = t;
	}

	public static void setFinalizedThreads(int finalizedThreads) {
		SaveFile.finalizedThreads = finalizedThreads;
	}

}
