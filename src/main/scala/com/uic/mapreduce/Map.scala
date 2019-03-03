package com.uic.mapreduce

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.Mapper
import org.slf4j.LoggerFactory

import scala.xml.{Node, XML}

/**
  * Mapper class
  * Input -> LongWritable, Text (Eg. 1 -> "_xml_")
  * Output -> Text, Text  (Eg. Co-author relationship ==> Mark Grechanik -> Ugo Buy)
  */
class Map extends Mapper[LongWritable, Text, Text, Text] {

  val logger = LoggerFactory.getLogger(classOf[Map])

  /**
    * maps from input key-value to output key-value format
    * @param key
    * @param value
    * @param context
    */
  override def map(key: LongWritable, value: Text, context:
  Mapper[LongWritable, Text, Text, Text]#Context): Unit = {

    logger.debug("Mapper input - " + value.toString)
    val article : scala.xml.Elem = XML.loadString(value.toString)

    val authorList : List[Node] = (article \ "author").toList

    /** adding a relationship for author with himself/herself (A-A)
      * this is done to handle cases where a publication has only one author
      */
    authorList.flatten.foreach(author => {
      context.write(new Text(author.child(0).toString()), new Text(author.child(0).toString()))
    })

    /**
      * Add key value relationship A-B and B-A
      */
    authorList.flatten.combinations(2).toList.foreach(combo => {

      context.write(new Text(combo(0).child(0).toString()), new Text(combo(1).child(0).toString()))
      context.write(new Text(combo(1).child(0).toString()), new Text(combo(0).child(0).toString()))
    })

  }

}

object Map {


}
