package com.uic.mapreduce

import java.lang

import scala.collection.JavaConverters._
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Reducer
import org.slf4j.LoggerFactory

/**
  * Reducer class
  * Input - Text, Text (Eg. Mark Grechanik -> Ugo Buy, Mark Grechanik -> Chris Kanich)
  * Output - Text, Text (Eg. Mark Grechanik -> Ugo Buy, Chris Kanich)
  */
class Reduce extends Reducer[Text, Text, Text, Text] {

  val logger = LoggerFactory.getLogger(classOf[Reduce])
  /**
    * Input Key values => A-B, A-C converted to output key values => A-B,C
    * @param key
    * @param values
    * @param context
    */
  override def reduce(key: Text, values: lang.Iterable[Text],
                      context: Reducer[Text, Text, Text, Text]#Context): Unit = {

	logger.debug("Reducer input - " + key.toString)
    val coAuthors = values.asScala.foldLeft("")(_ + "," + _).substring(1)
    context.write(key, new Text(coAuthors))

  }

}
