package com.uic.mapreduce

import java.io.{File, IOException}

import org.gephi.graph.api._
import org.gephi.io.exporter.api.ExportController
import org.gephi.project.api.{ProjectController, Workspace}
import org.openide.util.Lookup

class GraphTest {}
object GraphTest extends App {

  val pc: ProjectController = Lookup.getDefault().lookup(classOf[ProjectController])

  pc.newProject()
  val workspace : Workspace = pc.getCurrentWorkspace()

  val graphModel : GraphModel = Lookup.getDefault
    .lookup(classOf[GraphController]).getGraphModel(workspace)

  val n0: Node = graphModel.factory().newNode("n0")
  n0.setLabel("Node 0")
  val n1: Node = graphModel.factory().newNode("n1")
  n1.setLabel("Node 1")

  val e1 : Edge = graphModel.factory().newEdge(n1, n0, 1, false)

  val undirectedGraph : UndirectedGraph = graphModel.getUndirectedGraph

  undirectedGraph.addNode(n0)
  undirectedGraph.addNode(n1)
  undirectedGraph.addEdge(e1)

  val ec : ExportController = Lookup.getDefault().lookup(classOf[ExportController])

  try {

    ec.exportFile(new File("test-full.gexf"))

  } catch {
    case ex : IOException => {
      ex.printStackTrace()
    }
  }


}
