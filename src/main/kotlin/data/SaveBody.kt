package data

import data.property.Property
import data.property.PropertyHeader

typealias PropertyList = List<Pair<PropertyHeader, Property>>
typealias Vector3<T> = Triple<T, T, T>

data class SaveFileBody(
    val size: ULong,
    val grids: List<LevelGroupingGrid>,
    val levels: List<Level>,
    val references: List<ObjectReference>,
)

data class LevelGroupingGrid(
    val name: String,
    val levelInfos: List<Pair<String, UInt>>
)

data class Level(
    val name: String,
    val objectHeaders: List<ObjectHeader>,
    val collectables: List<ObjectReference>,
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
}

data class ActorObject(
    override val saveVersion: UInt,
    override val flag: Boolean,
    override val size: UInt,
    val parentObjectReference: ObjectReference,
    val components: List<ObjectReference>,
    override val properties: PropertyList,
    val trailingBytes: TrailingBytes?
) : ObjectBody {
    interface TrailingBytes

    data class ConveyorTrailingBytesElement(val length: UInt, val name: String, val position: Float)
    data class CircuitSubsystemTrailingBytesElement(val value: UInt, val reference: ObjectReference)
    data class VehicleTrailingBytesElement(val value: UInt, val data: List<Byte>)
    data class LightweightBuildableSubsystemTrailingBytesComponent(
        val rotation: List<Double>, // 4 Doubles (quaternion)
        val position: Vector3<Double>,
        val scale: Vector3<Double>,
        val swatch: String,
        val patternDescriptionNumber: String,
        val primaryColor: List<Float>, // 4 floats (rgba)
        val secondaryColor: List<Float>, // 4 floats (rgba)
        val data: List<Byte>,
        val recipe: String,
        val blueprintProxy: ObjectReference,
    )

    data class LightweightBuildableSubsystemTrailingBytesElement(
        val name: String,
        val components: List<LightweightBuildableSubsystemTrailingBytesComponent>
    )

    data class ConveyorChainElement(
        val chainActor: ObjectReference,
        val belt: ObjectReference,
        val elements: List<Vector3<Vector3<ULong>>>,
        val beltIndex: UInt,
        val items: List<Pair<String, UInt>> // name, count
    )

    data class ConveyorTrailingBytes(val elements: List<ConveyorTrailingBytesElement>) : TrailingBytes
    data class GameModeTrailingBytes(val references: List<ObjectReference>) : TrailingBytes
    data class PlayerStateTrailingBytes(val isEmpty: Boolean, val data: List<Byte>, val isSteam: Boolean? = null) :
        TrailingBytes

    data class CircuitSubsystemTrailingBytes(val elements: List<CircuitSubsystemTrailingBytesElement>) : TrailingBytes
    data class PowerLineTrailingBytes(val references: Pair<ObjectReference, ObjectReference>) : TrailingBytes
    data class VehicleTrailingBytes(val elements: List<VehicleTrailingBytesElement>) : TrailingBytes
    data class LightweightBuildableSubsystemTrailingBytes(
        val elements: List<LightweightBuildableSubsystemTrailingBytesElement>
    ) : TrailingBytes
    data class ConveyorChainTrailingBytes(
        val startingBelt: ObjectReference,
        val endingBelt: ObjectReference,
        val beltSize: UInt,
        val beltElements: List<ConveyorChainElement>,
    ) : TrailingBytes
}

data class ComponentObject(
    override val saveVersion: UInt,
    override val flag: Boolean,
    override val size: UInt,
    override val properties: PropertyList
) : ObjectBody
