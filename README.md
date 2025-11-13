# HGC: Hybrid Genome Compression Using Optimal k-mer Length

## Supporting Platform

- Linux
- Windows

## Tech Stack

Java 

## Supported File Formats

- Genome main domian of FASTA/Q and Multi-FASTA
- Genome main domian of raw sequence files


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
$ java -Xms12g hgc.HGC chrHg comp 4 15 1 1
```

Decompression

```sh
$ java -Xms4g hgc.HGC chrHg decomp 15 1 1
```

## Notice
1. argument 1 "comp" is the argument for compression, argument 0 "chr" is the file containing the names of the files to be compressed, argument 2 "4" is the thread pool size, argument 3 "4" is the k-mer length, argument 4 "1" is a flag for reference based compression, argument 5 "1" is for DNA/"0" for RNA compression.
2. argument 1 "decomp" is the argument for decompression, argument 0 "chr" is the file containing the names of the files, argument 2 "4" is the k-mer length, argument 3 "1" is a flag for reference based compression, argument 4 "1" for DNA/"0" for RNA compression

## Reference-fre

Compression

```sh
$ java -Xms12g hgc.HGC In comp 4 15 0 4 1
```

Decompression

```sh
$ java -Xms4g hgc.HGC Out decomp 15 0 4 1
```

## Notice
1. argument 1 "comp" is the argument for compression, argument 0 "In" is the desired name for the compressed file, argument 2 "4" is the thread pool size, argument 3 "4" is the k-mer length, argument 4 "0" is a flag for reference free compression, argument 5 "4" is the number of desired target files, argument 6 "1" for DNA/"0" for RNA compression.
2. argument 1 "decomp" is the argument for decompression, argument 0 "Out" is the name for the output file, argument 2 "4" is the k-mer length, argument 3 "0" is a flag for reference free compression, argument 4 "4" is the number of split/target files, argument 5 "1" for DNA/"0" for RNA compression
3. Please execute the procedure listed at https://github.com/AnshuOishik/RGCOK to create an executable file for the BSC compressor
4. In the last phase, an alternative is to utilize a 7-zip compressor; the procedure is described at the end of this file
5. The final compressed file created by the bsc compressor is called "BscC.bsc"
6. "ZipC.7z" is the name of the compressed file that the 7-zip compressor produced at the end
7. The number of threads is eight (4, by default, is the optional value)
8. -Xms10240m is the initial allocation of memory (MiB)
9. Please place the executable "bsc" and "7za" in the main class file's directory
10. For "bsc" and "7za" modes, kindly set "chmod 0777"
