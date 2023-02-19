package delivery.fee.support

import java.time.ZonedDateTime

/**
 *
 * @param cartValue Value of the shopping cart in cents.
 * @param deliveryDistance The distance between the store and customer’s location in meters.
 * @param numberOfItems The number of items in the customer's shopping cart.
 * @param time Order time in ISO format.
 */
final case class DeliveryInformation(cartValue: Int, deliveryDistance: Int, numberOfItems: Int, time: ZonedDateTime)
