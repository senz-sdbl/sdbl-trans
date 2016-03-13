package cyberdojo

/**
 * Created by eranga on 3/12/16.
 */
class RomanNumeral {

  var roman: Map[String, String] = Map()
  roman += ("1" -> "I")
  roman += ("5" -> "V")
  roman += ("10" -> "X")
  roman += ("50" -> "L")
  roman += ("100" -> "C")
  roman += ("500" -> "D")
  roman += ("1000" -> "M")

  def toRoman(v: Int, i: Int = 0): String = {
    val o = (math.pow(10, i).toInt * 1).toString
    val f = (math.pow(10, i).toInt * 5).toString
    val t = (math.pow(10, i).toInt * 10).toString

    if (0 < v && v < 4) {
      // 1 -> 3
      return roman(o) * v
    } else if (v == 4) {
      // 4
      return s"${roman(o)}${roman(f)}"
    } else if (v == 5) {
      // 5
      return roman(f)
    } else if (5 < v && v < 9) {
      // 6 -> 8
      return s"${roman(f)}${roman(o) * (v % 5)}"
    } else if (v == 9) {
      // 9
      return s"${roman(o)}${roman(t)}"
    }

    ""
  }

}
