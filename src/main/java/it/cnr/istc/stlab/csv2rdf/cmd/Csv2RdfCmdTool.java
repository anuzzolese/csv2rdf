package it.cnr.istc.stlab.csv2rdf.cmd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RiotException;

import it.cnr.istc.stlab.csv2rdf.Csv2Rdf;
import it.cnr.istc.stlab.csv2rdf.CsvReadingException;
import it.cnr.istc.stlab.csv2rdf.CsvTransformationConfig;

public class Csv2RdfCmdTool {

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
	
	public static void main(String[] args) {
		
		/*
         * Set-up the options for the command line parser.
         */
        Options options = new Options();
        
        Builder optionBuilder = Option.builder(SEPARATOR_OPTION);
        Option separatorOption = optionBuilder.argName("char")
                                 .desc("The character used as separator within the CSV file (e.g. , or ;).")
                                 .hasArg()
                                 .type(char.class)
                                 .required(false)
                                 .longOpt(SEPARATOR_OPTIONS_LONG)
                                 .build();
        
        optionBuilder = Option.builder(OUTPUT_OPTION);
        Option outputOption = optionBuilder.argName("file")
                .desc("The name of the file where to store the resulting RDF. If no file is provided the RDF is printed on screen.")
                .hasArg()
                .required(false)
                .longOpt(OUTPUT_OPTIONS_LONG)
                .build();
        
        optionBuilder = Option.builder(NAMESPACE_OPTION);
        Option namespaceOption = optionBuilder.argName("uri")
                .desc("The namespace to use for generating RDF objects. If no namespace is provided http://purl.org/example/ is used as default.")
                .hasArg()
                .required(false)
                .longOpt(NAMESPACE_OPTIONS_LONG)
                .build();
        
        optionBuilder = Option.builder(MAPPING_OPTION);
        Option mappingOption = optionBuilder.argName("file")
                .desc("A file providing the mapping between CSV columns and the properties of a target ontology/vocabulary. "
                		+ ""
                		+ "" 
                		+ "Such a file must contain as set of key=value lines, where each key represents a column position in the source CSV (the counting of positions starts from index 1) and each value is a pair property-datatype composed of property URI form a target ontology or vocabulary and a datatype URI. The property-datatype pairs are separated by the character '>'. The datatype is optional, hence it is possible to provide the property URI only without any datatype. We remark that if no datatype is provided, then the tool tries to infer the more appropriate datatype for the value to transform to RDF. However, the datatype inference is performed for values that can be typed as integer, double or boolean only. The following is an example that associates the properties http://xmlns.com/foaf/0.1/givenName and http://dbpedia.org/ontology/birthDate to the second and third columns of a given source CSV. Additionally, the example specifies that the values associated with the property http://dbpedia.org/ontology/birthDate have to be typed as http://www.w3.org/2001/XMLSchema#date:"
                		+ ""
                		+ "" 
                		+ "    2=http://xmlns.com/foaf/0.1/givenName" + '\n'
                		+ "    3=http://dbpedia.org/ontology/birthDate > http://www.w3.org/2001/XMLSchema#date "
                		+ "If no mapping or no value for a specific column in the mapping is provided then the URI for the predicate is generated from the column name by using either the default or the provided namespace.")
                
                .hasArg()
                .required(false)
                .longOpt(MAPPING_OPTIONS_LONG)
                .build();
        
        
        optionBuilder = Option.builder(FORMAT_OPTION);
        Option formatOption = optionBuilder.argName("string")
                .desc("The format that the tool has to use in order to serialise the RDF output. Available aternatives are: TURTLE, RDF/XML, RDF/XML-ABBREV, N-TRIPLES, JSON-LD, N3, RDF/JSON. If no explicit format is provided, the output is serialised as TURTLE by default.")
                .hasArg()
                .required(false)
                .longOpt(FORMAT_OPTIONS_LONG)
                .build();
        
        options.addOption(separatorOption);
        options.addOption(outputOption);
        options.addOption(namespaceOption);
        options.addOption(mappingOption);
        options.addOption(formatOption);
        
        CommandLine commandLine = null;
        
        CommandLineParser cmdLineParser = new DefaultParser();
        try {
            commandLine = cmdLineParser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "csv2rdf", options );
        }
        if(commandLine != null){
        	String[] arguments = commandLine.getArgs();
        	if(arguments.length == 0){
        		System.out.println("No file provided as input.");
        		System.exit(0);
        	}
        	String csv = commandLine.getArgs()[0];
        	String separator = commandLine.getOptionValue(SEPARATOR_OPTION);
        	char sepChar = ',';
        	
        	separator = StringEscapeUtils.unescapeJava(separator);
        	
        	if(separator != null)
        		sepChar = separator.charAt(0);
        	
        	String namespace = "http://purl.org/example/";
			
			if(commandLine.hasOption(NAMESPACE_OPTION)){
				namespace = commandLine.getOptionValue(NAMESPACE_OPTION);
				if(namespace == null)
					namespace = "http://purl.org/example/";
			}
        	
        	URI csvUri = null;
        	URI mappingUri = null;
        	try {
				csvUri = new URI(csv);
				
				if(commandLine.hasOption(MAPPING_OPTION)){
					
					String mapping = commandLine.getOptionValue(MAPPING_OPTION);
					if(mapping != null){
						mappingUri = new URI(mapping);
					}

				}
				
			} catch (URISyntaxException e1) {
				String errorInput = csvUri == null ? csv : commandLine.getOptionValue(MAPPING_OPTION);
				System.out.print(errorInput + " cannot be mapped to a valid URI.");
				System.exit(1);
			}
        	
        	CsvTransformationConfig cfg = new CsvTransformationConfig(sepChar, csvUri, mappingUri, namespace);
        	Model model = null;
			try {
				model = Csv2Rdf.getInstance().convert(cfg);
			} catch (CsvReadingException e1) {
				System.out.println(e1.getMessage());
				System.exit(2);
			}
			
			OutputStream out = System.out;
			if(commandLine.hasOption(OUTPUT_OPTION)){
				try {
					String value = commandLine.getOptionValue(OUTPUT_OPTION);
					if(value != null)
						out = new FileOutputStream(new File(value));
					else out = System.out;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					out = System.out;
				}
			}
				
			String format = null;
			if(commandLine.hasOption(FORMAT_OPTION)){
				format = commandLine.getOptionValue(FORMAT_OPTION);
				if(format != null){
					format = format.trim();
					if(format.isEmpty()) format = null;
				}
			}
			if(format == null) format = "TURTLE"; 
			
			try{
				model.write(out, format);
			} catch(RiotException e){
				System.out.println("It was not possible to serialise the output because of the following reason: " + e.getMessage());
			}
        }
		
		
	}
}
