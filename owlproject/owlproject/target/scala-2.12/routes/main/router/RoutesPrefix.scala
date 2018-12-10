// @GENERATOR:play-routes-compiler
// @SOURCE:D:/owlproject/owlproject/conf/routes
// @DATE:Mon Nov 19 23:02:35 EST 2018


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
