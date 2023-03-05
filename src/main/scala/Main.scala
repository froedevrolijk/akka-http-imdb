import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.froedevrolijk.restapi.Config
import com.froedevrolijk.restapi.core.search.InMemoryDatabase
import com.froedevrolijk.restapi.http.HttpRoute

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Main extends App {

  def startApp() = {

    implicit val system: ActorSystem = ActorSystem("akka-http-imdb")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContext = system.dispatcher

    val config = Config.load()

    val database = new InMemoryDatabase

    val httpRoute = new HttpRoute(database)

    Http().bindAndHandle(httpRoute.route, config.http.host, config.http.port).onComplete {
      case Success(_) => println("App started!")
      case Failure(exception) => s"Failed: ${exception.getMessage}"
    }
  }

  startApp()

}
