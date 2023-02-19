package scala.tests

import akka.stream.Materializer
import delivery.fee.controllers.DeliveryFeeController
import delivery.fee.service.{DeliveryFeeCalculator, DeliveryInformationValidator}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsJson, Results}
import play.api.test.Helpers._
import play.api.test.{FakeHeaders, FakeRequest}

import scala.support.TestCaseDateGenerator

/**
 * Simple tests of DeliveryFeeController to validate if it handles invalid and valid request correctly.
 * Calculations and exact validation tests are in DeliveryFeeCalculatorSuite and DeliveryInformationValidatorSuite
 */
class DeliveryFeeControllerSuite extends PlaySpec with Results with GuiceOneAppPerTest {
  private val deliveryFeeController: DeliveryFeeController = new DeliveryFeeController(stubControllerComponents(), new DeliveryFeeCalculator, new DeliveryInformationValidator)
  private val testCaseDateGenerator: TestCaseDateGenerator = new TestCaseDateGenerator()

  "DeliveryFeeControllerSuite calculateDeliveryFee" should {
    "return code 200 and delivery_fee for valid payload" in {
      implicit lazy val materializer: Materializer = app.materializer

      val body = Json.obj(
        "cart_value" -> 790,
        "delivery_distance" -> 2000,
        "number_of_items" -> 4,
        "time" -> testCaseDateGenerator.createNotFridayDateTime()
      )

      val result = call(deliveryFeeController.calculateDeliveryFee(), createDeliveryCostFakeRequest(body))

      status(result) must be(OK)
      contentAsString(result) must include("delivery_fee")
    }

    "return code 400 for payload with invalid scheme" in {
      implicit lazy val materializer: Materializer = app.materializer

      val body = Json.obj(
        "invalid_scheme1" -> 1,
        "delivery_distance" -> 1,
        "number_of_items" -> 1,
        "invalid_scheme2" -> testCaseDateGenerator.createNotFridayDateTime()
      )

      val result = call(deliveryFeeController.calculateDeliveryFee(), createDeliveryCostFakeRequest(body))

      status(result) must be(BAD_REQUEST)
    }

    "return code 400 for payload with invalid values(not passing validation) in payload" in {
      implicit lazy val materializer: Materializer = app.materializer

      val body = Json.obj(
        "cart_value" -> -1,
        "delivery_distance" -> -1,
        "number_of_items" -> -1,
        "time" -> testCaseDateGenerator.createNotFridayDateTime()
      )

      val result = call(deliveryFeeController.calculateDeliveryFee(), createDeliveryCostFakeRequest(body))

      status(result) must be(BAD_REQUEST)
    }
  }

  private def createDeliveryCostFakeRequest(body: JsValue): FakeRequest[AnyContentAsJson] =
    FakeRequest(POST, "/calculateDeliveryFee")
      .withJsonBody(body)
      .withHeaders(FakeHeaders(Seq(CONTENT_TYPE -> "application/json")))
}

