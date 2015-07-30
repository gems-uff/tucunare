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
	public static String getCommitsFilesPath(String shaHead, String shaBase, Integer commits, Integer filesPull) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcCommits = db.getCollection("commits");
		List<String> files = new ArrayList<String>();
		String shaTemp = shaHead;
		while(!shaTemp.equals(shaBase) && commits!=0){
			commits--;
			BasicDBObject queryHead = new BasicDBObject("sha",shaTemp);
			DBObject commit = null;
			BasicDBList listCommit = null, listFiles = null;
			try {
				commit = dbcCommits.findOne(queryHead);
				listCommit = (BasicDBList) commit.get("parents");
				listFiles = (BasicDBList) commit.get("files");	
				for (Object object : listFiles) {
					if(!files.contains((String) ((BasicDBObject) object).get("filename")) && files.size() < filesPull)
						files.add((String) ((BasicDBObject) object).get("filename"));
				}
				if(listCommit.get("0")!=null)
					shaTemp = (String) ((BasicDBObject) listCommit.get("0")).get("sha");
				else
					break;
			} catch (NullPointerException npe) {
				System.err.println("erro ao consultar o pull de commit Head "+shaHead +" no commit "+shaTemp);
				return "";
			}
		}
		return files.toString();
	}
	
	public static String getCommitsFilesPath2(String shaHead, String shaBase) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcCommits = db.getCollection("commits");
		List<String> files = new ArrayList<String>();
		String shaTemp = shaHead;
		while(!shaTemp.equals(shaBase)){
			//commits--;
			BasicDBObject queryHead = new BasicDBObject("sha",shaTemp);
			DBObject commit = null;
			BasicDBList listCommit = null, listFiles = null;
			try {
				commit = dbcCommits.findOne(queryHead);
				listCommit = (BasicDBList) commit.get("parents");
				listFiles = (BasicDBList) commit.get("files");	
				for (Object object : listFiles) {
					if(!files.contains((String) ((BasicDBObject) object).get("filename")) && listCommit.size()==1)
						files.add((String) ((BasicDBObject) object).get("filename"));
				}
				if(listCommit.get("0")!=null)
					shaTemp = (String) ((BasicDBObject) listCommit.get("0")).get("sha");
				else
					break;
			} catch (NullPointerException npe) {
				System.err.println("erro ao consultar o pull de commit Head "+shaHead +" no commit "+shaTemp);
				return "";
			}
		}
		return files.size()+"; "+files.toString();
	}

	public static String getAuthorCommits(String filesNames, String shaBase, String repo, Integer days) throws UnknownHostException{ 
		ArrayList<String> authors = new ArrayList<String>();
		String files[] = filesNames.split(", ");
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection commitsC = db.getCollection("commits");
		BasicDBObject queryBaseCommit = new BasicDBObject("sha", shaBase);
		DBObject baseCommitPull = commitsC.findOne(queryBaseCommit);
		String data = FormatDate.dataLimit(((BasicDBObject) ((BasicDBObject) baseCommitPull.get("commit")).get("committer")).get("date").toString(), days);
		BasicDBObject queryHead = new BasicDBObject("commit.committer.date", new BasicDBObject("$lt",((BasicDBObject)((BasicDBObject) baseCommitPull.get("commit")).get("committer")).get("date").toString()).append("$gt", data)); //consulta com data menor que a data do pull request
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
							authors.add(((BasicDBObject) dbo.get("committer")).get("login").toString());
						}catch(NullPointerException npe){
							String emailAuthor = "", nameAuthor = "";
							if(   ((BasicDBObject) ((BasicDBObject) dbo.get("commit")).get("committer")).get("email") != null   ){
								emailAuthor = ((BasicDBObject) ((BasicDBObject) dbo.get("commit")).get("committer")).get("email").toString();
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

	public static String getCommitsByFiles (String filesNames, String pullRequestDate, String repo, Integer days) throws UnknownHostException{
		String files[] = filesNames.split(", ");
		long numCommitsNoArquivo = 0L;
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection commitsC = db.getCollection("commits");
		String data = FormatDate.dataLimit(pullRequestDate, days);
		BasicDBObject queryHead = new BasicDBObject("commit.committer.date", new BasicDBObject("$lt",pullRequestDate).append("$gt", data)); //consulta com data menor que a data do pull request
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

	//recent contributors (3 months)
	public static String getContributors(String shaBase, String repo, String owner, Integer months) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection commitsC = db.getCollection("commits");
		BasicDBObject queryBaseCommit = new BasicDBObject("sha", shaBase);
		DBObject baseCommitPull = commitsC.findOne(queryBaseCommit);
		String data = FormatDate.dataLimitMonth(((BasicDBObject) ((BasicDBObject) baseCommitPull.get("commit")).get("committer")).get("date").toString(), months);
		BasicDBObject query = new BasicDBObject("commit.committer.date", new BasicDBObject("$lt",((BasicDBObject)((BasicDBObject) baseCommitPull.get("commit")).get("author")).get("date").toString()).append("$gt", data)); //consulta com data menor que a data do pull request
		query.append("html_url", new BasicDBObject("$regex", "("+owner+"/"+repo+")"));
		DBCursor cursor = commitsC.find(query);
		ArrayList<String> listCommitters = new ArrayList<String>();
		for (DBObject dbObject : cursor) {
			BasicDBList listParents = (BasicDBList) dbObject.get("parents");
			if(listParents.size()==1){
				if(((BasicDBObject) ((BasicDBObject) dbObject.get("commit")).get("committer")).get("email")!=null)
					if(!listCommitters.contains(((BasicDBObject) ((BasicDBObject) dbObject.get("commit")).get("committer")).get("email").toString()))
						listCommitters.add(((BasicDBObject) ((BasicDBObject) dbObject.get("commit")).get("committer")).get("email").toString());
			}	
		}
		return ""+listCommitters.size();
	}
	
	public static ArrayList<String> getContributorsList(String repo, String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection commitsC = db.getCollection("commits");
		BasicDBObject queryCommit = new BasicDBObject("html_url", new BasicDBObject("$regex", "("+owner+"/"+repo+")"));
		//System.out.println("Executando...");
		DBCursor cursor = commitsC.find(queryCommit);
		ArrayList<String> listCommitters = new ArrayList<String>();
		for (DBObject dbObject : cursor) {
			BasicDBList listParents = (BasicDBList) dbObject.get("parents");
			if(listParents.size()==1){
				if((BasicDBObject) dbObject.get("committer") != null){
					if(!listCommitters.contains(((BasicDBObject) dbObject.get("committer")).get("login").toString()))
						listCommitters.add(((BasicDBObject) dbObject.get("committer")).get("login").toString());
				}
				if((BasicDBObject) dbObject.get("author") != null){
					if(!listCommitters.contains(((BasicDBObject) dbObject.get("author")).get("login").toString()))
						listCommitters.add(((BasicDBObject) dbObject.get("author")).get("login").toString());
				}
			}	
		}
		return listCommitters;
	}
	
	//type developer
	public static String getTypeDeveloper(String user, String repo, String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbc = db.getCollection("commits");
		BasicDBObject query = new BasicDBObject("committer.login", user); 		
		query.append("html_url", new BasicDBObject("$regex", "("+owner+"/"+repo+")"));
		DBCursor o = dbc.find(query).limit(1);
		String type="";
		if (o.size()>0)
			type = "core";
		else 
			type = "external";
		return type;
	}
	
	
}