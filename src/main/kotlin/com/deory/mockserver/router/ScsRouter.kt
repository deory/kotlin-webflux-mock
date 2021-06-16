package com.deory.mockserver.router

import com.deory.mockserver.handler.ScsHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.router

@Configuration
class ScsRouter(private val handler: ScsHandler) {

    @Bean
    fun routerFunction() = RouterFunctions.nest(RequestPredicates.path("/"),
        router {
            listOf(
                POST("**", handler::post),
                PUT("**", handler::put)
            )
        }
    )

}