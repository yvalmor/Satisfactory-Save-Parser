package data

import parser.SatisfactorySaveParser

class LevelGroupingGrid private constructor(
    val name: String,
    val levelCount: UInt,
    val levelInfos: Array<Pair<String, UInt>>
) {
    companion object {
        fun parseLevelGroupingGrid(parser: SatisfactorySaveParser): LevelGroupingGrid {
            val name = parser.parseString()

            parser.parseUInt32()
            parser.parseUInt32()

            val levelCount = parser.parseUInt32()
            val levelInfos: MutableList<Pair<String, UInt>> = mutableListOf()

            for (i in 1u..levelCount) {
                levelInfos.add(
                    Pair(
                        parser.parseString(),
                        parser.parseUInt32()
                    )
                )
            }

            return LevelGroupingGrid(name, levelCount, levelInfos.toTypedArray())
        }
    }
}