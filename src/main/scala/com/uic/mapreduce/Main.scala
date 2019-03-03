package com.uic.mapreduce
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.{FileOutputFormat, TextOutputFormat}
import org.slf4j.LoggerFactory
//import pureconfig.generic.auto._

case class Config(name:String)

class Main{}

object Main {

  val logger = LoggerFactory.getLogger(classOf[Main])

  //val config = ConfigReader.loadConfig()

  def main(args: Array[String]): Unit = {

    val configuration = new Configuration()
    val job = Job.getInstance(configuration, "Co-Author discovery")

    job.setJarByClass(this.getClass)
    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[Text])

    job.setMapperClass(classOf[Map])
    job.setReducerClass(classOf[Reduce])

    job.setInputFormatClass(classOf[XMLInputFormat])
    job.setOutputFormatClass(classOf[TextOutputFormat[Text, Text]])

    FileInputFormat.addInputPath(job, new Path(args(0)))
    FileOutputFormat.setOutputPath(job, new Path(args(1)))
    System.exit(if(job.waitForCompletion(true))  0 else 1)

  }
  
}


