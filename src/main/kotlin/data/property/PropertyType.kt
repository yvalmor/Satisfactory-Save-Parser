package data.property

enum class PropertyType(val propertyName: String) {
    ArrayProperty("ArrayProperty"),
    BoolProperty("BoolProperty"),
    ByteProperty("ByteProperty"),
    EnumProperty("EnumProperty"),
    FloatProperty("FloatProperty"),
    DoubleProperty("DoubleProperty"),
    IntProperty("IntProperty"),
    Int8Property("Int8Property"),
    UInt32Property("UInt32Property"),
    Int64Property("Int64Property"),
    MapProperty("MapProperty"),
    NameProperty("NameProperty"),
    ObjectProperty("ObjectProperty"),
    SoftObjectProperty("SoftObjectProperty"),
    SetProperty("SetProperty"),
    StrProperty("StrProperty"),
    StructProperty("StructProperty"),
    TextProperty("TextProperty"),
    None("None")
}