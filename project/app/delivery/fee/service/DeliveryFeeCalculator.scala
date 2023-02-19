package delivery.fee.service

import delivery.fee.support.DeliveryInformation

import java.time.{DayOfWeek, LocalDateTime, ZoneOffset, ZonedDateTime}
import javax.inject.Singleton

@Singleton
class DeliveryFeeCalculator {
  private val MinimumCartValue = 1000
  private val MaximumDeliveryPrice = 1500
  private val CartValueNeededForFreeDelivery = 10000

  private val DistanceFeeForFirstThousandMeters = 200;
  private val DistanceForEach500Meters = 100;

  private val SurchargePerItem = 50;
  private val SurchargeForBulkOrders = 120;

  /**
   * Calculates total delivery fee in cents, using data from deliveryFeeInput (items quantity, distance, cart value etc.)
   * This method DOESN'T validate data, only calculates result
   */
  def calculateDeliveryFee(deliveryFeeInput: DeliveryInformation): Int = {
    if (deliveryFeeInput.cartValue >= CartValueNeededForFreeDelivery) return 0

    var deliveryFee = calculateCartValueSurcharge(deliveryFeeInput.cartValue)
    deliveryFee += calculateNumberOfItemsSurcharge(deliveryFeeInput.numberOfItems)
    deliveryFee += calculateDeliveryDistanceFee(deliveryFeeInput.deliveryDistance)

    deliveryFee = (deliveryFee * calculateDeliveryTimeFeeMultiplier(deliveryFeeInput.time)).toInt

    if (deliveryFee > MaximumDeliveryPrice) MaximumDeliveryPrice else deliveryFee
  }

  /**
   * Calculate delivery fee part, that depends on delivery distance.
   */
  def calculateDeliveryDistanceFee(deliveryDistanceInMeters: Int): Int = {
    // initial fee applied for first 1000 meters
    val distanceFee = DistanceFeeForFirstThousandMeters

    if (deliveryDistanceInMeters > 1000) {
      // additional fee applied for every 500 meters
      val additionalDistanceFee = math.ceil((deliveryDistanceInMeters - 1000) / 500f).toInt * DistanceForEach500Meters
      distanceFee + additionalDistanceFee
    }
    else {
      distanceFee
    }
  }

  /**
   * Calculate surcharge for deliveries with small cart value.
   * For cart values below certain break point surcharge is added
   */
  def calculateCartValueSurcharge(cartValueInCents: Int): Int = {
    if (cartValueInCents < MinimumCartValue) MinimumCartValue - cartValueInCents else 0
  }

  /**
   * Calculate surcharge for delivery item quantity.
   * Surcharge is 50 cent * number of items above 4 items.
   * Small additional fee added for "bulk" deliveries with more than 12 items.
   */
  def calculateNumberOfItemsSurcharge(numberOfItems: Int): Int = {
    if (numberOfItems >= 5) {
      // default fee for number of items above 4 items
      val numberOfItemsSurcharge = (numberOfItems - 4) * SurchargePerItem

      // additional bulk fee for more than 12 items
      if (numberOfItems > 12) numberOfItemsSurcharge + SurchargeForBulkOrders else numberOfItemsSurcharge
    } else {
      0
    }
  }

  /**
   * Calculate fee multiplier, related to order time.
   * Currently there is special multiplier for orders made in Friday Rush hours (Friday (3 - 7 PM UTC)).
   */
  def calculateDeliveryTimeFeeMultiplier(orderTime: ZonedDateTime): Float = {
    // as it is not specified which time zone input data can be, it must be converted here to UTC
    val utcOrderTime = LocalDateTime.ofInstant(orderTime.toInstant, ZoneOffset.UTC)

    if (utcOrderTime.getDayOfWeek == DayOfWeek.FRIDAY && utcOrderTime.getHour >= 15 && orderTime.getHour < 19) 1.2f else 1
  }
}
