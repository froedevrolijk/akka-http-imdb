package com.froedevrolijk.restapi.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.froedevrolijk.restapi.http.routes.SearchRoute
import com.froedevrolijk.restapi.core.search.Database

import scala.concurrent.ExecutionContext

class HttpRoute(database: Database)(implicit ec: ExecutionContext) {

  private val searchRoute = new SearchRoute(database)

  val route: Route =
  pathPrefix("v1") {
    searchRoute.route // ~
  } ~
  pathPrefix("healthcheck") {
    get {
      complete("OK")
    }
  }
}
