package main;

import networking.TaskServer;
import ui.TaskManagerFX;

public class Main {
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                TaskServer.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        TaskManagerFX.main(new String[]{});
    }
}
