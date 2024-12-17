				       ****************************** OptK-mer & HGC ****************************
								OptK-mer: Optimal k-mer Length
								HGC: Hybrid Genome Compression
								https://github.com/AnshuOishik/HGC
									Copyright (C) 2025 
=============================================================================================================================
Introduction
To utilize the code, please use the Notepad++ editor.
Java has been utilized by us in the implementation.
Please use Linux or Windows as your operating system.
=============================================================================================================================
OptK-mer: Optimal k-mer Length
The OptK-mer algorithm used the randomization method to determine the ideal k-mer length. HGC uses the ideal k-mer length discovered using OptK-mer to compress the particular sequence.

# Compilation Command:
> javac -d . *.java

# Execution Command:

Reference-based:
> java -Xms10240m optkmer.Main chr 9 22 4 1

Reference-free:
> java -Xms10240m optkmer.Main chr 9 22 4 0 4

Notice:
# -Xms10240m is the initial allocation of memory (MB)
# The list of target file directories and the reference file path (the first line) are both found in chr
# The k-mer length's lower and upper bounds are 9 and 22
# The number of threads is eight (4, by default, is the optional value)
# then it is a flag 0 or 1 for refence free or reference based
# then for reference based the 4 is the number of splits
=============================================================================================================================
HGC: Hybrid Genome Compression
The OptK-mer technique yields the ideal k-mer length, which is used by HGC to compress sequences.

Compilation Command:
> javac -d . *.java

Execution Command:

Reference-based:
  Compression:
  > java -Xms12g hgc.HGC chrHg comp 4 15 1 1

  Decompression:
  > java -Xms4g hgc.HGC chrHg decomp 15 1 1

Notice:
# argument 1 "comp" is the argument for compression, argument 0 "chr" is the file containing the names of the files to be compressed, argument 2 "4" is the thread pool size, argument 3 "4" is the k-mer length, argument 4 "1" is a flag for reference based compression, argument 5 "1" is for DNA compression.
# argument 1 "decomp" is the argument for decompression, argument 0 "chr" is the file containing the names of the files, argument 2 "4" is the k-mer length, argument 3 "1" is a flag for reference based compression, argument 4 "1" for DNA compression

Reference-free:
Compression:
> java -Xms12g hgc.HGC In comp 4 15 0 4 1

Decompression:
> java -Xms4g hgc.HGC Out decomp 15 0 4 1

Notice:
# argument 1 "comp" is the argument for compression, argument 0 "In" is the desired name for the compressed file, argument 2 "4" is the thread pool size, argument 3 "4" is the k-mer length, argument 4 "0" is a flag for reference free compression, argument 5 "4" is the number of desired target files, argument 6 "1" for DNA compression.
# argument 1 "decomp" is the argument for decompression, argument 0 "Out" is the name for the output file, argument 2 "4" is the k-mer length, argument 3 "0" is a flag for reference free compression, argument 4 "4" is the number of split/target files, argument 5 "1" for DNA compression

# Please execute the procedure listed at the end of this file to create an executable file for the BSC compressor
# In the last phase, an alternative is to utilize a 7-zip compressor; the procedure is described at the end of this file
# The final compressed file created by the bsc compressor is called "BscC.bsc"
# "ZipC.7z" is the name of the compressed file that the 7-zip compressor produced at the end
# The number of threads is eight (4, by default, is the optional value)
# -Xms10240m is the initial allocation of memory (MB)
# Please place the executable "bsc" and "7za" in the main class file's directory
# For "bsc" and "7za" modes, kindly set "chmod 0777"
=============================================================================================================================
#Commands for "bsc" executable file generation from available code at https://github.com/IlyaGrebnov/libbsc
Compilation commands:
> g++ -c libbsc/adler32/adler32.cpp
> g++ -c libbsc/bwt/libsais/libsais.c
> g++ -c libbsc/bwt/bwt.cpp
> g++ -c libbsc/coder/coder.cpp
> g++ -c libbsc/coder/qlfc/qlfc.cpp
> g++ -c libbsc/coder/qlfc/qlfc_model.cpp
> g++ -c libbsc/filters/detectors.cpp
> g++ -c libbsc/filters/preprocessing.cpp
> g++ -c libbsc/libbsc/libbsc.cpp
> g++ -c libbsc/lzp/lzp.cpp
> g++ -c libbsc/platform/platform.cpp
# Please change the platform.cpp file. In lines 51 and 66, change 'MEM_LARGE_PAGES' in Linux (Ubuntu) to 'MEM_4MB_PAGES' in Windows 10.
> g++ -c libbsc/st/st.cpp
> g++ -c bsc.cpp

#Linking command:
> g++ -o bsc bsc.o adler32.o bwt.o coder.o detectors.o libbsc.o libsais.o lzp.o platform.o preprocessing.o qlfc.o qlfc_model.o st.o

Notice:
# The created executable file name is bsc.
=============================================================================================================================
# Installing the 7-zip compressor for Windows can be done at https://www.7-zip.org. according to your operating system.
# Please set 7z path to environment variable.
# Please use the following command to install 7-zip on Linux.
> sudo apt-get update (If required)
> sudo apt-get install p7zip-full
=============================================================================================================================
### Contacts 
Please send an email to <subhankar.roy07@gmail.com> if you experience any issues.
