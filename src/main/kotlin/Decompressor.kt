import data.CompressedSaveFileBody
import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.util.zip.InflaterInputStream

class Decompressor : Closeable {
    private val tempFile: File = File.createTempFile("SatisfactorySaveParser", "Decompresser")

    init {
        if (!tempFile.canWrite()) {
            throw Error("Cannot write in temp file ${tempFile.path}")
        }
    }

    fun addData(data: CompressedSaveFileBody) {
        tempFile.appendBytes(data.bytes.toByteArray())
    }

    fun getStream() : InflaterInputStream {
        return InflaterInputStream(FileInputStream(tempFile))
    }

    override fun close() {
        tempFile.delete()
    }
}