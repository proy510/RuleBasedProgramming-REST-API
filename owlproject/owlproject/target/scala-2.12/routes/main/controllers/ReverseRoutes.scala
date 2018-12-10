// @GENERATOR:play-routes-compiler
// @SOURCE:D:/owlproject/owlproject/conf/routes
// @DATE:Mon Nov 19 23:02:35 EST 2018

import play.api.mvc.Call


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:6
package controllers {

  // @LINE:6
  class ReverseHomeController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:24
    def getRejectionLog(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "rejectionlog")
    }
  
    // @LINE:48
    def requestToAddTransaction(senderID:String, receiverID:String, bankID:String, category:String, amount:String, transactionRequestID:String): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "transactionrequest/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("senderID", senderID)) + "/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("receiverID", receiverID)) + "/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("bankID", bankID)) + "/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("category", category)) + "/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("amount", amount)) + "/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("transactionRequestID", transactionRequestID)))
    }
  
    // @LINE:41
    def addMerchant(uniqueID:String): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "addmerchant/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("uniqueID", uniqueID)))
    }
  
    // @LINE:51
    def isCommercial(transactionID:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "iscommercial/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("transactionID", transactionID)))
    }
  
    // @LINE:54
    def isPersonal(transactionID:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "ispersonal/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("transactionID", transactionID)))
    }
  
    // @LINE:37
    def getRejCount(bankID:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "bankrejections/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("bankID", bankID)))
    }
  
    // @LINE:60
    def isRefund(transactionID:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "isrefund/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("transactionID", transactionID)))
    }
  
    // @LINE:33
    def isBlacklisted(bankID:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "isblacklisted/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("bankID", bankID)))
    }
  
    // @LINE:20
    def addBank(nationality:String, bankID:String): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "addbank/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("nationality", nationality)) + "/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("bankID", bankID)))
    }
  
    // @LINE:57
    def isPurchase(transactionID:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "ispurchase/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("transactionID", transactionID)))
    }
  
    // @LINE:44
    def addConsumer(uniqueID:String): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "addconsumer/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("uniqueID", uniqueID)))
    }
  
    // @LINE:28
    def getAcceptanceLog(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "acceptancelog")
    }
  
    // @LINE:63
    def isTrusted(merchantID:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "istrusted/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("merchantID", merchantID)))
    }
  
    // @LINE:16
    def reset(): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "reset")
    }
  
    // @LINE:6
    def index(): Call = {
      
      Call("GET", _prefix)
    }
  
  }

  // @LINE:9
  class ReverseAssets(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:9
    def versioned(file:Asset): Call = {
      implicit lazy val _rrc = new play.core.routing.ReverseRouteContext(Map(("path", "/public"))); _rrc
      Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[play.api.mvc.PathBindable[Asset]].unbind("file", file))
    }
  
  }


}
