package components.execution.task;

import components.app.AppMainController;
import components.execution.ExecutionManager;
import graph.Target;
import task.enums.TaskType;
import task.configuration.Configuration;

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
                                     Configuration configuration,
                                     ExecutionManager executionManager,
                                     AppMainController mainController) {
        Task task = null;

        switch (taskType) {
            case SIMULATION:
                task = new TaskSimulation(target, configuration, executionManager, mainController);
                break;
            case COMPILATION:
                task = new TaskCompilation(target, configuration, executionManager, mainController);
                break;
        }

        return task;
    }
}