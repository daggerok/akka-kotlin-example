package com.github.daggerok.akkakotlinexample

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

fun CompletionStage<out Any>.print(): CompletableFuture<Void> = this
    .toCompletableFuture()
    .thenAccept { println("result: $it") }
    .exceptionally { println("oops, we are failed: ${it.localizedMessage}"); null }

fun main() {
    Counter.play()
    PingPong.play()
}
