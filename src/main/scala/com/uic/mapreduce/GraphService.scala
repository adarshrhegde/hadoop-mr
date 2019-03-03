package com.uic.mapreduce

import java.io.{File, IOException}

import org.gephi.appearance.api.{AppearanceController, AppearanceModel, Function}
import org.gephi.appearance.plugin.RankingNodeSizeTransformer
import org.gephi.graph.api._
import org.gephi.io.exporter.api.ExportController
import org.gephi.preview.api.{PreviewController, PreviewModel, PreviewProperty}
import org.gephi.project.api.{ProjectController, Workspace}
import org.openide.util.Lookup

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.io.Source
import util.control.Breaks._

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

  def initialize(): Unit ={
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
    for(line <- Source.fromFile("professors.txt").getLines()){
      professorList += line.trim
    }
  }

  def createNode(name : String) : Node = {
    if(nodeSet.contains(name)){
      println(s"Node already exists $name")
      return nodeList.toList
        .filter(node => node.getLabel == name)(0)
    } else {
      nodeSet += name
      println(s"Creating node for $name")
      val n = graphModel.factory().newNode(name)
      n.setLabel(name)
      n
    }
  }

  def createGraph(inputFilePath : String): Unit = {

    for (line <- Source.fromFile(inputFilePath).getLines) {
      breakable {
        println(line)

        val splits : Array[String] = line.split(";")
        println(splits.toString)
        println(s"Source " + splits(0))

        if(!professorList.contains(splits(0).trim)) break

        val src : Node = createNode(splits(0).trim)

        nodeList += src
        splits(1).split(",").filter(prof => prof != splits(0))
          .foreach( prof => {
            println(s"Dest $prof")
            val dest: Node = createNode(prof.trim)
            nodeList += dest
            val e : Edge = graphModel.factory().newEdge(src,dest, false)
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
    graphService.createGraph("D:\\cs441\\output_small\\part-r-00000")
    graphService.exportGraph("test2.gexf")
    /*val url : URL = GraphService.getClass()
      .getClassLoader()
      .getResource("D:\\cs441\\output_small\\part-r-00000")

    print(url)

    val f = new File(url
      .getPath())

    Source.fromFile(f).getLines().foreach(line => {

      print(line)
    })*/

  }
}
