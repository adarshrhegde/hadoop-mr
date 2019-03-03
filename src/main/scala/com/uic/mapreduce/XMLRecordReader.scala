package com.uic.mapreduce

import java.io.IOException

import com.typesafe.config.ConfigFactory
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FSDataInputStream, FileSystem, Path}
import org.apache.hadoop.io.{DataOutputBuffer, LongWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.FileSplit
import org.apache.hadoop.mapreduce.{InputSplit, RecordReader, TaskAttemptContext}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._


/**
  * XMLRecordReader object
  */
object XMLRecordReader {

  private val START_TAG_KEYS : List[String] = ConfigFactory.load()
    .getStringList("mapReduce.xml.startTags").asScala.toList

  private val END_TAG_KEYS : List[String] = ConfigFactory.load()
    .getStringList("mapReduce.xml.endTags").asScala.toList

  private val TOP_TAG : String = ConfigFactory.load().getString("mapReduce.xml.topTag")
}

/**
  * XMLRecordReader class
  * This class is an implementation of a Record Reader for XML type inputs
  * This class is used by Map Reduce to break the xml based on start and end tags
  */
class XMLRecordReader extends RecordReader[LongWritable, Text] {

  import XMLRecordReader._

  val logger = LoggerFactory.getLogger(classOf[XMLRecordReader])
  private var startTags : List[Array[Byte]] = _
  private var endTags : List[Array[Byte]] = _
  private var start : Long = _
  private var end : Long = _
  private var fsin : FSDataInputStream = _
  private var buffer : DataOutputBuffer = new DataOutputBuffer()
  private var key : LongWritable = new LongWritable()
  private var value: Text = new Text()
  private var topTag : Array[Byte] = _

  /**
    * Initialize the properties
    * @param inputSplit
    * @param taskAttemptContext
    * @throws java.io.IOException
    * @throws java.lang.InterruptedException
    */
  @throws(classOf[IOException])
  @throws(classOf[InterruptedException])
  override def initialize(inputSplit: InputSplit,
                          taskAttemptContext: TaskAttemptContext): Unit = {

    val conf : Configuration = taskAttemptContext.getConfiguration()
    startTags = START_TAG_KEYS.map(s => s.getBytes("utf-8"))
    endTags = END_TAG_KEYS.map(s => s.getBytes("utf-8"))
    topTag = (TOP_TAG).getBytes("utf-8")
    val fileSplit : FileSplit = inputSplit.asInstanceOf[FileSplit]


    start = fileSplit.getStart
    end = start + fileSplit.getLength
    val file : Path = fileSplit.getPath
    val fs : FileSystem = file.getFileSystem(conf)
    fsin = fs.open(file)
    fsin.seek(start)

  }

  /**
    * Check if next start tag - end tag exists in xml
    * @throws java.io.IOException
    * @throws java.lang.InterruptedException
    * @return
    */
  @throws(classOf[IOException])
  @throws(classOf[InterruptedException])
  override def nextKeyValue(): Boolean = {

    if(fsin.getPos < end){
      val idx : Int = readUntilMatchStart(startTags).getOrElse(-1)
      if(idx > -1) {
        try {

          buffer.write(topTag)
          buffer.write(startTags(idx))
          if(readUntilMatch(endTags(idx), true)){
            key.set(fsin.getPos)
            value.set(buffer.getData, 0, buffer.getLength)
            logger.debug("Found next key value")
            return true
          }
        } finally {
          buffer.reset()
        }
      }
    }

    false
  }

  /**
    * Get current key
    * @throws java.io.IOException
    * @throws java.lang.InterruptedException
    * @return
    */
  @throws(classOf[IOException])
  @throws(classOf[InterruptedException])
  override def getCurrentKey: LongWritable = key

  /**
    * Get current value
    * @throws java.io.IOException
    * @throws java.lang.InterruptedException
    * @return
    */
  @throws(classOf[IOException])
  @throws(classOf[InterruptedException])
  override def getCurrentValue: Text = value

  /**
    * Get progress of splitting process
    * @return
    */
  override def getProgress: Float = (fsin.getPos -start) / (end-start).asInstanceOf[Float]

  /**
    * Close the input stream
    * @throws java.io.IOException
    */
  @throws(classOf[IOException])
  override def close(): Unit = fsin.close()

  /**
    * Read the input stream until end tag is matched
    * @param matchLoc
    * @param withinBlock
    * @return
    */
  def readUntilMatch(matchLoc : Array[Byte], withinBlock : Boolean) : Boolean = {
    var i:Int = 0

    while (true){
      val b: Int = fsin.read()
      if(b == -1)
        return false

      if(withinBlock)
        buffer.write(b)

      if(b == matchLoc(i)){
        i = i+1
        if(i >= matchLoc.length){
            return true
          }

      } else
        i = 0

      if(!withinBlock && i ==0 && fsin.getPos >= end)
        return false
    }
    return false
  }

  /**
    * Read the input stream until start tag is matched
    * @param matchTags
    * @return
    */
  def readUntilMatchStart(matchTags : List[Array[Byte]]) : Option[Int] = {
    var i:Array[Int] = Array.fill(matchTags.size)(0)
    while (true){
      val b: Int = fsin.read()
      if(b == -1)
        return None

      matchTags.indices.foreach(idx => {

        if(b == matchTags(idx)(i(idx))){
          i(idx) = i(idx)+1

          if(i(idx) >= matchTags(idx).length){
            return Option(idx)
          }
        }

        else
          i(idx) =0

      })

      matchTags.indices.foreach(idx => {
        if(i(idx) ==0 && fsin.getPos >= end)
          return None
      })
    }
    return None
  }
}
