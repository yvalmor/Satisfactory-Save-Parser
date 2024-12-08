package data

data class SaveFile(val header: SaveFileHeader, val compressedBody: List<CompressedSaveFileBody>)

data class SaveFileHeader(
    val headerVersion: Int,
    val saveVersion: Int,
    val buildVersion: Int,
    val mapName: String,
    val mapOptions: String,
    val sessionName: String,
    val playedSeconds: Int,
    val saveTimestamp: Long,
    // Parsed as byte
    val sessionVisibility: Boolean,
    val editorObjectVersion: Int,
    val modMetadata: String,
    val modFlags: Int,
    val saveIdentifier: String,
    val cheatFlag: Boolean
)

data class CompressedSaveFileBody(
    val uePackageSignature: UInt,
    val maxChunkSize: UInt,
    val compressedSize: ULong,
    val uncompressedSize: ULong,
    val bytes: List<Byte>
)