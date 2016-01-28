package control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import model.Settings;
import teste.DialogStatus;
import util.Connect;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class SaveFile implements Runnable {
	private String repo = ""; 
	private String owner = "";
	private String file;
	public static int finalizedThreads = 0;
	public static String tempo="";
	private Settings settings;

	public SaveFile(String owner, String repo, String file, Settings settings) throws UnknownHostException{
		this.repo = repo; 
		this.owner = owner;
		this.file = file;
		this.settings = settings;
	}

	public void run() {
		System.out.println("Processando dados do Pull Request "+ repo+"\nThread utilizada: "+ Thread.currentThread().getId());
		long tempoInicial = System.currentTimeMillis(); 
		try {
			retrieveData(settings);
			tempo += Thread.currentThread().getName()+": "+((System.currentTimeMillis() - tempoInicial)/1000)+" : ";
			DialogStatus.setThreads(finalizedThreads);
		} catch (UnknownHostException e) {
			System.err.println("Erro ao processar os dados do repositórios "+repo);
			e.printStackTrace();
		}
	}

	//Usar threads para recuperar os dados e salvar tudo o que foi armazenado na String em uma única escrita de arquivo.
	public String retrieveData(Settings settings) throws UnknownHostException {
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcPullRequest = db.getCollection("pull_requests");

		BasicDBObject query = new BasicDBObject("repo",repo); //consulta com query

		System.out.println("Valid settings: "+settings.tryParseValues(repo, owner));

		//query.append("number", new BasicDBObject("$gt", Integer.parseInt("1243")));//maiores que number
		if (settings.getPrType() == 1)
			query.append("state", "open"); //Apenas pull requests abertos
		if (settings.getPrType() == 2)
			query.append("state", "closed"); //Apenas pull requests encerrados
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
			//instância da classe que contém os métodos.
			DataRecoveryMethods drm;

			/** 
			 *  Fim da Recuperação dos métodos 
			 */

			//Escrevendo o cabeçalho do arquivo
			writeHeader(settings.getHeader());

			for (DBObject dbObject : cursor) {
				String result = "";

				// Execução dos métodos.
				drm = new DataRecoveryMethods(dbcPullRequest, dbObject, settings);
				for (Method method : meth) {
					result += method.invoke(drm);
				}
				result += "\r\n";

				System.out.println("PullRequest ("+(Integer) dbObject.get("number")+") "+repo);

				if (!saveFile(result))
					System.err.println("Erro ao tentar escrever o PR: "+(Integer) dbObject.get("number")+", do repositório: "+repo);
				DialogStatus.addsPullRequests();
			}
			finalizedThreads++;
			return "success!";
		}catch(Exception ioe){
			ioe.printStackTrace();
			System.err.println("Exceção: "+ioe.getMessage());
			finalizedThreads++;
			return "Erro ao recuperar dados.";
		}
	}	

	public boolean saveFile(String pullRequestData){
		File fileTemp = new File(file+File.separator+repo+".csv");
		FileWriter fw = null;
		
		try {
			fw = new FileWriter(fileTemp, true);
			System.out.println(pullRequestData);
			fw.write(pullRequestData);

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("erro na escrita do arquivo.");
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

	public boolean writeHeader(String header){
		File fileTemp = new File(file+File.separator+repo+".csv");

		FileWriter fw = null;

		try {
			fw = new FileWriter(fileTemp);
			header += getCoreDevRecHeader();
			System.out.println("HEADER:\n"+header);
			fw.write(header);
			fw.write("\r\n");

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
		String result = ""; 

		if (settings.isPrior_evaluation()){
			ArrayList<String> prior_evalutionList = Commits.getCoreTeamPullList(repo, owner);
			for (int index = 0; index < prior_evalutionList.size(); index++)
				prior_evalutionList.set(index, "pe_"+prior_evalutionList.get(index));
			String pe = prior_evalutionList.toString();
			result = pe.substring(1, pe.length()-1).replaceAll(", ", ",")+",";
		}

		if (settings.isRecent_pulls()){
			ArrayList<String> recent_pullList = Commits.getCoreTeamPullList(repo, owner);
			for (int index = 0; index < recent_pullList.size(); index++)
				recent_pullList.set(index, "rp_"+recent_pullList.get(index));
			String rp = recent_pullList.toString();
			result += rp.substring(1, rp.length()-1).replaceAll(", ", ",")+",";
		}

		if (settings.isEvaluate_pulls()){
			ArrayList<String> evaluation_pullList = Commits.getCoreTeamPullList(repo, owner);
			for (int index = 0; index < evaluation_pullList.size(); index++)
				evaluation_pullList.set(index, "ep_"+evaluation_pullList.get(index));
			String ep = evaluation_pullList.toString();
			result += ep.substring(1, ep.length()-1).replaceAll(", ", ",")+",";
		}

		if (settings.isRecent_evaluation()){
			ArrayList<String> recent_evaluationList = Commits.getCoreTeamPullList(repo, owner);
			for (int index = 0; index < recent_evaluationList.size(); index++)
				recent_evaluationList.set(index, "re_"+recent_evaluationList.get(index));
			String re = recent_evaluationList.toString();
			result += re.substring(1, re.length()-1).replaceAll(", ", ",")+",";
		}

		if (settings.isEvaluate_time()){ 
			ArrayList<String> evaluate_timeList = Commits.getCoreTeamPullList(repo, owner);
			for (int index = 0; index < evaluate_timeList.size(); index++)
				evaluate_timeList.set(index, "et_"+evaluate_timeList.get(index));
			String et = evaluate_timeList.toString();
			result += et.substring(1, et.length()-1).replaceAll(", ", ",")+",";
		}

		if (settings.isLatest_time()){
			ArrayList<String> latest_timeList = Commits.getCoreTeamPullList(repo, owner);
			for (int index = 0; index < latest_timeList.size(); index++)
				latest_timeList.set(index, "lt_"+latest_timeList.get(index));
			String lt = latest_timeList.toString();
			result += lt.substring(1, lt.length()-1).replaceAll(", ", ",")+",";
		}

		if (settings.isFirst_time()){
			ArrayList<String> first_timeList = Commits.getCoreTeamPullList(repo, owner);
			for (int index = 0; index < first_timeList.size(); index++)
				first_timeList.set(index, "ft_"+first_timeList.get(index));
			String ft = first_timeList.toString();
			result += ft.substring(1, ft.length()-1).replaceAll(", ", ",")+",";
		}
		return result;
	}

}
