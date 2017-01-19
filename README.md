
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

* -s,--separator &lt;char&gt;   
The character used as separator within the CSV file (e.g. , or ;).

* -m,--mapping &lt;file&gt;   
A file providing the mapping between CSV columns and the properties of 						a target ontology/vocabulary.
    
Such a file must contain as set of key=value lines, where each key represents a column position in the source CSV (the counting of positions starts from index 1) and each value is a pair property-datatype composed of property URI form a target ontology or vocabulary and a datatype URI. The property-datatype pairs are separated by the character '>'. The datatype is optional, hence it is possible to provide the property URI only without any datatype. The following is an example that associates the properties http://xmlns.com/foaf/0.1/givenName and http://dbpedia.org/ontology/birthDate to the second and third columns of a given source CSV. Additionally, the example specifies that the values associated with the property http://dbpedia.org/ontology/birthDate have to be typed as http://www.w3.org/2001/XMLSchema#date:
    ```java
    2=http://xmlns.com/foaf/0.1/givenName
    3=http://dbpedia.org/ontology/birthDate > http://www.w3.org/2001/XMLSchema#date
    ```
* -n,--namespace &lt;uri&gt;   
The namespace to use for generating RDF objects. If no namespace is provided http://purl.org/example/ is used as default.

* -o,--output &lt;file&gt;  
The name of the file where to store the resulting RDF. If no file is provided the RDF is printed on screen.

## Example
 Let's take the following table as a possible CSV file, named *musicians.csv*, to convert to RDF.
 
| Name   | Last name | Albums | Date of birth |
| ----   |:--------:|:--------:|--------:|
| Miles | Davis | 102 | 1926-05-26 |
| Tim | Berners-Lee | 90 | 1947-01-08 |
| Paolo | Ciancarini | 123 | 1933-05-03 |
 
 Additionally, the following file, named *mapping* and containing key=property>datatype pairs, define the mapping to be used in order to generate the properties.
 ```java
 1=http://xmlns.com/foaf/0.1/givenName
 2=http://xmlns.com/foaf/0.1/lastName
 3=http://foo.org/myont/numberOfAlbums
 4=http://dbpedia.org/ontology/birthDate > http://www.w3.org/2001/XMLSchema#date
 ``` 
 
 Hence, the following line provides the example about how to use the tool from command line in order to obtain RDF from CSV and saving its content into a file named *musicians.ttl*. We suppose that the input CSV is actually a tab-separated file.
 ```bash
 java -jar stlab.csv2rdf-1.0.jar -s '\t' -m mapping -o musicians.ttl musicians.csv
 ```
 
 The execution of the tool with the arguments as provided in the example above produces the following RDF serialised by using the TURTLE syntax.
 ```turtle
 [ <http://dbpedia.org/ontology/birthDate>
          "1926-05-26"^^<http://www.w3.org/2001/XMLSchema#date> ;
  <http://foo.org/myont/numberOfAlbums>
          "102"^^<http://www.w3.org/2001/XMLSchema#int> ;
  <http://w3c/future-csv-vocab/row>
          1 ;
  <http://xmlns.com/foaf/0.1/givenName>
          "Miles" ;
  <http://xmlns.com/foaf/0.1/lastName>
          "Davis"
] .

[ <http://dbpedia.org/ontology/birthDate>
          "1947-01-8"^^<http://www.w3.org/2001/XMLSchema#date> ;
  <http://foo.org/myont/numberOfAlbums>
          "90"^^<http://www.w3.org/2001/XMLSchema#int> ;
  <http://w3c/future-csv-vocab/row>
          2 ;
  <http://xmlns.com/foaf/0.1/givenName>
          "David" ;
  <http://xmlns.com/foaf/0.1/lastName>
          "Bowie"
] .

[ <http://dbpedia.org/ontology/birthDate>
          "1933-05-03"^^<http://www.w3.org/2001/XMLSchema#date> ;
  <http://foo.org/myont/numberOfAlbums>
          "123"^^<http://www.w3.org/2001/XMLSchema#int> ;
  <http://w3c/future-csv-vocab/row>
          3 ;
  <http://xmlns.com/foaf/0.1/givenName>
          "James" ;
  <http://xmlns.com/foaf/0.1/lastName>
          "Brown"
] .
 ```