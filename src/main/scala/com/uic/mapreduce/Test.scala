package com.uic.mapreduce

import org.apache.hadoop.io.Text

import scala.xml.{Node, XML}

object Test extends App {

  /*private val TOP_TAG : String = """<?xml version="1.0" encoding="ISO-8859-1"?><!DOCTYPE dblp PUBLIC "-//DBLP//DTD//EN" "https://dblp.uni-trier.de/xml/dblp.dtd"><article mdate="2017-05-24" key="conf/icst/GrechanikHB13"> <author>Mark</author> <author>Ugo</author> <author>Isabel</author> <title>Testing Database-Centric Applications for Causes of Database Deadlocks.</title> <pages>174-183</pages> <year>2013</year> <booktitle>ICST</booktitle> <ee>https://doi.org/10.1109/ICST.2013.19</ee> <ee>http://doi.ieeecomputersociety.org/10.1109/ICST.2013.19</ee> <crossref>conf/icst/2013</crossref> <url>db/conf/icst/icst2013.html#GrechanikHB13</url> </article> <article mdate="2017-05-24" key="conf/icst/GrechanikHB13"> <author>Mark</author> <author>Isabel</author> <author>Hossain</author> <title>Testing Database-Centric Applications for Causes of Database Deadlocks.</title> <pages>174-183</pages> <year>2013</year> <booktitle>ICST</booktitle> <ee>https://doi.org/10.1109/ICST.2013.19</ee> <ee>http://doi.ieeecomputersociety.org/10.1109/ICST.2013.19</ee> <crossref>conf/icst/2013</crossref> <url>db/conf/icst/icst2013.html#GrechanikHB13</url> </article>"""

  val article : scala.xml.Elem = XML.loadString("""<?xml version="1.0" encoding="ISO-8859-1"?><!DOCTYPE dblp PUBLIC "-//DBLP//DTD//EN" "https://dblp.uni-trier.de/xml/dblp.dtd"><article mdate="2017-05-24" key="conf/icst/GrechanikHB13"> <author>Mark</author><title>Testing Database-Centric Applications for Causes of Database Deadlocks.</title> <pages>174-183</pages> <year>2013</year> <booktitle>ICST</booktitle> <ee>https://doi.org/10.1109/ICST.2013.19</ee> <ee>http://doi.ieeecomputersociety.org/10.1109/ICST.2013.19</ee> <crossref>conf/icst/2013</crossref> <url>db/conf/icst/icst2013.html#GrechanikHB13</url> </article>""")

  val authorList : List[Node] = (article \ "author").toList
  print(authorList(0).child(0).toString())
  authorList.flatten.combinations(2).toList.foreach(combo => {

    print(new Text(combo(0).child(0).toString()).toString, new Text(combo(1).child(0).toString()).toString)
  })*/

  var startTag : List[Array[Byte]]= List("article","inproceedings").map(s => s.getBytes("utf-8"))

  var res = readUntilMatchStart(startTag).getOrElse(-1)

  print(res)


/*
  val text = List("Mark", "Ugo", "Isabel")

  text.combinations(2).toList.foreach(combo => {

    print(combo(0) + "->" + combo(1))
    print(combo(1) + "->" + combo(0))
  })*/

  //val text = List("Mark", "Ugo", "Isabel")

  //print(text.foldLeft("")(_ + "," + _).substring(1))


  def readUntilMatchStart(matchTags : List[Array[Byte]]) : Option[Int] = {
    var str:String = "inproceedingss"
    var i:Array[Int] = new Array[Int](matchTags.size)
    var j : Int = 0
    while (true){
      val b: Int = str.charAt(j)
      j = j +1

      if(b == -1)
        return None

      matchTags.indices.foreach(idx => {

        if(b == matchTags(idx)(i(idx))){
          i(idx) = i(idx)+1

          if(i(idx) >= matchTags(idx).length){
            System.out.println("Match")
            return Option(idx)
          }
        }

        else
          i(idx) =0

      })

      matchTags.indices.foreach(idx => {
        if(i(idx) ==0 && j >= str.length())
          return None
      })
    }
    return None
  }
}
