package model

import play.api.libs.json.Json

case class User(id: Option[Long] = None, first: String, last: String)

object UserJson {
  implicit val userFormat = Json.format[User]
}
