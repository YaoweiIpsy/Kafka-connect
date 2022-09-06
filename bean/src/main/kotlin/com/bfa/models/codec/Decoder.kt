package com.bfa.models.codec

class Decoder(private val buffer:Array<UByte>) {
    var point = 0
    fun getNumber(): Long? {
        if (buffer[point] == (0u).toUByte()) {
            return null
        }
        var result = 0L
        var negative = (buffer[point].toUByte() and 64u)
        var bits = 0
        var mark:UByte = 0x3fu
        while (true) {
            result += (buffer[point] and mark).toLong().shl(bits)
            if (bits == 0) {
                mark = 0x7fu
                bits = 6
            }
            else {
                bits += 7
            }
            if (buffer[point++] < 128u) break
        }
        return if (negative > 0u) -result else result - 1
    }
    fun getData(len:Int): Array<Byte> {
        return Array(len) {buffer[point++].toByte()}
    }
}