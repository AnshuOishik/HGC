package hgc;
import java.io.*;
class ExtThread extends Thread {
    int seq_num;
    String temp_dir_path;
    ExtThread(int seq_num1, String temp_dir_path1) {
        seq_num = seq_num1;
        temp_dir_path = temp_dir_path1;
    }
    public void run() {
        GenSequence seq = new GenSequence();
        HGCCompress.tarSeqExt(seq_num, seq); 
        HGCCompress.firstLevelMatch(seq);
        if (seq_num <= HGCCompress.sec_seq_num) { 
            HGCCompress.match_result_vec.add(seq.getMatchResult());
            HGCCompress.matResHash(seq.getMatchResult());
        }
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(temp_dir_path + "compSeq-" + seq_num));
            if (seq_num != 1) { 
                if (seq.getMatchResult().size() < 3)
                    HGCCompress.saveFirstMatRes(bw, seq.getMatchResult());
                HGCCompress.secondLevelMatch(bw, seq.getMatchResult(), seq_num);
            } else {
                HGCCompress.saveFirstMatRes(bw, seq.getMatchResult());
            }
            seq.setMatchResult(null);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
