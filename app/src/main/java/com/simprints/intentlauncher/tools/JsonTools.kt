package com.simprints.intentlauncher.tools

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.simprints.intentlauncher.ui.intent.model.ResponseEvent

/**
 * Tries to extract the event items from the response JSON. This function then maps the data of each
 * object to the [ResponseEvent]
 */
fun String?.extractEventsFromJson(gson: Gson): List<ResponseEvent> {
    if (isNullOrEmpty()) return emptyList()

    try {
        val type = object : TypeToken<Map<String, Any>>() {}.type
        val eventsJson = getEventsJson(gson)
        val eventsMap: Map<String, Any> = gson.fromJson(eventsJson, type)
        val eventObjects = eventsMap["events"] as List<Map<String, Any>>
        return eventObjects.map { jsonEventEntry ->
            val payload = jsonEventEntry["payload"] as Map<String, Any>
            val createdAt = payload["createdAt"] as Map<String, Any>
            val createdAtMs = createdAt["ms"] as Long
            return@map ResponseEvent(
                id = jsonEventEntry["id"] as String,
                type = jsonEventEntry["type"] as String,
                scopeId = jsonEventEntry["id"] as String,
                projectId = jsonEventEntry["id"] as String,
                createdAtMs = createdAtMs,
                payload = jsonEventEntry["payload"] as Map<String, Any>,
            )

        }
    } catch (e: Exception) {
        e.printStackTrace()
        return emptyList()
    }
}

/**
 * Tries to extract the event JSON that is string-encoded within the JSON response.
 *
 * The response usually has the following structure:
 * {
 *   "events" : "{\"events\":[{\"id\":\"some-value\"...}]"
 * }
 *
 * It unwraps the "events" json string into the proper JSON format:
 * {
 *   "events": [
 *     {
 *       "id": "123456",
 *       "payload": {
 *         "createdAt": {
 *           "ms": 17122187574893,
 *           "isTrustworthy": true,
 *           "msSinceBoot": 4404982333
 *         },
 *         "eventVersion": 2,
 *         "endedAt": {
 *           "ms": 1712218598483,
 *           "isTrustworthy": true,
 *           "msSinceBoot": 4404982357
 *         },
 *         "pool": {
 *           "type": "PROJECT",
 *           "count": 2
 *         },
 *         "matcher": "RANK_ONE",
 *         "result": [],
 *         "type": "ONE_TO_MANY_MATCH"
 *       },
 *       "type": "ONE_TO_MANY_MATCH",
 *       "scopeId": "12345",
 *       "projectId": "some-value"
 *     },
 *     {[...]}
 *   ]
 * }
 */
fun String?.getEventsJson(gson: Gson): String = try {
    val type = object : TypeToken<Map<String, Any>>() {}.type
    val map: Map<String, Any> = gson.fromJson(this, type)
    val eventsString = map["events"] as String
    eventsString.replace("\\\"", "\"")
} catch (e: Exception) {
    e.printStackTrace()
    ""
}