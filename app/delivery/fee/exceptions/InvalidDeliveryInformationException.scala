package delivery.fee.exceptions

/**
 * Exception that should be thrown, if DeliveryInformation has invalid data.
 * (not logical data, for example negative item number)
 */
class InvalidDeliveryInformationException(private val message: String) extends IllegalArgumentException(message)
