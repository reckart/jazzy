package com.swabunga.util;

public class StringUtility{
    public static StringBuffer replace(StringBuffer buf, int start, int end, String text){
        int len = text.length();
        char[] ch = new char[buf.length() + len - (end - start)];
        buf.getChars(0, start, ch, 0);
        text.getChars(0, len, ch, start);
        buf.getChars(end, buf.length(), ch, start + len);
        buf.setLength(0);
        buf.append(ch);
        return buf;
    }

    public static void main(String[] args){
        System.out.println(StringUtility.replace(new StringBuffer(args[0]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[1]));
    }
}
