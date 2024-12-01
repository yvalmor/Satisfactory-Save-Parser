package data

import SaveFileParser
import mu.KotlinLogging

data class SaveFileHeader private constructor(
    val headerVersion: Int,
    val saveVersion: Int,
    val buildVersion: Int,
    val mapName: String,
    val mapOptions: String,
    val sessionName: String,
    val playedSeconds: Int,
    val saveTimestamp: Long,
    // Parsed as byte
    val sessionVisibility: Boolean,
    val editorObjectVersion: Int,
    val modMetadata: String,
    val modFlags: Int,
    val saveIdentifier: String,
    val cheatFlag: Boolean
) {
    private val logger = KotlinLogging.logger(javaClass.name)

    init {
        logger.info { this }
    }

    companion object {
        fun parseHeader(parser: SaveFileParser) : SaveFileHeader {
            val headerVersion = parser.parseInt()
            val saveVersion = parser.parseInt()
            val buildVersion = parser.parseInt()
            val mapName = parser.parseString()
            val mapOptions = parser.parseString()
            val sessionName = parser.parseString()
            val playedSeconds = parser.parseInt()
            val saveTimestamp = parser.parseLong()
            val sessionVisibility = parser.parseByte() == 1
            val editorObjectVersion = parser.parseInt()
            val modMetadata = parser.parseString()
            val modFlags = parser.parseInt()
            val saveIdentifier = parser.parseString()

            parser.parseInt()
            parser.parseInt()
            parser.parseLong()
            parser.parseLong()

            val cheatFlag = parser.parseInt() == 1

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
    }
}
