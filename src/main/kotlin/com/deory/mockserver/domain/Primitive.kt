package com.deory.mockserver.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Primitive (
    @JsonProperty("m2m:rqp") var requestPrimitive: RequestPrimitive? = null,
    @JsonProperty("m2m:rsp") var responsePrimitive: ResponsePrimitive? = null
) {
    fun toResponsePrimitive(rsc: Int, rsm: String): Primitive {
        this.requestPrimitive?.let {
            this.responsePrimitive = ResponsePrimitive(
                rsc,
                rsm,
                it.content,
                it.to,
                it.from,
                it.requestIdentifier,
            )
        }

        this.responsePrimitive?.content?.let {
            it.keys.forEach { root ->
                it[root].let { resource ->
                    resource?.getOrPut("rn") { java.util.UUID.randomUUID() }
                    resource?.set("ri", java.util.UUID.randomUUID())
                }
            }
        }

        this.requestPrimitive = null
        return this
    }
}

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class RequestPrimitive @JsonCreator constructor(
    @JsonProperty("op") var operation: Int,
    @JsonProperty("to") var to: String,
    @JsonProperty("fr") var from: String,
    @JsonProperty("rqi") var requestIdentifier: String,
    @JsonProperty("pc") var content: LinkedHashMap<String, LinkedHashMap<String, Any>>? = null
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonRootName("m2m:rsp")
data class ResponsePrimitive @JsonCreator constructor(
    @JsonProperty("rsc") var responseStatusCode: Int,
    @JsonProperty("rsm") var responseStatusMessage: String? = null,
    @JsonProperty("pc") var content: LinkedHashMap<String, LinkedHashMap<String, Any>>? = null,
    @JsonProperty("to") var to: String,
    @JsonProperty("fr") var from: String,
    @JsonProperty("rqi") var requestIdentifier: String,
    @JsonProperty("cnty") var contentType: String = "json"
)


