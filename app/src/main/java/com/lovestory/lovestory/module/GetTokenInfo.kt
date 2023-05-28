package com.lovestory.lovestory.module

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lovestory.lovestory.model.LoginPayload
import java.util.*

fun getTokenInfo(token: String): LoginPayload {
    val chunks: List<String> = token.split(".")
    val decoder: Base64.Decoder = Base64.getUrlDecoder()
    val payload = String(decoder.decode(chunks[1]))
    val payloadJSON: JsonObject = JsonParser.parseString(payload).asJsonObject
    return Gson().fromJson(payloadJSON, LoginPayload::class.java)
}