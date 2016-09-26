package de.gitzoz.akkastreamkafkasample.domain

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

final case class Clicked(what: String, where: String)

object MessagesOps {
  implicit val clickedWrites: Writes[Clicked] = new Writes[Clicked] {
    def writes(model: Clicked) = Json.obj(
      "what" -> model.what,
      "where" -> model.where)
  }
  val clickedBuilder = (JsPath \ "what").read[String] and
    (JsPath \ "where").read[String]
  implicit val clickedReads: Reads[Clicked] = clickedBuilder(Clicked.apply _)
}



