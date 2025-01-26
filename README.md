# Scala SBT Project

- Scala 3

## Table Processor

The main goal of the application is to process tables with formulas. We should be able to load the table, evaluate all formulas found in the table, filter the rows by their values in specified columns, and finally print the result.

The application is executed from the command-line or sbt shell, where with the arguments a user specifies the options. 

Available CLI Options.

´´´
Input options:
   --input-file [FILE] : The input CSV file (required).
   --input-separator [STRING] : The separator for input (optional, defaults to ",").
   
Output options:
   --output-file [FILE] : The file to output the table to (optional).
   --stdout : Print the table to the standard output (optional, by default true).
   --headers : Turns on printing of headers (optional, by default, they are not printed).
   --output-format [csv|md] : The format of the output (optional, default: csv).
   --output-separator [STRING] : For CSV output: the separator in the output file (optional, defaults to ",").
   
Transformation options:
   --filter [COLUMN] ( < | > | <= | >= | == | != ) [NUMBER] : Filter on the column (optional, can be repeated).
   --filter-is-empty [COLUMN] : Filter out lines with non-empty cells in the specified column (optional, can be repeated).
   --filter-is-not-empty [COLUMN] : Filter out lines with empty cells in the specified column (optional, can be repeated).
´´´
