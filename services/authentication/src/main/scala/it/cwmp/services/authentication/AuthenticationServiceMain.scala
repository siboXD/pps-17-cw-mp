package it.cwmp.services.authentication

import it.cwmp.services.wrapper.DiscoveryApiWrapper
import it.cwmp.utils.{Logging, ServiceArguments, VertxInstance}

import scala.util.{Failure, Success}

/**
  * AuthenticationService entry-point
  */
object AuthenticationServiceMain extends App with VertxInstance with Logging {

  val arguments = ServiceArguments(args)
  // scalastyle:off import.grouping
  import arguments._
  // scalastyle:on import.grouping

  // Executing the app
  log.info("Deploying AuthenticationService... ")
  vertx.deployVerticleFuture(AuthenticationServiceVerticle(current_port))
    .andThen {
      case Success(_) =>
        log.info("AuthenticationService up and running!")
        DiscoveryApiWrapper(discovery_host, discovery_port)
          .publish(Service.DISCOVERY_NAME, current_host, current_port)
      case Failure(ex) => log.info("Error deploying AuthenticationService", ex)
    }
}
