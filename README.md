# HGC: Hybrid Genome Compression Using Optimal k-mer Length

## Supporting Platform

- Linux
- Windows

## Tech Stack

Java 18

## Supported File Formats

- Genome main domian of raw sequence files, FASTA/Q and Multi-FASTA

## OptK-mer: Optimal k-mer Length
The OptK-mer algorithm used the randomization method to determine the ideal k-mer length. HGC uses the ideal k-mer length discovered using OptK-mer to compress the particular sequence.

## Compilation Command

```sh
$ javac -d . *.java
```

## Execution Command

Reference-based

```sh
$ java optkmer.Main chr 2 22 8 1
```

Reference-free

```sh
$ java optkmer.Main In 2 22 2 0 1
```

## Notice
1. For referential compression, the list of target file directories and the reference file path (the first line) are both found in the file chr.
2. For reference-free compression, In is the input file to compress.
3. The k-mer length's lower and upper bounds are 2 and 22.
4. The number of threads is eight/two (4, by default, is the optional value).
5. Then it is a flag 1 or 0 for reference-based or refence-free.
6. Then for reference-free the 1 is the number of splits.

## HGC: Hybrid Genome Compression
The OptK-mer technique yields the ideal k-mer length, which is used by HGC to compress sequences.

## Compilation Command

```sh
$ javac -d . *.java
```

## Execution Command

## Reference-based

Compression

```sh
$ java hgc.HGC chr comp 8 11 1 1
```

Decompression

```sh
$ java hgc.HGC chr decomp 11 1 1
```

## Notice
1. Argument 0 "chr" is the file containing the names of the files to be compressed (the first line is the reference file name), argument 1 "comp" is the argument for compression,  argument 2 "8" is the thread pool size, argument 3 "11" is the k-mer length, argument 4 "1" is a flag for reference-based compression, argument 5 "1" is for DNA and "0" for RNA sequence compression.
2. Argument 0 "chr" is the file containing the names of the files, argument 1 "decomp" is the argument for decompression, argument 2 "15" is the k-mer length, argument 3 "1" is a flag for reference-based compression, argument 4 "1" for DNA and "0" for RNA sequence compression

## Reference-free

Compression

```sh
$ java hgc.HGC In comp 2 11 0 1 1
```

Decompression

```sh
$ java hgc.HGC Out decomp 11 0 1 1
```

## Notice
1. Argument 0 "In" is the name of the to-be-compressed sequence, argument 1 "comp" is the argument for compression, argument 2 "2" is the thread pool size, argument 3 "11" is the k-mer length, argument 4 "0" is a flag for reference-free compression, argument 5 "1" is the number of desired target files, argument 6 "1" for DNA and "0" for RNA compression.
2. Argument 0 "Out" is the name for the compressed output file, argument 1 "decomp" is the argument for decompression, argument 2 "11" is the k-mer length, argument 3 "0" is a flag for reference-free compression, argument 4 "1" is the number of split/target files, argument 5 "1" for DNA and "0" for RNA sequence compression.
3. Please execute the procedure listed at https://github.com/AnshuOishik/RGCOK to create an executable file for the BSC compressor.
4. In the last phase, an alternative is to utilize a 7-zip compressor; the procedure is described at https://github.com/AnshuOishik/RGCOK.
5. The final compressed file created by the bsc compressor is called "HGC.bsc".
6. "ZipC.7z" is the name of the compressed file that the 7-zip compressor produced at the end.
7. The number of threads is two/eight (4, by default, is the optional value).
8. Please place the executable "bsc" and "7za" in the main class file's directory
9. For "bsc" and "7za" modes, kindly set "chmod 0777"
