package graph;

import task.TaskType;

public class PricingDTO {
    TaskType taskType;
    int price;

    public PricingDTO(TaskType taskType, int price) {
        this.taskType = taskType;
        this.price = price;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public int getPrice() {
        return price;
    }
}
