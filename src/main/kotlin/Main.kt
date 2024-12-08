import data.SaveFileBody
import data.SaveFileHeader
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import parser.SaveBodyParser
import parser.SaveFileParser
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream

fun main(args: Array<String>) {
    val argParser = ArgParser("Satisfactory save parser")

    val fileName by argParser.option(
        ArgType.String,
        shortName = "f",
        description = "Save file"
    ).required()

    argParser.parse(args)

    val saveFile = File(fileName)
    val input = DataInputStream(FileInputStream(saveFile))
    val parsedFileHeader: SaveFileHeader
    val decompressor = Decompressor()
    var totalUncompressedSize = 0UL

    input.use {
        val saveParser = SaveFileParser(it)
        parsedFileHeader = saveParser.parseHeader()

        var compressedBody = saveParser.parseCompressedBody()
        while (compressedBody != null) {
            totalUncompressedSize += compressedBody.uncompressedSize
            decompressor.addData(compressedBody)
            compressedBody = saveParser.parseCompressedBody()
        }
    }

    val saveBody: SaveFileBody

    decompressor.use {
        val decompressorStream = it.getStream()

        decompressorStream.use { inputStream ->
            val saveBodyParser = SaveBodyParser(inputStream)
            saveBody = saveBodyParser.parseSaveBody()
        }
    }
}