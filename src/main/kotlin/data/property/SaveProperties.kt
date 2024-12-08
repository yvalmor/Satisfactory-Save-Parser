package data.property

import data.ObjectReference
import data.PropertyList
import data.typed_data.TypedData

data class PropertyHeader(
    val name: String,
    val type: PropertyType,
)

data class ArrayProperty(
    override val size: UInt,
    override val index: UInt,
    val type: String,
    val elements: List<ArrayPropertyElement>,
) : Property {
    interface ArrayPropertyElement

    data class ByteArrayElement(val value: Int): ArrayPropertyElement
    data class StrArrayElement(val value: String): ArrayPropertyElement
    data class ObjectArrayElement(val level: String, val path: String): ArrayPropertyElement
    data class IntArrayElement(val value: Int): ArrayPropertyElement
    data class Int64ArrayElement(val value: Long): ArrayPropertyElement
    data class FloatArrayElement(val value: Float): ArrayPropertyElement
    data class SoftObjectArrayElement(val reference: ObjectReference, val value: UInt): ArrayPropertyElement
    data class StructArrayElement(
        val name: String,
        val type: String,
        val size: UInt,
        val elementType: String,
        val data: TypedData,
    ): ArrayPropertyElement
}

data class BoolProperty(
    override val size: UInt,
    override val index: UInt,
    val value: Boolean,
) : Property

data class ByteProperty(
    override val size: UInt,
    override val index: UInt,
    val type: String,
    val byteValue: Int?,
    val stringValue: String?,
) : Property

data class EnumProperty(
    override val size: UInt,
    override val index: UInt,
    val type: String,
    val value: String,
) : Property

data class FloatProperty(
    override val size: UInt,
    override val index: UInt,
    val value: Float,
) : Property

data class DoubleProperty(
    override val size: UInt,
    override val index: UInt,
    val value: Double,
) : Property

data class IntProperty(
    override val size: UInt,
    override val index: UInt,
    val value: Int,
) : Property

data class Int8Property(
    override val size: UInt,
    override val index: UInt,
    val value: Int,
) : Property

data class UInt32Property(
    override val size: UInt,
    override val index: UInt,
    val value: UInt,
) : Property

data class Int64Property(
    override val size: UInt,
    override val index: UInt,
    val value: Long,
) : Property

data class MapProperty(
    override val size: UInt,
    override val index: UInt,
    val keyType: String,
    val valueType: String,
    val mode: UInt,
    val elements: Map<MapPropertyKey, MapPropertyValue>
) : Property {
    interface MapPropertyKey
    interface MapPropertyValue

    data class ObjectMapKey(val reference: ObjectReference) : MapPropertyKey
    data class IntMapKey(val value: Int) : MapPropertyKey
    data class StructMapKey(val value: Triple<Int, Int, Int>) : MapPropertyKey

    data class ByteMapValue(val value: Int) : MapPropertyValue
    data class IntMapValue(val value: Int) : MapPropertyValue
    data class Int64MapValue(val value: Long) : MapPropertyValue
    data class StructMapValue(val value: PropertyList) : MapPropertyValue
}

data class NameProperty(
    override val size: UInt,
    override val index: UInt,
    val value: String,
) : Property

data class ObjectProperty(
    override val size: UInt,
    override val index: UInt,
    val reference: ObjectReference,
) : Property

data class SoftObjectProperty(
    override val size: UInt,
    override val index: UInt,
    val reference: ObjectReference,
    val value: UInt,
) : Property

data class SetProperty<T: SetProperty.SetPropertyElement>(
    override val size: UInt,
    override val index: UInt,
    val elementType: String,
    val elements: List<T>
) : Property {
    interface SetPropertyElement

    data class UInt32SetElement(val value: UInt) : SetPropertyElement
    data class StructSetElement(val value: Pair<ULong, ULong>) : SetPropertyElement
    data class ObjectReferenceSetElement(val value: ObjectReference) : SetPropertyElement
}

data class StrProperty(
    override val size: UInt,
    override val index: UInt,
    val value: String,
) : Property

data class StructProperty(
    override val size: UInt,
    override val index: UInt,
    val type: String,
    val data: TypedData,
) : Property

data class TextProperty(
    override val size: UInt,
    override val index: UInt,
    val flags: UInt,
    val historyType: Int,
    val textCultureInvariant: Boolean,
    val value: String,
) : Property
