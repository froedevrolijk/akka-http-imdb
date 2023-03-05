package com.froedevrolijk.restapi.http.routes

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.ValidationRejection
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.froedevrolijk.restapi.core.search.{CastCrew, GenreInfo, InMemoryDatabase, RankInfo, TitleInfo}
import org.scalatest.{Matchers, WordSpec}

class SearchRouteTest extends WordSpec with Matchers with ScalatestRouteTest {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  private val corbettPrimaryTitles = Vector("Corbett and Courtney Before the Kinetograph", "The Corbett-Fitzsimmons Fight")
  private val corbettStartYear = Vector("1894", "1897")
  private val topRatedMoviesForGenre = Vector("tt0001456", "tt0001787", "tt0002145", "tt0002437", "tt0002582",
                                              "tt0004030", "tt0004821", "tt0006160", "tt0007254", "tt0008876")

  "GET /search" should {

    "return an empty array" in new Context {
      Get("/search/title?text=Froede") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        responseAs[Vector[(TitleInfo, CastCrew)]] shouldBe empty
      }
    }

    "search titles by text" in new Context {
      Get("/search/title?text=Corbett") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        responseAs[Vector[(TitleInfo, CastCrew)]].map(_._1.primaryTitle) should contain theSameElementsInOrderAs corbettPrimaryTitles
        responseAs[Vector[(TitleInfo, CastCrew)]].map(_._1.startYear) should contain theSameElementsInOrderAs corbettStartYear
      }
    }

    "search genre by text" in new Context {
      Get("/search/genre?text=Comedy") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        responseAs[Vector[(GenreInfo, RankInfo)]].map(_._1.tconst) should contain theSameElementsInOrderAs topRatedMoviesForGenre
        responseAs[Vector[(GenreInfo, RankInfo)]].map(_._2.averageRating shouldNot be < 8.5)
      }
    }

    "search James Cagney by text" in new Context {
      Get("/search/person?text=James%20Cagney") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        responseAs[String] should equal("The degrees of separation between this person and Kevin Bacon is 1")
      }
    }

    "search Ingmar Bergman by text" in new Context {
      Get("/search/person?text=Ingmar%20Bergman") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        responseAs[String] should equal("The degrees of separation between this person and Kevin Bacon is 2")
      }
    }

    "search Marlon Brando by text" in new Context {
      Get("/search/person?text=Marlon%20Brando") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        contentType shouldEqual ContentTypes.`application/json`
        responseAs[String] should equal("The degrees of separation between this person and Kevin Bacon is 3")
      }
    }

    "not search with empty text" in new Context {
    Get("/search/title?text=") ~> router.route ~> check {
      rejection shouldBe a[ValidationRejection]
    }
  }

    "not search without parameters" in new Context {
      Get("/search/title") ~> router.route ~> check {
        rejection shouldBe a[ValidationRejection]
      }
    }

  }

  trait Context {
    val repository = new InMemoryDatabase
    val router = new SearchRoute(repository)
  }

}