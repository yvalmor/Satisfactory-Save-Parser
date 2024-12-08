package parser

import data.*
import data.property.*
import data.typed_data.*
import java.io.InputStream
import java.security.InvalidParameterException

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
        val collectables: MutableList<ObjectReference> = mutableListOf()
        for (i in 1u..collectableCount)
            collectables.add(parseObjectReference())

        parseUInt64() // Object size
        val objectCount = parseUInt32()
        val objectBodies: MutableList<ObjectBody> = mutableListOf()
        for (i in 1u..objectCount)
            objectBodies.add(parseObjectBody(objectHeaders[i.toInt() - 1]))

        val secondCollectableCount = parseUInt32()
        for (i in 1u..secondCollectableCount)
            parseObjectReference()

        return Level(
            name,
            objectHeaders,
            collectables,
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
        val trailingBytes = parseActorObjectTrailingBytes(header.path)

        return ActorObject(
            saveVersion,
            flag,
            size,
            parentReference,
            components,
            properties,
            trailingBytes
        )
    }

    private fun parseComponentObject(header: ComponentHeader): ComponentObject {
        logger.info { "Parsing ComponentObject" }

        val saveVersion = parseUInt32()
        val flag = parseUInt32() == 1u
        val size = parseUInt32()

        val properties = if (size > 0u)
            parsePropertyList()
        else listOf()

        parseUInt32()
        parseComponentObjectTrailingBytes(header.path)

        return ComponentObject(
            saveVersion,
            flag,
            size,
            properties,
        )
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

        var paddingSize = 4
        if (elementType == "")
            paddingSize = 1

        for (i in 1..paddingSize)
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
    ): MapProperty {
        logger.info { "Parsing MapProperty" }

        val keyType = parseString()
        val valueType = parseString()

        parseByte()

        val modeType = parseUInt32()
        val count = parseUInt32()

        val map: MutableMap<MapProperty.MapPropertyKey, MapProperty.MapPropertyValue> = mutableMapOf()

        for (i in 1u..count) {
            map[parseMapPropertyKey(keyType)] = parseMapPropertyValue(valueType)
        }

        return MapProperty(size, index, keyType, valueType, modeType, map)
    }

    private fun parseMapPropertyKey(keyType: String): MapProperty.MapPropertyKey {
        return when (keyType) {
            "ObjectProperty" -> MapProperty.ObjectMapKey(parseObjectReference())
            "IntProperty" -> MapProperty.IntMapKey(parseInt())
            "StructProperty" -> MapProperty.StructMapKey(Triple(parseInt(), parseInt(), parseInt()))
            else -> throw InvalidParameterException("$keyType is not recognised as a possible map property key")
        }
    }

    private fun parseMapPropertyValue(valueType: String): MapProperty.MapPropertyValue {
        return when (valueType) {
            "ByteProperty" -> MapProperty.ByteMapValue(parseByte())
            "IntProperty" -> MapProperty.IntMapValue(parseInt())
            "Int64Property" -> MapProperty.Int64MapValue(parseLong())
            "StructProperty" -> MapProperty.StructMapValue(parsePropertyList())
            else -> throw InvalidParameterException("$valueType is not recognised as a possible map property value")
        }
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

        val type = parseString()

        parseByte()
        parseUInt32()

        val count = parseUInt32()
        val elements: MutableList<SetProperty.SetPropertyElement> = mutableListOf()

        for (i in 1u..count)
            elements.add(parseSetPropertyElement(type))

        return SetProperty(size, index, type, elements)
    }

    private fun parseSetPropertyElement(elementType: String): SetProperty.SetPropertyElement {
        return when (elementType) {
            "UInt32Property" -> SetProperty.UInt32SetElement(parseUInt32())
            "StructProperty" -> SetProperty.StructSetElement(Pair(parseUInt64(), parseUInt64()))
            "ObjectProperty" -> SetProperty.ObjectReferenceSetElement(parseObjectReference())
            else -> throw InvalidParameterException("$elementType is not recognised as a possible set property element")
        }
    }

    private fun parseStrProperty(size: UInt, index: UInt): StrProperty {
        logger.info { "Parsing StrProperty" }

        parseByte()
        return StrProperty(size, index, parseString())
    }

    private fun parseStructProperty(size: UInt, index: UInt): StructProperty {
        logger.info { "Parsing StructProperty" }

        val type = parseString()

        parseLong()
        parseLong()
        parseByte()

        val data = parseTypedData(type)
        return StructProperty(size, index, type, data)
    }

    private fun parseTextProperty(size: UInt, index: UInt): TextProperty {
        logger.info { "Parsing TextProperty" }

        parseByte()

        val flags = parseUInt32()
        val historyType = parseByte()
        val textCultureInvariant = parseUInt32() == 1u
        val value = parseString()

        return TextProperty(size, index, flags, historyType, textCultureInvariant, value)
    }

    private fun parseTypedData(type: String): TypedData {
        logger.info { "Parsing TypedData $type" }

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
        val flag: Boolean
        val type: String
        val size: UInt

        if (name.isNotEmpty()) {
            flag = parseUInt32() == 1u
            parseUInt32()
            type = parseString()
            size = parseUInt32()
        } else {
            flag = false
            type = ""
            size = parseUInt32()
        }
        val elements = if (size > 0u)
            parsePropertyList()
        else listOf()

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

    private fun parseActorObjectTrailingBytes(objectName: String): ActorObject.TrailingBytes? {
        return when {
            isObjectConveyorBelt(objectName) -> parseConveyorBeltActor()
            isObjectGameMode(objectName) -> parseGameModeActor()
            isObjectPlayerState(objectName) -> parsePlayerStateActor()
            isObjectCircuitSubsystem(objectName) -> parseCircuitSubsystemActor()
            isObjectPowerLine(objectName) -> parsePowerLineActor()
            isObjectVehicle(objectName) -> parseVehicleActor()
            isObjectLightweightBuildableSubsystem(objectName) -> parseLightweightBuildableSubsystemActor()
            isObjectConveyorChainActor(objectName) -> parseConveyorChainActor()
            else -> {
                parseUInt32()
                return null
            }
        }
    }

    private fun parseConveyorBeltActor(): ActorObject.ConveyorTrailingBytes {
        val count = parseUInt32()
        val elements: MutableList<ActorObject.ConveyorTrailingBytesElement> = mutableListOf()

        for (i in 1u..count) {
            val length = parseUInt32()
            val name = parseString()

            parseString()
            parseString()

            elements.add(ActorObject.ConveyorTrailingBytesElement(length, name, parseFloat()))
        }

        return ActorObject.ConveyorTrailingBytes(elements)
    }

    private fun parseGameModeActor(): ActorObject.GameModeTrailingBytes {
        val count = parseUInt32()
        val references: MutableList<ObjectReference> = mutableListOf()

        for (i in 1u..count)
            references.add(parseObjectReference())

        return ActorObject.GameModeTrailingBytes(references)
    }

    private fun parsePlayerStateActor(): ActorObject.PlayerStateTrailingBytes {
        val formatting = parseByte()

        if (formatting == 3)
            return ActorObject.PlayerStateTrailingBytes(true, listOf())

        if (formatting != 241)
            throw InvalidParameterException("Player state formatting is not recognised: $formatting")

        val isSteam = parseByte() == 6
        val size = parseUInt32()
        val data = input.readNBytes(size.toInt())

        return ActorObject.PlayerStateTrailingBytes(false, data.toList(), isSteam)
    }

    private fun parseCircuitSubsystemActor(): ActorObject.CircuitSubsystemTrailingBytes {
        val count = parseUInt32()
        val elements: MutableList<ActorObject.CircuitSubsystemTrailingBytesElement> = mutableListOf()

        for (i in 1u..count) {
            elements.add(
                ActorObject.CircuitSubsystemTrailingBytesElement(
                    parseUInt32(),
                    parseObjectReference(),
                )
            )
        }

        return ActorObject.CircuitSubsystemTrailingBytes(elements)
    }

    private fun parsePowerLineActor(): ActorObject.PowerLineTrailingBytes {
        return ActorObject.PowerLineTrailingBytes(
            Pair(
                parseObjectReference(),
                parseObjectReference(),
            )
        )
    }

    private fun parseVehicleActor(): ActorObject.VehicleTrailingBytes {
        val count = parseUInt32()
        val elements: MutableList<ActorObject.VehicleTrailingBytesElement> = mutableListOf()
        val elementByteSize = 105 // Fixed data size

        for (i in 1u..count) {
            elements.add(
                ActorObject.VehicleTrailingBytesElement(
                    parseUInt32(),
                    input.readNBytes(elementByteSize).toList(),
                )
            )
        }

        return ActorObject.VehicleTrailingBytes(elements)
    }

    private fun parseLightweightBuildableSubsystemActor(): ActorObject.LightweightBuildableSubsystemTrailingBytes {
        val count = parseUInt32()
        val elements: MutableList<ActorObject.LightweightBuildableSubsystemTrailingBytesElement> = mutableListOf()

        for (i in 1u..count)
            elements.add(parseLightweightBuildableSubsystemElement())

        return ActorObject.LightweightBuildableSubsystemTrailingBytes(elements)
    }

    private fun parseLightweightBuildableSubsystemElement(): ActorObject.LightweightBuildableSubsystemTrailingBytesElement {
        parseUInt32()
        val name = parseString()
        val count = parseUInt32()
        val components: MutableList<ActorObject.LightweightBuildableSubsystemTrailingBytesComponent> = mutableListOf()

        for (i in 1u..count) {
            val rotationQuaternion = listOf(parseDouble(), parseDouble(), parseDouble(), parseDouble())
            val position = Triple(parseDouble(), parseDouble(), parseDouble())
            val scale = Triple(parseDouble(), parseDouble(), parseDouble())

            parseUInt32()

            val swatch = parseString()

            parseUInt32()
            parseUInt32()
            parseUInt32()

            val patternDescriptionNumber = parseString()

            parseUInt32()
            parseUInt32()

            val primaryColor = listOf(parseFloat(), parseFloat(), parseFloat(), parseFloat())
            val secondaryColor = listOf(parseFloat(), parseFloat(), parseFloat(), parseFloat())

            parseUInt32()

            val size = parseUInt32()
            val data = input.readNBytes(size.toInt()).toList()

            parseUInt32()
            parseByte()

            val recipe = parseString()
            val blueprintProxy = parseObjectReference()

            components.add(
                ActorObject.LightweightBuildableSubsystemTrailingBytesComponent(
                    rotationQuaternion,
                    position,
                    scale,
                    swatch,
                    patternDescriptionNumber,
                    primaryColor,
                    secondaryColor,
                    data,
                    recipe,
                    blueprintProxy
                )
            )
        }

        return ActorObject.LightweightBuildableSubsystemTrailingBytesElement(
            name,
            components
        )
    }

    private fun parseConveyorChainActor(): ActorObject.ConveyorChainTrailingBytes {
        val startingBelt = parseObjectReference()
        val endingBelt = parseObjectReference()
        val count = parseUInt32()
        val belts: MutableList<ActorObject.ConveyorChainElement> = mutableListOf()

        for (i in 1u..count) {
            val chainActor = parseObjectReference()
            val belt = parseObjectReference()
            val elementCount = parseUInt32()
            val elements: MutableList<Vector3<Vector3<ULong>>> = mutableListOf()

            for (j in 1u..elementCount)
                elements.add(
                    Triple(
                        Triple(parseUInt64(), parseUInt64(), parseUInt64()),
                        Triple(parseUInt64(), parseUInt64(), parseUInt64()),
                        Triple(parseUInt64(), parseUInt64(), parseUInt64()),
                    )
                )

            parseUInt32()
            parseUInt32()
            parseUInt32()
            parseInt()
            parseInt()

            val beltIndex = parseUInt32()

            parseUInt32()
            parseInt()
            parseInt()
            parseInt()

            val itemCount = parseUInt32()
            val items: MutableList<Pair<String, UInt>> = mutableListOf()

            for (j in 1u..itemCount)
                items.add(Pair(parseString(), parseUInt32()))

            belts.add(ActorObject.ConveyorChainElement(chainActor, belt, elements, beltIndex, items))
        }

        return ActorObject.ConveyorChainTrailingBytes(startingBelt, endingBelt, count, belts)
    }

    private fun parseComponentObjectTrailingBytes(objectName: String) {
        if (isObjectFGComponent(objectName))
            parseUInt32()
    }
}