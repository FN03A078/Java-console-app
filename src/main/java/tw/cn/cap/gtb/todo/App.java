package tw.cn.cap.gtb.todo;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class App {

    private static final String TODO_PATH = System.getProperty("user.home") + File.separator + ".todo";
    private static final String TASKS_FILE = TODO_PATH + File.separator + "tasks";
    private static final String INIT_MSG = "Initialized successfully.";
    private static final String INIT_ERR_MSG = "Please run 'todo init' before running '%s' command.";
    private static List<Task> tasks;


    public static void main(String[] args) {
        if (args.length != 0) {
            App app = new App();
            if ("init".equals(args[0])) {
                app.init();
            } else if (!preCheck()) {
                //list
            } else {
                System.out.printf(INIT_ERR_MSG, args[0]);
            }
        }
    }

    private static boolean preCheck() {
        return Files.notExists(Path.of(TASKS_FILE));
    }

    private void readFile() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(TASKS_FILE))) {
            tasks = new ArrayList<>();
            String record;
            while ((record = bufferedReader.readLine()) != null) {
                storeList(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //isDone id taskName
    private void storeList(String record) {
        String[] split = record.split("\\s+", 3);
        tasks.add(new Task(
                Boolean.parseBoolean(split[0]),
                Integer.parseInt(split[1]),
                split[2]));
    }

    private void init() {
        try {
            if (!Files.exists(Path.of(TODO_PATH))) {
                Files.createDirectories(Path.of(TODO_PATH));
            }
            if (!Files.exists(Path.of(TASKS_FILE))) {
                Files.createFile(Path.of(TASKS_FILE));
            }
            System.out.println(INIT_MSG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
