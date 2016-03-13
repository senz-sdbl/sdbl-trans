package cyberdojo

import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by eranga on 3/12/16.
 */
class RomanNumeralTests extends FlatSpec with Matchers {

  "toRoman " should "returns II when number is 2" in {
    val r = new RomanNumeral()
    r.toRoman(2) should be("II")
  }

  it should "returns IV when number is 4" in {
    val r = new RomanNumeral()
    r.toRoman(4) should be("IV")
  }

  it should "returns V when number is 5" in {
    val r = new RomanNumeral()
    r.toRoman(5) should be("V")
  }

  it should "returns VIII when number is 8" in {
    val r = new RomanNumeral()
    r.toRoman(8) should be("VIII")
  }

  it should "returns IX when number is 9" in {
    val r = new RomanNumeral()
    r.toRoman(9) should be("IX")
  }

  it should "returns empty when number is 0" in {
    val r = new RomanNumeral()
    r.toRoman(0) should be("")
  }

  it should "returns XL when number is 40" in {
    val r = new RomanNumeral()
    r.toRoman(4, 1) should be("XL")
  }

  it should "returns L when number is 50" in {
    val r = new RomanNumeral()
    r.toRoman(5, 1) should be("L")
  }

  it should "returns XC when number is 90" in {
    val r = new RomanNumeral()
    r.toRoman(9, 1) should be("XC")
  }

}
