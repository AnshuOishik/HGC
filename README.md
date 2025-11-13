# HGC: Hybrid Genome Compression Using Optimal k-mer Length

## Supporting Platform

- Linux
- Windows

## Tech Stack

Java 

## Supported File Formats

- Genome main domian of FASTA/Q and Multi-FASTA
- Genome main domian raw sequence files


## OptK-mer: Optimal k-mer Length
The OptK-mer algorithm used the randomization method to determine the ideal k-mer length. HGC uses the ideal k-mer length discovered using OptK-mer to compress the particular sequence.

## Compilation Command

```sh
$ javac -d . *.java
```

## Execution Command

Reference-based

```sh
$ java -Xms10240m optkmer.Main chr 9 22 4 1
```

Reference-free

```sh
$ java -Xms10240m optkmer.Main chr 9 22 4 0 4
```

## Notice
1. -Xms10240m is the initial allocation of memory (MiB)
2. The list of target file directories and the reference file path (the first line) are both found in chr
3. The k-mer length's lower and upper bounds are 9 and 22
4. The number of threads is eight (4, by default, is the optional value)
5. then it is a flag 0 or 1 for refence free or reference based
6. then for reference based the 4 is the number of splits
