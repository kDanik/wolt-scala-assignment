package scala.support

import java.time.ZonedDateTime

/**
 * This class contains methods to generate ZonedDateTime objects for test cases of delivery fee calculation tests
 */
class TestCaseDateGenerator() {
  /**
   * returns ZonedDateTime which is in Friday Rush time period (Friday, between 3 and 7 pm)
   */
  def createFridayRushDateTime(): ZonedDateTime = {
    ZonedDateTime.parse("2022-12-09T15:11:12Z")
  }

  /**
   * returns ZonedDateTime which is NOT Friday
   */
  def createNotFridayDateTime(): ZonedDateTime = {
    ZonedDateTime.parse("2022-12-08T15:11:12Z")
  }

  /**
   * returns ZonedDateTime which is friday, but NOT in Friday Rush period
   */
  def createFridayButNotRushDateTime(): ZonedDateTime = {
    ZonedDateTime.parse("2022-12-09T14:11:12Z")
  }
}
