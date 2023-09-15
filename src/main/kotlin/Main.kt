import java.io.File
import java.io.FileWriter
import java.io.IOException

fun main() {
    // Specify the paths for input.txt
    val inputFilePath = "src/path/to/input.txt"

    try {
        // Read the content of input.txt
        val inputText = File(inputFilePath).readText()

        val wordSequences = extractWordSequences(inputText)

        // Find the maximum sequence and count lengths for formatting
        val (maxSequenceLength, maxCountLength) = calculateMaxLengths(wordSequences)

        // Write the word sequences to CSV files with proper alignment and sorting
        for ((length, sequences) in wordSequences) {
            val outputFileName = "occurrence_length_$length.csv"
            writeWordSequencesToCSV(sequences, outputFileName, maxSequenceLength, maxCountLength)
            println("Word sequences of length $length have been saved to $outputFileName.")
        }
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
    maxCountLength: Int
) {
    val outputFilePath = "src/path/to/output/folder/$outputFileName"
    val fileWriter = FileWriter(outputFilePath)

    try {
        // Sort the sequences by count (descending), then alphabetically
        val sortedSequences = sequences.entries.sortedWith(compareBy({ -it.value }, { it.key }))

        // Generate and write the top dashed line of the table
        fileWriter.append("-".repeat(maxSequenceLength + 2)) // Add 2 for extra padding
        fileWriter.append("|")
        fileWriter.append("-".repeat(maxCountLength + 2)) // Add 2 for extra padding
        fileWriter.append("|\n")

        // Write the sorted sequences to the CSV file with proper alignment
        for ((sequence, count) in sortedSequences) {
            fileWriter.append("| ${sequence.padEnd(maxSequenceLength)} | ${count.toString().padStart(maxCountLength)} |\n")
        }

        // Generate and write the bottom dashed line of the table
        fileWriter.append("-".repeat(maxSequenceLength + 2)) // Add 2 for extra padding
        fileWriter.append("|")
        fileWriter.append("-".repeat(maxCountLength + 2)) // Add 2 for extra padding
        fileWriter.append("|\n")
    } catch (e: IOException) {
        println("Error writing to $outputFileName: ${e.message}")
    } finally {
        fileWriter.close()
    }
}