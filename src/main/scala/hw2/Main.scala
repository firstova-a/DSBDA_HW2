package hw2

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.rdd.RDD
import com.datastax.spark.connector._
import com.datastax.driver.core.MetadataHook

/** Class for resutl storing
  *
  * @param hour hour number
  * @param origin origin coutry
  * @param destination destination country
  * @param number number of flights for this hour
  */
case class Result(
    hour: Int,
    origin: String,
    destination: String,
    number: Int
)

/** Main class for program run.
  */
object Main {

  /** Method for data processing implementing business logic.
    *
    * @param data RDD with input strings
    * @return RDD with Result records
    */
  def processData(
      data: RDD[String],
      catalog: Map[String, String]
  ): RDD[Result] = {
    data
      .map(_.split(","))
      .map(x => {
        val hour = x(1).split(":")(0).toInt
        val originCountry = catalog(x(2))
        val destinationCountry = catalog(x(3))
        ((hour, originCountry, destinationCountry), 1)
      })
      .reduceByKey(_ + _)
      .map(x => {
        Result(x._1._1, x._1._2, x._1._3, x._2)
      })
  }

  /** Main function.
    *
    * @param args Sequence of arguments, can be empty.
    */
  def main(args: Array[String]): Unit = {
    val dataFilePath = "hdfs://namenode:9000/user/root/input/data-*"
    val catalogFilePath = "hdfs://namenode:9000/user/root/input/catalog-*"
    val appName = "hw2"

    val conf = new SparkConf()
      .setAppName(appName)
      .set("spark.cassandra.connection.host", "cassandra")
      .set("com.datastax.driver.USE_NATIVE_CLOCK", "false")

    val sparkContext = new SparkContext(conf)

    sparkContext.setLogLevel("WARN")

    val data = sparkContext.textFile(dataFilePath)
    val catalogRDD = sparkContext
      .textFile(catalogFilePath)
      .map(x => {
        val Array(airport, country) = x.split(",")
        (airport, country)
      })
      .collect()
      .toMap

    val catalog = sparkContext.broadcast(catalogRDD)
    val res = processData(data, catalog.value)

    res.saveToCassandra("hw2", "output")
  }
}
