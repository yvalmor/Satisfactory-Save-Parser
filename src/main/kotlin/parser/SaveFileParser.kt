package parser

import java.io.InputStream

class SaveFileParser(private val input: InputStream) : SatisfactorySaveParser(input) {
    fun parseCompressedBytes(size: ULong): List<Byte> {
        logger.debug { "parsing compressed bytes" }

        var compressedSize: ULong = size
        val buffers: MutableList<ByteArray> = mutableListOf()

        while (compressedSize > 0u) {
            var bufferSize: Int = Int.SIZE_BYTES
            if (compressedSize < bufferSize.toUInt())
                bufferSize = compressedSize.toInt()

            val buffer = input.readNBytes(bufferSize)
            buffers.add(buffer)

            compressedSize -= bufferSize.toUInt()
        }

        return buffers.flatMap(ByteArray::asList)
    }
}
