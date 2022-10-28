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
    private static final String INIT_ERR_MSG = "Please run 'todo init' before running '%s' command.\n";

    //private static final String PARAS_ERR = "";

    //collection used to store records from local file
    private static List<Task> tasks;

    //variable used to indicate the newly added task id
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
                    case "mark" -> app.mark(app.getParas(args));
                    case "remove" -> app.remove(app.getParas(args));
                }
            } else {
                System.out.printf(INIT_ERR_MSG, args[0]);
            }
        }
    }


    /**
     * Check whether the user has executed the "init" command before,
     * if not, the tasks file will not exist.
     *
     * @return if "tasks" file not exists, return true, else false
     */
    private static boolean preCheck() {
        return Files.notExists(Path.of(TASKS_FILE));
    }


    /**
     * get the paras without command.
     *
     * @param args User's Command Line Parameters
     * @return an array contains the paras without command
     */
    private String[] getParas(String[] args) {
        return Arrays.stream(args).skip(1).toArray(String[]::new);
    }


    /**
     * get the paras without command then convert to String
     *
     * @param args User's Command Line Parameters
     * @return task name to be added
     */
    private String getTaskName(String[] args) {
        String[] paras = getParas(args);
        return String.join(" ", paras);
    }


    /**
     * read first line and assign to nextId,
     * then read remaining lines and store in list "tasks".
     */
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


    /**
     * Split parma "record" into isDone, id and taskName,
     * then store in list "tasks".
     *
     * @param record Each line read from the file
     */
    private void storeList(String record) {
        String[] split = record.split(" ",3);
        tasks.add(new Task(
                Boolean.parseBoolean(split[0]),
                Integer.parseInt(split[1]),
                split[2]));
    }


    /**
     * write the nextId at first line of "tasks" file,
     * Then write the records in "tasks" list to local file.
     */
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


    private void listByStatus(Map<Boolean, List<Task>> collect, Boolean flag) {
        if (collect.get(flag) != null) {
            for (Task task : collect.get(flag)) {
                System.out.println(task);
            }
        } else {
            System.out.println("Empty");
        }
    }


    // command method below

    /**
     * create "tasks" file then
     * print message when created successfully.
     */
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


    /**
     * list all tasks by isDone flag.
     */
    private void list() {
        Map<Boolean, List<Task>> collect =
                tasks.stream().collect(Collectors.groupingBy(Task::getDone));
        System.out.println("# To be done");
        listByStatus(collect, false);
        System.out.println("# Completed");
        listByStatus(collect, true);
    }


    /**
     * add new task which has auto-increment & unique id to list "tasks",
     * then write tasks file.
     *
     * @param taskName entered by the user
     */
    private void add(String taskName) {
        int taskId = nextId == null ? 1 : Integer.parseInt(nextId);
        nextId = String.valueOf(taskId + 1);
        tasks.add(new Task(false, taskId, taskName));
        writeFile();
    }


    /**
     * mark the finished task.
     *
     * @param markIds task ids to be marked
     */
    private void mark(String[] markIds) {
        if (markIds.length != 0 && !tasks.isEmpty()) {
            for (String markId : markIds) {
                for (Task task : tasks) {
                    try {
                        if (task.getId() == Integer.parseInt(markId)) {
                            task.setDone(true);
                        }
                    } catch (NumberFormatException e) {
                        //System.out.print(PARAS_ERR);
                    }
                }
            }
            writeFile();

        }
    }


    /**
     * delete tasks from tasks file,
     * this method will permanently delete the assigned tasks from local file.
     *
     * @param removeIds task ids to be removed
     */
    private void remove(String[] removeIds) {
        if (removeIds.length != 0 && !tasks.isEmpty()) {
            for (String removeId : removeIds) {
                try {
                    tasks.removeIf(task -> task.getId() == Integer.parseInt(removeId));
                } catch (NumberFormatException e) {
                    //System.out.print(PARAS_ERR);
                }
            }
            writeFile();
        }
    }
}
