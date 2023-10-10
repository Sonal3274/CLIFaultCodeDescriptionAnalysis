import java.io.File
import java.io.FileWriter
import java.io.IOException

fun main() {
    // Specify the paths for input.txt and output folder
    val inputFilePath = "src/path/to/input.txt"
    val outputFolderPath = "src/path/to/output/folder"

    try {
        // Read the content of input.txt
        val inputText = File(inputFilePath).readText()

        // Delete existing CSV files in the output folder
        deleteExistingCSVFiles(outputFolderPath)

        val wordSequences = extractWordSequences(inputText)

        // Find the maximum sequence and count lengths for formatting
        val (maxSequenceLength, maxCountLength) = calculateMaxLengths(wordSequences)

        // Write the word sequences to CSV files with proper alignment and sorting
        for ((length, sequences) in wordSequences) {
            val outputFileName = "occurrence_length_$length.csv"
            writeWordSequencesToCSV(sequences, outputFileName, maxSequenceLength, maxCountLength, outputFolderPath)
            println("Word sequences of length $length have been saved to $outputFileName.")
        }

        // Read all CSV files and get top sequences
        val topSequences = readAllCSVFiles(outputFolderPath)

        // Create a separate file to store top sequences
        writeTopSequencesToFile(topSequences, outputFolderPath)
        println("Top sequences have been saved to top_sequences.txt.")

        // Count lines affected by top sequences and write to lines_affected.txt
        val linesAffected = countLinesAffected(inputFilePath, topSequences)
        writeLinesAffectedToFile(linesAffected, outputFolderPath)
        println("Lines affected have been saved to lines_affected.txt.")
    } catch (e: IOException) {
        println("Error: ${e.message}")
    }
}

fun extractWordSequences(inputText: String): Map<Int, Map<String, Int>> {
    val wordSequences = mutableMapOf<Int, MutableMap<String, Int>>()

    // Split the inputText into words using various separators
    val words = inputText.split("\\s+|\\-|/|\\t|,".toRegex())

    // Process and count unique word sequences of varying lengths
    for (i in words.indices) {
        for (length in 1..words.size - i) {
            val sequence = words.subList(i, i + length).joinToString(" ").toLowerCase()
            if (sequence.isNotBlank()) {
                val currentSequences = wordSequences.getOrPut(length) { mutableMapOf() }
                currentSequences[sequence] = currentSequences.getOrDefault(sequence, 0) + 1
            }
        }
    }

    return wordSequences
}

fun calculateMaxLengths(wordSequences: Map<Int, Map<String, Int>>): Pair<Int, Int> {
    var maxSequenceLength = 0
    var maxCountLength = 0

    for ((_, sequences) in wordSequences) {
        for ((sequence, count) in sequences) {
            maxSequenceLength = maxOf(maxSequenceLength, sequence.length)
            maxCountLength = maxOf(maxCountLength, count.toString().length)
        }
    }

    return Pair(maxSequenceLength, maxCountLength)
}

fun writeWordSequencesToCSV(
    sequences: Map<String, Int>,
    outputFileName: String,
    maxSequenceLength: Int,
    maxCountLength: Int,
    outputFolderPath: String
) {
    val outputFilePath = "src/path/to/output/folder/$outputFileName"
    val fileWriter = FileWriter(outputFilePath)

    try {
        // Use a comma as the delimiter in the CSV file
        val delimiter = ","

        // Sort the sequences by count (descending), then alphabetically
        val sortedSequences = sequences.entries.sortedWith(compareBy({ -it.value }, { it.key }))

        // Generate and write the top dashed line of the table
        fileWriter.append("-".repeat(maxSequenceLength + 2)) // Add 2 for extra padding
        fileWriter.append(delimiter)
        fileWriter.append("-".repeat(maxCountLength + 2)) // Add 2 for extra padding
        fileWriter.append("\n")

        // Write the sorted sequences to the CSV file with proper alignment
        for ((sequence, count) in sortedSequences) {
            fileWriter.append("${sequence.padEnd(maxSequenceLength)}$delimiter${count.toString().padStart(maxCountLength)}\n")
        }

        // Generate and write the bottom dashed line of the table
        fileWriter.append("-".repeat(maxSequenceLength + 2)) // Add 2 for extra padding
        fileWriter.append(delimiter)
        fileWriter.append("-".repeat(maxCountLength + 2)) // Add 2 for extra padding
        fileWriter.append("\n")
    } catch (e: IOException) {
        println("Error writing to $outputFileName: ${e.message}")
    } finally {
        fileWriter.close()
    }
}

fun readAllCSVFiles(outputFolderPath: String): Map<Int, List<String>> {
    val topSequences = mutableMapOf<Int, List<String>>()

    val outputFolder = File(outputFolderPath)

    // List all files in the output folder
    val csvFiles = outputFolder.listFiles { file -> file.name.startsWith("occurrence_length_") && file.name.endsWith(".csv") }

    if (csvFiles != null) {
        for (csvFile in csvFiles) {
            val length = csvFile.name.substringAfterLast("_").substringBeforeLast(".csv").toInt()
            val sequences = readCSVFile(csvFile)
            topSequences[length] = sequences
        }
    }

    return topSequences
}

fun readCSVFile(csvFile: File): List<String> {
    val sequences = mutableListOf<String>()

    try {
        val lines = csvFile.readLines()
        for (line in lines) {
            // Skip header lines and dashed lines
            if (!line.startsWith("-")) {
                val sequence = line.split(",")[0].trim() // Use a comma as the delimiter
                if (sequence.isNotEmpty()) {
                    sequences.add(sequence)
                }
            }
        }
    } catch (e: IOException) {
        println("Error reading ${csvFile.name}: ${e.message}")
    }

    return sequences
}

fun writeTopSequencesToFile(topSequences: Map<Int, List<String>>, outputFolderPath: String) {
    val outputFilePath = "$outputFolderPath/top_sequences.txt"

    val fileWriter = FileWriter(outputFilePath)

    try {
        val uniqueTopSequences = mutableSetOf<String>()

        for ((_, sequences) in topSequences) {
            for (sequence in sequences) {
                uniqueTopSequences.add(sequence)
            }
        }

        // Write the sorted unique top sequences to the file
        fileWriter.append("Top Sequences:\n")
        for (sequence in uniqueTopSequences.sorted().take(10)) {
            fileWriter.append("  - $sequence\n")
        }
    } catch (e: IOException) {
        println("Error writing to files: ${e.message}")
    } finally {
        fileWriter.close()
    }
}

fun countLinesAffected(inputFilePath: String, topSequences: Map<Int, List<String>>): Int {
    var linesAffected = 0

    try {
        val inputText = File(inputFilePath).readText().toLowerCase()

        val inputLines = inputText.split("\r\n|\r|\n") // Split input text into lines

        for (inputLine in inputLines) {
            for ((_, sequences) in topSequences) {
                for (sequence in sequences) {
                    // Check if the exact word sequence is present in the input line, ignoring case
                    val regex = Regex("\\b${Regex.escape(sequence.toLowerCase())}\\b")
                    val match = regex.find(inputLine)
                    if (match != null && match.value.equals(sequence, ignoreCase = true)) {
                        linesAffected++
                        break  // Break from the inner loop since we've counted this sequence
                    }
                }
            }
        }
    } catch (e: IOException) {
        println("Error counting lines affected: ${e.message}")
    }

    return linesAffected
}
fun writeLinesAffectedToFile(linesAffected: Int, outputFolderPath: String) {
    val linesAffectedFilePath = "$outputFolderPath/lines_affected.txt"

    val linesAffectedWriter = FileWriter(linesAffectedFilePath)

    try {
        // Write the number of lines affected to the lines_affected.txt file
        linesAffectedWriter.write(linesAffected.toString())
    } catch (e: IOException) {
        println("Error writing to lines_affected.txt: ${e.message}")
    } finally {
        linesAffectedWriter.close()
    }
}
fun deleteExistingCSVFiles(outputFolderPath: String) {
    val outputFolder = File(outputFolderPath)
    val csvFiles = outputFolder.listFiles { file -> file.name.startsWith("occurrence_length_") && file.name.endsWith(".csv") }
    csvFiles?.forEach { it.delete() }
}