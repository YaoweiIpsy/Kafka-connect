package com.bfa.models.codec

import com.bfa.models.BfaPojo
import java.nio.charset.Charset
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*

class Serializer (val encoder:Encoder = Encoder()) {
    private fun <T : Any> append(data:T) {
        if (data is Int) {
            encoder.append((data as Int).toLong())
        }
        else if (data is Long) {
            encoder.append(data as Long)
        }
        else if (data is Short) {
            encoder.append((data as Short).toLong())
        }
        else if (data is Boolean) {
            encoder.append(if (data) 1 else 0)
        }
        else if (data is UInt) {
            encoder.append((data as UInt).toLong())
        }
        else if (data is Float) {
            encoder.append((data as Float).toBits().toLong())
        }
        else if (data is Double) {
            encoder.append((data as Double).toBits())
        }
        else if (data is String) {
            val str = data as String
            encoder.append(str.length.toLong())
            encoder.append(str.toByteArray(Charset.defaultCharset()).toTypedArray())
        } else if (data is Array<*>) {
            val array = data as Array<Any>
            encoder.append(array.size.toLong())
            for (item in array) {
                append(item)
            }
        }
        else if (data::class.isData) {
            val clazz = data::class

            clazz.primaryConstructor?.parameters?.forEach {param ->
                val f = clazz.memberProperties.first({ it.name == param.name }) as KProperty1<Any, *>
                append(f.get(data) as Any)
            }
        } else {
            throw UnsupportedOperationException("unsupported")
        }
    }
    fun serialize(model: Any) {
        val clazz = model::class
        assert(clazz.isData && clazz.hasAnnotation<BfaPojo>())
        val bfaPojo = clazz.findAnnotation<BfaPojo>()!!
        encoder.append("BFA".toByteArray(Charset.defaultCharset()).toTypedArray())
        append(bfaPojo.group + ":" + bfaPojo.name + ":" + bfaPojo.version)
        append(model)
    }
}