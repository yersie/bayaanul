package yersih.bayaanul.pdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ReverseDhivehi {

    static String reverse(String s) {

        if (isDhivehi(s)) {
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

    static String reverseUnicode(String s) {
        if (isDhivehi(s)) {
            s = "\u202B" + s;
        }
        return s;
    }

    private static boolean isDhivehi(String s) {
        return !s.matches(".*[a-zA-Z].*");
    }

}
