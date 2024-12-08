package parser

import data.CompressedSaveFileBody
import data.SaveFileHeader
import java.io.InputStream

class SaveFileParser(private val input: InputStream) : SatisfactorySaveParser(input) {
    fun parseHeader() : SaveFileHeader {
        logger.info { "Parsing save header" }

        val headerVersion = parseInt()
        val saveVersion = parseInt()
        val buildVersion = parseInt()
        val mapName = parseString()
        val mapOptions = parseString()
        val sessionName = parseString()
        val playedSeconds = parseInt()
        val saveTimestamp = parseLong()
        val sessionVisibility = parseByte() == 1
        val editorObjectVersion = parseInt()
        val modMetadata = parseString()
        val modFlags = parseInt()
        val saveIdentifier = parseString()

        parseInt()
        parseInt()
        parseLong()
        parseLong()

        val cheatFlag = parseInt() == 1

        return SaveFileHeader(
            headerVersion,
            saveVersion,
            buildVersion,
            mapName,
            mapOptions,
            sessionName,
            playedSeconds,
            saveTimestamp,
            sessionVisibility,
            editorObjectVersion,
            modMetadata,
            modFlags,
            saveIdentifier,
            cheatFlag
        )
    }

    fun parseCompressedBody(): CompressedSaveFileBody? {
        logger.info { "Parsing compressed body" }

        if (input.available() == 0)
            return null

        val uePackageSignature: UInt = parseUInt32()
        parseUInt32()

        val maxChunkSize: UInt = parseUInt32()

        parseByte()
        parseUInt32()

        val compressedSize: ULong = parseUInt64()
        val uncompressedSize: ULong = parseUInt64()

        parseUInt64()
        parseUInt64()

        val compressedBytes = parseCompressedBytes(compressedSize)

        return CompressedSaveFileBody(
            uePackageSignature,
            maxChunkSize,
            compressedSize,
            uncompressedSize,
            compressedBytes
        )
    }

    private fun parseCompressedBytes(size: ULong): List<Byte> {
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
