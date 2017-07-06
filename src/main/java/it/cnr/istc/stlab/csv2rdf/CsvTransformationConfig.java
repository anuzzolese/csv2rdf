package it.cnr.istc.stlab.csv2rdf;

import java.net.URI;

public class CsvTransformationConfig {

	private char separator;
	private URI csvUri;
	private URI mappingUri;
	private String namespace;
	
	public CsvTransformationConfig(char separator, URI csvUri, URI mappingUri, String namespace) {
		this.separator = separator;
		this.csvUri = csvUri;
		this.mappingUri = mappingUri;
		this.namespace = namespace;
	}

	public char getSeparator() {
		return separator;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}

	public URI getCsvUri() {
		return csvUri;
	}

	public void setCsvUri(URI csvUri) {
		this.csvUri = csvUri;
	}

	public URI getMappingUri() {
		return mappingUri;
	}

	public void setMappingUri(URI mappingUri) {
		this.mappingUri = mappingUri;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	
	
}
