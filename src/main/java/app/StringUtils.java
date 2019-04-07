package app;

import java.io.File;

public class StringUtils {
    public static String getFileExtension(File file) throws Exception{
        String fileName = file.toString();
        String substring = fileName.substring(fileName.lastIndexOf('.'));
        if (".txt".equals(substring)) {
            return substring;
        }

        if (!substring.equals(""))
            throw new Exception("Not .txt extension");
        return substring;
    }

}
