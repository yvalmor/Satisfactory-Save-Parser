import data.SaveFileHeader
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
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
    val saveHeader = SaveFileHeader.parseHeader(saveParser)
}