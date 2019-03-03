package com.uic.mapreduce

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.Mapper

import scala.xml.{Node, XML}

class Map extends Mapper[LongWritable, Text, Text, Text] {

  override def map(key: LongWritable, value: Text, context:
  Mapper[LongWritable, Text, Text, Text]#Context): Unit = {
    System.out.println("Mapper_input >>" + value.toString)
    val article : scala.xml.Elem = XML.loadString(value.toString)

    val authorList : List[Node] = (article \ "author").toList

    //if(authorList.length == 1)
      

    //else {
    authorList.flatten.foreach(author => {
      context.write(new Text(author.child(0).toString()), new Text(author.child(0).toString()))
    })

    authorList.flatten.combinations(2).toList.foreach(combo => {
      

      context.write(new Text(combo(0).child(0).toString()), new Text(combo(1).child(0).toString()))
      context.write(new Text(combo(1).child(0).toString()), new Text(combo(0).child(0).toString()))
    })
    //}


  }



}

object Map {


}
