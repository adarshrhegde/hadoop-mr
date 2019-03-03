package com.uic.mapreduce
//import pureconfig.generic.auto._
import org.slf4j.LoggerFactory

class ConfigReader{}

object ConfigReader {
  val logger = LoggerFactory.getLogger(classOf[ConfigReader])
  /*def loadConfig() : Option[Config] = {

    val configuration = pureconfig.loadConfig[Config]
    configuration.fold(
      l => {
        logger.error("Error {} occurred while reading from config file at {}", l.head.description, l.head.location.get.url.toString:Any)
        None
      },
      r => {
        logger.debug("Picked up config {}", r.toString)
        Option(r)
      })
  }*/

}