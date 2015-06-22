package isat.model


/**
 * @author Tarek Auel
 * @since June 03, 2015.
 */
trait Vertex extends Serializable {

  def getLabel: String

  def getJson: String = {
    Gson.gson.toJson(this)
    //JacksMapper.writeValueAsString(this)
  }

  def getId: Long



}
