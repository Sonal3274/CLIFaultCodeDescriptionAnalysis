# Fault Code Description Analysis CLI Program

This is a Command-Line Interface (CLI) program written in Kotlin that analyzes Fault 
Code Descriptions from an input text file and generates various output files as 
described in the challenge provided.

## Table of Contents
- [Requirements](#Requirements)
- [Getting Started](#Getting Started)
- [Prerequisites](#Prerequisites)
- [Installation](#Installation)
- [Usage](#Usage)
- [Executing the Program](#Executing the Program)
- [Functionality](#Functionality)
- [Documentation](#Documentation)
- [File-Level Comments](#File-Level Comments)
- [Function-Level Comments](#Function-Level Comments)
- [Inline Comments](#Inline Comments)
- [Parameter Comments](#Parameter Comments)
- [Return Value Comments](#Return Value Comments)
- [Function-Level Comments](#Function-Level Comments)
- [Exception Handling Comments](#Exception Handling Comments)
- [License](#License)


## Requirements

- This program is written in Kotlin, a statically typed language.
- Ensure that you have the Kotlin compiler installed to build and run the program.
- This program does not use any dynamically typed languages such as Python or JavaScript.

# Getting Started

## Prerequisites

- Before running this program, make sure you have the following prerequisites installed:

1. Kotlin Compiler
2. Java Development Kit (JDK)

## Installation

Clone this repository to your local machine:

git clone https://github.com/Sonal3274/CLIFaultCodeDescriptionAnalysis.git

## Usage

### Executing the Program
Specify the input text file (input.txt) containing Fault Code Descriptions.
Specify the directory where output CSV files will be stored.
Run the program using the following command:

`kotlin -classpath . MainKt`

The program will execute and generate the required output files in the specified output folder.

## Functionality
This CLI program performs the following tasks:

1. Reads Fault Code Descriptions from an input text file.
2. Extracts and counts all unique word sequences of varying lengths.
3. Writes the word sequences and their occurrence counts to CSV files.
4. Sorts the CSV files by the number of occurrences and sequences.
5. Finds the top 10 sequences among all CSV files.
6. Counts the number of lines in the input text containing any of the top sequences.
7. Writes the number of affected lines to an output file.

## Documentation

The code is well-documented with comments to explain its functionality and usage. 
Here's a breakdown of the comments:

### File-Level Comments - 
At the beginning of the Kotlin source file, there is a file-level comment explaining 
the purpose of the code and its usage.

### Function-Level Comments -
Each function is accompanied by a comment describing its purpose and functionality.

### Inline Comments -
Throughout the code, there are inline comments providing additional context and 
explanations for specific code blocks.

### Parameter Comments -
Function parameters are documented with comments that describe their roles and 
expected values.

### Return Value Comments -
Functions that return values are documented with comments explaining the meaning and 
format of their return values.

### Exception Handling Comments -
Exception handling is documented with comments explaining how errors are handled and 
any relevant error messages.

## License
This project is not licensed and is for personal use only. 
You are welcome to view and use the code for reference or personal projects.