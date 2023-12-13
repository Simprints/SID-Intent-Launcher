package com.simprints.intentlauncher.tools

import android.os.Bundle
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import kotlin.math.ceil

@Suppress("UNCHECKED_CAST")
class BundleTypeAdapterFactory : TypeAdapterFactory {

    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (!Bundle::class.java.isAssignableFrom(type.rawType)) {
            return null
        }
        return BundleTypeAdapter(gson) as TypeAdapter<T>
    }
}

private class BundleTypeAdapter(private val gson: Gson) : TypeAdapter<Bundle>() {

    override fun write(writer: JsonWriter, bundle: Bundle?) {
        if (bundle == null) {
            writer.nullValue()
            return
        }

        writer.beginObject()
        for (key in bundle.keySet()) {
            writer.name(key)
            val value = bundle[key]
            if (value == null) {
                writer.nullValue()
            } else {
                gson.toJson(value, value.javaClass, writer)
            }
        }
        writer.endObject()
    }

    override fun read(reader: JsonReader): Bundle? = when (reader.peek()) {
        JsonToken.NULL -> {
            reader.nextNull()
            null
        }

        JsonToken.BEGIN_OBJECT -> toBundle(readObject(reader))
        else -> throw IOException("expecting object: " + reader.path)
    }

    @SuppressWarnings("unchecked")
    private fun toBundle(values: List<Pair<String, Any?>>): Bundle = Bundle().also { bundle ->
        for ((key, value) in values) {
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Parcelable -> bundle.putParcelable(key, value)
                is List<*> -> {
                    val objectValues = value as List<Pair<String, Any?>>
                    val subBundle = toBundle(objectValues)
                    bundle.putParcelable(key, subBundle)
                }

                else -> {}
            }
        }
    }

    private fun readObject(reader: JsonReader): List<Pair<String, Any?>> =
        mutableListOf<Pair<String, Any?>>().also { objects ->
            reader.beginObject()
            while (reader.peek() != JsonToken.END_OBJECT) {
                when (reader.peek()) {
                    JsonToken.NAME -> {
                        val name = reader.nextName()
                        val value: Any? = readValue(reader)

                        objects.add(name to value)
                    }

                    JsonToken.END_OBJECT -> {}
                    else -> {}
                }
            }
            reader.endObject()
        }

    private fun readValue(reader: JsonReader): Any? = when (reader.peek()) {
        JsonToken.BEGIN_ARRAY -> readArray(reader)
        JsonToken.BEGIN_OBJECT -> readObject(reader)
        JsonToken.BOOLEAN -> reader.nextBoolean()
        JsonToken.NUMBER -> readNumber(reader)
        JsonToken.STRING -> reader.nextString()
        JsonToken.NULL -> {
            reader.nextNull()
            null
        }

        else -> null
    }

    private fun readNumber(reader: JsonReader): Any {
        val doubleValue = reader.nextDouble()
        if (doubleValue - ceil(doubleValue) != 0.0) {
            return doubleValue
        }
        val longValue = doubleValue.toLong()
        return if (longValue >= Int.MIN_VALUE && longValue <= Int.MAX_VALUE) {
            longValue.toInt()
        } else longValue
    }

    private fun readArray(reader: JsonReader): List<Any?> = mutableListOf<Any?>().also { list ->
        reader.beginArray()
        while (reader.peek() != JsonToken.END_ARRAY) {
            val element = readValue(reader)
            list.add(element)
        }
        reader.endArray()
    }
}
