package control;

import java.net.UnknownHostException;

import util.Connect;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class TestPull {
	public static void main(String[] args) throws UnknownHostException {
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbc = db.getCollection("pull_requests");
		BasicDBObject query = new BasicDBObject("repo","katello"); //consulta com query
		DBCursor cursor = dbc.find(query);
		System.out.println(cursor.count());
		//	Consulta toda a collection
		for (DBObject dbObject : cursor) {
			//alocação
			String user = ((BasicDBObject)dbObject.get("user")).get("login").toString();
			String number = dbObject.get("number").toString();
			String closed_by = Issues.getClosedbyPull((Integer) dbObject.get("number"), "katello");
			
			if(dbObject!=null && !closed_by.equals(user)){
				//String files = Commits.getCommitsFiles(((BasicDBObject)dbObject.get("head")).get("sha").toString(), ((BasicDBObject)dbObject.get("base")).get("sha").toString());
				//arquivos
				String filesPath = Commits.getCommitsFilesPath(((BasicDBObject)dbObject.get("head")).get("sha").toString(), ((BasicDBObject)dbObject.get("base")).get("sha").toString(), Integer.parseInt(dbObject.get("commits").toString()), Integer.parseInt(dbObject.get("changed_files").toString()));
				//			String filesPath = Commits.getCommitsFilesPath2(((BasicDBObject)dbObject.get("head")).get("sha").toString(), ((BasicDBObject)dbObject.get("base")).get("sha").toString());
				String files="";
				
				if(!filesPath.equals("")){
					//int j = filesPath.lastIndexOf("]");
					files = filesPath.substring(1, filesPath.length()-1);
					
				}
				
				//tratamento para caminho dos arquivos para buscar o último diretório
				String dirFinal = "";
				String [] path = files.split(", ");
				for(int x=0; x < path.length; x++){
					int lastBarIndex = 0;
					lastBarIndex = path[x].lastIndexOf("/");
					if(lastBarIndex<0 && x == 0){
						dirFinal += "root";
						continue;
					}else 
						if(lastBarIndex<0){
							dirFinal += "|root";
						continue;
					}
					
					String str = path[x].substring(0, lastBarIndex);
					if(x>=1)	
						dirFinal += "|"+str;
					else
						dirFinal += str;
				}

				
				//retorna apenas o [ultimo diretorio
//				for(int x=0; x < path.length; x++){
//					int lastBarIndex = 0, secondLastIndex = 0;
//					lastBarIndex = path[x].lastIndexOf("/");
//					if(lastBarIndex<0 && x == 0){
//						dirFinal += "root";
//						continue;
//					}else 
//						if(lastBarIndex<0){
//							dirFinal += "|root";
//						continue;
//					}
//					String str = path[x];
//					secondLastIndex = str.lastIndexOf("/", lastBarIndex-1);
//					if(secondLastIndex<0){
//						if(!str.equals(""))
//							dirFinal += "|"+str.substring(0, lastBarIndex);
//						else
//							dirFinal += "root|";
//						continue;
//					}	
//					dirFinal += "|"+path[x].substring(secondLastIndex+1, lastBarIndex);
//				}

				String participantes = Users.getParticipants(number, dbObject.get("repo").toString());
				
				System.out.println(dbObject.get("id")+", "+
					dbObject.get("number")+", "+//+commitsPorArquivos+", "+

//					dbObject.get("owner")+"/"+dbObject.get("repo")+", "+
					//comments+", commits:"+
					dbObject.get("commits")+", "+
					dbObject.get("changed_files")+"= "+
					participantes+", "+
					//filesPath+", "+
//					dbObject.get("additions")+", "+
//					dbObject.get("deletions")//+", "+
					dirFinal

					);
			}
		}
		Connect.getInstance().close();
	}

}
