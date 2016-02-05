package control;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import util.Connect;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Teste {

	public static void main(String[] args) throws UnknownHostException {
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcPull = db.getCollection("pull_requests");
		BasicDBObject queryPull = new BasicDBObject("repo","linguist"); //consulta com query
		DBCursor cursorPull = dbcPull.find(queryPull);
		for (DBObject dbObjectPull : cursorPull) {
			ArrayList<String> authors = new ArrayList<String>();
			String filesPath = Commits.getCommitsFilesPath(((BasicDBObject)dbObjectPull.get("head")).get("sha").toString(), ((BasicDBObject)dbObjectPull.get("base")).get("sha").toString(), Integer.parseInt(dbObjectPull.get("commits").toString()), Integer.parseInt(dbObjectPull.get("changed_files").toString()));
			String filesNames = filesPath.substring(1, filesPath.length()-1);
			String files[] = filesNames.split(", ");
			long numCommitsNoArquivo = 0L;
			DBCollection commitsC = db.getCollection("commits");
			
			String shaBase = ((BasicDBObject) dbObjectPull.get("base")).get("sha").toString();
			BasicDBObject queryBaseCommit = new BasicDBObject("sha", shaBase);
			DBObject baseCommitPull = commitsC.findOne(queryBaseCommit);
			
			String data = dataLimit(((BasicDBObject) ((BasicDBObject) baseCommitPull.get("commit")).get("author")).get("date").toString());
			
			BasicDBObject queryHead = new BasicDBObject("commit.author.date", new BasicDBObject("$lt",((BasicDBObject)((BasicDBObject) baseCommitPull.get("commit")).get("author")).get("date").toString()).append("$gt", data)); //consulta com data menor que a data do pull request
			queryHead.append("html_url", new BasicDBObject("$regex", "(linguist)"));
			
			DBCursor dbcCommits = commitsC.find(queryHead);
//			numCommitsNoArquivo = 0L;
			int count=0,max=Integer.MIN_VALUE;
			String a="";
			for (int i=0 ; i<files.length ; i++) {
				for (DBObject dbo: dbcCommits){
					BasicDBList commitFilesList = (BasicDBList) dbo.get("files");
					for (Object object : commitFilesList){
						if(((String) ((BasicDBObject) object).get("filename")).equals(files[i])){
							numCommitsNoArquivo++;
							try{
								authors.add( ((BasicDBObject) ((BasicDBObject) dbo.get("commit")).get("author")).get("email").toString());
							}catch(NullPointerException npe){
								System.err.println(dbo.get("sha"));
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
	                	System.out.println(a+" realizou "+ max+" commits"+" em "+files[i]);
	                }   
	            } 
			}
			System.out.println(dbObjectPull.get("number")+","+numCommitsNoArquivo+", "+authors.size()+", "+a);
		}
	}
	private static String dataLimit(String pullRequestDateString){
		//data 3 meses atrás.
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date pullRequestDate = null;
		String beforeDate="";
		try {
			pullRequestDate = formatter.parse(pullRequestDateString);
			GregorianCalendar pastDate = new GregorianCalendar();
			pastDate.setTime(pullRequestDate);
			pastDate.add(Calendar.DAY_OF_WEEK, -7);
			Date d = pastDate.getTime();
			beforeDate = formatter.format(d);
		} catch (ParseException e){
			System.err.println("Erro na conversão de data do autor do commit.");
		}
		return beforeDate;	
	}
	
}
