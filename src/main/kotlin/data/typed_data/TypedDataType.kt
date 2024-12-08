package data.typed_data

enum class TypedDataType(val typeName: String) {
    Box("Box"),
    FluidBox("FluidBox"),
    InventoryItem("InventoryItem"),
    LinearColor("LinearColor"),
    Quat("Quat"),
    RailroadTrackPosition("RailroadTrackPosition"),
    Vector("Vector"),
    DateTime("DateTime"),
    ClientIdentityInfo("ClientIdentityInfo"),
}