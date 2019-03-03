package com.uic.mapreduce

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.{InputSplit, RecordReader, TaskAttemptContext}
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat

class XMLInputFormat extends TextInputFormat {

  override def createRecordReader(split: InputSplit, context: TaskAttemptContext):
  RecordReader[LongWritable, Text] = {
    new XMLRecordReader()
  }

}


