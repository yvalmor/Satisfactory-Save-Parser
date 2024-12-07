package data

import parser.SaveFileParser
import mu.KotlinLogging

class CompressedSaveFileBody private constructor(
    val uePackageSignature: UInt,
    val maxChunkSize: UInt,
    val compressedSize: ULong,
    val uncompressedSize: ULong,
    val bytes: List<Byte>
) {
    private val logger = KotlinLogging.logger(CompressedSaveFileBody::class.java.name)

    init {
        logger.info { this }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    companion object {
        private val logger = KotlinLogging.logger(CompressedSaveFileBody::class.java.name)
        private val magicNumbers = uintArrayOf(
            0x9E2A83C1u,
            0x22222222u,
            0x03000000u
        )

        fun parseCompressedBody(parser: SaveFileParser): CompressedSaveFileBody {
            val uePackageSignature: UInt = parser.parseUInt32()
            logger.debug { "signature is correct: ${uePackageSignature == magicNumbers[0]}" }

            var ignoredUInt: UInt = parser.parseUInt32()
            logger.debug { "signature second part is correct: ${ignoredUInt == magicNumbers[1]}" }

            val maxChunkSize: UInt = parser.parseUInt32()
            logger.debug { "max chunk size is correct: ${maxChunkSize == 128u * 1024u}" }

            parser.parseByte()

            ignoredUInt = parser.parseUInt32()
            logger.debug { "signature third part is correct: ${ignoredUInt == magicNumbers[2]}" }

            val compressedSize: ULong = parser.parseUInt64()
            val uncompressedSize: ULong = parser.parseUInt64()

            val compressedSizeRepeat: ULong = parser.parseUInt64()
            val uncompressedSizeRepeat: ULong = parser.parseUInt64()

            logger.debug { "compressed size is same as repeat: ${compressedSize == compressedSizeRepeat}" }
            logger.debug { "uncompressed size is same as repeat: ${uncompressedSize == uncompressedSizeRepeat}" }

            val compressedBytes = parser.parseCompressedBytes(compressedSize)

            return CompressedSaveFileBody(
                uePackageSignature,
                maxChunkSize,
                compressedSize,
                uncompressedSize,
                compressedBytes
            )
        }
    }
}