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
import java.util.List;

//import org.apache.commons.math3.stat.descriptive.moment.Variance;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
//import weka.attributeSelection.ClassifierSubsetEval;
import weka.attributeSelection.CorrelationAttributeEval;
//import weka.attributeSelection.ExhaustiveSearch;
//import weka.attributeSelection.GeneticSearch;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.ReliefFAttributeEval;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.Puk;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.PKIDiscretize;

public class Classifier_Experiment_Time_Discrete {
	//Variaveis estaticas
	static String [][] matrizProb = null;
	
	public static void main(String[] args) throws Exception{
		//diretorio com arquivos .arrf ou .csv
		File f = new File("/home/manoel/wekafiles/Time/data/new-attributes/discrete-2/");
		//File f = new File("D:/Universidade/Disciplinas/TESI - II/2015/tesi2/bases tempo/baseDiscreta/teste");
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
				double test = 1.0;
				int firstTrain = 10;
				double [] soma = new double[5];
				double [] media = new double[5];
				
				double [][] m = null;//matrizes de confusão
				double [][] n = null;
				
				int count = 0;
				int windowTest = (int) (totalInstances * test / 100);
				int windowTrain = (int) (totalInstances * firstTrain / 100);
				int trainSizeInicio = 0;
				int window = (int) (windowTest);
				for(int w=windowTrain;w<=totalInstances-windowTest;w+=window){
					
					trainSizeInicio = w-windowTrain;  
					Instances trainInstances = new Instances(instances, trainSizeInicio, windowTrain); 
					Instances testInstances = new Instances(instances, w, windowTest); 
					trainInstances.setClassIndex(trainInstances.numAttributes() - 1); 
					testInstances.setClassIndex(testInstances.numAttributes() - 1);
					
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
					
					//Discretização não supervisionada com PKI
//					PKIDiscretize discPKIUnsuper = new PKIDiscretize();
//					discPKIUnsuper.setAttributeIndicesArray(attributes);
//					discPKIUnsuper.setInvertSelection(true);
//					discPKIUnsuper.setFindNumBins(true);
//					discPKIUnsuper.setInputFormat(trainInstances);
//					trainInstances = Filter.useFilter(trainInstances, discPKIUnsuper);
//					testInstances = Filter.useFilter(testInstances, discPKIUnsuper);
					*/
					
					//Selecao de atributos==========================================================================
					//InfoGain
//					AttributeSelection attInfoGain = infoGainAS();
//					attInfoGain.SelectAttributes(trainInstances);
					//importancia dos atributos
//					int [] attributes2 = attInfoGain.selectedAttributes();
//					double [][] ranked = attInfoGain.rankedAttributes();
//					for(int z=0;z<attributes2.length-1;z++)
//						System.out.println(projects[0].toString()+","+(count+1)+","+(z+1)+","+String.valueOf(new DecimalFormat("0.###").format((double)ranked[z][1]).replace(',', '.'))+","+trainInstances.attribute(attributes[z]).name());
					//reduz dimensionalidade do modelo
//					trainInstances = attInfoGain.reduceDimensionality(trainInstances);
//					testInstances = attInfoGain.reduceDimensionality(testInstances);
					
					//cfsGreedyStepwise
//					AttributeSelection attcfsGreedyStepwise = cfsGreedyStepwise();
//					attcfsGreedyStepwise.SelectAttributes(train);
//					train = attcfsGreedyStepwise.reduceDimensionality(train);
//					test = attcfsGreedyStepwise.reduceDimensionality(test);
					
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
					
					
//					Relief
//					AttributeSelection attRelief = relief();
//					attRelief.SelectAttributes(trainInstances);
//					trainInstances = attRelief.reduceDimensionality(trainInstances);
//					testInstances = attRelief.reduceDimensionality(testInstances);
					
					//cfsGenetic
//					AttributeSelection attCfsExhaustive = cfsExhaustive();
//					attCfsExhaustive.SelectAttributes(train);
//					train = attCfsExhaustive.reduceDimensionality(train);
//					test = attCfsExhaustive.reduceDimensionality(test);
					
					//Wrapper
//					weka.filters.Filter filter = wrapperAS(classifier);
//					filter.setInputFormat(train);
//			        train = weka.filters.Filter.useFilter(train, filter);
//			        test = weka.filters.Filter.useFilter(test, filter);

					classifier.buildClassifier(trainInstances);
					Evaluation eval = new Evaluation(testInstances);
					eval.evaluateModel(classifier, testInstances);
				
					count++;
					
					m = eval.confusionMatrix();
					if(count==1)
						n=m;
					confusionMatrixFinal(m, n);
				}//end instances
				
				//Desvio Padrão
//				Variance variancia = new Variance();
//				double sd = Math.sqrt(variancia.evaluate(acc)); 
				
				//Kernel for SMO
				String kernel = "";
				if(cls[j]==5)//Poly
					kernel = "Poly";
				if(cls[j]==6)
					kernel = "Puk";
				if(cls[j]==7)
					kernel = "RBF";
				
				//resultado pela matriz
				double accMatriz = confusionMatrixAccuracy(n);
				double kappaFinal = confusionMatrixKappa(n);
		
				//Imprime Resultado
				for(int c=1; c<=1; c++){
					System.out.println(projects[0].toString()+","+retornaClassificador(cls[j]).getClass().getSimpleName()+kernel+","+String.valueOf(new DecimalFormat("0.##").format((double)(accMatriz)).replace(',', '.')));
				}
				
//				Imprime matriz
//				confusionMatrixPrint(n);
				
//				
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
		int [] classificadores = {3};//1->"J48",2->"NaiveBayes",3->"RandomForest",4->"Ibk",5->"SMO(Poly)",6->"SMO(Puk), 7->"SMO(RBF)"
		return classificadores;
	}

	public static Classifier retornaClassificador(int classificador){
		switch (classificador) {
		case 1: 
			return new J48();	
		case 2: 
			return new NaiveBayes();
		case 3: 
			RandomForest rf =new RandomForest();
			rf.setNumIterations(100); rf.setMaxDepth(0); rf.setNumExecutionSlots(2); rf.setSeed(1);//rf.setNumFeatures(0);
			return rf;
		case 4: 
			IBk ibk = new IBk(7);//fazer as variações ímpares de k
			return ibk;	
		case 5: 
			PolyKernel poly = new PolyKernel(); 
			SMO smo = new SMO();
			smo.setKernel(poly);
			return smo;
		case 6: 
			Puk puk = new Puk();
			SMO smo2 = new SMO();
			smo2.setKernel(puk);
			return smo2;
		case 7: 
			RBFKernel rbf = new RBFKernel();
			SMO smo3 = new SMO();
			smo3.setKernel(rbf);
			return smo3;
		default:
			return null;
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
	
	
//	Métodos para seleção de atributos
	public static AttributeSelection infoGainAS(){//InfoGain
		AttributeSelection attSelection = new AttributeSelection();
		InfoGainAttributeEval info = new InfoGainAttributeEval();
		Ranker rank = new Ranker();
		attSelection.setRanking(true);
//		rank.setThreshold(0.0);
		rank.setNumToSelect(20);
		attSelection.setEvaluator(info);
		attSelection.setSearch(rank);
		return attSelection;
	}
	
	public static AttributeSelection relief() throws Exception{//Relief
		AttributeSelection attSelection = new AttributeSelection();
		ReliefFAttributeEval info = new ReliefFAttributeEval();
		info.setNumNeighbours(11);
		info.setSampleSize(-1);
		info.setSeed(1);
		info.setSigma(2);
		Ranker rank = new Ranker();
		attSelection.setRanking(true); 
		//rank.setThreshold(0.0);
		rank.setNumToSelect(20);//Quantidade de atributos selecionados
//		rank.setStartSet("4,5,6,15,17,19,20,21,22,29,31,33,34,47");
		rank.setGenerateRanking(true);
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
