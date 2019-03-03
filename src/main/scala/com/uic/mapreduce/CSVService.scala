package com.uic.mapreduce

import java.io.{BufferedWriter, File, FileWriter}

import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}

import scala.io.Source
import scala.util.control.Breaks.break
import util.control.Breaks._

/**
  * The CSVService
  * Generates the csv file that will be fed to the Gephi graph visualization tool
  * Takes map reduce output as input
  *
  */
object CSVService {

  val logger : Logger = LoggerFactory.getLogger(classOf[CSVService])

  def main(args: Array[String]): Unit = {

    logger.info("Generating CSV file")
    val file = new File(ConfigFactory.load().getString("graph.outputFile"))
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write("Source,Target\n")

    var professorList : Set[String] = Set()
    for(line <- Source.fromFile(ConfigFactory.load().getString("professorFile")).getLines()){
      professorList += line.trim
    }

    for (line <- Source.fromFile(ConfigFactory.load().getString("graph.inputFile")).getLines) {
      breakable {
      val splits : Array[String] = line.split(";")

      if(!professorList.contains(splits(0).trim)) break

      splits(1).split(",")
        .foreach( prof => {
          logger.debug(s"Adding edge " + splits(0) + s" -> $prof")

          bw.write(splits(0) + "," + prof + "\n")
        })
      }
    }

    bw.close()
    logger.info("Completed generating CSV file")
  }
}


class CSVService {

}

