package com.github.daggerok.akkakotlinexample

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.UntypedAbstractActor
import akka.pattern.Patterns
import java.time.Duration

class PingPongActor : UntypedAbstractActor() {

    companion object Factory {
        private val type = PingPongActor::class.java
        val props: Props = Props.create(type) { PingPongActor() }
        val name: String = type.simpleName
    }

    private val log = context.system.log()

    override fun onReceive(message: Any?) {
        when (message) {
            "ping" -> {
                log.info("received: $message")
                sender.tell("pong", self)
            }
            "pong" -> {
                log.info("received: $message")
                sender.tell("ping", self)
            }
            ////not good:
            //else -> throw RuntimeException("message not supported")
            //better:
            else -> unhandled(message)
        }
    }

    internal fun myUnhandled(any: Any): Unit {
        log.info("oops: $any")
    }
}

object PingPong {
    fun play() {
        val actorSystem = ActorSystem.create("main-system")
        val actorRef = actorSystem.actorOf(PingPongActor.props, PingPongActor.name)
        val duration = Duration.ofSeconds(1)

        Patterns.ask(actorRef, "ping", duration).print()
        Patterns.ask(actorRef, "pong", duration).print()
        Patterns.ask(actorRef, "fail me please!", duration).print()

        Thread.sleep(duration.toMillis() * 3)
        actorSystem.terminate()
    }
}
