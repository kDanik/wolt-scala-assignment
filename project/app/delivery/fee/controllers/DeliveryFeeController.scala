package delivery.fee.controllers

import delivery.fee.service.{DeliveryFeeCalculator, DeliveryInformationValidator}
import delivery.fee.support.{DeliveryInformation, ErrorJsonWrapper}
import play.api.libs.json.JsonConfiguration.Aux
import play.api.libs.json._
import play.api.mvc.{Action, BaseController, ControllerComponents, Result}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.util.{Failure, Success}

@Singleton
class DeliveryFeeController @Inject()(val controllerComponents: ControllerComponents,
                                      val deliveryFeeCalculator: DeliveryFeeCalculator,
                                      val deliveryFeeInputValidator: DeliveryInformationValidator) extends BaseController {

  implicit val jsonNamingStrategy: Aux[Json.MacroOptions] = JsonConfiguration(JsonNaming.SnakeCase)
  implicit val deliveryFeeInputJson: OFormat[DeliveryInformation] = Json.format[DeliveryInformation]

  /**
   * /deliveryCost Endpoint, calculates delivery cost in cents using provided data in json format (DeliveryInformation)
   *
   * @return Json with calculated delivery cost.
   *         If error occurred returns json with error code and description
   */
  def calculateDeliveryFee(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DeliveryInformation] match {
      case JsSuccess(value, _) => Future.successful(calculateDeliveryFee(value))
      case _ => Future.successful(
        BadRequest(ErrorJsonWrapper("Invalid payload", "DeliveryInformation can't be created from request body", 400).asJsonObj)
      )
    }
  }

  private def calculateDeliveryFee(deliveryFeeInput: DeliveryInformation): Result = {
    deliveryFeeInputValidator.validate(deliveryFeeInput) match {
      case Success(_) => Ok(Json.obj("delivery_fee" -> deliveryFeeCalculator.calculateDeliveryFee(deliveryFeeInput)))
      case Failure(exception) => BadRequest(ErrorJsonWrapper("Validation failed", exception.getMessage, 400).asJsonObj)
    }
  }
}
