import data.SaveFileHeader
import mu.KotlinLogging
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

class SaveFileParser constructor(private val input: InputStream) {
    private val logger = KotlinLogging.logger(SaveFileParser::class.java.name)

    init {
        logger.info { "Initializing parser" }
    }

    /**
     * Returns a signed integer between -128 and 127
     */
    fun parseByte(): Int {
        val byte: Int = input.read()
        val returnValue = byte - 128
        logger.debug { "Parsing byte $returnValue" }
        return returnValue
    }

    /**
     * Parses four bytes in little-endian order that represents a signed integer
     * between -2,147,483,648 and 2,147,483,647 and returns it
     */
    fun parseInt(): Int {
        val bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
        val readBytes = input.readNBytes(4)

        bytes.put(readBytes)
        bytes.position(0)
        val returnValue = bytes.getInt()
        logger.debug { "Parsing int $returnValue" }
        return returnValue
    }

    /**
     * Parses four bytes in little-endian order that represents an unsigned integer
     * between 0 and 4,294,967,295 and returns it
     */
    fun parseUInt32(): UInt {
        val bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
        val readBytes = input.readNBytes(4)

        bytes.put(readBytes)
        bytes.position(0)
        val returnValue = bytes.getInt().toUInt()
        logger.debug { "Parsing int $returnValue" }
        return returnValue
    }

    /**
     * Parses four bytes in little-endian order that represents a signed integer
     * between -9,223,372,036,854,775,80 and 9,223,372,036,854,775,807 and returns it
     */
    fun parseLong(): Long {
        val bytes = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
        val readBytes = input.readNBytes(8)

        bytes.put(readBytes)
        bytes.position(0)
        val returnValue = bytes.getLong()
        logger.debug { "Parsing long $returnValue" }
        return returnValue
    }

    /**
     * Parses four bytes in little-endian order that represents an unsigned integer
     * between 0 and 18,446,744,073,709,551,615 and returns it
     */
    fun parseUInt64(): ULong {
        val bytes = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
        val readBytes = input.readNBytes(8)

        bytes.put(readBytes)
        bytes.position(0)
        val returnValue = bytes.getLong().toULong()
        logger.debug { "Parsing int $returnValue" }
        return returnValue
    }

    /**
     * Parses four bytes in little-endian order that represents a signed floating-point number
     * with single precision according to the binary32 format of IEEE 754 and returns it
     */
    fun parseFloat(): Float {
        val bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
        val readBytes = input.readNBytes(4)

        bytes.put(readBytes)
        bytes.position(0)
        val returnValue = bytes.getFloat()
        logger.debug { "Parsing float $returnValue" }
        return returnValue
    }

    /**
     * Parses one or two bytes depending on whether the character is encoded in utf8 or utf16
     * @param isUtf16 whether characters are encoded in utf16 or utf8
     */
    fun parseChar(isUtf16: Boolean = false): Char {
        val bytes = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
        val readBytes = input.readNBytes(
            if (isUtf16) 2 else 1
        )

        bytes.put(readBytes)
        if (!isUtf16)
            bytes.put(0)

        bytes.position(0)
        val returnValue = bytes.getChar()
        logger.debug { "Parsing char $returnValue" }
        return returnValue
    }

    /**
     * Parses a null-terminated string with variable length
     */
    fun parseString(): String {
        var stringSize = parseInt()
        var isUtf16 = false

        if (stringSize == 0) {
            val returnValue = ""
            logger.debug { "Parsing var string '$returnValue'" }
            return returnValue
        }

        if (stringSize < 0) {
            stringSize *= -1
            isUtf16 = true
        }

        val readBytes = ByteBuffer.wrap(input.readNBytes(stringSize - 1))
        val returnValue =
            if (isUtf16)
                StandardCharsets.UTF_16.decode(readBytes).toString()
            else
                StandardCharsets.UTF_8.decode(readBytes).toString()

        // Used to parse null termination bytes on the string
        parseChar(isUtf16)

        logger.debug { "Parsing var string '$returnValue'" }
        return returnValue
    }

    /**
     * Parses a fixed size string
     */
    fun parseString(size: Int): String {
        val readBytes = ByteBuffer.wrap(input.readNBytes(size))
        val returnValue = StandardCharsets.UTF_8.decode(readBytes).toString()
        logger.debug { "Parsing fix string '$returnValue'" }
        return returnValue
    }

    fun parseCompressedBytes(size: ULong): List<Byte> {
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
