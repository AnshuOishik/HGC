package hgc;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
class HGCCompress {
    static int max_cha_num = 1 << 28, k_mer_len; //Max allowed size of a sequence = 2^28 = 256 MB, Optimal k_mer_len value is obtained using MtK-mer algorithm                     
	static int kMer_bit_num, hash_table_len;     
	static final int max_arr_num_bit = 30, max_arr_num_bit_match = max_arr_num_bit >> 1, max_arr_num = 1 << max_arr_num_bit; 
	static int vec_size = 1 << 20, min_rep_len;
    static int seq_number, seq_bucket_len, sec_seq_num;                          
    static int ref_code_len;
    static char[] ref_code;
    static int[] ref_loc, ref_bucket; //ref_bucket is f_arr, ref_loc is s_arr
    static List<String> seq_paths;
    static List<List<MatEntry>> match_result_vec;
    static List<int[]> seq_bucket_vec;
    static List<List<Integer>> seq_loc_vec; 
    static String info;
    static StringBuilder sb; //As there is no multi-threading, we are using it because it is faster due to not being thread-safe.
	static int dr = 1; //For DNA (1) or RNA (0). Default = 1 
	
    static void secondLevelMatch(BufferedWriter bw, List<MatEntry> mat_res, int seq_num) {
        int hash_value, pre_seq_id = 1, id, pos, i, j, max_pos = 0, pre_pos = 0, delta_pos, len, max_len, delta_len, seq_id = 0, delta_seq_id;
        List<MatEntry> misMatchEntry = new ArrayList<>();
        try {
            misMatchEntry.add(mat_res.get(0));
			for (i = 1; i < mat_res.size() - 1; i++) {  
                hash_value = Math.abs(getTempHashValue(mat_res.get(i)) + getTempHashValue(mat_res.get(i + 1))) % seq_bucket_len; 		
				max_len = 0;
                for (j = 0; j < Math.min(seq_num - 1, sec_seq_num); j++) { 
                    id = seq_bucket_vec.get(j)[hash_value]; 		
                    if (id != -1) { 
                        for (pos = id; pos != -1; pos = seq_loc_vec.get(j).get(pos)) { 
                            len = getMatLen(match_result_vec.get(j), pos, mat_res, i);  
                            if (len > 1 && len > max_len) { 
                                seq_id = j + 1; 
                                max_pos = pos; 
                                max_len = len; 
                            }
                        }
                    }
                }
                if (max_len != 0) {
                    delta_seq_id = seq_id - pre_seq_id; 
                    delta_len = max_len - 2; 
                    delta_pos = max_pos - pre_pos;
                    pre_seq_id = seq_id; 
                    pre_pos = max_pos + max_len;
                    for (j = 0; j < misMatchEntry.size(); j++)
                        saveMatRes(bw, misMatchEntry.get(j));
                    bw.flush();
                    misMatchEntry = new ArrayList<>();
                    bw.write("\n" + delta_seq_id + " " + delta_pos + " " + delta_len );
                    i += max_len - 1; 
                } else
                    misMatchEntry.add(mat_res.get(i));
            }
            if (i == mat_res.size() - 1)
                misMatchEntry.add(mat_res.get(i));
            for (j = 0; j < misMatchEntry.size(); j++) 
               saveMatRes(bw, misMatchEntry.get(j));
            bw.flush();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

	static void saveFirstMatRes(BufferedWriter bw, List<MatEntry> mes) {
        int i;
		try {
            for (i = 0; i < mes.size(); i++)
                saveMatRes(bw, mes.get(i));
            bw.flush();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
	
	static int getTempHashValue(MatEntry me) { 
        int i, res = 0;
		String str = me.getMisMatStr();
		int p1 = 80021, p2 = p1 - 30000, p3 = p1 - 40012;
        for (i = 0; i < str.length(); i++)
			res = res + p1 * str.charAt(i);
		res = res + p2 * me.getPos() + p3 * me.getLen();
        res = res % seq_bucket_len;
        return res;
    }
	
    static void matResHash(List<MatEntry> mat_res) {
        int hash_value1, hash_value2, hash_value, i;
        List<Integer> seq_loc = new ArrayList<Integer>(vec_size);
        int[] seq_bucket = new int[seq_bucket_len];
        for (i = 0; i < seq_bucket_len; i++)
            seq_bucket[i] = -1;
        hash_value1 = getTempHashValue(mat_res.get(0)); 
        if (mat_res.size() < 2)
            hash_value2 = 0;
        else 
            hash_value2 = getTempHashValue(mat_res.get(1));
        hash_value = Math.abs(hash_value1 + hash_value2) % seq_bucket_len;
		seq_loc.add(seq_bucket[hash_value]);
        seq_bucket[hash_value] = 0;
        for (i = 1; i < mat_res.size() - 1; i++) {
            hash_value1 = hash_value2;
            hash_value2 = getTempHashValue(mat_res.get(i + 1));
            hash_value = Math.abs(hash_value1 + hash_value2) % seq_bucket_len;
            seq_loc.add(seq_bucket[hash_value]);
            seq_bucket[hash_value] = i;
        }
        seq_loc_vec.add(seq_loc);
		seq_bucket_vec.add(seq_bucket);
    }
	
	static int twoBitIntCoding(char c) {
		int r;
        switch (c) {
            case 'A':	r = 0; break;
            case 'C':	r = 1; break;
            case 'G':	r = 2; break;
			case 'T':	r = 3; break;
            case 'U':	r = 3; break;
            default:	r = -1;
        }
		return r;
    }
	
	//mis_mat_str to ASCII code converter
	static String stringToASCIICode(String mis_mat_str){
		int L, miss_L=0, i, k;
		char ch;
		int[] miss_match_code;

		L = mis_mat_str.length();
		miss_match_code = new int[L];
        for (i = 0; i < L; i++) {
            ch = mis_mat_str.charAt(i);
			miss_match_code[miss_L++] = twoBitIntCoding(ch);
		}
		
		StringBuilder sb = new StringBuilder(L);
		
		//Converting miss-code to ASCII value
		int step_len = miss_L/4;
		int miss_match_value = 0;
		for (i =0; i < step_len; i++) {
			miss_match_value = 0;
            for (k = 3; k >= 0; k--) {
                miss_match_value <<= 2;
                miss_match_value += miss_match_code[4*i+k];
            }
			//32 (For space) as RME are spilit w.r.t space, 65 (A), 67 (C), G (71), T (84), U(85), 48 to 57 for count in RLE, and for value 10 and 13 
			//[RME = (mis_match pos len)]
			if(miss_match_value == 10 || miss_match_value == 13|| miss_match_value == 32 || miss_match_value == 65 || miss_match_value == 67 || miss_match_value == 71 || miss_match_value == 84 || miss_match_value ==85 ||(miss_match_value >= 48 && miss_match_value <= 57) ) 
				for (k = 0; k <= 3; k++){ 
					if(miss_match_code[4*i+k] == 0)
						sb.append('A');
					else if(miss_match_code[4*i+k] == 1)
						sb.append('C');
					else if(miss_match_code[4*i+k] == 2)
						sb.append('G');
					else if(miss_match_code[4*i+k] == 3){
						if(dr == 1) 
							sb.append('T');
						else 
							sb.append('U');
					}
				}
			else
				sb.append((char)miss_match_value);
		}
		//If odd length i.e. 1,2,3 remains
		int rem_len = miss_L%4; 
		miss_match_value = 0;
		for (i = miss_L-rem_len; i < miss_L ; i++){
			if(miss_match_code[i] == 0)
				sb.append('A');
			else if(miss_match_code[i] == 1)
				sb.append('C');
			else if(miss_match_code[i] == 2)
				sb.append('G');
			else if(miss_match_code[i] == 3){
				if(dr == 1) 
					sb.append('T');
				else 
					sb.append('U');
			}
		}
		mis_mat_str = sb.toString();
		return mis_mat_str;
	}
	
    static void firstLevelMatch(GenSequence seq) { 
        char[] seq_code = seq.getCode();
        int seq_code_len = seq.getCodeLen(), pre_pos = 0, step_len = seq_code_len - k_mer_len + 1; 
        int i, j, id, k, ref_id, tar_id, len, max_len, max_k;
		long tar_value; 
        MatEntry me = new MatEntry(); 
        List<MatEntry> mat_res = new ArrayList<>(vec_size);
		String mis_mat_str = String.valueOf(seq_code[0]);		
        for (i = 1; i < step_len; i++) { 
            tar_value = 0;
            for (j = k_mer_len - 1; j >= 0; j--) { 
                tar_value <<= 2; 
                tar_value += twoBitIntCoding(seq_code[i + j]);
            }
			if(k_mer_len>15)
				id = ref_bucket[(int)(tar_value&(long)(max_arr_num - 1))];
			else
				id = ref_bucket[(int)tar_value];
            if (id > -1) {
                max_len = -1;
                max_k = -1;
                for (k = id; k != -1; k = ref_loc[k]) {
					//If the loop executes, the entry exists, that indicates that at least one k-mer in ref is the same as the k-mer of tar (the case where different k-mers produce the same hashcode can easily be excluded by string comparison from index 15 onwards)
					if(k_mer_len > 15){
						ref_id = k + max_arr_num_bit_match;
						tar_id = i + max_arr_num_bit_match;
						len = max_arr_num_bit_match;
					}
					else{
						ref_id = k + k_mer_len;
						tar_id = i + k_mer_len;
						len = k_mer_len;
					}
                    while (ref_id < ref_code_len && tar_id < seq_code_len && ref_code[ref_id++] == seq_code[tar_id++])
                        len++;   
                    min_rep_len = k_mer_len+1;
					if (len >= min_rep_len && len > max_len) {
                        max_len = len; 
                        max_k = k; 
                    }
                }
                if (max_len > -1) {
					mis_mat_str = stringToASCIICode(mis_mat_str);
                    me.setMisMatStr(mis_mat_str); 
                    me.setPos(max_k - pre_pos); 
                    me.setLen(max_len - min_rep_len); 
                    mat_res.add(me);   
                    me = new MatEntry();
                    i += max_len;
                    pre_pos = max_k + max_len;
                    mis_mat_str = "";
                    if (i < seq_code_len) {
						mis_mat_str += String.valueOf(seq_code[i]);
                    }
                    continue;
                }
            }
			mis_mat_str += String.valueOf(seq_code[i]);
        }
        if (i < seq_code_len) {
            for (; i < seq_code_len; i++) {
				mis_mat_str += String.valueOf(seq_code[i]);
            }
			me.setPos(0);
			me.setLen(-1);			
			mis_mat_str = stringToASCIICode(mis_mat_str);
            me.setMisMatStr(mis_mat_str); 
            mat_res.add(me);
        }
        seq.setMatchResult(mat_res);
        seq.setCodeLen(0);
        seq.setCode(null);
    }
	
    static void tarSeqExt(int seq_num, GenSequence seq) { 
        String path = seq_paths.get(seq_num);
        File f = new File(path);
        int seq_code_len = 0; 
        char[] seq_code = new char[max_cha_num]; //So target max allowed size = 256 MB
		String str;
        char ch;
        int i;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
                str = br.readLine();
                for (i = 0; i < str.length(); i++) {
                    ch = str.charAt(i);
                    ch = Character.toUpperCase(ch);
                    if (ch == 'A' || ch == 'C' || ch == 'G' || ch == 'T' || ch == 'U') {
                        seq_code[seq_code_len++] = ch;
                    }
                }
			br.close();
        } catch (Exception e) { 
            System.out.println(e);
        } 
        seq.setCode(seq_code);
        seq.setCodeLen(seq_code_len);
    } 	

    static void saveMatRes(BufferedWriter bw, MatEntry me) {
        try {
            bw.write("\n" + me.getMisMatStr() + " " + me.getPos() + " " + me.getLen());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

     static int getMatLen(List<MatEntry> ref_mat_res, int ref_idx, List<MatEntry> tar_mat_res, int tar_idx) {
        int len = 0;
        while (ref_idx < ref_mat_res.size() && tar_idx < tar_mat_res.size() && compMatEntry(ref_mat_res.get(ref_idx++), tar_mat_res.get(tar_idx++)))
            len++; 
        return len; 
    }
	
    static Boolean compMatEntry(MatEntry ref, MatEntry tar) {
        if (ref.getPos() == tar.getPos() && ref.getLen() == tar.getLen() && ref.getMisMatStr().equals(tar.getMisMatStr()))
            return true;
        else
            return false;
    }
	
    //BSC Compression
	public static void bscCompression() { 
		try {
			String path = seq_paths.get(0)+".targ";
			String tarCommand = "tar -cf " + "TarC.tar " + path + " chr.rgc";
			Process p1 = Runtime.getRuntime().exec(tarCommand);
            p1.waitFor();
            String bscCommand = "./bsc e " + "TarC.tar " + "HGC.bsc -e2"; //Linux, bsc is the generated executable file name of the bsc compressor 
            //String bscCommand = "bsc e " + "TarC.tar " + "HGC.bsc -e2"; //Windows
			Process p2 = Runtime.getRuntime().exec(bscCommand);
            p2.waitFor();

            deleteFile(new File("TarC.tar"));
            deleteFile(new File(path));
            deleteFile(new File("chr.rgc"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
	
	// 7-zip Compression
	public static void sevenZipCompression() {
		try{
			//Please use the following for Linux platform
	//String zip = "7za a " + "ZipC" + ".7z " + "chr.id " + "chr.rgc" + " -m0=PPMD"; 
			//Please use the following for Windows platform
	String zip = "7z a " + "ZipC" + ".7z " + "chr.id " + "chr.rgc" + " -m0=PPMD"; 
			Process p = Runtime.getRuntime().exec(zip);
			p.waitFor();
			deleteFile(new File("chr.id"));
            deleteFile(new File("chr.rgc"));
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	static boolean deleteFile(File f) { 
        if (!f.exists()) 
            return false;
        if (f.isDirectory()) {
            File[] fs = f.listFiles();
            for (File f1 : fs) 
                deleteFile(f1);
        }
        return f.delete();
    }
	
	static void multiThreading(String temp_dir_path, int pool_size) { 
        ExecutorService exe_service_first = Executors.newSingleThreadExecutor(); 
        int i;
		for (i = 1; i <= sec_seq_num; i++)
            exe_service_first.execute(new ExtThread(i, temp_dir_path));
        exe_service_first.shutdown(); 
        while (!exe_service_first.isTerminated()) {}
        ExecutorService exe_service_later = Executors.newFixedThreadPool(pool_size);
        for (i = sec_seq_num + 1; i < seq_number; i++)
            exe_service_later.execute(new ExtThread(i, temp_dir_path));
        exe_service_later.shutdown();
        while (!exe_service_later.isTerminated()) {}
    }
	
	static void kMerHash() { 
        ref_loc = new int[max_cha_num];
		if(k_mer_len > 15)
			hash_table_len = 1 << max_arr_num_bit;
		else
			hash_table_len = 1 << kMer_bit_num;
        ref_bucket = new int[hash_table_len]; 
		int i;
        for (i = 0; i < hash_table_len; i++)
            ref_bucket[i] = -1;
        long value1 = 0; 
        int value2, step_len = ref_code_len - k_mer_len + 1, shift_bit_num = 2 * (k_mer_len - 1);; 
        for (i = k_mer_len - 1; i >= 0; i--) { 
            value1 <<= 2; 
            value1 += twoBitIntCoding(ref_code[i]);
        }
		if(k_mer_len>15)
			value2 = (int)(value1&(long)(max_arr_num - 1));
		else
			value2 = (int) value1;
        ref_loc[0] = ref_bucket[value2]; 
        ref_bucket[value2] = 0; 
        for (i = 1; i < step_len; i++) { 
            value1 >>= 2; 
            value1 += (twoBitIntCoding(ref_code[i + k_mer_len - 1])) << shift_bit_num;
			if(k_mer_len>15)
				value2 = (int)(value1&(long)(max_arr_num - 1));
			else
				value2 = (int) value1;
            ref_loc[i] = ref_bucket[value2]; //s_arr
            ref_bucket[value2] = i; //f_arr
        }
    }
	
	static void refSeqExt(String ref_path) {
        File f = new File(ref_path);
        ref_code = new char[max_cha_num];
        ref_code_len = 0;
        int i; 
        String str;
        char ch;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            str = br.readLine();
            for (i = 0; i < str.length(); i++) {
                ch = str.charAt(i);
                ch = Character.toUpperCase(ch);
                if (ch == 'A' || ch == 'C' || ch == 'G' || ch == 'T' || ch == 'U'){
                    ref_code[ref_code_len++] = ch;
                }
            }
			br.close();
        } catch (Exception e) {
             System.out.println(e);
        } 
    }
	
	static void seqCompress(int pool_size, int dr1) { 
		dr = dr1;
        String temp_dir_path = "tempFile/";  
        File hgc_file = new File("chr.rgc"); 
        if (hgc_file.exists()) hgc_file.delete(); 
        // Ensure the temporary directory exists
    	File tempDir = new File(temp_dir_path);
    	if (!tempDir.exists()) {
    	    tempDir.mkdir(); // Create the directory
    	}
        refSeqExt(seq_paths.get(0)); 
		//System.out.println("refSeqExt done!");
        kMerHash();
		//System.out.println("hashing done!");
        BufferedReader br = null;
        BufferedWriter bw1 = null,bw2 = null;
        try {
            multiThreading(temp_dir_path, pool_size); 
			//System.out.println("multithreading done!");
            File f;
            String str;
			int i, count=0, matchedLine;
            bw1 = new BufferedWriter(new FileWriter(hgc_file, true));
            for (i = 1; i < seq_number; i++) { 
                f = new File(temp_dir_path + "compSeq-" + i);
                br = new BufferedReader(new FileReader(f));
				
				matchedLine = 0;
				while ((str = br.readLine()) != null)
                    matchedLine++;
				br = new BufferedReader(new FileReader(f));
				str = br.readLine();
				bw1.write(str); //Here, bw1.write(str), Originally bw1.write(str+"\n");
				bw1.write((matchedLine-1)+"");
				while ((str = br.readLine()) != null)
                    bw1.write("\n" + str); 
				count++;
				if(count != seq_number - 1)
					bw1.write("\n");
            }
            deleteFile(new File(temp_dir_path));
			bw1.close();
			br.close();
        } catch (Exception e) { 
            System.out.println(e);
        }
		//System.out.println("seqCompress done!");
    }
	
	static int getPrimeNumber(int n) { 
        int next_prime = n + 1,i;
        boolean p = false;
        while (!p) {
            p = true;
            for (i = 2; i < Math.sqrt(n) + 1; ++i) {
                if (next_prime % i == 0) {
                    p = false;
                    break;
                }
            }
            if (!p) {
                next_prime++;
            }
        }
        return next_prime;
    }  
	
    // Modify beginingSettings to accept a List or array of sequence paths directly
    static void beginingSettings(List<String> seq, int opt_kmer_length) {
        seq_paths = seq;
        seq_number = seq_paths.size(); // Number of sequences provided
        sec_seq_num = 1;
		k_mer_len = opt_kmer_length;
	    kMer_bit_num = 2 * k_mer_len;
        seq_bucket_len = getPrimeNumber(vec_size);
        seq_bucket_vec = new ArrayList<>(seq_number);
        seq_loc_vec = new ArrayList<>(seq_number);
        match_result_vec = new ArrayList<>(sec_seq_num);
    }
}

