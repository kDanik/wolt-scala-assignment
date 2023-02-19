package delivery.fee.service

import delivery.fee.exceptions.InvalidDeliveryInformationException
import delivery.fee.support.DeliveryInformation

import javax.inject.Singleton
import scala.util.Try

@Singleton
class DeliveryInformationValidator {
  /**
   * Validates DeliveryInformation and returns Try object
   * (Success = object is valid)
   * (Failure = object is invalid, exception message contains description of validation error)
   */
  def validate(deliveryFeeInput: DeliveryInformation): Try[Unit] = {
    Try {
      validateCartValue(deliveryFeeInput.cartValue)
      validateDeliveryDistance(deliveryFeeInput.deliveryDistance)
      validateNumberOfItems(deliveryFeeInput.numberOfItems)
    }
  }

  private def validateCartValue(cartValue: Int): Unit = {
    // Cart value can't be 0 or negative
    if (cartValue < 1) {
      throw new InvalidDeliveryInformationException("Invalid cart value! Negative or null cart value is not allowed")
    }
  }

  private def validateDeliveryDistance(deliveryDistanceInMeters: Int): Unit = {
    // Delivery distance can't be negative (I guess distance could be 0 somehow, if someone is living in same building as restaurant)
    if (deliveryDistanceInMeters < 0) {
      throw new InvalidDeliveryInformationException("Invalid delivery distance! Distance can't be negative")
    }
  }

  private def validateNumberOfItems(numberOfItems: Int): Unit = {
    // Number of items can't be 0 or negative
    if (numberOfItems < 1) {
      throw new InvalidDeliveryInformationException("Invalid number of items! At least one item must be delivered")
    }
  }
}
