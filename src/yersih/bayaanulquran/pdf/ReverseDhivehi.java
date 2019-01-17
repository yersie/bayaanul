package yersih.bayaanulquran.pdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReverseDhivehi {

    public static String reverse(String s) {

        if (!isEnglish(s)) {
            List<Character> characters = new ArrayList<>();
            for (char c : s.toCharArray()) {
                characters.add(c);
            }
            Collections.reverse(characters);

            StringBuilder sb = new StringBuilder();
//            sb.append("\u202B");
            for (Character c : characters) {
                sb.append(c);
            }
            return sb.toString();
        }
        return s;
    }

    public static String reverseUnicode(String s) {
        if (!isEnglish(s)) {
            s = "\u202B" + s;
        }
        return s;
    }

    private static boolean isEnglish(String s) {
        return s.matches(".*[a-zA-Z].*");
    }

}
