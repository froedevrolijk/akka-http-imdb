package com.froedevrolijk.restapi.http

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import com.froedevrolijk.restapi.core.search.InMemoryDatabase

class HttpRouteTest extends WordSpec with Matchers with ScalatestRouteTest {

  "GET /healthcheck" should {

    "return 200 OK" in {
      val repository = new InMemoryDatabase()
      val router = new HttpRoute(repository)

      Get("/healthcheck") ~> router.route ~> check {
        responseAs[String] shouldBe "OK"
        status.intValue() shouldBe 200
      }
    }

  }

}