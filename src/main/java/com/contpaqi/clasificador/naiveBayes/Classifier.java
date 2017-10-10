package com.contpaqi.clasificador.naiveBayes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.ml.AbstractTrainer;
import opennlp.tools.ml.naivebayes.NaiveBayesTrainer;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

public class Classifier {
	private InputStreamFactory dataIn;
	private DoccatModel model;

	private File trainFile;

	public Classifier() {
		this.trainFile=new File("/home/tostisoft/opennlp-models/myModels" + File.separator + "groups.train");
	}

	public void train(File trainFile) {
		this.trainFile=trainFile;
		train();
	}
	
	//Lee el dataset de entrenamiento (un archivo de texto plano con los datos a entrenar)
	private void train() {
		ObjectStream<DocumentSample> sampleStream = null;
		ObjectStream<String> lineStream;
		try {
			this.dataIn = new MarkableFileInputStreamFactory(this.trainFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (this.dataIn != null) {
			try {
				lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
				sampleStream = new DocumentSampleStream(lineStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		//Define los parametros de entrenamiento
		TrainingParameters params = new TrainingParameters();
		//Define las iteraciones y la forma de como se divide el dataset para entrenar
		params.put(TrainingParameters.ITERATIONS_PARAM, 100 + "");
		params.put(TrainingParameters.CUTOFF_PARAM, 5 + "");
		//Define el algoritmo de entrenamiento, que es una red bayesiana
		params.put(AbstractTrainer.ALGORITHM_PARAM, NaiveBayesTrainer.NAIVE_BAYES_VALUE);

		// Esta sección es donde se crea el modelo.
		if (sampleStream != null) {
			try {
				model = DocumentCategorizerME.train("es", sampleStream, params, new DoccatFactory());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//Si ya existe el modelo, este es leido de manera directa. El modelo es un archivo binario
	public void loadModel(File fileModel) throws IOException {
		BufferedInputStream modelIn = new BufferedInputStream(new FileInputStream(fileModel.getAbsolutePath()));
		this.model = new DoccatModel(modelIn);

		System.out.println("Modelo cargado " + model.isLoadedFromSerialized());
	}

	// Salva el modelo en disco duro
	public void saveModel(File pathName) throws IOException {
		BufferedOutputStream modelOut = new BufferedOutputStream(new FileOutputStream(pathName));
		model.serialize(modelOut);
	}

	// Realiza la búsqueda del texto deseado en el modelo
	// Al final regresa la categoria con mayor probabilidad 
	public String findClass(String text) {
		DocumentCategorizerME doccat = new DocumentCategorizerME(model);
		String[] docWords = text.replaceAll("[^A-Za-z]", " ").split(" ");
		double[] aProbs = doccat.categorize(docWords);

		System.out.println(
				"\n---------------------------------\nCategoria: Probabilidad\n---------------------------------");
		for (int i = 0; i < doccat.getNumberOfCategories(); i++) {
			System.out.println(doccat.getCategory(i) + " : " + aProbs[i]);
		}
		System.out.println("---------------------------------");

		System.out.println("\n" + doccat.getBestCategory(aProbs) + " : es la categoria que le corresponde.");

		return doccat.getBestCategory(aProbs);

	}

	// Realiza la búsqueda del texto deseado en el modelo
	// Al final regresa un listado con las categorias que tienen una mayor probabilidad de 0.09999 
	public List<String> findClasses(String text) {
		List<String> tmpList = new ArrayList<String>();
		DocumentCategorizerME doccat = new DocumentCategorizerME(model);
		String[] docWords = text.replaceAll("[^A-Za-z]", " ").split(" ");
		double[] aProbs = doccat.categorize(docWords);
		for (int index = 0; index < doccat.getNumberOfCategories(); index++) {
			if (aProbs[index] > 0.09999) {
				tmpList.add(doccat.getCategory(index));
			}
		}

		System.out.println(
				"\n---------------------------------\nCategoria: Probabilidad\n---------------------------------");
		for (int i = 0; i < doccat.getNumberOfCategories(); i++) {
			System.out.println(doccat.getCategory(i) + " : " + aProbs[i]);
		}
		System.out.println("---------------------------------");

		return tmpList;
	}

	//Obtiene todas las categorias del modelo
	public List<String> listTokens() {
		DocumentCategorizerME doccat = new DocumentCategorizerME(this.model);
		List<String> tmpList = new ArrayList<String>();
		for (int i = 0; i < doccat.getNumberOfCategories(); i++) {
			tmpList.add(doccat.getCategory(i));

		}
		return tmpList;
	}
}
