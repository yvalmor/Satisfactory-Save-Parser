package data

import mu.KotlinLogging
import parser.SaveBodyParser

class SaveFileBody private constructor(
    val size: ULong,
    val grids: Array<LevelGroupingGrid>,
    val subLevelCount: UInt,
    val levels: Array<Void>,
    val referenceCount: UInt,
    val references: Array<Void>,
) {
    private val logger = KotlinLogging.logger(SaveFileBody::class.java.name)

    init {
        logger.info { this.toString() }
    }

    companion object {
        private val logger = KotlinLogging.logger(SaveFileBody::class.java.name)
        fun parseCompressedBody(parser: SaveBodyParser): SaveFileBody {
            val size = parser.parseUInt64()

            parser.parseInt()
            parser.parseString()
            parser.parseInt()
            parser.parseInt()
            parser.parseInt()
            parser.parseString()
            parser.parseInt()

            val grids: MutableList<LevelGroupingGrid> = mutableListOf()
            for (i in 1..5) {
                grids.add(LevelGroupingGrid.parseLevelGroupingGrid(parser))
            }

            return SaveFileBody(
                size,
                grids.toTypedArray(),
                0u,
                arrayOf(),
                0u,
                arrayOf()
            )
        }
    }
}