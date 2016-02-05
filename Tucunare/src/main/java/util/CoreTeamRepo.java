package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import control.Commits;

public class CoreTeamRepo {
	public static void main(String[] args) throws UnknownHostException {
		ArrayList<String> list = Commits.getCoreTeamList3("js_of_ocaml","ocsigen");
		//Collections.sort(list);
		File file = new File("c:/Ruby200/lib/ruby/gems/2.0.0/gems/ghtorrent-0.10/projects/100-200/","CoreTeam4.sh");
		try{
			FileWriter fw = new FileWriter(file, true);
			fw.write("#!/bin/bash"+"\n");
			for (String object : list) {
				fw.write("ght-retrieve-user "+object.toString()+"\n");
			} 
			fw.close();
		}catch(IOException ioe){
			System.err.println("Erro na criação do arquivo!");
		}
		System.out.println("Fim");
	}
}