package hw2

import com.holdenkarau.spark.testing.{
  RDDComparisons,
  RDDGenerator,
  SharedSparkContext
}
import org.scalatest.matchers.should.Matchers
import org.apache.spark.rdd.RDD
import org.scalatest.flatspec.AnyFlatSpec

/** Main class for unit tests.
  * Responsible for data processing correctness checking
  */
class ProcessorTestSpec
    extends AnyFlatSpec
    with Matchers
    with SharedSparkContext {
  val input = Array(
    "0,14:45:18,AUG,BEB",
    "1,03:45:18,RLT,AJU",
    "2,14:45:18,AGS,BEB",
    "3,21:45:18,BGW,PEK",
    "4,21:50:18,BGW,PEK"
  )

  val out = Set(
    Result(14, "USA", "United Kingdom", 2),
    Result(3, "Niger", "Brazil", 1),
    Result(21, "Iraq", "China", 2)
  )

  "processing" should "work correctly" in {
    val data = sc.parallelize(input)
    val catalog = Map(
      "AUG" -> "USA",
      "BEB" -> "United Kingdom",
      "RLT" -> "Niger",
      "AJU" -> "Brazil",
      "AGS" -> "USA",
      "BGW" -> "Iraq",
      "PEK" -> "China"
    )

    val output = Main.processData(data, catalog)

    assert(output.count == 3)
    assert(output.collect().toSet == out)
  }
}
