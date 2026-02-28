package hgc;
import java.io.*;
import java.util.*;
class HGCDecompress {
    static int max_cha_num = 1 << 28, k_mer_len; 
    static int vec_size = 1 << 20, min_rep_len; 
    static int seq_number, sec_seq_num;
    static int ref_code_len;
    static char[] ref_code;
	static List<String> seq_paths;
    static List<List<MatEntry>> match_res_vec;
    static BufferedReader br1;
    static BufferedWriter bw;
    static int dr = 1;
	
	static void saveSeqFile(GenSequence seq, int seq_num) {
        String str = seq_paths.get(seq_num);
		//System.out.println(str);
        String out_folder_path = "Output/";
        File f = new File(out_folder_path + str);
        if (f.exists())
            f.delete();
        int code_len = seq.getCodeLen();
        int i;
        char[] c_seq = seq.getCode();
        try {
            bw = new BufferedWriter(new FileWriter(f, true));
            for (i = 0; i < code_len; i ++) {
                bw.write(c_seq[i]);
            }
           bw.flush();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
	
	static String asciiCodeToString(String mis_str){
		int L = mis_str.length();
		StringBuilder sb = new StringBuilder(L);
		int av, r, count, i;
		char ch;
        for (i = 0; i < L; i++) {
			ch = mis_str.charAt(i);
			//ASCII value to base 4 convertion
			av=(int)ch;
			if(ch == 'A' || ch == 'C' || ch == 'G' || ch == 'T' || ch == 'U')
				sb.append(ch);
			else{
				count=0;
				while(av>3){ 
					r=av%4; 
					av=av/4;
					if(r == 0)
						sb.append('A');
					else if(r == 1)
						sb.append('C');
					else if(r == 2)
						sb.append('G');
					else if(r == 3){
						if(dr == 1) //For DNA
							sb.append('T');
						else //For RNA
							sb.append('U');
					}
					count++;
				}
				if(av == 0)
					sb.append('A');
				else if(av == 1)
					sb.append('C');
				else if(av == 2)
					sb.append('G');
				else if(av == 3){
					if(dr == 1) //For DNA
						sb.append('T');
					else //For RNA
						sb.append('U');
				}
				count++;
				//To make it 4 digit 
				for(int j=0;j<4-count;j++)
					sb.append('A');
			}
		}
		return sb.toString();
	}
	
	static void readCompTarSeq(GenSequence seq) {
        List<MatEntry> mat_res = seq.getMatchResult();
        int pos, pre_pos = 0, cur_pos, l;
        String mis_str;
        char[] code_arr = new char[max_cha_num];
        int i, j, k, n, code_len = 0;
        for (i = 0; i < mat_res.size(); i++) {
			mis_str = asciiCodeToString(mat_res.get(i).getMisMatStr());
            for (j = 0; j < mis_str.length(); j++){
				code_arr[code_len++] = mis_str.charAt(j);
			}
            pos = mat_res.get(i).getPos();
            cur_pos = pos + pre_pos;
			if(mat_res.get(i).getLen() == -1) //No match
				l = 0;
            else
				l = mat_res.get(i).getLen() + min_rep_len;
            pre_pos = cur_pos + l;

            for (k = cur_pos, n = 0; n < l; k++, n++)
                code_arr[code_len++] = ref_code[k];
        }
        seq.setCodeLen(code_len);
        seq.setCode(code_arr);
    }
	
	static void getMatRes(int id, int pos, int l, List<MatEntry> mat_res) {
        for (int i = 0; i < l; i++){
            mat_res.add(match_res_vec.get(id).get(pos++));
		}
    }
	
	static void readMatRes(GenSequence seq) {
        List<MatEntry> mat_res = new ArrayList<>(vec_size);
        MatEntry mat_ent = new MatEntry();
        String s;
        String[] str;
        int id, pos, l, pre_seq_id = 0, pre_pos = 0,sLen=0;
        try {
			s = br1.readLine();
			sLen = Integer.parseInt(s);
			for(int i=0; i<sLen; i++){
                s = br1.readLine();
				//str = s.split("\\s+"); //Regular expression for one (\s) or more space (\s+), not working!!!
				str = s.split(" ");
				try{
					id = Integer.valueOf(str[0]);
					pos = Integer.valueOf(str[1]);
                    l = Integer.valueOf(str[2]);
                    id += pre_seq_id;
                    pre_seq_id = id;
                    pos += pre_pos;
                    l += 2;
                    pre_pos = pos + l;
                    getMatRes(id, pos, l, mat_res);
				}
				catch(NumberFormatException e){
					mat_ent.setMisMatStr(str[0]);
					pos = Integer.valueOf(str[1]);
                    l = Integer.valueOf(str[2]);
                    mat_ent.setPos(pos);
                    mat_ent.setLen(l);
                    mat_res.add(mat_ent);
                    mat_ent = new MatEntry();
				}
            }
            seq.setMatchResult(mat_res);
        } catch (IOException e) {
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
                if (ch == 'A' || ch == 'C' || ch == 'G' || ch == 'T' || ch == 'U'){
                    ref_code[ref_code_len++] = ch;
				}
			}
            br.close();
        } catch (IOException e) {
            System.out.println(e);
        } 
    }

	static void seqDecompress(int dr1) {
		dr = dr1;
        String hgc_path = "chr.rgc";    //stored the base info of genomes
        File hgc_file = new File(hgc_path);
        try {
            br1 = new BufferedReader(new FileReader(hgc_path));
            refSeqExt(seq_paths.get(0));
            GenSequence seq = new GenSequence();
            String out_folder_path = "Output/";
            File out_folder = new File(out_folder_path);
            if (out_folder.exists()) 
                deleteFile(out_folder);
            out_folder.mkdir();
            for (int i = 1; i < seq_number; i++) {
                readMatRes(seq);
                if (i <= sec_seq_num && i != seq_number - 1)
                    match_res_vec.add(seq.getMatchResult());
                readCompTarSeq(seq);
                saveSeqFile(seq, i);
                seq = new GenSequence();
            }
			br1.close();
			bw.close();
        } catch (IOException e) { 
            System.out.println(e);
        } 
		deleteFile(hgc_file);
	}
		
	//BSC Decompression
	public static void bscDecompression() {
        try {
            String bscCommand = "./bsc d " + "HGC.bsc " + "TarC.tar"; //Linux, bsc is the generated executable file name of the bsc compressor 
            //String bscCommand = "bsc d " + "HGC.bsc " + "TarC.tar"; //Windows
			Process p1 = Runtime.getRuntime().exec(bscCommand);
            p1.waitFor();
            String tarCommand = "tar -xf " + "TarC.tar";
            Process p2 = Runtime.getRuntime().exec(tarCommand);
            p2.waitFor();
			deleteFile(new File("TarC.tar"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
	
	// 7-zip Decompression
	public static void sevenZipDecompression() {
		try{
			//Please use the following for Linux platform
			String unzip = "7za e " + "ZipC.7z" + " -aos";
			//Please use the following for Windows platform
			//String unzip = "7z e " + "ZipC.7z" + " -aos";
			Process p = Runtime.getRuntime().exec(unzip);
			p.waitFor();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
    static void beginingSettings(List<String> seq, int opt_kmer_length) {
        seq_paths=seq;
        seq_number = seq_paths.size(); // Number of sequences provided
		sec_seq_num = 1;
		k_mer_len = opt_kmer_length;
	    min_rep_len = k_mer_len+1;
	    match_res_vec = new ArrayList<>(sec_seq_num);
    }
}       
