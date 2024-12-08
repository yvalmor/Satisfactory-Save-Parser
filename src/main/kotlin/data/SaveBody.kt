package data

import data.property.Property
import data.property.PropertyHeader

typealias PropertyList = List<Pair<PropertyHeader, Property>>

data class SaveFileBody(
    val size: ULong,
    val grids: List<LevelGroupingGrid>,
    val levels: List<Level>,
    val references: List<ObjectReference>,
) {
    val subLevelCount: Int
        get() = levels.size

    val referencesCount: Int
        get() = references.size
}

data class LevelGroupingGrid(
    val name: String,
    val levelInfos: List<Pair<String, UInt>>
) {
    val levelCount: Int
        get() = levelInfos.size
}

data class Level(
    val name: String,
    val objectHeaders: List<ObjectHeader>,
    val objectBodies: List<ObjectBody>,
)

interface ObjectHeader {
    val path: String
    val root: String
    val instance: String
}

data class ActorHeader(
    override val path: String,
    override val root: String,
    override val instance: String
) : ObjectHeader

data class ComponentHeader(
    override val path: String,
    override val root: String,
    override val instance: String,
    val parentActor: String
) : ObjectHeader

data class ObjectReference(
    val levelName: String,
    val pathName: String,
)

interface ObjectBody {
    val saveVersion: UInt
    val flag: Boolean
    val size: UInt
    val properties: PropertyList
    val trailingBytes: List<Byte>

    val propertySize: Int
        get() = properties.size
}

data class ActorObject(
    override val saveVersion: UInt,
    override val flag: Boolean,
    override val size: UInt,
    val parentObjectReference: ObjectReference,
    val components: List<ObjectReference>,
    override val properties: PropertyList,
    override val trailingBytes: List<Byte>
) : ObjectBody {
    val componentCount: Int
        get() = components.size
}

data class ComponentObject(
    override val saveVersion: UInt,
    override val flag: Boolean,
    override val size: UInt,
    override val properties: PropertyList,
    override val trailingBytes: List<Byte>
): ObjectBody
