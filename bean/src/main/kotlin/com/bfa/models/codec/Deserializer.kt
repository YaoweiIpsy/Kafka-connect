package com.bfa.models.codec

import com.bfa.users.models.User
import com.bfa.users.models.UserWithAge
import com.bfa.users.models.UserWithFLName
import java.nio.charset.Charset
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class Deserializer(buffer: Array<UByte>) {
    private val decoder = Decoder(buffer)
    val models = mapOf(
        "com.bfa.users:user:1.0.0" to User::class,
        "com.bfa.users:user:2.0.0" to UserWithFLName::class,
        "com.bfa.users:user:2.1.0" to UserWithAge::class
    )
    @Suppress("UNCHECKED_CAST")
    fun <T:Any> get(clazz: KClass<T>): T? {
        if (clazz == Int::class) {
            return decoder.getNumber()?.toInt() as T
        }
        if (clazz == Long::class) {
            return decoder.getNumber() as T
        }
        if (clazz == String::class) {
            val len = decoder.getNumber()!!.toInt()
            val bytes = decoder.getData(len)
            return String(bytes.toByteArray(), Charset.defaultCharset()) as T
        }
//        if (clazz == IntArray::class) {
//            val len = decoder.getNumber()!!.toInt()
//            val results = Array(len) {
//                get(Int::class)!!
//            }
//            return results as T
//        }
//
//        if (clazz.java.isArray) {
//            val type = clazz.typeParameters.get(0)
//            val len = decoder.getNumber()!!.toInt()
//            println(clazz.typeParameters)
//            val results = Array<Any>(len) {
////                get<Any>(clazz.typeParameters.get(0).)!!
//            }
//            return null
//        }
        if (clazz.isData) {
            return clazz.primaryConstructor?.let {
                val params = it.parameters?.map {
                    it to get(it.type.classifier as KClass<*>)
                }?.toMap()!!

                it.callBy(params)
            }
        }

        return null
    }
    fun <T:Any> deserialize(): T? {
        val header = String(decoder.getData(3).toByteArray(), Charset.defaultCharset())
        val clazzName = get(String::class)
        val clazz = models.get(clazzName)!!
        return get(clazz) as T
    }
}