package cyberdojo

/**
 * Created by eranga on 3/12/16.
 */
class Zprinter {
  def Zprint() = {

  }

  def isZ(i: Int): Option[Z] = {
    //None
    if (i % 3 == 0 || i.toString.contains("3"))
      Some(TZ(i))
    else if (i % 5 == 0 || i.toString.contains("5"))
      Some(FZ(i))
    else None
  }
}
