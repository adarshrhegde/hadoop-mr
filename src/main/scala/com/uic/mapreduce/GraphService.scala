package com.uic.mapreduce

import java.io.{File, IOException}

import com.typesafe.config.ConfigFactory
import org.gephi.appearance.api.{AppearanceController, AppearanceModel, Function}
import org.gephi.appearance.plugin.RankingNodeSizeTransformer
import org.gephi.graph.api._
import org.gephi.io.exporter.api.ExportController
import org.gephi.preview.api.{PreviewController, PreviewModel, PreviewProperty}
import org.gephi.project.api.{ProjectController, Workspace}
import org.openide.util.Lookup
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.io.Source
import util.control.Breaks._

/**
  * GraphService class
  * This class creates the graph programmatically
  */
class GraphService {

  var pc: ProjectController = _
  var workspace : Workspace = _
  var graphModel : GraphModel = _
  var ec : ExportController = _
  var undirectedGraph : UndirectedGraph = _
  var nodeList : ListBuffer[Node] = _
  var nodeSet : Set[String] = _
  var edgeList : ListBuffer[Edge] = _
  var appearanceController : AppearanceController = _
  var appearanceModel : AppearanceModel = _
  var previewModel : PreviewModel = _
  var professorList : Set[String] = _
  val logger : Logger = LoggerFactory.getLogger(classOf[GraphService])

  /**
    * Initialize all the properties for graph creation
    */
  def initialize(): Unit ={
    logger.info("Initializing the graph service")

    pc = Lookup.getDefault().lookup(classOf[ProjectController])
    pc.newProject()
    workspace = pc.getCurrentWorkspace()
    graphModel = Lookup.getDefault
      .lookup(classOf[GraphController]).getGraphModel(workspace)

    ec = Lookup.getDefault().lookup(classOf[ExportController])
    undirectedGraph = graphModel.getUndirectedGraph

    nodeList = ListBuffer()
    edgeList = ListBuffer()
    nodeSet = Set()

    appearanceController = Lookup.getDefault().lookup(classOf[AppearanceController])
    appearanceModel = appearanceController.getModel(workspace)
    previewModel = Lookup.getDefault().lookup(classOf[PreviewController]).getModel(workspace)

    previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, true)
    previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, false)

    professorList = Set()
    for(line <- Source.fromFile(ConfigFactory.load().getString("professorFile")).getLines()){
      professorList += line.trim
    }
  }

  /**
    * Create a node given the name and size
    * @param name
    * @param size
    * @return
    */
  def createNode(name : String, size : Int) : Node = {

    if(nodeSet.contains(name)){
      val n = nodeList.toList
        .filter(node => node.getLabel == name)(0)
      n.setSize(size)
      n

    } else {

      logger.debug(s"Creating node $name")
      nodeSet += name
      val n = graphModel.factory().newNode(name)
      n.setLabel(name)
      n
    }
  }

  /**
    * Create a graph from the input text file with the result of map reduce
    * @param inputFilePath
    */
  def createGraph(inputFilePath : String): Unit = {

    logger.info(s"Creating graph from input file $inputFilePath")

    for (line <- Source.fromFile(inputFilePath).getLines) {
      breakable {

        val splits : Array[String] = line.split(";")
        println(splits.toString)
        logger.debug(s"Source node " + splits(0))

        if(!professorList.contains(splits(0).trim)) break

        val src : Node = createNode(splits(0).trim,
          splits(1).split(",").count(prof => prof == splits(0)))

        nodeList += src
        splits(1).split(",").filter(prof => prof != splits(0))
          .foreach( prof => {
            logger.debug(s"Destination node $prof")

            val dest: Node = createNode(prof.trim, 0)
            nodeList += dest
            val e : Edge = graphModel.factory().newEdge(src,dest,0,1, false)
            edgeList += e
          })
      }

    }

    undirectedGraph.addAllNodes(nodeList.asJava)
    undirectedGraph.addAllEdges(edgeList.asJava)

    val degreeRanking : Function = appearanceModel.getNodeFunction(
      undirectedGraph, AppearanceModel.GraphFunction.NODE_DEGREE,
      classOf[RankingNodeSizeTransformer])

    val nodeSizeTransformer : RankingNodeSizeTransformer = degreeRanking.getTransformer().asInstanceOf[RankingNodeSizeTransformer]

    nodeSizeTransformer.setMinSize(1)
    nodeSizeTransformer.setMaxSize(4)
    appearanceController.transform(degreeRanking)

  }

  def exportGraph(fileName : String): Unit = {
    try {
      ec.exportFile(new File(fileName))

    } catch {
      case ex : IOException => {
        ex.printStackTrace()
      }
    }
  }

}

object GraphService {

  def main(args: Array[String]): Unit = {

    val graphService = new GraphService
    graphService.initialize()
    graphService.createGraph(ConfigFactory.load().getString("graph.inputFile"))
    graphService.exportGraph(ConfigFactory.load().getString("graph.outputFile"))

  }
}
