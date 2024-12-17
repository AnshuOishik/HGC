//TARG Decompression Code
package hgc;
import java.io.*;
class Decompress{
	static String info;
	static StringBuilder sb;
	static int dr = 1;
	
	public static void stringToAsciiRleDecode(String inputFileName, int dr1)throws IOException{
		dr = dr1;
		try{
			String compressedFileName = inputFileName + ".targ";
			File fr = new File(compressedFileName);
			BufferedReader br = new BufferedReader(new FileReader(fr));
			File fw = new File(inputFileName);
			BufferedWriter bw = new BufferedWriter(new FileWriter(fw));
			while((info = br.readLine()) != null){
				info = modifiedRleDecode(info);
				info = asciiCodeToString(info);
				bw.write(info);
				bw.write("\n");
			}
			br.close();
			bw.flush();
			info = null;
			delFile(fr);
		}catch(IOException e) {
            System.out.println(e);
        }
	}
	
	//Modified RLE 
    static String modifiedRleDecode(String info) { 
		char [] arrInfo = info.toCharArray();
		int len = info.length();
		sb = new StringBuilder(len);
        int i, j, count;
		if (len > 0) { 
			sb.append(arrInfo[0]); 
            for (i = 1; i < len; i++) { 
                if(arrInfo[i] == '1'){
					i++;
					count = arrInfo[i] - 48 + 4;
					for(j = 1; j < count; j++)
						sb.append(arrInfo[i-2]);
				}
				else
					sb.append(arrInfo[i]); 
            }
        }
		return sb.toString();
    }
	
	static String asciiCodeToString(String str){
		int L = str.length();
		StringBuilder sb = new StringBuilder(L);
		int av, r, count, i, j;
		char ch;
        for (i = 0; i < L; i++) {
			ch = str.charAt(i);
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
					else if(r == 3)
						if(dr == 1) //For DNA
							sb.append('T');
						else //For RNA
							sb.append('U');
					count++;
				}
				if(av == 0)
					sb.append('A');
				else if(av == 1)
					sb.append('C');
				else if(av == 2)
					sb.append('G');
				else if(av == 3)
					if(dr == 1) //For DNA
						sb.append('T');
					else //For RNA
						sb.append('U');
				count++;
				//To make it 4 digit 
				for(j=0;j<4-count;j++)
					sb.append('A');
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
