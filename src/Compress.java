//TARG Compression Code
package hgc;
import java.io.*;
class Compress{	
	static String info;
	static StringBuilder sb; //As there is no multi-threading, we are using it because it is faster due to not being thread-safe.
	static int dr = 1;
	
	//Strings to extended ASCII to modified RLE code
	static void stringToAsciiRleEncode(String inputFileName, int dr1){
		dr = dr1;
		int len, code_L = 0, i, k, step_len, code_ascii_value, rem_len;
		char ch;
		int[] two_bit_code;
		try{
			File f = new File(inputFileName);
			BufferedReader br = new BufferedReader(new FileReader(f));
            String outputFileName = inputFileName + ".targ";
			File f1 = new File(outputFileName);
			BufferedWriter bw = new BufferedWriter(new FileWriter(f1));
			while((info = br.readLine()) != null){
				len = info.length();
				two_bit_code = new int[len];
				//Converting to two_bit_code
				for (i = 0; i < len; i++) {
					ch = info.charAt(i);
					two_bit_code[code_L++] = twoBitIntCoding(ch);
				}
				sb = new StringBuilder(len);
		
				//Converting two_bit_code to extended ASCII code
				step_len = code_L/4;
				code_ascii_value = 0;
				for (i =0; i < step_len; i++) {
					code_ascii_value = 0;
					for (k = 3; k >= 0; k--) {
						code_ascii_value <<= 2;
						code_ascii_value += two_bit_code[4*i+k];
					}
					//For A, C, G, T/U and 49 (1) (Flag for RLE), and for value 10 and 13 
					if(code_ascii_value == 10 || code_ascii_value == 13|| code_ascii_value == 65 || code_ascii_value == 67 || code_ascii_value == 71 || code_ascii_value == 84|| code_ascii_value == 85 || code_ascii_value == 49) 
						for (k = 0; k <= 3; k++){
							if(two_bit_code[4*i+k] == 0)
								sb.append('A');
							else if(two_bit_code[4*i+k] == 1)
								sb.append('C');
							else if(two_bit_code[4*i+k] == 2)
								sb.append('G');
							else if(two_bit_code[4*i+k] == 3)
								if(dr == 1) //For DNA
									sb.append('T');
								else //For RNA
									sb.append('U');
						}
					else
						sb.append((char)code_ascii_value);
				}
				//If odd length i.e. 1,2,3 remains
				rem_len = code_L%4; 
				for (i = code_L-rem_len; i < code_L ; i++){
					if(two_bit_code[i] == 0)
						sb.append('A');
					else if(two_bit_code[i] == 1)
						sb.append('C');
					else if(two_bit_code[i] == 2)
						sb.append('G');
					else if(two_bit_code[i] == 3)
						if(dr == 1) //For DNA
							sb.append('T');
						else //For RNA
							sb.append('U');
				}
				info = sb.toString();
				//modified RLE
				info = modifiedRleEncode(info);
				bw.write(info);
				bw.write("\n");
			}
			br.close();
			bw.flush();
			info = null;
			//delFile(f);
		}catch(IOException e) {
            System.out.println(e);
        }	
	}
	
	//Character to 2-bit Integer Coding {A - 0, C - 1, G - 2, T/U - 3}
	static int twoBitIntCoding(char c) {
		int r;
        switch (c) {
            case 'A':	r = 0; break;
            case 'C':	r = 1; break;
            case 'G':	r = 2; break;
			case 'T':   r = 3; break;
            case 'U':	r = 3; break;
            default:	r = -1;
        }
		return r;
    }
	
	//Modified RLE 
    static String modifiedRleEncode(String info) { 
		char [] arrInfo = info.toCharArray();
		int len = info.length();
		sb = new StringBuilder(len);
        int i, j, count = 1;
		if (len > 0) { 
			sb.append(info.charAt(0)); 
			count = 1;
            for (i = 1; i < len; i++) { 
                if (arrInfo[i-1] == arrInfo[i]) {
                    count++; 
				}
                else {
					if(count >= 4 && count<= 13){
						sb.append('1').append(count - 4); 
					}else if(count > 13){
						sb.append("19");
						for(j = 0; j < (count-13); j++)
							sb.append(arrInfo[i-1]);
					}else{
						for(j = 1; j < count; j++)
							sb.append(arrInfo[i-1]); 
					}
					sb.append(arrInfo[i]);
					count = 1;
                }
            }
			if(count >= 4 && count<= 13){
				sb.append('1').append(count - 4); 
			}else if(count > 13){
				sb.append("19");
				for(j = 0; j < (count-13); j++)
					sb.append(arrInfo[i-1]);
			}else{
				for(j = 1; j < count; j++)
					sb.append(arrInfo[i-1]); 
			}
        }
		return sb.toString();
    }
	
	public static boolean delFile(File f) {
        if (!f.exists()) {
            return false;
        }
		if (f.isDirectory()) {
            File[] fs = f.listFiles();
            for (File f1 : fs) 
                delFile(f1);
        }
        return f.delete();
    }
}
