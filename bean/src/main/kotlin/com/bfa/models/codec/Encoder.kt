package com.bfa.models.codec

class Encoder(val initial:Int = 512, val factor:Float = 0.5f) {
    var buffer:Array<UByte>
    var point: Int = 0
    init {
        buffer = Array(initial) { 0u }
    }
    fun extension(requireSize: Int = 16) {
        var nextSize:Int = buffer.size
        while (point > nextSize - requireSize) {
            nextSize = (nextSize * (1 + factor)).toInt()
        }
        buffer = Array(nextSize) { if (it < point) buffer[it] else 0u }
    }
    fun append(int: Long?) {
        extension()
        int?.let {
            var byte:UByte = 0u
            var number:Long = it + 1
            var mask = 0x3FL
            var bits = 6
            if ( it < 0 ) {
                byte = 64u
                number = -it
            }
            while ( number > 0 ) {
                byte = byte or (number and mask).toUByte()
                number = number.shr(bits)
                if (number > 0) byte = byte or 128u
                if (mask == 0x3FL) {
                    mask = 0x7FL
                    bits = 7
                }
                buffer[point++] = byte
                byte = 0u
            }
            return
        }
        buffer[point++] = 0u
    }
    fun append(data: Array<Byte>) {
        extension(data.size + 16)
        data.forEach { buffer[point++] = it.toUByte() }
    }
    fun toBuffer(): Array<UByte> {
        return Array(point) {buffer[it]}
    }
}