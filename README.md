APPROACH

This is a Hadoop Map-Reduce project that takes an XML file with publication information of professors and generates relationship graph for UIC professors. The relationships are determined by co-authorship of publications.
The project has two steps:

1.	Map-Reduce step: This is accomplished using the Hadoop Map-Reduce
The input to the map-reduce program is the XML file. Each publication is defined within <article></article> or <inproceedings></ inproceedings> tags.
The program implements the following classes:

XMLInputFormat: This class is used to define an input format specific to XML input

XMLRecordReader: This class takes the input split information for each split and finds the relevant information for each publication within start and end tags. The purpose of implementing this class is to make sure that the splitting of the data doesn’t result in separation of start and end tags into different splits. This implementation can get data from latter splits.
The record reader provides the input to the mapper as follows:
(K,V) => (1, <article>content</article>)

Map: This class implements the mapper. It generates the following key values:

(Mark Grechanik -> Ugo Buy) (Mark Grechanik -> Chris Kanich)

Reduce: This class implements the reducer and takes the mapper output as input. It generates the following key values:

(Mark Grechanik -> Ugo Buy,Chris Kanich ) - Such (K,V) pairs will help in calculating the co-authorship relationship between authors.

Note: It is possible that a professor can be a solo author for a publication. Also, we want to calculate the total number of publications for an author too. Thus, we add the following key-value pair:

(Mark Grechanik -> Mark Grechanik) – Such (K,V) pairs will help in calculating the total publications of an author.


2.	Generate the csv which will be fed into Gephi to generate the graph
I have implemented this using two approaches:

a.	Graph generated programmatically using Gephi toolkit
Output of MR is used to generate graph programmatically and store in gexf file

b.	Graph generated from CSV using Gephi Software
The output of the Map-Reduce program is converted into a csv file. This csv file is imported into the Gephi software for graph visualization.


SETUP


1.	Clone the project repository
2.	Set the javaHome in build.sbt to point to your jdk 1.7 installation directory
3.	In the application.conf file change the graph.inputFile and mapReduce.xml.topTag config 

graph {
inputFile = "D:\\cs441\\output_small\\part-r-00000" // Path to output of map-reduce
program copied local filesystem
  
}

mapReduce {
  xml {
    topTag = """<?xml version="1.0" encoding="ISO-8859-1"?><!DOCTYPE dblp PUBLIC "-//DBLP//DTD//EN" "<add path to DTD>">"""
  }
}
4.	Set the jar within the lib directory to classpath (Was not working with sbt import)
5.	Create hadoop input output directory and add the input file using the following command:

hadoop fs -put dblp.xml <Hadoop_input-dir>

6.	Ensure the output directory is empty and the dtd is added in the path mentioned in config above


HOW TO RUN


1.	Perform the following on terminal:
sbt clean assembly
2.	Transfer the created jar to the VM

3.	Run the jar using the following command:
hadoop jar cs441_hw2-assembly-0.1.jar <Hadoop_input_dir> <Hadoop_output_dir>

4.	Copy the output of the MR program from hdfs to local file system

hadoop fs -get <Hadoop_output_dir>* <local_dir>
Note: local_dir is the same as graph.inputFile in config
5.	To Generate the graph using the two approaches mentioned above do as follows:
a.	Run the program GraphService to generate the gexf file and load it into Gephi
b.	OR Run the program CSVService to generate the CSV and import the csv into Gephi


OUTPUT

Once you have loaded the graph in Gephi through csv or gexf file do the following changes to make the node labels, node sizes, edge labels, edge weights visible.
1.	Open Data Laboratory -> Nodes
2.	Copy Id column to Label
3.	Open Data Laboratory -> Edges
4.	Copy Weight column to Label
5.	Open Preview
6.	Select show nodes, show nodes labels, show edges, show edge labels

Following is the resulting graph for a section of the input:
 
Please refer to graph.pdf for image
