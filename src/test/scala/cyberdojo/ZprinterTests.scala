package cyberdojo

import org.scalatest.{FlatSpec, Matchers}

/**
 * Created by eranga on 3/12/16.
 */
class ZprinterTests extends FlatSpec with Matchers {
  "isZ " should "returns TZ(_) when number divide by 3" in {
    val z = new Zprinter
    z.isZ(3) match {
      case Some(TZ(_)) =>
      case p =>
        fail("Not a TZ its a " + p)
    }
  }

  it should "returns FZ(_) when number divide by 5" in {
    val z = new Zprinter
    z.isZ(5) match {
      case Some(FZ(_)) =>
      case p =>
        fail("Not a FZ its a " + p)
    }
  }

  it should "returns TZ(_) when number contains a 3" in {
    val z = new Zprinter
    z.isZ(32) match {
      case Some(TZ(_)) =>
      case p =>
        fail("Not a TZ its a " + p)
    }
  }

  it should "returns FZ(_) when number contains a 5" in {
    val z = new Zprinter
    z.isZ(59) match {
      case Some(FZ(_)) =>
      case p =>
        fail("Not a FZ its a " + p)
    }
  }
}
