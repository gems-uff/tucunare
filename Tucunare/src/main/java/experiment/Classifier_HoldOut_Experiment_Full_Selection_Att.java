package experiment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CorrelationAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class Classifier_HoldOut_Experiment_Full_Selection_Att {
	//Variaveis est�ticas
	static String [][] matrizProb = null;

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception{
		//pasta com arquivos .arrf ou .csv
		File f = new File("D:/ICMLA2015/files/coletados/MultivaluedAttributes/");
		String [] listaArquivos = f.list(); //vetor de arquivos
		System.out.println("Project,Attributes,Ordem,Train,Test,Classifier,Rank,Accuracy");
		String [] projects = null;

		for(int a=0;a<listaArquivos.length;a++){ //la�o para percorrer todos os arquivos do vetor
			Instances instances = lerArquivo(f.toString()+"/"+listaArquivos[a]); //quantidade de tuplas de cada arquivo
			instances.setClassIndex(instances.numAttributes() - 1); //o �ltimo atributo � setado como a classe
			//Percent Split=================================================================================
			int totalInstances = instances.numInstances(); 
			int limitTest = 99;//int limitTrain = 90;
			//Para variar o test altera-se limitTest e window
			//Para variar o train altera-se apenas o w
			double windowTest = 1.0;
			//for(int windowTest=10;windowTest>=1;windowTest--)
			double [] soma = new double[3];
			double [] media = new double[3];

			for(int w=10;w<=limitTest;w++){
				for(int tr=10; tr<=10; tr++){
					ArrayList<Double> acc1 = new ArrayList<>();
					ArrayList<Double> acc2 = new ArrayList<>();
					ArrayList<Double> acc3 = new ArrayList<>();
					for(int att=1;att<114;att++){//Sele��o de atributos
						double percentTrain = tr;
						double percentTest = windowTest;

						int windowTestSize = totalInstances * (w) / 100;
						int trainSizeInicio = totalInstances * (w-tr) / 100;

						int trainSize = (int) (totalInstances * percentTrain / 100);
						int testSize = (int) (totalInstances * percentTest / 100);

						Instances train = new Instances(instances, trainSizeInicio, trainSize); 
						Instances test = new Instances(instances, windowTestSize, testSize); 
						Instances instancesTemp = new Instances(instances, trainSizeInicio, (trainSize+testSize));
						train.setClassIndex(train.numAttributes() - 1); 
						test.setClassIndex(test.numAttributes() - 1);

						//caracter�sticas da base======================================================================
						int[] distClasses =  instancesTemp.attributeStats(instancesTemp.classIndex()).nominalCounts;//vetor com as quantidades de cada classe da base
						int numClasses = distClasses.length;
						Arrays.sort(distClasses); //ordena o vetor das quantidades para imprimir as classes majorit�rias
						matrizProb = new String[distClasses.length][2];
						double [] top = new double[distClasses.length];
						//tipo de arquivo
						if(listaArquivos[a].contains(".csv"))
							projects = listaArquivos[a].split(".csv");
						else
							projects = listaArquivos[a].split(".arff");
						//double somaMajoritarias = 0;
						//for (int i = numClasses-1; i >= numClasses-1; i--){//la�o para imprimir as classes majorit�rias e percentuais
						//if(distClasses[i]!=0)
						//System.out.println(projects[0].toString()+","+percentTrain+","+percentTest+","+"MC,"+(distClasses.length-i)+","+distClasses[i]+","+String.valueOf(new DecimalFormat("0.##").format((double)(distClasses[i]*100)/instancesTemp.numInstances())).replace(',', '.'));
						//somaMajoritarias += distClasses[i];
						//}

						String [] cls = retornaClassificadores(); //Retorna os classificadores que ser�o executados
						//sele�ao de atributos==========================================================================
						AttributeSelection attSelection = new AttributeSelection();
						CorrelationAttributeEval eva = new CorrelationAttributeEval();
						attSelection.setEvaluator(eva);
						attSelection.setRanking(true);
						Ranker rank = new Ranker();
						rank.setNumToSelect(att);
						rank.setThreshold(-1.7976931348623157E308);
						attSelection.setSearch(rank);
						attSelection.SelectAttributes(train);
						train = attSelection.reduceDimensionality(train);
						test = attSelection.reduceDimensionality(test);
						
						for(int j=0;j<cls.length;j++){
							Classifier classifier = retornaClassificador(cls[j]);//inst�ncia do classificador
							//System.out.println("Classificador: "+classifier.getClass().getSimpleName()+" - Ranking");
							//System.out.println("Quantidade de atributos utilizados: "+indices.length+"("+eva.toString()+")");
							double incorretos=0;//variav�is do ranking
							int tamClasses=0;

							classifier.buildClassifier(train);
							Evaluation eval = new Evaluation(instances);
							eval.evaluateModel(classifier, test);

							//coleta da distribui��o de probabilidade com cada conjunto de teste 
							for (int i = 0; i <test.numInstances(); i++){//i < numTestInstances
								String classLabel = test.instance(i).toString(test.classIndex()); // Classe atual
								double predictionIndex = classifier.classifyInstance(test.instance(i)); // Classe prevista
								String predictedClassLabel = test.classAttribute().value((int) predictionIndex);// Valor da classe prevista
								double[] predDist = classifier.distributionForInstance(test.instance(i));// Vetor com a Distribui��o de probilidade da tupla
								tamClasses=predDist.length;

								if(classLabel.equals(predictedClassLabel))//verifica se o 1� do ranking foi correto
									top[0]++;
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
								rankingDevelopers(numClasses, classLabel, top);
							}

							//fim split========================================================================================
							Arrays.sort(top); //ordena o vetor das quantidades para imprimir o ranking
							//Imprime Acur�cias
							acc1.add(top[distClasses.length-1]*100/test.numInstances());
							acc2.add(top[distClasses.length-2]*100/test.numInstances());
							acc3.add(top[distClasses.length-3]*100/test.numInstances());
							//}

						}//fim classificador

					}//fim sele��o de atributos
					int position=0,cont=1;
					double [] temp = new double[3];
					for (Double double1 : acc1) {
						if(double1>temp[0]){
							temp[0] = double1;
							temp[1] = acc2.get(cont-1);
							temp[2] = acc3.get(cont-1);
							position = cont;
						}
						cont++;
					}

					for(int c=1; c<=3; c++){
						soma[c-1] += temp[c-1];
						//System.out.println(projects[0].toString()+","+(w)+","+tr+","+windowTest+",RANDOM,2,SA,"+position+","+c+","+String.valueOf(new DecimalFormat("0.##").format((double)temp[c-1]).replace(',', '.')));
					}	

				}//fim treinamento	

			}//Fim testes 10 at� 99
			for(int c=1; c<=3; c++){
				media[c-1] = soma[c-1]/90;
				System.out.println(projects[0].toString()+","+4+","+10+","+windowTest+",RANDOM"+","+c+","+String.valueOf(new DecimalFormat("0.##").format(media[c-1]).replace(',', '.')));
			}
			//double media = somaTop/90;
			//System.out.println(projects[0].toString()+",1,"+"RANDOM+SA,1,"+String.valueOf(new DecimalFormat("0.##").format(media).replace(',', '.')));

		}	
		//FileWriter arquivo = new FileWriter("D:/Downloads/results new/data/rails/Experimento Completo/probabilidade_rails_Random.csv",true); //Arquivo de resultados

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
						}
					}	
			}
	}

	public static void rankingDevelopers(int numClasses, String classLabel, double [] top){
		for(int x=1;x<numClasses;x++){//verifica se o 2� ou o 3� do ranking foi correto
			String c = classLabel;
			while(x<numClasses){
				if(matrizProb[x][0].equals(c))
					top[x]++;
				x++;
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
