/*
 * Copyright 2016 Stefan Roehrbein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gitzoz.akkastreamkafkasample.domain

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.JsPath
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.Reads
import play.api.libs.json.Reads.StringReads
import play.api.libs.json.Reads.applicative
import play.api.libs.json.Reads.functorReads
import play.api.libs.json.Writes

final case class Clicked(what: String, where: String)

object MessagesOps {
  implicit val clickedWrites: Writes[Clicked] = new Writes[Clicked] {
    def writes(model: Clicked) =
      Json.obj("what" -> model.what, "where" -> model.where)
  }
  val clickedBuilder = (JsPath \ "what").read[String] and
      (JsPath \ "where").read[String]
  implicit val clickedReads: Reads[Clicked] = clickedBuilder(Clicked.apply _)
}
