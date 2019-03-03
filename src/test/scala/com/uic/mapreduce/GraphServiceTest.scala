package com.uic.mapreduce

import com.typesafe.config.ConfigFactory
import org.scalatest.{ FunSuite}
import java.nio.file.{Paths, Files}

class GraphServiceTest extends FunSuite {



  test("testCreateNode") {
    val graphService = new GraphService
    graphService.initialize()
    graphService.createNode("n1", 1)
    assert(graphService.nodeSet.count(node => node == "n1") > 0)

  }

  test("testCreateGraph") {
    val graphService = new GraphService
    graphService.initialize()
    graphService.createGraph(ConfigFactory.load().getString("graph.inputFile"))

    assert(graphService.graphModel.getGraph.getEdgeCount() != 0)
    assert(graphService.graphModel.getGraph.getNodeCount != 0)
  }


  test("testExportGraph") {
    val graphService = new GraphService
    graphService.initialize()
    graphService.createGraph(ConfigFactory.load().getString("graph.inputFile"))
    graphService.exportGraph(ConfigFactory.load().getString("graph.outputFile"))
    Files.exists(Paths.get(ConfigFactory.load().getString("graph.outputFile")))

  }

}
