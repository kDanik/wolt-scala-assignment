package scala.tests

import delivery.fee.service.DeliveryInformationValidator
import delivery.fee.support.DeliveryInformation
import org.scalatest.PrivateMethodTester
import org.scalatest.funsuite.AnyFunSuite

import scala.support.TestCaseDateGenerator

/**
 * Various test for validation class DeliveryInformationValidator
 */
class DeliveryInformationValidatorSuite extends AnyFunSuite with PrivateMethodTester {
  private val deliveryFeeInputValidator: DeliveryInformationValidator = new DeliveryInformationValidator
  private val testCaseDateGenerator: TestCaseDateGenerator = new TestCaseDateGenerator()

  test("validation shouldn't throw exception for valid input") {
    val validDeliveryFeeInput = DeliveryInformation(10, 250, 1, testCaseDateGenerator.createNotFridayDateTime())

    assert(deliveryFeeInputValidator.validate(validDeliveryFeeInput).isSuccess)
  }

  test("validation shouldn't throw exception for valid input (using only corner cases as parameters)") {
    val validDeliveryFeeInput = DeliveryInformation(1, 0, 1, testCaseDateGenerator.createNotFridayDateTime())

    assert(deliveryFeeInputValidator.validate(validDeliveryFeeInput).isSuccess)
  }

  test("validation should throw exception for multiple invalid fields") {
    val invalidDeliveryFeeInput = DeliveryInformation(-1, -1, 0, testCaseDateGenerator.createNotFridayDateTime())
    assert(deliveryFeeInputValidator.validate(invalidDeliveryFeeInput).isFailure)
  }

  test("validation should throw exception for invalid (too small) number of items") {
    val invalidNumberOfItemsInput = DeliveryInformation(-1, -1, 0, testCaseDateGenerator.createNotFridayDateTime())
    assert(deliveryFeeInputValidator.validate(invalidNumberOfItemsInput).isFailure)
  }

  test("validation should throw exception for negative distance") {
    val negativeDistanceInput = DeliveryInformation(100, -1, 3, testCaseDateGenerator.createNotFridayDateTime())
    assert(deliveryFeeInputValidator.validate(negativeDistanceInput).isFailure)
  }

  test("validation should throw exception for negative cart value") {
    val negativeCartValueInput = DeliveryInformation(-1, 100, 3, testCaseDateGenerator.createNotFridayDateTime())
    assert(deliveryFeeInputValidator.validate(negativeCartValueInput).isFailure)
  }

  test("validation should throw exception for null cart value") {
    val nullCartValueInput = DeliveryInformation(0, 100, 3, testCaseDateGenerator.createNotFridayDateTime())
    assert(deliveryFeeInputValidator.validate(nullCartValueInput).isFailure)
  }
}
