package com.github.daggerok.akkakotlinexample

import akka.actor.AbstractActor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.Patterns
import com.github.daggerok.akkakotlinexample.CounterActor.Factory.Commands.*
import java.time.Duration

class CounterActor : AbstractActor() {

    companion object Factory {
        sealed class Commands {
            override fun toString(): String = javaClass.simpleName

            object Increment : Commands()
            object Decrement : Commands()
            object GetStatus : Commands()
        }

        private val type = CounterActor::class.java
        val props: Props = Props.create(type) { CounterActor() }
        val name: String = type.simpleName
    }

    override fun createReceive(): Receive = withCounter(0)

    private fun withCounter(counter: Int): Receive =
        receiveBuilder()
            .matchAny { cmd ->
                val response = "current: $counter, received: $cmd"
                context.system.log().info(response)
                when (cmd) {
                    is Increment -> {
                        sender.tell(response, self)
                        context.become(withCounter(counter + 1))
                    }
                    is Decrement -> {
                        sender.tell(response, self)
                        context.become(withCounter(counter - 1))
                    }
                    is GetStatus -> sender.tell("current counter: $counter", self)
                    else -> unhandled(cmd)
                }
            }
            .build()
}

object Counter {
    fun play() {
        val actorSystem = ActorSystem.create("counter-system")
        val actorRef = actorSystem.actorOf(CounterActor.props, CounterActor.name)
        val duration = Duration.ofSeconds(1)

        Patterns.ask(actorRef, GetStatus, duration)
            .print()

        Patterns.ask(actorRef, Increment, duration)
        Patterns.ask(actorRef, GetStatus, duration)
            .print()

        Patterns.ask(actorRef, Increment, duration)
        Patterns.ask(actorRef, Increment, duration)
        Patterns.ask(actorRef, GetStatus, duration)
            .print()

        Patterns.ask(actorRef, Increment, duration)
        Patterns.ask(actorRef, Decrement, duration)
        Patterns.ask(actorRef, Increment, duration)
        Patterns.ask(actorRef, GetStatus, duration)
            .print()

        Patterns.ask(actorRef, "fail me please!", duration)
            .print()

        Thread.sleep(duration.toMillis() * 3)
        actorSystem.terminate()
    }
}
