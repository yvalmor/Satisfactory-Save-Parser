import data.CompressedSaveFileBody
import data.SaveFileBody
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

    val saveParser = SaveFileParser(input)
    val parsedFile = saveParser.parseSaveFile()

    val decompresserStream = getDecompresserStream(parsedFile.compressedBody)

    val saveBodyParser = SaveBodyParser(decompresserStream)
    val saveBody = saveBodyParser.parseSaveBody()
}