import data.CompressedSaveFileBody
import java.io.ByteArrayInputStream
import java.util.zip.InflaterInputStream

fun getDecompresserStream(compressedBodies: List<CompressedSaveFileBody>): InflaterInputStream {
    val compressedConcatenatedBody = compressedBodies
        .flatMap { body -> body.bytes }
        .toByteArray()

    val inputStream = ByteArrayInputStream(compressedConcatenatedBody)
    return InflaterInputStream(inputStream)
}