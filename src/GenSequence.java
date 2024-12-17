package hgc;
import java.util.*;
class GenSequence {
    int code_len; 
    char[] code;
    List<MatEntry> match_result;
    GenSequence() {
        int max_cha_num = 1 << 28, vec_size = 1 << 20; 
        code = new char[max_cha_num];
        match_result = new ArrayList<>(vec_size);
    }
    int getCodeLen() {
        return code_len;
    }
    void setCodeLen(int code_len1) {
        code_len = code_len1;
    }
    char[] getCode() {
        return code;
    }
    void setCode(char[] code1) {
        code = code1;
    }
    List<MatEntry> getMatchResult() {
        return match_result;
    }
    void setMatchResult(List<MatEntry> match_result1) {
        match_result = match_result1;
    }
}
