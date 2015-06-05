package control;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import util.Connect;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Commits {
	//Retorna o n�mero de commits de um pull request
	
	public static long getCommits(String shaHead, String shaBase){
		long numCommits = 1L;
		DB db = new Connect().getDB();
		DBCollection dbc = db.getCollection("commits");
		BasicDBObject queryHead = new BasicDBObject("sha",shaHead); //consulta com query
		DBObject commit = dbc.findOne(queryHead);
		BasicDBList list = (BasicDBList) commit.get("parents");
		if(list.isEmpty())
			return 1;
		String shaTemp = (String) ((BasicDBObject) list.get("0")).get("sha");
		while(!shaTemp.equals(shaBase)){
			numCommits++;
			queryHead = new BasicDBObject("sha",shaTemp);
			commit = dbc.findOne(queryHead);
			list = (BasicDBList) commit.get("parents");
			if(list.isEmpty())
				return 1;
			shaTemp = (String) ((BasicDBObject) list.get("0")).get("sha");
		}
		return numCommits;
	}

	//retorna a estatistica dos commits
	public static String getCommitStats(String shaHead, String shaBase){
		long totalLines = 0L, additions = 0L, deletions = 0L;
		DB db = new Connect().getDB();
		DBCollection dbc = db.getCollection("commits");
		BasicDBObject queryHead = new BasicDBObject("sha",shaHead); //consulta com query
		DBObject commit = dbc.findOne(queryHead);
		Integer totalTemp = (Integer) ((BasicDBObject)commit.get("stats")).get("total");
		Integer additionsTemp = (Integer) ((BasicDBObject)commit.get("stats")).get("additions");
		Integer deletionsTemp = (Integer) ((BasicDBObject)commit.get("stats")).get("deletions");
		String stats = totalTemp+", "+additionsTemp+", "+deletionsTemp;

		BasicDBList list = (BasicDBList) commit.get("parents");
		String shaTemp = (String) ((BasicDBObject) list.get("0")).get("sha");
		while(!shaTemp.equals(shaBase)){
			queryHead = new BasicDBObject("sha",shaTemp);
			commit = dbc.findOne(queryHead);
			list = (BasicDBList) commit.get("parents");
			if(list.isEmpty()){
				totalLines = 0;
				return stats;
			}
			Integer total = (Integer) ((BasicDBObject)commit.get("stats")).get("total");
			Integer add = (Integer) ((BasicDBObject)commit.get("stats")).get("additions");
			Integer del = (Integer) ((BasicDBObject)commit.get("stats")).get("deletions");
			totalLines += total;
			additions +=add;
			deletions += del;
			shaTemp = (String) ((BasicDBObject) list.get("0")).get("sha");
		}
		return (totalLines+totalTemp)+", "+(additions+additionsTemp)+", "+(deletions+deletionsTemp);
	}

	public static String getCommitsFiles(String shaHead, String shaBase){
		DB db = new Connect().getDB();
		DBCollection dbc = db.getCollection("commits");
		BasicDBObject queryHead = new BasicDBObject("sha",shaHead); //consulta com query
		DBObject commit = dbc.findOne(queryHead);


		BasicDBList list = (BasicDBList) commit.get("files");
		//Iterator<Object> arq = list.listIterator();
		String files = "";
		long countFiles = 0L;
		for (Object object : list) {
			String [] temp = ((String) ((BasicDBObject) object).get("filename")).split("/");
			files += temp[temp.length-1]+"; ";
		}

		countFiles = list.size();
		BasicDBList listCommit = (BasicDBList) commit.get("parents");
		String shaTemp = (String) ((BasicDBObject) listCommit.get("0")).get("sha");

		while(!shaTemp.equals(shaBase)){
			queryHead = new BasicDBObject("sha",shaTemp);
			commit = dbc.findOne(queryHead);
			listCommit = (BasicDBList) commit.get("parents");
			if(listCommit.isEmpty()){
				return countFiles+"; "+files;
			}
			list = (BasicDBList) commit.get("files");
			for (Object object : list) {
				String [] temp = ((String) ((BasicDBObject) object).get("filename")).split("/");
				files += temp[temp.length-1]+"; ";
			}
			countFiles += list.size();
			shaTemp = (String) ((BasicDBObject) listCommit.get("0")).get("sha");
		}
		return countFiles+"; "+files;
	}

	public static String getCommitsFilesPath(String shaHead, String shaBase){
		DB db = new Connect().getDB();
		DBCollection dbc = db.getCollection("commits");
		BasicDBObject queryHead = new BasicDBObject("sha",shaHead); //consulta com query
		DBObject commit = dbc.findOne(queryHead);


		BasicDBList list = (BasicDBList) commit.get("files");
		//Iterator<Object> arq = list.listIterator();
		String files="";
		long countFiles = 0L;
		
		for (Object object : list) {
			files += (String) ((BasicDBObject) object).get("filename")+"; ";
		}
		
		countFiles = list.size();
		BasicDBList listCommit = (BasicDBList) commit.get("parents");
		String shaTemp = (String) ((BasicDBObject) listCommit.get("0")).get("sha");

		while(!shaTemp.equals(shaBase)){
			queryHead = new BasicDBObject("sha",shaTemp);
			commit = dbc.findOne(queryHead);
			listCommit = (BasicDBList) commit.get("parents");
			if(listCommit.isEmpty()){
				return countFiles+"; "+files;
			}
			list = (BasicDBList) commit.get("files");
			for (Object object : list) {
				files += (String) ((BasicDBObject) object).get("filename") +"; ";
			}
			countFiles += list.size();			
			shaTemp = (String) ((BasicDBObject) listCommit.get("0")).get("sha");
		}
		return countFiles+"; "+files;
	}
	
	public static String getCommitsByFiles (String filesNames, String pullRequestDate){
		//String mapArquivosCommits = "";
		
		//removendo espaços em branco
		filesNames = filesNames.replaceAll(" ", "");  
		
		String files[] = filesNames.split(";");
		
		long numCommitsNoArquivo = 0L;

		DB db = new Connect().getDB();
		DBCollection commitsC = db.getCollection("commits");
		DBCursor dbc = commitsC.find();
		
		String result="";

		System.out.println("Qtd. arquivos "+files[0]+" ");
		//Na posi��o 0 est� armazenada a quantidade de arquivos do commit
		for (int i=1 ; i<files.length ; i++) {
			System.out.println(files[i]);
			numCommitsNoArquivo = 0L;
			for (DBObject dbo: dbc){
				BasicDBObject authorCommit = (BasicDBObject) dbo.get("commit");
				authorCommit = (BasicDBObject) authorCommit.get("author");

				List<String> filesTemp = new ArrayList<String>();

				//Se a data est� dentro dos �ltimos 3 meses.
				if (dataValida(authorCommit.getString("date"), pullRequestDate)){
					
					BasicDBList commitFilesList = (BasicDBList) dbo.get("files");

					//Acredito que o ideal seja fazer a valida��o pelo SHA do arquivo.
					for (Object object : commitFilesList) {
						filesTemp.add(((String) ((BasicDBObject) object).get("filename")));
					}
					
					for (String file : filesTemp) {
						
						if (file.equals(files[i])){
							numCommitsNoArquivo++;
							//mapArquivosCommits += dbo.get("sha").toString() +" : " + files[i]+"\n";
							break;
						}
					}
				}
				
			}
			
			result += numCommitsNoArquivo+"; ";
		}
		//System.out.println(mapArquivosCommits);
		return result;
	}

	private static boolean dataValida(String commitDateString, String pullRequestDateString){
		//data 3 meses atrás.
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date commitDate = null;
		Date pullRequestDate = null;
		try {
			commitDate = formatter.parse(commitDateString);
			pullRequestDate = formatter.parse(pullRequestDateString);

		} catch (ParseException e){
			System.err.println("Erro na conversão de data do autor do commit.");
		}

		if (commitDate != null && pullRequestDate != null){
			Calendar c2 = Calendar.getInstance();
			c2.setTime(commitDate);
			
			GregorianCalendar pastDate = new GregorianCalendar();
			pastDate.setTime(pullRequestDate);
			pastDate.add(Calendar.MONTH, -3);			
			if (pastDate.before(c2))
				return true;
			else
				return false;
		}

		return false;
	}
	/*
	//teste
	public static void main(String [] args){
		DB db = new Connect().getDB();
		DBCollection dbc = db.getCollection("commits");
		BasicDBObject queryHead = new BasicDBObject("sha","d7d52aaef2b034f5507209640f073fd575ae073a"); //consulta com query

		DBObject commit = dbc.findOne(queryHead);
		BasicDBList list = (BasicDBList) commit.get("parents");
		System.out.println(((BasicDBObject) list.get("0")).get("sha"));
	}
	 */
}
