# Compiler for the high-level programming language Jack, implemented in Java

The project includes three parts: a Compiler, a VMTranslator, and an Assembler.

## Compiler

My implementation of the Jack Compiler includes 3 classes: JackCompiler, CompilationEngine and JackTokenizer.

### JackCompiler.java

The program takes an input of a .jack file or a folder that contains .jack files, and creates a .vm file for each file.

### JackTokenizer.java

The program takes an input of a .vm file and provides an API to obtain each token separately.
