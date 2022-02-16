package file;

import graph.DependenciesGraph;
import task.TaskManager;

import java.io.Serializable;

public class SaveObject implements Serializable {
//    private static final long serialVersionUID = 2; // 29-Nov-2021
    private static final long serialVersionUID = 3; // 16-Feb-2021, removed max parallelism


    private DependenciesGraph dependenciesGraph;
    private TaskManager taskManagerDELETE;

    public SaveObject(DependenciesGraph graph, TaskManager taskManagerDELETE) {
        if (graph != null) {
            this.dependenciesGraph = graph;
        }

        this.taskManagerDELETE = taskManagerDELETE;
    }


    public DependenciesGraph getDependenciesGraph() {
        return dependenciesGraph;
    }

    public TaskManager getTaskManager() {
        return taskManagerDELETE;
    }
}