package de.codepitbull.akka.reactivestreams

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.{ Broadcast, FlowGraph, Sink, Source }
import io.vertx.core.buffer.Buffer
import org.reactivestreams.{ Subscription, Subscriber }

import scala.concurrent.forkjoin.ThreadLocalRandom
import scala.util.{ Failure, Success }

class RandomValuesFlow {

  def start(subscriber: Subscriber[Buffer]): Unit = {
    implicit val system = ActorSystem("Sys")
    import system.dispatcher
    implicit val materializer = ActorFlowMaterializer()

    val bufferSource: Source[Buffer, Unit] =
      Source(() => Iterator.continually(Buffer.buffer("random value: "+ThreadLocalRandom.current().nextInt(1000000))))

    val mySink = Sink.apply(subscriber);
    val consoleSink = Sink.foreach[Buffer] { value =>
      println("console "+value.getString(0, value.length()))
    }

    val materialized = FlowGraph.closed(consoleSink, mySink)((console, _) => console) { implicit builder =>
      (console, mine) =>
        import FlowGraph.Implicits._
        val broadcast = builder.add(Broadcast[Buffer](2))
        bufferSource ~> broadcast ~> console
        broadcast ~> mine
    }.run()

    materialized.onComplete {
      case Success(_) =>
        system.shutdown()
      case Failure(e) =>
        println(s"Failure: ${e.getMessage}")
        system.shutdown()
    }

  }
}
