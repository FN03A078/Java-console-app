package tw.cn.cap.gtb.todo;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class App {

    private static final String TODO_PATH = System.getProperty("user.home") + File.separator + ".todo";
    private static final String TASKS_FILE = TODO_PATH + File.separator + "tasks";
    private static final String INIT_MSG = "Initialized successfully.";
    private static final String INIT_ERR_MSG = "Please run 'todo init' before running '%s' command.";
    private static List<Task> tasks;
    private static String nextId;


    public static void main(String[] args) {
        if (args.length != 0) {
            App app = new App();
            if ("init".equals(args[0])) {
                app.init();
            } else if (!preCheck()) {
                app.readFile();
                switch (args[0]) {
                    case "list" -> app.list();
                    case "add" -> app.add(app.getTaskName(args));
                }
            } else {
                System.out.printf(INIT_ERR_MSG, args[0]);
            }
        }
    }

    private static boolean preCheck() {
        return Files.notExists(Path.of(TASKS_FILE));
    }

    private String[] getParas(String[] args) {
        return Arrays.stream(args).skip(1).toArray(String[]::new);
    }

    private String getTaskName(String[] args) {
        String[] paras = getParas(args);
        return String.join(" ", paras);
    }


    private void readFile() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(TASKS_FILE))) {
            nextId = bufferedReader.readLine();
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

    private void list() {
        Map<Boolean, List<Task>> collect =
                tasks.stream().collect(Collectors.groupingBy(Task::getDone));
        System.out.println("# To be done");
        listByStatus(collect, false);
        System.out.println("# Completed");
        listByStatus(collect, true);
    }

    private void listByStatus(Map<Boolean, List<Task>> collect, Boolean flag) {
        if (collect.get(flag) != null) {
            for (Task task : collect.get(flag)) {
                System.out.println(task);
            }
        } else {
            System.out.println("Empty");
        }
    }

    private void add(String taskName) {
        int taskId = nextId == null ? 1 : Integer.parseInt(nextId);
        nextId = String.valueOf(taskId + 1);
        tasks.add(new Task(false,taskId,taskName));
        writeFile();
    }

    private void writeFile() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(TASKS_FILE))) {
            bufferedWriter.write(nextId);
            bufferedWriter.newLine();
            for (Task task : tasks) {
                String str = task.getDone() + " " + task.getId() + " " + task.getTaskName();
                bufferedWriter.write(str);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
