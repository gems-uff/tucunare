package experiment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;

import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class Top5Developers {

	public static void main(String[] args) throws Exception{
		File f = new File("/home/manoel/wekafiles/Time/data/new-attributes/discrete/");
		String [] listaArquivos = f.list();
		for(int a=0;a<listaArquivos.length;a++){
			Instances instances = lerArquivo(f.toString()+"/"+listaArquivos[a]);
			instances.setClassIndex(instances.numAttributes() - 1);
			int totalInstances = instances.numInstances(); 
			instances.setClassIndex(instances.numAttributes()-1);// atributo classe
			double train = 10.0;
			
			int windowTrain = (int) (totalInstances * train / 100);
			int windowTest = (int) (totalInstances - windowTrain);
			Instances testInstances = new Instances(instances, windowTrain, windowTest);
			
			//		caracter�sticas da base
//			System.out.println(listaArquivos[a]+","+instances.classAttribute().numValues());
//			System.out.println("Quantidade de inst�ncias: "+instances.numInstances());
//			System.out.println("Quantidade de desenvolvedores:"+instances.classAttribute().numValues());
			int[] distClasses =  instances.attributeStats(instances.classIndex()).nominalCounts;
			Arrays.sort(distClasses);
//			System.out.println(listaArquivos[a]+","+new DecimalFormat("0.##").format((double)(distClasses[distClasses.length-1]*100)/instances.numInstances())+","+instances.numInstances());
			String [] projects = listaArquivos[a].split(".arff");
			System.out.print(projects[0].toString()+",CM,");
			double somaMajoritarias = 0;
			for (int i = distClasses.length-1; i >= (distClasses.length-1); i--){
				System.out.println(String.valueOf(String.valueOf(new DecimalFormat("0.##").format((double)(distClasses[i]*100)/instances.numInstances()).replace(',', '.'))));
				
				somaMajoritarias += distClasses[i];
			}
			//System.out.println("");
			///System.out.println("Soma CM3: "+new DecimalFormat("0.##").format((somaMajoritarias*100)/testInstances.numInstances()));
			//System.out.println("-----------------------------------------------------");
			
		}
	}//end main

	public static Instances lerArquivo(String caminho) throws FileNotFoundException, IOException{
		BufferedReader arquivo;
		Instances data = null;
		boolean csv = caminho.contains(".csv");
		// load CSV
		if(csv){
			CSVLoader loader = new CSVLoader();
			loader.setSource(new File(caminho));
			data = loader.getDataSet();
		}
		if(!csv){//load ARFF
			arquivo = new BufferedReader(new FileReader(caminho)); //Arquivo ARFF
			data = new Instances(arquivo);
			arquivo.close();
		}
		return data;
	}

}




















