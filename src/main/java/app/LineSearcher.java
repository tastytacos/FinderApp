package app;

import sun.awt.windows.ThemeReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class LineSearcher implements Callable<List<String>> {

    private File file;
    private String keyWord;

    LineSearcher(File file, String keyWord) {
        this.file = file;
        this.keyWord = keyWord;
    }

    private List<String> findLines(File file, String keyWord) {
        List<String> strings = new ArrayList<>();
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            String line;
            System.out.println("Thread " + Thread.currentThread().getName() + " is checking");
            while ((line = randomAccessFile.readLine()) != null) {
                if (line.contains(keyWord)) {
                    System.out.println(line);
                    strings.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings;
    }

    @Override
    public List<String> call() throws Exception {
        return findLines(file, keyWord);
    }
}
