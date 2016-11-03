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

import weka.attributeSelection.ASSearch;

//import org.apache.commons.math3.stat.descriptive.moment.Variance;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
//import weka.attributeSelection.ClassifierSubsetEval;
//import weka.attributeSelection.ExhaustiveSearch;
//import weka.attributeSelection.GeneticSearch;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.ReliefFAttributeEval;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.EvaluationUtils;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.Puk;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.functions.supportVector.RegOptimizer;
import weka.classifiers.lazy.IBk;
import weka.classifiers.mlr.MLRClassifier;
import weka.classifiers.pmml.consumer.Regression;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.M5P;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Normalize;

public class Classifier_Experiment_Time {
	//Variaveis estaticas
	static String [][] matrizProb = null;
	
	public static void main(String[] args) throws Exception{
		//diretorio com arquivos .arrf ou .csv
		//File f = new File("D:/ICMLA2015/files/projects/A+B+C-all/");
		File f = new File("/home/manoel/wekafiles/Time/data/yu/continuous/");
		String [] listaArquivos = f.list(); //vetor de arquivos
		System.out.println("Dataset,Classifier,Accuracy");
		String [] projects = null;
		
		int [] cls = retornaClassificadores(); //Retorna os classificadores
		for(int j=0;j<cls.length;j++){
			for(int a=0;a<listaArquivos.length;a++){ //arquivos do diretorio
				Instances instances = lerArquivo(f.toString()+"/"+listaArquivos[a]); //quantidade de tuplas de cada arquivo
				instances.setClassIndex(instances.numAttributes() - 1); //classe
				//Split Train and Test=================================================================================
				int totalInstances = instances.numInstances(); 
				double test = 1.0; //Tamanho das bases de Teste
				int firstTrain = 10; //Tamanho das bases de Treino
				double [] soma = new double[5];
				double [] media = new double[5];
				
				double [][] m = null;//matrizes de confusão
				double [][] n = null;
				
				int count = 0;
				int windowTest = (int) (totalInstances * test / 100);
				int windowTrain = (int) (totalInstances * firstTrain / 100);
				int trainSizeInicio = 0;
				double [] acc = new double[(totalInstances/windowTest)-firstTrain];
				int window = (int) (windowTest);
				double somaR2 = 0;
				for(int w=windowTrain;w<=totalInstances-windowTest;w+=window){
					ArrayList<Double> acc1 = new ArrayList<Double>();
					ArrayList<Double> acc2 = new ArrayList<Double>();
					ArrayList<Double> acc3 = new ArrayList<Double>();
					ArrayList<Double> acc4 = new ArrayList<Double>();
					ArrayList<Double> acc5 = new ArrayList<Double>();
					
					trainSizeInicio = w-windowTrain;//comentar essa linha para acumular o treino  
					Instances trainInstances = new Instances(instances, trainSizeInicio, windowTrain);//testando w no lugar de windowTrain para acumular o treino 
					Instances testInstances = new Instances(instances, w, windowTest); 
					Instances instancesTemp = new Instances(instances, trainSizeInicio, (windowTest+windowTest));
					trainInstances.setClassIndex(trainInstances.numAttributes() - 1); 
					testInstances.setClassIndex(testInstances.numAttributes() - 1);
					//caracteristicas da base======================================================================
					int[] distClasses =  instancesTemp.attributeStats(instancesTemp.classIndex()).nominalCounts;//vetor com as quantidades de cada classe da base
					
					//int numClasses = distClasses.length;
//					Arrays.sort(distClasses); //ordena o vetor das quantidades para imprimir as classes majoritarias
//					matrizProb = new String[distClasses.length][2];
//					double [] top = new double[distClasses.length];
					//tipo de arquivo
					if(listaArquivos[a].contains(".csv"))
						projects = listaArquivos[a].split(".csv");
					else
						projects = listaArquivos[a].split(".arff");
					
					//classificador
					Classifier classifier = retornaClassificador(cls[j]);
					
					//Normalização para IBK
					if(cls[j]==4){
						Normalize norm = new Normalize();
						classifier.getCapabilities();
						norm.setInputFormat(trainInstances);
						norm.setInputFormat(testInstances);
						trainInstances=Normalize.useFilter(trainInstances, norm);
						testInstances=Normalize.useFilter(testInstances, norm);
					}
					/*
					//InfoGain para discretização
					AttributeSelection infoGain = infoGainD();
					infoGain.SelectAttributes(trainInstances);
					//atributos com infogain maior que zero
					int [] attributes = infoGain.selectedAttributes();
										
					//Discretização supervisionada
					Discretize discSuper = new Discretize();
					discSuper.setUseBetterEncoding(true);
					discSuper.setAttributeIndicesArray(attributes);
					discSuper.setInputFormat(trainInstances);
					trainInstances = Filter.useFilter(trainInstances, discSuper);
					testInstances = Filter.useFilter(testInstances, discSuper);
					
					//Discretização não supervisionada
					weka.filters.unsupervised.attribute.Discretize discUnsuper = new weka.filters.unsupervised.attribute.Discretize();
					discUnsuper.setAttributeIndicesArray(attributes);
					discUnsuper.setInvertSelection(true);
					discUnsuper.setBins(2);
					discUnsuper.setUseEqualFrequency(true);
					discUnsuper.setInputFormat(trainInstances);
					trainInstances = Filter.useFilter(trainInstances, discUnsuper);
					testInstances = Filter.useFilter(testInstances, discUnsuper);
					*/
					//Discretização não supervisionada com PKI
//					PKIDiscretize discPKIUnsuper = new PKIDiscretize();
//					discPKIUnsuper.setAttributeIndicesArray(attributes);
//					discPKIUnsuper.setInvertSelection(true);
//					discPKIUnsuper.setFindNumBins(true);
//					discPKIUnsuper.setInputFormat(trainInstances);
//					trainInstances = Filter.useFilter(trainInstances, discPKIUnsuper);
//					testInstances = Filter.useFilter(testInstances, discPKIUnsuper);
					
					//Selecao de atributos==========================================================================
					//relief
//					AttributeSelection attRelief = relief();
//					attRelief.SelectAttributes(trainInstances);
					//reduz dimensionalidade do modelo
//					trainInstances = attRelief.reduceDimensionality(trainInstances);
//					testInstances = attRelief.reduceDimensionality(testInstances);
					
					
					//InfoGain
//					AttributeSelection attInfoGain = infoGainAS();
//					attInfoGain.SelectAttributes(trainInstances);
					//importancia dos atributos
					//int [] attributes = attInfoGain.selectedAttributes();
					//double [][] ranked = attInfoGain.rankedAttributes();
					//for(int z=0;z<attributes.length-1;z++)
						//System.out.println(projects[0].toString()+","+(count+1)+","+(z+1)+","+String.valueOf(new DecimalFormat("0.###").format((double)ranked[z][1]).replace(',', '.'))+","+trainInstances.attribute(attributes[z]).name());
					//reduz dimensionalidade do modelo
//					trainInstances = attInfoGain.reduceDimensionality(trainInstances);
//					testInstances = attInfoGain.reduceDimensionality(testInstances);
					
					//CFS
//					AttributeSelection attCFS = cfsAS();
//					attCFS.SelectAttributes(trainInstances);
					//lista atributos
//					int [] attributes = attCFS.selectedAttributes();
//					att += attributes.length;
//					for(int z=0;z<attributes.length-1;z++)
//						System.out.println(projects[0].toString()+","+w+","+(z+1)+","+train.attribute(attributes[z]).name());
//					trainInstances = attCFS.reduceDimensionality(trainInstances);
//					testInstances = attCFS.reduceDimensionality(testInstances);
					
					
					//Wrapper
//					weka.filters.Filter filter = wrapperAS(classifier);
//					filter.setInputFormat(train);
//			        train = weka.filters.Filter.useFilter(train, filter);
//			        test = weka.filters.Filter.useFilter(test, filter);

					double incorretos=0;
					int tamClasses=0;
					classifier.buildClassifier(trainInstances);
					Evaluation eval = new Evaluation(testInstances);
					eval.evaluateModel(classifier, testInstances);
					
					//coleta da distribuicao de probabilidade com cada conjunto de teste 
					for (int i = 0; i <testInstances.numInstances(); i++){//i < numTestInstances
						String classLabel = testInstances.instance(i).toString(testInstances.classIndex()); // Classe atual
						double predictionIndex = classifier.classifyInstance(testInstances.instance(i)); // Classe prevista
						String predictedClassLabel = testInstances.classAttribute().value((int) predictionIndex);// Valor da classe prevista
						double[] predDist = classifier.distributionForInstance(testInstances.instance(i));// Vetor com a Distribui��o de probilidade da tupla
						tamClasses=predDist.length;

//						if(classLabel.equals(predictedClassLabel))//verifica se o 1 do ranking foi correto
//							top[0]++;
//						else
							incorretos++;
						//Valores nominais das classes.
						String[] classes = new String[predDist.length];
						for (int predDistIndex = 0; predDistIndex < predDist.length; predDistIndex++){//predDistIndex < predDist.length
							classes[predDistIndex] = testInstances.classAttribute().value(predDistIndex);
						}
						//constroi a matriz das probabilidades de cada classe na tupla
//						for(int x=0;x<tamClasses;x++){
//							for(int y=0;y<2;y++){
//								if(y==0)
//									matrizProb[x][y]= classes[x];
//								else
//									matrizProb[x][y]= String.valueOf(predDist[x]);
//							}	
//						}
						//ordena a matriz pela probabilidade de cada classe
//						ordenaMatriz(matrizProb, tamClasses);
//						if(numClasses > 2)
//							rankingDevelopers(numClasses, classLabel, top);
					}//end probabilidades

					//Acurácias
//					acc1.add((double)(top[0]*100)/testInstances.numInstances());
//					acc2.add((double)(top[1]*100)/testInstances.numInstances());
//					if(numClasses > 2){
//						acc3.add((double)(top[2]*100)/testInstances.numInstances());
//						acc4.add((double)(top[3]*100)/testInstances.numInstances());
//						acc5.add((double)(top[4]*100)/testInstances.numInstances());
//					}
					
//					acc[count] = acc1.get(0);
					count++;
					
//					soma[0] += acc1.get(0);
//					soma[1] += acc2.get(0);
//					if(numClasses > 2){
//						soma[2] += acc3.get(0);
//						soma[3] += acc4.get(0);
//						soma[4] += acc5.get(0);	
//					}
					
//					Imprime resultados para cada modelo(treino e teste)
					//System.out.println(projects[0].toString()+","+(w-1)+","+count+","+(trainSizeInicio)+","+retornaClassificador(cls[j]).getClass().getSimpleName()+","+String.valueOf(new DecimalFormat("0.##").format((double)acc1.get(0)).replace(',', '.')));

					//Imprime outras medidas
//					System.out.println(eval.toSummaryString());
//					System.out.println(eval.toMatrixString());

					
//					Coeficiente de corelação
					if(cls[j]>=7 || cls[j]<=12){
						somaR2 += Math.pow(eval.correlationCoefficient(), 2);
					}else{
						m = eval.confusionMatrix();//Captura matriz de confusão de cada modelo
						if(count==1)
							n=m;
						confusionMatrixFinal(m, n);
					}
//					Imprime resultados para cada modelo(treino e teste)
//					System.out.println(projects[0].toString()+","+(w-1)+","+count+","+(trainSizeInicio)+","+retornaClassificador(cls[j]).getClass().getSimpleName()+","+String.valueOf(new DecimalFormat("0.##").format((double)eval.correlationCoefficient()).replace(',', '.')));
			
					
				}//end instances
				
				double R2Avg = somaR2/count;
				
				//Desvio Padrão
//				Variance variancia = new Variance();
//				double sd = Math.sqrt(variancia.evaluate(acc)); 
				
				//Kernel for SMO
				String kernel = "";
				if(cls[j]==4)//Poly
					kernel = "Poly";
				if(cls[j]==5)
					kernel = "Puk";
				if(cls[j]==6)
					kernel = "RBF";
				
				//resultado pela matriz
//				double accMatriz = confusionMatrixAccuracy(n);
//				double kappaFinal = confusionMatrixKappa(n);
//				
//				double precision = confusionMatrixPrecision(n);
//				double recall = confusionMatrixRecall(n);
//				double fMeasure = (2 * (precision * recall)) / (precision + recall);
				
				//Imprime Resultado
				for(int c=1; c<=1; c++){
					media[c-1] = soma[c-1]/count;
					//System.out.println(projects[0].toString()+","+retornaClassificador(cls[j]).getClass().getSimpleName()+kernel+","+String.valueOf(new DecimalFormat("0.##").format((double)media[c-1]).replace(',', '.'))+","+String.valueOf(new DecimalFormat("0.####").format((double)(accMatriz)).replace(',', '.'))+","+String.valueOf(new DecimalFormat("0.####").format((double)(kappaFinal)).replace(',', '.')));
					System.out.println(projects[0].toString()+","+
										retornaClassificador(cls[j]).getClass().getSimpleName()+
										kernel+","+
//										String.valueOf(new DecimalFormat("0.####").format((double)(accMatriz)).replace(',', '.'))+","+
										String.valueOf(new DecimalFormat("0.####").format((double)(R2Avg)).replace(',', '.'))//+","+
//										String.valueOf(new DecimalFormat("0.####").format((double)(precision)).replace(',', '.'))+","+
//										String.valueOf(new DecimalFormat("0.####").format((double)(recall)).replace(',', '.'))+","+
//										String.valueOf(new DecimalFormat("0.####").format((double)(fMeasure)).replace(',', '.'))
										);
				}
				
//				Imprime matriz
				//confusionMatrixPrint(n);
				
			}//end files
		}//end array classifiers
	}//end main

//	Métodos estáticos
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
	
	public static int [] retornaClassificadores(){
		int [] classificadores = {12};//1->"J48",2->"NaiveBayes",3->"RandomForest",4->"Ibk",5->"SMO(Poly)",6->"SMO(Puk), 7->"SMO(RBF)"
		return classificadores;
	}

	public static Classifier retornaClassificador(int classificador) throws Exception{
		switch (classificador) {
		case 1: 
			return new J48();
		case 2: 
			return new NaiveBayes();
		case 3: 
			IBk ibk = new IBk(3);//fazer as variações ímpares de k
			return ibk;	
		case 4: 
			PolyKernel poly = new PolyKernel(); 
			SMO smo = new SMO();
			smo.setKernel(poly);
			return smo;
		case 5: 
			Puk puk = new Puk();
			SMO smo2 = new SMO();
			smo2.setKernel(puk);
			return smo2;
		case 6: 
			RBFKernel rbf = new RBFKernel();
			SMO smo3 = new SMO();
			smo3.setKernel(rbf);
			return smo3;
		case 7:
			LinearRegression lr = new LinearRegression();
			lr.setEliminateColinearAttributes(true);
			return lr;
		case 8: 
			RandomForest rf =new RandomForest();
			rf.setNumIterations(100); //setNumTrees(100);
			rf.setMaxDepth(0); rf.setNumExecutionSlots(2); rf.setSeed(1);rf.setNumFeatures(0);
			return rf;
		case 9:
			DecisionTable dt = new DecisionTable();
			dt.setUseIBk(true); dt.setBatchSize("100"); dt.setCrossVal(1);
	        BestFirst bf = new BestFirst(); 
	        bf.setLookupCacheSize(1); bf.setSearchTermination(5);
			dt.setSearch(bf);
			return dt;
		case 10:
			SMOreg smoRegPoly = new SMOreg(); smoRegPoly.setKernel(new PolyKernel());
			return smoRegPoly;
		case 11:
			M5P m5P = new M5P(); m5P.setMinNumInstances(4);
			return m5P;
		case 12:
//			MLRClassifier mlrLasso = new MLRClassifier();
//			Tag [] algorithms = MLRClassifier.TAGS_LEARNER;
//			SelectedTag lasso = new SelectedTag(87, algorithms);
//			mlrLasso.setRLearner(lasso);
//			return mlrLasso;
			MLRClassifier mlrLasso = new MLRClassifier();
//			Tag [] algorithms = MLRClassifier.TAGS_LEARNER;
//			SelectedTag lasso = new SelectedTag(87, algorithms);
			String [] lasso = "-learner regr.rpart -batch 100 -S 1".split(" ");
			mlrLasso.setOptions(lasso);
			return mlrLasso;
		default:
			return null;
		}
	}
	
	//Ordena matriz
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
		for(int x=1;x<5;x++){//verifica se o 2 at o 5 do ranking foi correto
			String c = classLabel;
			while(x<5){
				if(matrizProb[x][0].equals(c)){
					top[x]++;
					break;
				}	
				x++;
			}
		}
	}
	
	public static void confusionMatrixFinal(double [][] m, double [][] n){
		//acumula valores na matriz de confusão final
		for(int x=0;x<m.length;x++)
			for(int y=0;y<m.length;y++){
				n[x][y] += m[x][y];
			}
	}
	
	public static double confusionMatrixAccuracy(double [][] n){
		int somaDP = 0, somaTotal = 0;
		double accMatriz = 0;
		
		for(int x=0;x<n.length;x++)
			for(int y=0;y<n.length;y++){
				if(x==y)
					somaDP += n[x][y];//Diagonal Principal
			}
		
		for(int x=0;x<n.length;x++)
			for(int y=0;y<n.length;y++){
				somaTotal += n[x][y];//Soma total
			}
		
		accMatriz = ((double)somaDP/somaTotal)*100;
		return accMatriz;
	}
	
	public static double confusionMatrixPrecision(double [][] n){
		double TP = 0, somaFP = 0, precisionAgv = 0, precision = 0;
		for(int x=0;x<n.length;x++){
			for(int y=0;y<n.length;y++){
				if(x==y){
					TP = n[x][y];//TP
					somaFP = 0;
					for(int z=0;z<n.length;z++)
						somaFP += n[z][y];//Soma FP
					precision += TP/somaFP;
				}
			}
		}
		precisionAgv = precision/n.length;
		return precisionAgv;
	}
	
	public static double confusionMatrixRecall(double [][] n){
		double TP = 0, somaP = 0, recallAgv = 0, recall = 0;
		for(int x=0;x<n.length;x++){
			for(int y=0;y<n.length;y++){
				if(x==y){
					TP = n[x][y];//TP
					somaP = 0;
					for(int z=0;z<n.length;z++)
						somaP += n[x][z];//Soma P
					recall += TP/somaP;
					
				}
			}
		}
		recallAgv = recall/n.length;
		return recallAgv;
	}
	
	public static double confusionMatrixKappa(double [][] n){
		int somaDP = 0, somaTotal = 0;
		double accMatriz = 0;
		
		for(int x=0;x<n.length;x++)
			for(int y=0;y<n.length;y++){
				if(x==y)
					somaDP += n[x][y];//Diagonal Principal
			}
		for(int x=0;x<n.length;x++)
			for(int y=0;y<n.length;y++){
				somaTotal += n[x][y];//Soma total
			}
		
		//Calculo da medida kappa
		int [] linha = new int[n.length];
		int [] coluna = new int[n.length];
		
		for(int x=0;x<n.length;x++){
			int somaParcial=0;
			for(int y=0;y<n.length;y++){
				somaParcial += (int)n[x][y];
			}
			linha[x] = somaParcial;
		}
		
		for(int x=0;x<n.length;x++){
			int somaParcial=0;
			for(int y=0;y<n.length;y++){
				somaParcial += (int)n[y][x];
			}
			coluna[x] = somaParcial;
		}
		
		//vetores com as somas parciais
		double [] parcial = new double[n.length];
		for(int x=0;x<n.length;x++){
			parcial[x] = ((double)linha[x]/somaTotal * (double)coluna[x]/somaTotal);
		}
		double acaso = 0, kappaFinal;
		for(int x=0;x<n.length;x++){
			acaso += parcial[x];
		}
		
		accMatriz = ((double)somaDP/somaTotal)*100;
		
		kappaFinal = (((double)somaDP/somaTotal) - acaso) / (1 - acaso); 
		return kappaFinal;
	}
	
	public static void confusionMatrixPrint(double [][] n){
		//imprime matriz de confusão final
		for(int x=0;x<n.length;x++)
			for(int y=0;y<n.length;y++){
				if(y==n.length-1)
					System.out.println((int)n[x][y]+" ");
				else
					System.out.print((int)n[x][y]+",");
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
	
//	Métodos para seleção de atributos
	public static AttributeSelection infoGainAS(){//InfoGain
		AttributeSelection attSelection = new AttributeSelection();
		InfoGainAttributeEval info = new InfoGainAttributeEval();
		Ranker rank = new Ranker();
		attSelection.setRanking(true);
//		rank.setThreshold(0.0);
		rank.setNumToSelect(30);
		attSelection.setEvaluator(info);
		attSelection.setSearch(rank);
		return attSelection;
	}
	
	public static AttributeSelection relief(){//InfoGain
		AttributeSelection attSelection = new AttributeSelection();
		ReliefFAttributeEval info = new ReliefFAttributeEval();
		Ranker rank = new Ranker();
		attSelection.setRanking(true);
		rank.setThreshold(0.0);
		//rank.setNumToSelect(30);
		attSelection.setEvaluator(info);
		attSelection.setSearch(rank);
		return attSelection;
	}
	
	public static AttributeSelection infoGainD(){//InfoGain
		AttributeSelection attSelection = new AttributeSelection();
		InfoGainAttributeEval info = new InfoGainAttributeEval();
		Ranker rank = new Ranker();
		attSelection.setRanking(true);
		rank.setThreshold(0.0);
		attSelection.setEvaluator(info);
		attSelection.setSearch(rank);
		return attSelection;
	}
	
	public static AttributeSelection cfsAS() throws Exception{
		AttributeSelection attSelection = new AttributeSelection();
		CfsSubsetEval cfs = new CfsSubsetEval();
		BestFirst best = new BestFirst();
		String [] op = {"-P", "","-D", "1","-N", "5"};//-P 1-22 -D 1 -N 5
		best.setOptions(op);
		attSelection.setEvaluator(cfs);
		attSelection.setSearch(best);
		return attSelection;
	}
	
	public static AttributeSelection cfsGenetic() throws Exception{
		AttributeSelection attSelection = new AttributeSelection();
		CfsSubsetEval cfs = new CfsSubsetEval();
//		GeneticSearch genetic = new GeneticSearch();
		String [] op = {"-Z", "20","-G", "50", "-C", "0.6", "-M", "0.033", "-R", "20", "-S", "-1"};//-P 1-22 -D 1 -N 5, weka.attributeSelection.GeneticSearch -Z 20 -G 20 -C 0.6 -M 0.033 -R 20 -S 1
//		genetic.setOptions(op);
//		attSelection.setEvaluator(cfs);
//		attSelection.setSearch(genetic);
		return attSelection;
	}
	
	public static AttributeSelection cfsExhaustive() throws Exception{
		AttributeSelection attSelection = new AttributeSelection();
		CfsSubsetEval cfs = new CfsSubsetEval();
//		ExhaustiveSearch exhaustive = new ExhaustiveSearch();
//		attSelection.setEvaluator(cfs);
//		attSelection.setSearch(exhaustive);
		return attSelection;
	}
	
	public static AttributeSelection cfsGreedyStepwise() throws Exception{
		AttributeSelection attSelection = new AttributeSelection();
		CfsSubsetEval cfs = new CfsSubsetEval();
		GreedyStepwise gStep = new GreedyStepwise();
		gStep.setNumExecutionSlots(2);
		gStep.setThreshold(-1.7976931348623157E308);
		gStep.setNumToSelect(-1);
		//gStep.setSearchBackwards(true);
		attSelection.setEvaluator(cfs);
		attSelection.setSearch(gStep);
		return attSelection;
	}
	
	public static weka.filters.supervised.attribute.AttributeSelection wrapperAS(Classifier classifier) throws Exception{
		BestFirst best = new BestFirst();
		String [] op = {"-P", "","-D", "1","-N", "5"};//-P 1-22 -D 1 -N 5
		best.setOptions(op);
//		GreedyStepwise gre = new GreedyStepwise();
//		gre.setSearchBackwards(true);
//		gre.setNumExecutionSlots(4);
		weka.filters.supervised.attribute.AttributeSelection attSelection = new weka.filters.supervised.attribute.AttributeSelection();
		WrapperSubsetEval wra = new WrapperSubsetEval();
//		Classifier c = new J48();
		wra.setClassifier(classifier);
		wra.setFolds(2);
		attSelection.setEvaluator(wra);
		attSelection.setSearch(best);
		return attSelection;
	}
}
