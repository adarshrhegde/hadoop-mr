package com.uic.mapreduce

import java.lang
import scala.collection.JavaConverters._
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Reducer

class Reduce extends Reducer[Text, Text, Text, Text] {

  override def reduce(key: Text, values: lang.Iterable[Text],
                      context: Reducer[Text, Text, Text, Text]#Context): Unit = {

	System.out.println("Reducer_input >>" + key)
    val coAuthors = values.asScala.foldLeft("")(_ + "," + _).substring(1)
    context.write(key, new Text(coAuthors))

  }

}
