# csv2rdf

csv2rdf is an Java based application, which relies on Apache Jena to convert tabular data to RDF.
The binaries can be obtained by compiling the source code with MAVEN from command line, i.e.

## Compiling

```bash 
mvn clean install
```
Once the source code have been compiled a JAR named stlab.csv2rdf-1.0.jar is available in the target folder.

## Usage

The JAR stlab.csv2rdf-1.0.jar can be used as a command line tool.
The synopsis is the following

```bash
java -jar stlab.csv2rdf-1.0.jar [OPTIONS] CSV_FILE
```

The options available are the following:

1. -s,--separator <char>   
The character used as separator withing the CSV file (e.g. , or ;).
2. -m,--mapping <file>     
A file providing the mappping between CSV columns and the properties of 						a target ontology/vocabulary.
Such a file must contain as set of key=value lines, where each key represent a column position in the source CSV (starting from 1) and each value is a property form a target ontology or vocabulary. The following is an example that associates the properties http://xmlns.com/foaf/0.1/givenName and http://xmlns.com/foaf/0.1/familyName to the second and third columns of a given source CSV:
    ```java
    2=http://xmlns.com/foaf/0.1/givenName
    3=http://xmlns.com/foaf/0.1/lastName
    ```
3. -n,--namespace <uri>    
The namespace to use for generating RDF objects. If no namespace is provided http://purl.org/example/ is used as deafault.

4. -o,--output <file>
The name of the file where to store the resulting RDF. If no file is provided the RDF is printed on screen.

## Example
 Let's take the following table as a possible CSV file, named *people.csv*, to convert to RDF.
 
| Name   | Last name | Affiliation |
| ----   |:--------:|-----------:|
| Andrea | Nuzzolese | STLab, ISTC-CNR |
| Tim | Berners-Lee | W3C |
| Paolo | Ciancarini | University of Bologna | 
 
 Additionally, the following file, named *mapping* and containing key=value pairs, define the mapping to be used in order to generate the properties.
 ```java
 1=http://xmlns.com/foaf/0.1/givenName
 2=http://xmlns.com/foaf/0.1/lastName
 3=https://www.w3.org/ns/org#memberOf
 ``` 
 
 Hence, the following line provides the example about how to use the tool from command line in order to obtain RDF from CSV and saving its content into a file named *people.ttl*. We suppose that the input CSV is actually a tab-separated file.
 ```bash
 java -jar stlab.csv2rdf-1.0.jar -s '\t' -m mapping -o people.ttl people.csv
 ```
 
 The execution of the tool with the arguments as provided in the example above produces the following RDF serialised by using the TURTLE syntax.
 ```turtle
 [ <http://w3c/future-csv-vocab/row>
          1 ;
  <http://xmlns.com/foaf/0.1/givenName>
          "Andrea" ;
  <http://xmlns.com/foaf/0.1/lastName>
          "Nuzzolese"
] .

[ <http://w3c/future-csv-vocab/row>
          2 ;
  <http://xmlns.com/foaf/0.1/givenName>
          "Tim" ;
  <http://xmlns.com/foaf/0.1/lastName>
          "Berners-Lee"
] .

[ <http://w3c/future-csv-vocab/row>
          3 ;
  <http://xmlns.com/foaf/0.1/givenName>
          "Paolo" ;
  <http://xmlns.com/foaf/0.1/lastName>
          "Ciancarini"
] .
 ```