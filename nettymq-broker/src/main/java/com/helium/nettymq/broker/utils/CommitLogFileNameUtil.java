package com.helium.nettymq.broker.utils;

public class CommitLogFileNameUtil {

    public static String buildFirstCommitLogName() {
        return "00000000";
    }

    public static String incrCommitLogFileName(String oldFileName) {
        if (oldFileName.length() != 8) {
            throw new IllegalArgumentException("fileName must has 8 chars");
        }
        Long fileIndex = Long.valueOf(oldFileName);
        fileIndex++;
        String newFileName = String.valueOf(fileIndex);
        int newFileNameLen = newFileName.length();
        int needFullLen = 8 - newFileNameLen;
        if (needFullLen < 0) {
            throw new RuntimeException("unKnow fileName error");
        }
        StringBuffer stb = new StringBuffer();
        for (int i = 0; i < needFullLen; i++) {
            stb.append('0');
        }
        stb.append(newFileName);
        return stb.toString();
    }

    public static void main(String[] args) {
        String newFileName = CommitLogFileNameUtil.incrCommitLogFileName("00000011");
        System.out.println(newFileName);
    }
}
