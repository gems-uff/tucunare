package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

import control.Commits;
//Classe para obter os contibuidores de um repositório
//Gera um arquivo para recuperar as informações de seguidores dos desenvolvedores
//para executar no GHTorrent basta digitar o nome do aqruivo gerado no diretório 
public class ContibutorsRepo {
	public static void main(String[] args) throws UnknownHostException {
		ArrayList<String> list = Commits.getContributorsList("infinispan", "infinispan");
		Collections.sort(list);
		File file = new File("d:/Ruby200/lib/ruby/gems/2.0.0/gems/ghtorrent-0.10/","infinispanContributors.sh");
		try{
			FileWriter fw = new FileWriter(file, false);
			fw.write("#!/bin/bash"+"\n");
			for (String user : list) {
				fw.write("ght-retrieve-user "+user+"\n");
			}
			fw.close();
		}catch(IOException ioe){
			System.err.println("Erro na criação do arquivo!");
		}
		System.out.println("Fim");
	}
}
