package de.orangecode.entitymanager

import de.orangecode.Context
import isat.model.{Tweet, Gson, HashTag}
import org.apache.spark.Logging

import scala.io.Source

/**
 * @author Tarek Auel
 * @since June 05, 2015.
 */
class HashTagManager private(ctx: Context)
    extends EntityManager[HashTag](ctx, HashTag.getHashTag)
    with Logging {

  override val filename = "parquet/hashtags"
}

object HashTagManager {

  private[this] var INSTANCE: HashTagManager = null

  def get = INSTANCE

  def apply(ctx: Context): Unit = {
    INSTANCE = new HashTagManager(ctx)
  }

}
