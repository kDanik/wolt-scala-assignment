package scala.tests

import delivery.fee.service.DeliveryFeeCalculator
import delivery.fee.support.DeliveryInformation
import org.scalatest.funsuite.AnyFunSuite

import java.time.ZonedDateTime
import scala.support.TestCaseDateGenerator

/**
 * Various tests delivery fee calculation of DeliveryFeeCalculator
 */
class DeliveryFeeCalculatorSuite extends AnyFunSuite {
  private val deliveryFeeCalculator: DeliveryFeeCalculator = new DeliveryFeeCalculator()
  private val testCaseDateGenerator: TestCaseDateGenerator = new TestCaseDateGenerator()

  test("calculate delivery distance fee for distance below or equal to 1000m") {
    assert(deliveryFeeCalculator.calculateDeliveryDistanceFee(501) == 200)
    assert(deliveryFeeCalculator.calculateDeliveryDistanceFee(1) == 200)
    assert(deliveryFeeCalculator.calculateDeliveryDistanceFee(1000) == 200)
  }

  test("calculate delivery distance fee for distance more than 1000m") {
    assert(deliveryFeeCalculator.calculateDeliveryDistanceFee(1001) == 300)
    assert(deliveryFeeCalculator.calculateDeliveryDistanceFee(1500) == 300)
    assert(deliveryFeeCalculator.calculateDeliveryDistanceFee(1501) == 400)
    assert(deliveryFeeCalculator.calculateDeliveryDistanceFee(30000) == 6000)
  }

  test("calculate cart value surcharge with cart value above minimal cart value (surcharge should not be applied)") {
    assert(deliveryFeeCalculator.calculateCartValueSurcharge(1000) == 0)
    assert(deliveryFeeCalculator.calculateCartValueSurcharge(2000) == 0)
    assert(deliveryFeeCalculator.calculateCartValueSurcharge(10000) == 0)
  }

  test("calculate cart value surcharge with cart value below minimal cart value (surcharge should be applied)") {
    assert(deliveryFeeCalculator.calculateDeliveryDistanceFee(1001) == 300)
    assert(deliveryFeeCalculator.calculateDeliveryDistanceFee(1500) == 300)
    assert(deliveryFeeCalculator.calculateDeliveryDistanceFee(1501) == 400)
    assert(deliveryFeeCalculator.calculateDeliveryDistanceFee(30000) == 6000)
  }

  test("validate that item fee is NOT applied for orders with small number of items") {
    assert(deliveryFeeCalculator.calculateNumberOfItemsSurcharge(1) == 0)
    assert(deliveryFeeCalculator.calculateNumberOfItemsSurcharge(4) == 0)
  }

  test("validate that normal item quantity fee is applied correctly") {
    assert(deliveryFeeCalculator.calculateNumberOfItemsSurcharge(5) == 50)
    assert(deliveryFeeCalculator.calculateNumberOfItemsSurcharge(10) == 300)
    assert(deliveryFeeCalculator.calculateNumberOfItemsSurcharge(12) == 400)
  }

  test("validate that 'bulk' fee is applied for big number of items") {
    assert(deliveryFeeCalculator.calculateNumberOfItemsSurcharge(13) == 570)
    assert(deliveryFeeCalculator.calculateNumberOfItemsSurcharge(14) == 620)
  }

  test("validate that friday rush delivery cost multiplier is applied (on Friday Rush)") {
    assert(deliveryFeeCalculator.calculateDeliveryTimeFeeMultiplier(testCaseDateGenerator.createFridayRushDateTime()) == 1.2f)
  }

  test("validate that friday rush delivery cost multiplier is NOT applied (outside of Friday Rush)") {
    assert(deliveryFeeCalculator.calculateDeliveryTimeFeeMultiplier(testCaseDateGenerator.createFridayButNotRushDateTime()) == 1f)
    assert(deliveryFeeCalculator.calculateDeliveryTimeFeeMultiplier(testCaseDateGenerator.createNotFridayDateTime()) == 1f)
  }

  test("validate if delivery is free for orders with cart value more than 100€") {
    val deliveryInformation1 = DeliveryInformation(10000, 10000, 20, testCaseDateGenerator.createNotFridayDateTime())
    assert(deliveryFeeCalculator.calculateDeliveryFee(deliveryInformation1) == 0)

    // extra test case with order made during friday rush
    val deliveryInformation2 = DeliveryInformation(30000, 2000, 5, testCaseDateGenerator.createFridayRushDateTime())
    assert(deliveryFeeCalculator.calculateDeliveryFee(deliveryInformation2) == 0)
  }

  test("validate that delivery fee is never exceeding maximum value") {
    val deliveryInformation1 = DeliveryInformation(300, 100000, 20, testCaseDateGenerator.createFridayRushDateTime())
    assert(deliveryFeeCalculator.calculateDeliveryFee(deliveryInformation1) == 1500)

    val deliveryInformation2 = DeliveryInformation(300, 100000, 20, testCaseDateGenerator.createNotFridayDateTime())
    assert(deliveryFeeCalculator.calculateDeliveryFee(deliveryInformation2) == 1500)
  }

  test("validate delivery fee calculation for some normal cases") {
    // delivery cost is from calculation in example from task description
    val deliveryInformation1 = DeliveryInformation(790, 2235, 4, ZonedDateTime.parse("2021-10-12T13:00:00Z"))
    assert(deliveryFeeCalculator.calculateDeliveryFee(deliveryInformation1) == 710)

    // delivery cost is from my calculation on a sheet of paper :)
    val deliveryInformation2 = DeliveryInformation(890, 1499, 13, testCaseDateGenerator.createFridayRushDateTime())
    assert(deliveryFeeCalculator.calculateDeliveryFee(deliveryInformation2) == 1176)

    val deliveryInformation3 = DeliveryInformation(890, 1499, 13, testCaseDateGenerator.createFridayButNotRushDateTime())
    assert(deliveryFeeCalculator.calculateDeliveryFee(deliveryInformation3) == 980)
  }
}
