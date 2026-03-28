package hgc;

import java.io.*;
import java.nio.file.*;
import java.util.*;

class HGC {
	static String command = "";
    public static void main(String[] a) throws IOException {
        // Check if file name is passed as command line argument
        if (a.length < 1) {
            System.out.println("Usage: java HGC <filename> <operation> [thread_pool_size] [kmer_length] [useFileList] [number_of_splits]");
            return;
        }
        String[] args = new String[7];
        args[0] = a[0];//file name
        args[1] = a[1];//operation comp or decomp[]
        String nt = "4", b="0",k="4",dt="1";
        if (args[1].equals("comp")) {
            if (a.length == 2) {
                args[2] = nt;//thread pool size
                args[3] = k;// k mer length
                args[4] = b;// boolean: 1-refernce based, 0-reference free
                args[5] = args[4].equals("1")?"0":args[2];// number of split
				args[6]=dt;
                command=args[0]+" "+args[1];
            } else if (a.length == 3) {
                args[2] = a[2];//thread pool size
                args[3] = k;// k mer length
                args[4] = b;// boolean: 1-refernce based, 0-reference free
                args[5] = args[4].equals("1")?"0":args[2];// number of split
                args[6]=dt;
				command=args[0]+" "+args[1]+" "+args[2];
            }else if (a.length == 4) {
                args[2] = a[2];//thread pool size
                args[3] = a[3];// k mer length
                args[4] = b;// boolean: 1-refernce based, 0-reference free
                args[5] = args[4].equals("1")?"0":args[2];// number of split
				args[6]=dt;
                command=args[0]+" "+args[1]+" "+args[2]+" "+args[3];
            }else if (a.length == 5) {
                args[2] = a[2];//thread pool size
                args[3] = a[3];// k mer length
                args[4] = a[4];// boolean: 1-refernce based, 0-reference free
                args[5] = args[4].equals("1")?"0":args[2];// number of split
				args[6]=dt;
                command=args[0]+" "+args[1]+" "+args[2]+" "+args[3]+" "+args[4];
            } 
            else if (a.length == 6) {
                args[2] = a[2];//thread pool size
                args[3] = a[3];// k mer length
                args[4] = a[4];// boolean: 1-refernce based, 0-reference free
                args[5] = args[4].equals("1")?"0":a[5];// number of split
            	args[6]=args[4].equals("1")?a[5]:dt;
				command=a[0]+" "+a[1]+" "+a[2]+" "+a[3]+" "+a[4]+" "+a[5];
            } 
			else
			{
				args[2] = a[2];//thread pool size
                args[3] = a[3];// k mer length
                args[4] = a[4];// boolean: 1-refernce based, 0-reference free
                args[5] = a[5];// number of split
				args[6]=a[6];
            	command=args[0]+" "+args[1]+" "+args[2]+" "+args[3]+" "+args[4]+" "+args[5]+" "+args[6];
			}
        }
        else{
            if (a.length == 2) {
                args[2] = k;// k mer length
                args[3] = b;// boolean: 1-refernce based, 0-reference free
                args[4] = args[3].equals("1")?"0":nt;// number of split
            	args[5]=dt;
				command=args[0]+" "+args[1];
            } else if (a.length == 3) {
                args[2] = a[2];// k mer length
                args[3] = b;// boolean: 1-refernce based, 0-reference free
                args[4] = args[3].equals("1")?"0":nt;// number of split
            	args[5]=dt;
				command=args[0]+" "+args[1]+" "+args[2];
            }else if (a.length == 4) {
                args[2] = a[2];// k mer length
                args[3] = a[3];// boolean: 1-refernce based, 0-reference free
                args[4] = args[3].equals("1")?"0":nt; // number of split
            	args[5]=dt;
				command=args[0]+" "+args[1]+" "+args[2]+" "+args[3];
            }
            else if (a.length == 5) {
                args[2] = a[2];// k mer length
                args[3] = a[3];// boolean: 1-refernce based, 0-reference free
                args[4] = a[4];// number of split
				args[5]=args[3].equals("1")?a[4]:dt;
            	command=a[0]+" "+a[1]+" "+a[2]+" "+a[3]+" "+a[4];
            }
			else {
                args[2] = a[2];// k mer length
                args[3] = a[3];// boolean: 1-refernce based, 0-reference free
                args[4] = a[4];// number of split
				args[5]=a[5];
            	command=args[0]+" "+args[1]+" "+args[2]+" "+args[3]+" "+args[4]+" "+args[5];
            }
            
        }
        int thread_pool_size = args[1].equals("comp")?Integer.parseInt(args[2]):0;
        int opt_k_mer_len = args[1].equals("comp")?Integer.parseInt(args[3]):Integer.parseInt(args[2]);
        boolean useFileList = args[1].equals("comp")?args[4].equals("1"):args[3].equals("1");
        int number_of_splits = args[1].equals("comp")?Integer.parseInt(args[5]):Integer.parseInt(args[4]);  // number of splits for the remaining 80%
        int dr =args[1].equals("comp")?Integer.parseInt(args[6]):Integer.parseInt(args[5]);;
		if (args[1].equals("comp")) {
            System.out.println("Only the primary bases of the raw, FASTA/Q or multi-FASTA file can be compressed using HGC!.");
            Long start_time = System.currentTimeMillis();
            System.out.println("Compression Started...");
            List<String> seq_paths;
            if(useFileList){
                seq_paths = readSeqPathsFromFile(args[0]);
            }
            else{
                splitFastaFile(args[0], number_of_splits);
                seq_paths = generateSeqPaths(number_of_splits);
            }
            HGCCompress.beginingSettings(seq_paths, opt_k_mer_len);
            
            HGCCompress.seqCompress(thread_pool_size, dr);
            
	        Compress.stringToAsciiRleEncode(seq_paths.get(0), dr);
	        
            // Compression using bsc encoder
            HGCCompress.bscCompression();
			
			// Compression using 7zip encoder
			//HGCCompress.sevenZipCompression();
        
            // Delete files in seq_paths
            if(!useFileList){
				deleteFiles(seq_paths);
            }

            Long end_time = System.currentTimeMillis();
            System.out.println("Total compression time = " + (1.0 * end_time - start_time) / 1000 + " s");
            System.out.println("Compression Completed...");
        } 
        else if (args[1].equals("decomp")) {
            Long start_time = System.currentTimeMillis();
            System.out.println("Decompression Started...");
            List<String> seq_paths;
            if(useFileList){
                seq_paths = readSeqPathsFromFile(args[0]);
            }
            else{
                seq_paths = generateSeqPaths(number_of_splits);
            }
            HGCDecompress.beginingSettings(seq_paths, opt_k_mer_len);
	        
	        // Decompression using bsc decoder
            HGCDecompress.bscDecompression();
			
			// Decompression using 7zip decoder
			//HGCDecompress.sevenZipDecompression();
            
	        Decompress.stringToAsciiRleDecode(seq_paths.get(0),dr);

            HGCDecompress.seqDecompress(dr);  

            if(!useFileList){
                 // Combine files after decompression
                List<String> inputFiles = new   ArrayList<>();
                inputFiles.add("R");  // The first part is always R.fa
                for (int i = 0; i < number_of_splits; i++) {
                    inputFiles.add("Output/T"   + i);
                }   
                combineFiles(inputFiles, "Output/" + args[0]);  // Combine into final output

                // Delete files in inputFiles
                deleteFiles(inputFiles);
            }
            Long end_time = System.currentTimeMillis();
            System.out.println("Total decompression time = " + (1.0 * end_time - start_time) / 1000 + " s");
            System.out.println("Decompression Completed...");
        }
    }

     // Function to generate file names based on the number of splits
    private static List<String> generateSeqPaths(int numberOfSplits) {
        List<String> seq_paths = new ArrayList<>();
        seq_paths.add("R");  // The first 40-60% goes to R.fa
        for (int i = 0; i < numberOfSplits; i++) {
            seq_paths.add("T" + i);
        }
        return seq_paths;
    }

     // Function to read file paths from an input file
    private static List<String> readSeqPathsFromFile(String filePath) throws IOException {
        List<String> seq_paths = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                seq_paths.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file paths from " + filePath);
            e.printStackTrace();
        }
        return seq_paths;
    }  

    // Function to split the Fasta file
    private static void splitFastaFile(String inputFilename, int numberOfSplits) {
        String outputFileR = "R";   // First 40-60%

        try {
            // Read the input file into memory
            Path inputPath = Paths.get(inputFilename);
			
            byte[] inputBytes = Files.readAllBytes(inputPath);
            int totalSize = inputBytes.length;

            // Calculate the Ref. size
			Scanner sc = new Scanner(System.in);
			System.out.print("Enter Split (s = 2/1.66/1.42/1.25/1.1) ratio: "); //Ref. size 50% -90% of the total file size
			double s = sc.nextDouble();
            int splitSizeR = (int) (totalSize / s);  
            int remainingSize = totalSize - splitSizeR;
            int splitSizeT = remainingSize / numberOfSplits;  // Divide the remaining 60-40%

            // Write the first 40-60% to R.fa
            try (FileOutputStream outFileR = new FileOutputStream(outputFileR)) {
                outFileR.write(inputBytes, 0, splitSizeR);
            }

            // Write the remaining parts to T0.fa, T1.fa, T2.fa, etc.
            for (int i = 0; i < numberOfSplits; i++) {
                String outputFileT = "T" + i;
                int startPos = splitSizeR + i * splitSizeT;
                int endPos = (i == numberOfSplits - 1) ? totalSize : startPos + splitSizeT;  // Handle last part carefully

                try (FileOutputStream outFileT = new FileOutputStream(outputFileT)) {
                    outFileT.write(inputBytes, startPos, endPos - startPos);
                }
            }

            System.out.println("File successfully split into R.fa and T0.fa to T" + (numberOfSplits - 1) + ".fa");
        } catch (IOException e) {
            System.out.println("An error occurred during file splitting: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Function to combine R.fa, T0.fa, T1.fa, T2.fa, and T3.fa into one file inside the Output folder
    private static void combineFiles(List<String> inputFiles, String outputFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (String inputFile : inputFiles) {
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                }
                reader.close();
            }
            System.out.println("Combined files into " + outputFile);
        } catch (IOException e) {
            System.out.println("An error occurred while combining files: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static void deleteFiles(List<String> filePaths) {
        for (String filePath : filePaths) {
            try {
                Files.delete(Paths.get(filePath));
                System.out.println("Deleted file: " + filePath);
            } catch (IOException e) {
                System.out.println("Failed to delete file: " + filePath);
                e.printStackTrace();
            }
        }
    }
}