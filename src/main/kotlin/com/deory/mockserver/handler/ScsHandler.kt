package com.deory.mockserver.handler

import com.deory.mockserver.domain.Primitive
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.net.URI
import java.time.Duration
import java.util.concurrent.atomic.AtomicLong

@Component
class ScsHandler {

    val successCount = AtomicLong()
    val failCount = AtomicLong()

    fun post(req: ServerRequest): Mono<ServerResponse> {
        return Mono.just(req)
            .map { it.queryParam("delay").orElseGet { "0" } }
            .flatMap {
                Flux.interval(Duration.ofMillis(it.toLong())).take(1L).toMono()
            }
            .flatMap {
                req.bodyToMono(Primitive::class.java)
                    .map { it.toResponsePrimitive(2001, "created") }
            }
            .flatMap {
                 created(URI("/"))
                     .contentType(MediaType.APPLICATION_JSON)
                     .header("X-M2M-RSC", "2001")
                     .header("X-M2M-RSM", "created")
                     .body(Mono.just(it), Primitive::class.java)
            }
            .doOnNext { successCount.incrementAndGet() }
            .doOnError { failCount.incrementAndGet() }
            .switchIfEmpty(
                badRequest().build()
            )
    }

    fun put(req: ServerRequest): Mono<ServerResponse> = ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(req.bodyToMono(String::class.java), String::class.java)
        .doOnNext { successCount.incrementAndGet() }
        .doOnError { failCount.incrementAndGet() }
        .switchIfEmpty(
            badRequest().build()
        )

    @Scheduled(fixedDelay = 1000)
    fun monitor() {
        println("Success TPS : ${successCount.getAndSet(0)}, Fail TPS : ${failCount.getAndSet(0)}")
    }
}