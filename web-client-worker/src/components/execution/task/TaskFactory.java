package components.execution.task;

import exception.InvalidInputRangeException;
import graph.DependenciesGraph;
import graph.Target;
import task.TaskType;
import task.configuration.Configuration;
import task.configuration.ConfigurationDTO;

import java.util.HashMap;
import java.util.Map;

public class TaskFactory {
//    public static Map<TaskType, TaskProcess> init(DependenciesGraph dependenciesGraph) {
//        Map<TaskType, TaskProcess> tasks = new HashMap<>();
//
//        try {
//            tasks.put(TaskType.SIMULATION, getTaskGeneral(TaskType.SIMULATION, dependenciesGraph, null));
//            tasks.put(TaskType.COMPILATION, getTaskGeneral(TaskType.COMPILATION, dependenciesGraph, null));
//        } catch (InvalidInputRangeException ignore) { // Cannot receive exception on null configuration
//        }
//
//        return tasks;
//    }

//    public static TaskProcess getTaskGeneral(TaskType taskType, DependenciesGraph dependenciesGraph, Configuration configuration) throws InvalidInputRangeException {
//        TaskProcess newTaskProcess = null;
//
//        switch (taskType) {
//            case SIMULATION:
//                newTaskProcess = new TaskProcess(TaskType.SIMULATION, dependenciesGraph, configuration);
//                break;
//            case COMPILATION:
//                newTaskProcess = new TaskProcess(TaskType.COMPILATION, dependenciesGraph, configuration);
//                // TODO: open folder here?
//                break;
//        }
//
//        return newTaskProcess;
//    }

    public static Task getActualTask(TaskType taskType,
                                     Target target,
                                     Configuration configuration) {
        Task task = null;

        switch (taskType) {
            case SIMULATION:
                task = new TaskSimulation(target, configuration);
                break;
            case COMPILATION:
                task = new TaskCompilation(target, configuration);
                break;
        }

        return task;
    }
}