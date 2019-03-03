package com.uic.mapreduce

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.{InputSplit, RecordReader, TaskAttemptContext}
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat

/**
  * XMLInputFormat class
  * This class extends TextInputFormat and is used to define an new
  * input format for XML type input
  */
class XMLInputFormat extends TextInputFormat {

  override def createRecordReader(split: InputSplit, context: TaskAttemptContext):
  RecordReader[LongWritable, Text] = {
    new XMLRecordReader()
  }

}


