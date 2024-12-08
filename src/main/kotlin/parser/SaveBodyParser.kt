package parser

import data.*
import data.property.*
import data.typed_data.*
import java.io.InputStream
import java.security.InvalidParameterException
import kotlin.reflect.typeOf

class SaveBodyParser(private val input: InputStream) : SatisfactorySaveParser(input) {
    fun parseSaveBody(): SaveFileBody {
        logger.info { "Parsing save body" }

        val size = parseUInt64()

        parseUInt32()
        parseString()
        parseUInt32()
        parseUInt32()
        parseUInt32()
        parseString()
        parseUInt32()

        val levelGroupingGrids: MutableList<LevelGroupingGrid> = mutableListOf()
        for (i in 1..5)
            levelGroupingGrids.add(parseLevelGroupingGrid())

        val subLevelCount = parseUInt32()
        val levels: MutableList<Level> = mutableListOf()
        for (i in 0u..subLevelCount) {
            levels.add(parseLevel())
        }

        parseUInt32()

        val referenceCount = parseUInt32()
        val references: MutableList<ObjectReference> = mutableListOf()
        for (i in 1u..referenceCount) {
            references.add(parseObjectReference())
        }

        return SaveFileBody(
            size,
            levelGroupingGrids,
            levels,
            references
        )
    }

    private fun parseLevelGroupingGrid(): LevelGroupingGrid {
        logger.info { "Parsing level grouping grid" }

        val name = parseString()

        parseUInt32()
        parseUInt32()

        val levelCount = parseUInt32()
        val levelInfos: MutableList<Pair<String, UInt>> = mutableListOf()

        for (i in 1u..levelCount) {
            levelInfos.add(
                Pair(
                    parseString(),
                    parseUInt32()
                )
            )
        }

        return LevelGroupingGrid(name, levelInfos)
    }

    private fun parseLevel(): Level {
        logger.info { "Parsing level" }

        val name = parseString()

        parseUInt64()

        val objectHeaderCount = parseUInt32()
        val objectHeaders: MutableList<ObjectHeader> = mutableListOf()
        for (i in 1u..objectHeaderCount)
            objectHeaders.add(parseObjectHeader())

        val collectableCount = parseUInt32()
        for (i in 1u..collectableCount)
            parseObjectReference()

        parseUInt64() // Object size
        val objectCount = parseUInt32()
        val objectBodies: MutableList<ObjectBody> = mutableListOf()
        for (i in 1u..objectCount)
            objectBodies.add(parseObjectBody(objectHeaders[i.toInt()]))

        val secondCollectableCount = parseUInt32()
        for (i in 1u..secondCollectableCount)
            parseObjectReference()

        return Level(
            name,
            objectHeaders,
            objectBodies,
        )
    }

    private fun parseObjectHeader(): ObjectHeader {
        logger.info { "Parsing ObjectHeader" }

        val isActorHeader = parseUInt32() == 1u
        return if (isActorHeader) parseActorHeader() else parseComponentHeader()
    }

    private fun parseActorHeader(): ActorHeader {
        logger.info { "Parsing ActorHeader" }

        val path = parseString()
        val root = parseString()
        val instance = parseString()

        parseUInt32()
        parseFloat()
        parseFloat()
        parseFloat()
        parseFloat()
        parseFloat()
        parseFloat()
        parseFloat()
        parseFloat()
        parseFloat()
        parseFloat()
        parseUInt32()

        return ActorHeader(path, root, instance)
    }

    private fun parseComponentHeader(): ComponentHeader {
        logger.info { "Parsing ComponentHeader" }

        return ComponentHeader(
            parseString(),
            parseString(),
            parseString(),
            parseString(),
        )
    }

    private fun parseObjectReference(): ObjectReference {
        logger.info { "Parsing ObjectReference" }

        return ObjectReference(
            parseString(),
            parseString(),
        )
    }

    private fun parseObjectBody(header: ObjectHeader): ObjectBody {
        logger.info { "Parsing ObjectBody" }

        return when (header) {
            is ActorHeader -> parseActorObject(header)
            is ComponentHeader -> parseComponentObject(header)
            else -> throw InvalidParameterException()
        }
    }

    private fun parseActorObject(header: ActorHeader): ActorObject {
        logger.info { "Parsing ActorObject" }

        val saveVersion = parseUInt32()
        val flag = parseUInt32() == 1u
        val size = parseUInt32()
        val parentReference = parseObjectReference()

        val componentCount = parseUInt32()
        val components: MutableList<ObjectReference> = mutableListOf()
        for (i in 1u..componentCount)
            components.add(parseObjectReference())

        val properties = parsePropertyList()
        val trailingBytes: MutableList<Byte> = mutableListOf()

        TODO()
    }

    private fun parseComponentObject(header: ComponentHeader): ComponentObject {
        logger.info { "Parsing ComponentObject" }

        val saveVersion = parseUInt32()
        val flag = parseUInt32() == 1u
        val size = parseUInt32()
        val properties = parsePropertyList()

        parseUInt32()

        TODO()
    }

    private fun parsePropertyList(): List<Pair<PropertyHeader, Property>> {
        logger.info { "Parsing PropertyList" }

        val properties: MutableList<Pair<PropertyHeader, Property>> = mutableListOf()

        while (input.available() > 0) {
            val header = parsePropertyHeader()
            if (header.type == PropertyType.None)
                break

            properties.add(Pair(header, parseProperty(header.type)))
        }

        return properties
    }

    private fun parsePropertyHeader(): PropertyHeader {
        logger.info { "Parsing PropertyHeader" }

        val name = parseString()
        if (name == "None")
            return PropertyHeader(name, PropertyType.None)

        val type = parseString()
        return PropertyHeader(
            name,
            PropertyType.valueOf(type)
        )
    }

    private fun parseProperty(type: PropertyType): Property {
        logger.info { "Parsing Property" }

        val size = parseUInt32()
        val index = parseUInt32()

        return when (type) {
            PropertyType.ArrayProperty -> parseArrayProperty(size, index)
            PropertyType.BoolProperty -> parseBoolProperty(index)
            PropertyType.ByteProperty -> parseByteProperty(size, index)
            PropertyType.EnumProperty -> parseEnumProperty(size, index)
            PropertyType.FloatProperty -> parseFloatProperty(size, index)
            PropertyType.DoubleProperty -> parseDoubleProperty(size, index)
            PropertyType.IntProperty -> parseIntProperty(size, index)
            PropertyType.Int8Property -> parseInt8Property(size, index)
            PropertyType.UInt32Property -> parseUInt32Property(size, index)
            PropertyType.Int64Property -> parseInt64Property(size, index)
            PropertyType.MapProperty -> parseMapProperty(size, index)
            PropertyType.NameProperty -> parseNameProperty(size, index)
            PropertyType.ObjectProperty -> parseObjectProperty(size, index)
            PropertyType.SoftObjectProperty -> parseSoftObjectProperty(size, index)
            PropertyType.SetProperty -> parseSetProperty(size, index)
            PropertyType.StrProperty -> parseStrProperty(size, index)
            PropertyType.StructProperty -> parseStructProperty(size, index)
            PropertyType.TextProperty -> parseTextProperty(size, index)
            else -> throw InvalidParameterException()
        }
    }

    private fun parseArrayProperty(size: UInt, index: UInt): ArrayProperty {
        logger.info { "Parsing ArrayProperty" }

        val type = parseString()
        parseByte()
        val length = parseUInt32()

        val parseMethod: () -> ArrayProperty.ArrayPropertyElement
        when (type) {
            "ByteProperty" -> {
                parseMethod = { ArrayProperty.ByteArrayElement(parseByte()) }
            }

            "EnumProperty", "StrProperty" -> {
                parseMethod = { ArrayProperty.StrArrayElement(parseString()) }
            }

            "InterfaceProperty", "ObjectProperty" -> {
                parseMethod = { ArrayProperty.ObjectArrayElement(parseString(), parseString()) }
            }

            "IntProperty" -> {
                parseMethod = { ArrayProperty.IntArrayElement(parseInt()) }
            }

            "Int64Property" -> {
                parseMethod = { ArrayProperty.Int64ArrayElement(parseLong()) }
            }

            "FloatProperty" -> {
                parseMethod = { ArrayProperty.FloatArrayElement(parseFloat()) }
            }

            "SoftObjectProperty" -> {
                parseMethod = { ArrayProperty.SoftObjectArrayElement(parseObjectReference(), parseUInt32()) }
            }

            "StructProperty" -> {
                parseMethod = { parseStructArrayPropertyElement() }
            }

            else -> throw InvalidParameterException("$type is not recognised as a possible type for a property array")
        }

        return ArrayProperty(
            size,
            index,
            type,
            parseArrayPropertyElements(length, parseMethod)
        )
    }

    private fun parseStructArrayPropertyElement(): ArrayProperty.StructArrayElement {
        logger.info { "Parsing StructArrayPropertyElement" }

        val name = parseString()
        val type = parseString()
        val size = parseUInt32()

        parseUInt32() // Padding

        val elementType = parseString()

        for (i in 1..4)
            parseUInt32() // Padding
        parseByte() // Padding

        val data = parseTypedData(elementType)

        return ArrayProperty.StructArrayElement(name, type, size, elementType, data)
    }

    private fun parseArrayPropertyElements(
        length: UInt,
        parseMethod: () -> ArrayProperty.ArrayPropertyElement
    ): List<ArrayProperty.ArrayPropertyElement> {
        logger.info { "Parsing ArrayPropertyElements" }

        val elements: MutableList<ArrayProperty.ArrayPropertyElement> = mutableListOf()

        for (i in 1u..length)
            elements.add(parseMethod())

        return elements
    }

    private fun parseByteProperty(size: UInt, index: UInt): ByteProperty {
        logger.info { "Parsing ByteProperty" }

        val type = parseString()

        parseByte()

        if (type == "None") {
            return ByteProperty(
                size,
                index,
                type,
                parseByte(),
                null
            )
        } else {
            return ByteProperty(
                size,
                index,
                type,
                null,
                parseString()
            )
        }
    }

    private fun parseBoolProperty(index: UInt): BoolProperty {
        logger.info { "Parsing BoolProperty" }

        val value = parseByte() == 1
        parseByte() // Padding
        return BoolProperty(0u, index, value)
    }

    private fun parseEnumProperty(size: UInt, index: UInt): EnumProperty {
        logger.info { "Parsing EnumProperty" }

        val type = parseString()
        parseByte() // Padding
        val value = parseString()

        return EnumProperty(size, index, type, value)
    }

    private fun parseFloatProperty(size: UInt, index: UInt): FloatProperty {
        logger.info { "Parsing FloatProperty" }

        parseByte() // Padding
        return FloatProperty(size, index, parseFloat())
    }

    private fun parseDoubleProperty(size: UInt, index: UInt): DoubleProperty {
        logger.info { "Parsing DoubleProperty" }

        parseByte() // Padding
        return DoubleProperty(size, index, parseDouble())
    }

    private fun parseIntProperty(size: UInt, index: UInt): IntProperty {
        logger.info { "Parsing IntProperty" }

        parseByte() // Padding
        return IntProperty(size, index, parseInt())
    }

    private fun parseInt8Property(size: UInt, index: UInt): Int8Property {
        logger.info { "Parsing Int8Property" }

        parseByte() // Padding
        return Int8Property(size, index, parseByte())
    }

    private fun parseUInt32Property(size: UInt, index: UInt): UInt32Property {
        logger.info { "Parsing UInt32Property" }

        parseByte() // Padding
        return UInt32Property(size, index, parseUInt32())
    }

    private fun parseInt64Property(size: UInt, index: UInt): Int64Property {
        logger.info { "Parsing Int64Property" }

        parseByte() // Padding
        return Int64Property(size, index, parseLong())
    }

    private fun parseMapProperty(
        size: UInt,
        index: UInt
    ): MapProperty<MapProperty.MapPropertyKey, MapProperty.MapPropertyValue> {
        logger.info { "Parsing MapProperty" }

        TODO()
    }

    private fun parseNameProperty(size: UInt, index: UInt): NameProperty {
        logger.info { "Parsing NameProperty" }

        parseByte()
        return NameProperty(size, index, parseString())
    }

    private fun parseObjectProperty(size: UInt, index: UInt): ObjectProperty {
        logger.info { "Parsing ObjectProperty" }

        parseByte()
        return ObjectProperty(size, index, parseObjectReference())
    }

    private fun parseSoftObjectProperty(size: UInt, index: UInt): SoftObjectProperty {
        logger.info { "Parsing SoftObjectProperty" }

        parseByte()
        return SoftObjectProperty(size, index, parseObjectReference(), parseUInt32())
    }

    private fun parseSetProperty(size: UInt, index: UInt): SetProperty<SetProperty.SetPropertyElement> {
        logger.info { "Parsing SetProperty" }

        TODO()
    }

    private fun parseStrProperty(size: UInt, index: UInt): StrProperty {
        logger.info { "Parsing StrProperty" }

        parseByte()
        return StrProperty(size, index, parseString())
    }

    private fun parseStructProperty(size: UInt, index: UInt): StructProperty {
        logger.info { "Parsing StructProperty" }

        TODO()
    }

    private fun parseTextProperty(size: UInt, index: UInt): TextProperty {
        logger.info { "Parsing TextProperty" }

        TODO()
    }

    private fun parseTypedData(): TypedData {
        logger.info { "Parsing TypedData" }

        TODO()
    }

    private fun parseTypedData(type: String): TypedData {
        "" +logger.info { "Parsing TypedData $type" }

        return when (type) {
            "Box" -> parseBoxData()
            "FluidBox" -> FluidBox(parseFloat())
            "InventoryItem" -> parseInventoryItemData()
            "LinearColor" -> LinearColor(parseFloat(), parseFloat(), parseFloat(), parseFloat())
            "Quat" -> Quat(parseFloat(), parseFloat(), parseFloat(), parseFloat())
            "Vector" -> Vector(parseFloat(), parseFloat(), parseFloat())
            "DateTime" -> DateTime(parseLong())
            "ClientIdentityInfo" -> parseClientIdentityInfo()
            else -> OtherTypedData(parsePropertyList())
        }
    }

    private fun parseBoxData(): Box {
        logger.info { "Parsing BoxData" }

        return Box(
            parseDouble(),
            parseDouble(),
            parseDouble(),
            parseDouble(),
            parseDouble(),
            parseDouble(),
            parseByte() == 1
        )
    }

    private fun parseInventoryItemData(): InventoryItem {
        logger.info { "Parsing InventoryItemData" }

        parseUInt32()
        val name = parseString()
        val flag = parseUInt32() == 1u
        parseUInt32()
        val type = parseString()
        val size = parseUInt32()
        val elements = parsePropertyList()

        if (elements.size != size.toInt()) {
            throw Error()
        }

        return InventoryItem(name, flag, type, elements)
    }

    private fun parseClientIdentityInfo(): ClientIdentityInfo {
        logger.info { "Parsing ClientIdentityInfo" }

        val uuid = parseString()
        val count = parseUInt32()

        val identities: MutableList<ClientIdentityInfo.Identity> = mutableListOf()

        for (i in 1u..count)
            identities.add(parseClientIdentity())

        return ClientIdentityInfo(uuid, identities)
    }

    private fun parseClientIdentity(): ClientIdentityInfo.Identity {
        logger.info { "Parsing ClientIdentity" }

        val type = parseByte()
        val size = parseUInt32()
        val data = input.readNBytes(size.toInt())

        return ClientIdentityInfo.Identity(
            if (type == 1) ClientIdentityInfo.IdentityType.EPIC
            else ClientIdentityInfo.IdentityType.STEAM,
            data.toList()
        )
    }

    private fun parseActorObjectTrailingBytes(objectName: String) {
        when {
            isObjectConveyorBelt(objectName) -> {}
            else -> throw Error()
        }
    }

    private fun parseComponentObjectTrailingBytes(objectName: String) {
    }
}