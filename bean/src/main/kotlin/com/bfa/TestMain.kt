package com.bfa

import com.bfa.models.codec.Deserializer
import com.bfa.models.codec.Serializer
import com.bfa.users.models.User
import com.bfa.users.models.UserWithAge
import com.bfa.users.models.UserWithFLName
import com.google.gson.Gson

fun Array<UByte>.toHex(): String = "Bfa [len: %d] -> ".format(size) + joinToString(" ") {
    it.toString(radix = 16).padStart(2, '0')
}

fun <T: Any> test(data: T) {
     val serializer = Serializer()
    serializer.serialize(data)
    val buf = serializer.encoder.toBuffer()
    println(buf.toHex())
    val json = Gson().toJson(data)
    println("Json [len: %d] -> %s".format(json.length, json))

    val deserializer = Deserializer(buf)
    val data0 = deserializer.deserialize<T>()
    assert(data == data0)
}

fun main(argv:Array<String>) {
    test(User("Yaowei Li"))
    test(UserWithFLName("Yaowei", "Li"))
    test(UserWithAge("Yaowei", "Li"))
}