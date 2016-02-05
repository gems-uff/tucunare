package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

import control.Commits;
//Classe para obter os contibuidores de um repositório
//Gera um arquivo para recuperar as informações de seguidores dos desenvolvedores
//para executar no GHTorrent basta digitar o nome do aqruivo gerado no diretório 
public class ContibutorsRepo {
	public static void main(String[] args) throws UnknownHostException {
		Object [] list = Commits.getContributorsList("infinispan", "infinispan");
		Arrays.sort(list);
		File file = new File("d:/Ruby200/lib/ruby/gems/2.0.0/gems/ghtorrent-0.10/","infinispanContributors.sh");
		try{
			FileWriter fw = new FileWriter(file, false);
			fw.write("#!/bin/bash"+"\n");
			for (int i = 0; i < list.length; i++) {
				Object object = list[i];
				fw.write("ght-retrieve-user "+object.toString()+"\n");
			} 
			fw.close();
		}catch(IOException ioe){
			System.err.println("Erro na criação do arquivo!");
		}
		System.out.println("Fim");
	}
}