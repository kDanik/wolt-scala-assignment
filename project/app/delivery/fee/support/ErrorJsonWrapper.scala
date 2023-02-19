package delivery.fee.support

import play.api.libs.json.JsonConfiguration.Aux
import play.api.libs.json._

/**
 * Class used for creation of error responses in JSON format
 */
case class ErrorJsonWrapper(title: String, detail: String, code: Int) {
  private implicit val jsonCaseConversion: Aux[Json.MacroOptions] = JsonConfiguration(JsonNaming.SnakeCase)
  private implicit val errorJsonWrapperWriter: OWrites[ErrorJsonWrapper] = Json.writes[ErrorJsonWrapper]

  /**
   * @return returns contents of this object as JSON obj
   */
  def asJsonObj: JsValue = {
    Json.toJson(this)
  }
}
