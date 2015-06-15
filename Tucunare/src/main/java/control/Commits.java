package control;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.Connect;
import util.FormatDate;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Commits {
	public static String getCommitsFilesPath(String shaHead, String shaBase, Integer commits) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcCommits = db.getCollection("commits");
		//BasicDBObject queryHead = new BasicDBObject("sha",shaHead); //consulta com query
		//DBObject commit = dbcCommits.findOne(queryHead);
		//BasicDBList listCommit = (BasicDBList) commit.get("parents");
		//BasicDBList list;// = (BasicDBList) commit.get("files");
		List<String> files = new ArrayList<String>();
		String shaTemp = shaHead;
		while(!shaTemp.equals(shaBase) && commits>0){
			commits--;
			BasicDBObject queryHead = new BasicDBObject("sha",shaTemp);
			DBObject commit = null;
			BasicDBList listCommit = null, listFiles = null;
			
			try {
				commit = dbcCommits.findOne(queryHead);
				listCommit = (BasicDBList) commit.get("parents");
				listFiles = (BasicDBList) commit.get("files");	
			} catch (NullPointerException npe) {
				System.err.println("erro ao consultar o pull de commit Head "+shaHead +" no commit "+shaTemp);
				return "";
			}
			
			for (Object object : listFiles) {
				if(!files.contains((String) ((BasicDBObject) object).get("filename")))// && listCommit.size()==1)
					files.add((String) ((BasicDBObject) object).get("filename"));
			}
			shaTemp = (String) ((BasicDBObject) listCommit.get("0")).get("sha");
		}
		return files.toString();
	}

	public static String getAuthorCommits(String filesNames, String shaBase, String repo) throws UnknownHostException{
		ArrayList<String> authors = new ArrayList<String>();
		String files[] = filesNames.split(", ");
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection commitsC = db.getCollection("commits");
		//String shaBase = ((BasicDBObject) dbObjectPull.get("base")).get("sha").toString();
		BasicDBObject queryBaseCommit = new BasicDBObject("sha", shaBase);
		DBObject baseCommitPull = commitsC.findOne(queryBaseCommit);
		String data = FormatDate.dataLimit(((BasicDBObject) ((BasicDBObject) baseCommitPull.get("commit")).get("author")).get("date").toString());
		BasicDBObject queryHead = new BasicDBObject("commit.author.date", new BasicDBObject("$lt",((BasicDBObject)((BasicDBObject) baseCommitPull.get("commit")).get("author")).get("date").toString()).append("$gt", data)); //consulta com data menor que a data do pull request
		queryHead.append("html_url", new BasicDBObject("$regex", "("+repo+")"));
		DBCursor dbcCommits = commitsC.find(queryHead);
		int count=0,max=Integer.MIN_VALUE;
		String a="";
		//System.out.println("arquivos: "+files.length);
		for (int i=0 ; i<files.length ; i++) {
			for (DBObject dbo: dbcCommits){
				BasicDBList commitFilesList = (BasicDBList) dbo.get("files");
				for (Object object : commitFilesList){
					if(((String) ((BasicDBObject) object).get("filename")).equals(files[i])){
						try{
							authors.add(((BasicDBObject) dbo.get("author")).get("login").toString());
						}catch(NullPointerException npe){
							String emailAuthor = "", nameAuthor = "";
							if(   ((BasicDBObject) ((BasicDBObject) dbo.get("commit")).get("author")).get("email") != null   ){
								emailAuthor = ((BasicDBObject) ((BasicDBObject) dbo.get("commit")).get("author")).get("email").toString();
								if(emailAuthor.lastIndexOf("@")>0)
									nameAuthor = emailAuthor.substring(0, emailAuthor.lastIndexOf("@"));
							}
							authors.add(nameAuthor);
						}
					}
				}	
			}
			Collections.sort(authors);
			for (int j=0; j < authors.size(); j++){  
                count = Collections.frequency(authors, authors.get(j));
                if(count>max){
                	max=count;
                	a=authors.get(j);
                }   
            } 
		}
//		if(a.equals(""))
//			a = "None";
		return a;
		
	}

	public static String getCommitsByFiles (String filesNames, String pullRequestDate, String repo) throws UnknownHostException{
		String files[] = filesNames.split(", ");
		long numCommitsNoArquivo = 0L;
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection commitsC = db.getCollection("commits");
		String data = FormatDate.dataLimit(pullRequestDate);
		BasicDBObject queryHead = new BasicDBObject("commit.author.date", new BasicDBObject("$lt",pullRequestDate).append("$gt", data)); //consulta com data menor que a data do pull request
		queryHead.append("html_url", new BasicDBObject("$regex", "("+repo+")"));
		DBCursor dbc = commitsC.find(queryHead);
		for (int i=0 ; i<files.length ; i++) {
			//numCommitsNoArquivo = 0L;
			for (DBObject dbo: dbc){
				BasicDBList commitFilesList = (BasicDBList) dbo.get("files");
				for (Object object : commitFilesList)
					if(((String) ((BasicDBObject) object).get("filename")).equals(files[i]))
						numCommitsNoArquivo++;
			}
			//numCommitsNoArquivo += numCommitsNoArquivo;
		}
		return ""+numCommitsNoArquivo;
	}

}
