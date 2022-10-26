package tw.cn.cap.gtb.todo;

/**
 * @Author: SHAN
 * @Description:
 * @Date: created in 14:40 2022/10/26
 */
public class Task {
    private Boolean isDone;
    private final Integer id;
    private final String taskName;

    public Task(Boolean isDone, Integer id, String taskName) {
        this.isDone = isDone;
        this.id = id;
        this.taskName = taskName;
    }

    public Boolean getDone() {
        return isDone;
    }

    public Integer getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    @Override
    public String toString() {
        return getId() + " " + getTaskName();
    }
}
