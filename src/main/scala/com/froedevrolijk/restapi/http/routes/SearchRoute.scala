package com.froedevrolijk.restapi.http.routes

import akka.http.scaladsl.server.Route
import com.froedevrolijk.restapi.core.search.{Database, Search}
import com.froedevrolijk.restapi.utils.errorhandling._

import scala.concurrent.ExecutionContext

class SearchRoute(database: Database)(implicit ec: ExecutionContext) extends MovieDirectives {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val route: Route =
    pathPrefix("search") {
      path("title") {
        get {
          parameters('text.?).as(Search) { searchTitle =>
            handleWithGeneric(database.getTitleIDs(searchTitle)) { titles =>
              handleWithGeneric(database.getTitleInfo(titles)) { t =>
                complete(t)
              }
            }
          }
        }
      } ~
      path("genre") {
        get {
          parameters('text.?).as(Search) { searchGenre =>
            handleWithGeneric(database.getGenreIDs(searchGenre)) { genres =>
              handleWithGeneric(database.getGenreInfo(genres)) { g =>
                complete(g)
              }
            }
          }
        }
      } ~
      path("person") {
        get {
          parameters('text.?).as(Search) { searchPerson =>
            handleWithGeneric(database.getPersonID(searchPerson)) { persons =>
              handleWithGeneric(database.findTitlesAndPersonsForPersonID(persons.head)) { p =>
                val degreesOfSeparation = database.checkIfKevinBaconInSet(p)
                complete(s"The degrees of separation between this person and Kevin Bacon is $degreesOfSeparation")
              }
            }
          }
        }
      }
    }

}
