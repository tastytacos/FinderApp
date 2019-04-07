package app;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.swing.*;

public class Clock extends JLabel implements Runnable {
    private MainWindow mainWindow;
    private boolean running = true;
    private DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");

    public void stop() {
        this.running = false;
    }

    Clock(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void start() {
        this.running = true;
    }

    @Override
    public void run() {
        while (running) {
            LocalTime time = new LocalTime();
            mainWindow.setTime(fmt.print(time));
        }
    }
}
