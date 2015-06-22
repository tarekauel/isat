package de.orangecode.graph

import de.orangecode.Context
import isat.model.{HashTag, Tweet, User, Vertex}
import org.apache.spark.graphx.Graph
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

/**
 * @author Tarek Auel
 * @since June 07, 2015.
 */
object GraphDisplay {

  def fromSpreadingActivation[ED: ClassTag](
      ctx: Context,
      g: Graph[Vertex, ED],
      saResult: RDD[(Long, Map[Long, Double])],
      count: Int): Unit = {

    def getGroup(v: Vertex, d: Double): Int = {
      if (d == 1.0) 1
      else v match {
        case t: Tweet => 2
        case h: HashTag => 3
        case u: User => 4
      }

    }

    val sumSaResult =
      saResult.map((t) => (t._1, t._2.values.sum))

    val gWithRes: Graph[(Vertex, Double), ED] =
      g.outerJoinVertices(sumSaResult)({
        (_, v: Vertex, value: Option[Double]) =>
          (v, value getOrElse 0.0)
      })

    val topKNodes = gWithRes
      .vertices.sortBy(_._2._2, ascending = false).take(count)

    val vidTopK = ctx.sc.broadcast(topKNodes.map(_._1).toSet)

    /*var subGraph =
      gWithRes.subgraph(
        (et) => true,
        (vid, t) => vidTopK.value.contains(vid))*/

    //if (subGraph.edges.isEmpty()) {
    var subGraph =
      gWithRes.subgraph(
        (et) => vidTopK.value.contains(et.srcId) || vidTopK.value.contains(et.dstId),
        (vid, t) => true)

    subGraph = subGraph.outerJoinVertices(subGraph.degrees){
      (_, x, deg) => (x, deg.getOrElse(0))}.subgraph(
      (et) => true,
        (vid, t) => t._2 > 1 || vidTopK.value.contains(vid)).mapVertices((vid, t) => t._1)
    //}

    val maxValue = topKNodes.map(_._2._2).filter(_ != 1.0).max

    val newSubGraph =
      subGraph.mapVertices(
        (vid, t) => (vid,
          (t._1, t._2, if (t._2 == 1.0) 3 else 1 / (maxValue / t._2) * 2)))

    val newIds = subGraph.vertices.keys.zipWithIndex().collect().toMap

    var out = "{ \"nodes\": ["

    if (!newSubGraph.vertices.isEmpty()) {
      out += newSubGraph.vertices.map((x) => {
        "{\"group\": " + getGroup(x._2._2._1, x._2._2._2) +", \"size\": " +
          x._2._2._3 + ", \"id\": " + newIds(x._1) + ", \"label\": \"" + x._2._2._1.getLabel.replaceAll("\"", "").replaceAll("\n", "") + "\"}"
      }).reduce((a, b) => a + "," + b)
    }

    out += "], \"links\": ["

    if (!newSubGraph.edges.isEmpty()) {
      out += newSubGraph.edges.map((x) => {
        "{\"source\": " + newIds(x.srcId) + ", \"target\": " + newIds(x.dstId) + ", \"value\": 1}"
      }).reduce((a, b) => a + "," + b)
    }

    out += "]}"

    println(out)

  }

}
