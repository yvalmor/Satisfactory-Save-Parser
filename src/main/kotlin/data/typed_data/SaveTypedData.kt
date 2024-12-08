package data.typed_data

import data.ObjectReference
import data.PropertyList
import data.property.Property

data class Box(
    val minX: Double,
    val minY: Double,
    val minZ: Double,
    val maxX: Double,
    val maxY: Double,
    val maxZ: Double,
    val valid: Boolean,
) : TypedData

data class FluidBox(
    val value: Float,
) : TypedData

data class InventoryItem(
    val name: String,
    val flag: Boolean,
    val type: String,
    val properties: PropertyList
) : TypedData {
    val propertyCount: Int
        get() = properties.size
}

data class LinearColor(
    val red: Float,
    val green: Float,
    val blue: Float,
    val alpha: Float,
) : TypedData

data class Quat(
    val x: Float,
    val y: Float,
    val z: Float,
    val w: Float,
) : TypedData

data class RailroadTrackPosition(
    val reference: ObjectReference,
    val offset: Float,
    val forward: Float,
) : TypedData

data class Vector(
    val x: Float,
    val y: Float,
    val z: Float,
) : TypedData

data class DateTime(
    val value: Long,
) : TypedData

data class ClientIdentityInfo(
    val uuid: String,
    val identities: List<Identity>
) : TypedData {
    enum class IdentityType {
        EPIC,
        STEAM,
    }

    data class Identity(
        val type: IdentityType,
        val data: List<Byte>
    ) {
        val dataSize: Int
            get() = data.size
    }

    val identitySize: Int
        get() = identities.size
}

data class OtherTypedData(val properties: PropertyList) : TypedData
