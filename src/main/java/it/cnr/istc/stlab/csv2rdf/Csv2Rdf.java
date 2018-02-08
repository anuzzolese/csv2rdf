package it.cnr.istc.stlab.csv2rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Properties;

import org.apache.jena.propertytable.lang.CSV2RDF;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import it.cnr.istc.stlab.csv2rdf.csvgraph.GraphCSV;

public class Csv2Rdf {

	public static final String SEPARATOR_OPTION = "s";
	public static final String SEPARATOR_OPTIONS_LONG = "separator";
	
	public static final String OUTPUT_OPTION = "o";
	public static final String OUTPUT_OPTIONS_LONG = "output";
	
	public static final String NAMESPACE_OPTION = "n";
	public static final String NAMESPACE_OPTIONS_LONG = "namespace";
	
	public static final String MAPPING_OPTION = "m";
	public static final String MAPPING_OPTIONS_LONG = "mapping";
	
	public static final String FORMAT_OPTION = "f";
	public static final String FORMAT_OPTIONS_LONG = "format";
	
	
	private static Csv2Rdf instance;
	
	private Csv2Rdf(){
		CSV2RDF.init();	
	}
	
	public static Csv2Rdf getInstance(){
		if(instance == null) instance = new Csv2Rdf();
		return instance;
	}
	
	public Model convert(CsvTransformationConfig csvTransformationConfig) throws CsvReadingException {
		
		
		Model model = ModelFactory.createDefaultModel();
		
		File file = null;
		
		char sepChar = csvTransformationConfig.getSeparator();
		
		long csvSize = 0; 
		
		try {
			file = File.createTempFile("tmp", ".csv");
			
			URI csvUri = csvTransformationConfig.getCsvUri();
			
			InputStream csvIs = null;
			if(!csvUri.isAbsolute()) csvIs = new FileInputStream(new File(csvUri.toString()));
			else csvIs = csvUri.toURL().openStream();
			
			CSVReader reader = new CSVReader(new InputStreamReader(csvIs, csvTransformationConfig.getEncoding()), sepChar);
			CSVWriter writer = new CSVWriter(new FileWriter(file)); 
			String[] row = null;
		
			while((row = reader.readNext()) != null){
				if(csvSize < 10){
					for(String column : row)
						System.out.println(getClass() + ": " + column);
				
				}
				writer.writeNext(row);
				csvSize++;
			}
			
			reader.close();
			writer.close();
		} catch (IOException e) {
			System.out.println("An error occurred while reading file " + csvTransformationConfig.toString() + ".");
			System.out.println('\t' + e.getMessage());
		}
		
		if(file != null){
			
			String namespace = csvTransformationConfig.getNamespace();
			
			Properties mapping = new Properties();
			try {
				
				URI mappingUri = csvTransformationConfig.getMappingUri();
				if(mappingUri != null){
					InputStream mappingIs = null;
					if(!mappingUri.isAbsolute()) mappingIs = new FileInputStream(new File(mappingUri.toString()));
					else mappingIs = mappingUri.toURL().openStream();
					
					mapping.load(mappingIs);
				}
				else mapping = null;
			} catch (IOException e) {
				mapping = null;
				System.out.print("An error occurred while reading the mapping file provided (i.e. the file named " + csvTransformationConfig.getMappingUri().toString() + ").");
				System.out.println('\t' + e.getMessage());
			}
			
			Model csv = ModelFactory.createModelForGraph(new GraphCSV(namespace, mapping, file.getPath())) ;
			model.add(csv.listStatements());
			
			file.delete();
			
			return model;
		}
		else throw new CsvReadingException(csvTransformationConfig.getCsvUri().toString());
		
		
	}
}
