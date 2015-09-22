package experiment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CorrelationAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AddClassification;

public class ClassifierTop3_SAC {
	//Variaveis est�ticas
	static String [][] matrizProb = null;

	public static void main(String[] args) throws Exception{
		File f = new File("D:/ICMLA2015/files/frequentes/rank/scala/");
		//pasta com arquivos .arrf
		//File f = new File("D:/Downloads/results new/data/base artigo/projetos/time/");
		String [] listaArquivos = f.list(); //vetor de arquivos
		for(int a=0;a<listaArquivos.length;a++){ //la�o para percorrer todos os arquivos do vetor
			Instances instances = lerArquivo(f.toString()+"/"+listaArquivos[a]); //quantidade de tuplas de cada arquivo
			instances.setClassIndex(instances.numAttributes() - 1); //o �ltimo atributo � setado como a classe
            //System.out.println(instances.toString());
			//caracter�sticas da base
			System.out.println("Projeto: "+listaArquivos[a]);
			System.out.println("Quantidade de inst�ncias: "+instances.numInstances());
			System.out.println("Quantidade de desenvolvedores:"+instances.classAttribute().numValues()+"\t\t%");
			int[] distClasses =  instances.attributeStats(instances.classIndex()).nominalCounts;//vetor com as quantidades de cada classe da base
			Arrays.sort(distClasses); //ordena o vetor das quantidades para imprimir as classes majorit�rias
			matrizProb = new String[distClasses.length][2];

			double somaMajoritarias = 0;
			for (int i = distClasses.length-1; i >= (distClasses.length-3); i--){//la�o para imprimir as classes majorit�rias e percentuais
				System.out.println("Tamanho da classe majorit�ria "+(distClasses.length-i)+"="+distClasses[i]+"\t\t"+new DecimalFormat("0.##").format((double)(distClasses[i]*100)/instances.numInstances()));
				somaMajoritarias += distClasses[i];
			}
			System.out.println("-----------------------------------------------------");
			System.out.println("Total de inst�ncias majorit�rias: "+somaMajoritarias+"\t"+ new DecimalFormat("0.##").format((somaMajoritarias*100)/instances.numInstances()));

			int totalInstances = instances.numInstances(); 
            String [] cls = retornaClassificadores(); //Retorna os classificadores que ser�o executados
			
          //sele�ao de atributos
			AttributeSelection attSelection = new AttributeSelection();
			CorrelationAttributeEval eva = new CorrelationAttributeEval();
			
			
			attSelection.setEvaluator(eva);
			attSelection.setRanking(true);
			Ranker rank = new Ranker();
			rank.setNumToSelect(33); //numero de atributos para cada projeto
			rank.setThreshold(-1.7976931348623157E308);
			attSelection.setSearch(rank);
			
			
            for(int j=0;j<cls.length;j++){
				Classifier classifier = retornaClassificador(cls[j]);//inst�ncia do classificador
				classifier.buildClassifier(instances);
				double top1=0, top2=0, top3=0, incorretos=0;//variav�is do ranking
				int tamClasses=0;
				//Cross Validation========================================================================================
				Random rand = new Random(1);//randomize
				Instances randData = new Instances(instances);
				randData.randomize(rand);
				int folds = 10;
				if (randData.classAttribute().isNominal())
					randData.stratify(folds);
				// performance do cross-validation
				Evaluation eval = new Evaluation(randData);
				for (int n = 0; n < folds; n++) {
					Instances test = randData.testCV(folds, n);
					Instances train = randData.trainCV(folds, n, rand);
					
					//sele��o em cada fold de treinamento
					attSelection.SelectAttributes(train);
					train = attSelection.reduceDimensionality(train);
					test = attSelection.reduceDimensionality(test);
					
					// constroi e avalia o classificador
					classifier.buildClassifier(train);
					eval.evaluateModel(classifier, test);
					int numTestInstances = test.numInstances();
					//coleta da distribui��o de probabilidade com cada conjunto de teste 
					for (int i = 0; i <numTestInstances; i++){//i < numTestInstances
						String trueClassLabel = test.instance(i).toString(test.classIndex()); // Classe atual
						double predictionIndex = classifier.classifyInstance(test.instance(i)); // Classe prevista
						String predictedClassLabel = test.classAttribute().value((int) predictionIndex);// Valor da classe prevista
						double[] predDist = classifier.distributionForInstance(test.instance(i));// Vetor com a Distribui��o de probilidade da tupla
						tamClasses=predDist.length;
						if(trueClassLabel.equals(predictedClassLabel))//verifica se o 1� do ranking foi correto
							top1++;
						else
							incorretos++;
						//Valores nominais das classes.
						String[] classes = new String[predDist.length];
						for (int predDistIndex = 0; predDistIndex < predDist.length; predDistIndex++){//predDistIndex < predDist.length
							classes[predDistIndex] = test.classAttribute().value(predDistIndex);
						}
						//la�o que constroi a matriz das probabilidades de cada classe na tupla
						for(int x=0;x<tamClasses;x++){
							for(int y=0;y<2;y++){
								if(y==0)
									matrizProb[x][y]= classes[x];
								else
									matrizProb[x][y]= String.valueOf(predDist[x]);
							}	
						}

						//ordena a matriz pela probabilidade de cada classe
						ordenaMatriz(matrizProb, tamClasses);

						for(int x=1;x<3;x++){//verifica se o 2� ou o 3� do ranking foi correto
							String c = trueClassLabel;
							if(matrizProb[1][0].equals(c)){
								top2++; break;}
							if(matrizProb[2][0].equals(c)){
								top3++;break;}
						}

					}
				}
				System.out.println();
				//fim cross validation========================================================================================
				System.out.println("=====================================================");
				System.out.println("Classificador: "+classifier.getClass().getSimpleName()+" - Ranking at� 3");
				System.out.println("Corretos\t%Posi��o\t%Total");
				System.out.println("Top1:"+(int)top1+" \t"+new DecimalFormat("0.##").format((top1*100)/totalInstances));
				System.out.println("Top2:"+(int)top2+" \t"+new DecimalFormat("0.##").format((top2*100)/totalInstances)+"\t\t"+new DecimalFormat("0.##").format(((top1+top2)*100)/totalInstances));
				System.out.println("Top3:"+(int)top3+" \t"+new DecimalFormat("0.##").format((top3*100)/totalInstances)+"\t\t"+new DecimalFormat("0.##").format(((top1+top2+top3)*100)/totalInstances));
				double somaTop3 = top1+top2+top3;
				System.out.println("-----------------------------------------------------");
				System.out.println("Total Corretos:"+(int)somaTop3+"\t\t"+new DecimalFormat("0.##").format((somaTop3*100)/totalInstances));
				System.out.println("Incorretos:"+(int)(incorretos-(top2+top3))+"\t\t\t"+new DecimalFormat("0.##").format(((incorretos-(top2+top3))*100)/totalInstances));

				System.out.println();
			}
		}
		//FileWriter arquivo = new FileWriter("D:/Downloads/results new/data/rails/Experimento Completo/probabilidade_rails_Random.csv",true); //Arquivo de resultados

	}//end main

	public static Instances lerArquivo(String caminho) throws FileNotFoundException, IOException{
		BufferedReader arquivo;
		arquivo = new BufferedReader(new FileReader(caminho)); //Arquivo ARFF
		Instances data = new Instances(arquivo);
		arquivo.close();
		return data;
	}

	public static String [] retornaClassificadores(){
		String [] classificadores = {"RandomForest"};//"J48","NaiveBayes","RandomForest","Ibk","SMO"
		return classificadores;
	}

	public static Classifier retornaClassificador(String classificador){
		switch (classificador) {
		case "NaiveBayes": 
			return new NaiveBayes();
		case "smo": 
			return new SMO();
		case "RandomForest": 
			RandomForest rf =new RandomForest();
			rf.setNumTrees(100); rf.setMaxDepth(0); rf.setNumExecutionSlots(4); rf.setSeed(1);rf.setNumFeatures(0);
			return rf;
		case "Ibk": 
			return new IBk(3);	
		case "J48": 
			return new J48();	
		default:
			return null;
		}
	}

	//	M�todo ordena matriz
	public static void ordenaMatriz(String [][] matriz, int tamClasses){
		//ordenando matriz de probabilidades (metodo bolha)
		
		for(int z=0;z<tamClasses;z++)
			for(int x=0;x<tamClasses-1;x++){
				double aux1=0; 
				String aux2="";
				for(int y=0;y<2;y++)
					if(y==1){
						double a = Double.parseDouble(matriz[x][y]);
						double b = Double.parseDouble(matriz[x+1][y]);
						if(a<b){
							aux1 = Double.parseDouble(matriz[x][y]);
							aux2 = matriz[x][y-1];
							matriz[x][y] = matriz[x+1][y];
							matriz[x][y-1] = matriz[x+1][y-1];
							matriz[x+1][y] = String.valueOf(aux1);
							matriz[x+1][y-1] = aux2;
//						if(matriz[x][y]<matriz[x+1][y]){
//							aux1 = matriz[x][y];
//							aux2 = matriz[x][y-1];
//							matriz[x][y] = matriz[x+1][y];
//							matriz[x][y-1] = matriz[x+1][y-1];
//							matriz[x+1][y] = aux1;
//							matriz[x+1][y-1] = aux2;
						}
					}	
			}
	}
	public static void imprimeProbabilidades(String [][] matrizMedia, int tamClasses) throws IOException{
		FileWriter arquivoProb = new FileWriter("D:/Downloads/results new/data/rails/Experimento Completo/probabilidade_porClasse_xbmc_random.csv",true); //Arquivo de resultados

		for(int x=0;x<tamClasses;x++){
			for(int y=0;y<2;y++){
				if(y==0)
					arquivoProb.write(matrizMedia[x][y]+";");
				else
					arquivoProb.write(new DecimalFormat("0.###").format(matrizMedia[x][y])+"\n");
			}

		}
		arquivoProb.close();
	}
}
