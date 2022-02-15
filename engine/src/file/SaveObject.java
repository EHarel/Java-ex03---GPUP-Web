package file;

import graph.DependenciesGraph;
import task.TaskManager;

import java.io.Serializable;

public class SaveObject implements Serializable {
//    private static final long serialVersionUID = 1;
    private static final long serialVersionUID = 2; // 29-Nov-2021


    private DependenciesGraph dependenciesGraph;
    private TaskManager taskManagerDELETE;
    private int maxParallelism;

    public SaveObject(DependenciesGraph graph, TaskManager taskManagerDELETE, int maxParallelism) {
        if (graph != null) {
            this.dependenciesGraph = graph;
        }

        this.taskManagerDELETE = taskManagerDELETE;
        this.maxParallelism = maxParallelism;
    }

    public int getMaxParallelism() {
        return maxParallelism;
    }

    public DependenciesGraph getDependenciesGraph() {
        return dependenciesGraph;
    }

    public TaskManager getTaskManager() {
        return taskManagerDELETE;
    }
}