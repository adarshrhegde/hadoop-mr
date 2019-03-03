package com.uic.mapreduce
import com.typesafe.config.ConfigFactory
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.{FileOutputFormat, TextOutputFormat}
import org.slf4j.LoggerFactory
//import pureconfig.generic.auto._


class Main{}

/**
  * Main program
  * Map Reduce execution starts here
  */
object Main {

  val logger = LoggerFactory.getLogger(classOf[Main])

  def main(args: Array[String]): Unit = {

    val configuration = new Configuration()
    /** Separator between key value in output set to comma */
    configuration.set("mapreduce.output.textoutputformat.separator",";")

    val job = Job.getInstance(configuration, ConfigFactory.load().getString("mapReduce.jobName"))

    /** Setting configurations for the job  */
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


