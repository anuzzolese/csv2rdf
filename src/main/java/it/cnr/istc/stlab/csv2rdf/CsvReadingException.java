package it.cnr.istc.stlab.csv2rdf;

public class CsvReadingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2119903074079564261L;
	
	private String csvName;
	
	public CsvReadingException(String csvName) {
		this.csvName = csvName;
	}
	
	public String getMessage() {
		return "An error occurred while accessing or reading the CSV " + csvName;
	}
}
