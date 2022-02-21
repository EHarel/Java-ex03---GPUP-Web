package components.execution;

import componentcode.executiontable.ExecutionListRefresher;
import components.app.AppMainController;
import components.execution.task.Task;
import components.execution.task.TaskFactory;
import graph.Target;
import graph.TargetDTO;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import task.TaskType;
import task.configuration.*;
import utilsharedclient.Constants;

import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ExecutionManager {
    private final AppMainController mainController;
    private final int maxThreadCount;
    Queue<TargetDTO> targetsToWorkOn;
    TargetFetcher targetFetcher;
    private BooleanProperty autoUpdate;
    private Timer timer;
    ThreadPoolExecutor executor;


    public ExecutionManager(int threadCount, AppMainController mainController) {


        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);

        this.maxThreadCount = threadCount;


        targetsToWorkOn = new ArrayBlockingQueue<TargetDTO>(threadCount);
        autoUpdate = new SimpleBooleanProperty();

        this.mainController = mainController;

        setActive(true);
        startTargetFetcher();
    }

    public void setActive(boolean isActive) {
        getAutoUpdateProperty().set(isActive);
    }

    public BooleanProperty getAutoUpdateProperty() {
        return autoUpdate;
    }


    public void startTargetFetcher() {
        targetFetcher = new TargetFetcher(
                autoUpdate,
                null, // Aviad code sent something else here
                null, // TODO: change this?
                this,
                mainController);
        timer = new Timer();
        timer.schedule(targetFetcher, Constants.REFRESH_RATE_TARGETS, Constants.REFRESH_RATE_TARGETS);
    }


    public void acceptNewTargets(List<TargetDTO> targetDTOs) {
        System.out.println("[ExecutionManager - acceptNewTargets()] - Start.");

        targetDTOs.forEach(targetDTO -> {
            TaskType type = targetDTO.getTaskStatusDTO().getConfigData().getTaskType();
            Target target = Target.recreateTargetWithoutDependencies(targetDTO);

            Configuration activeConfiguration = null;
            ConfigurationDTO configDTO = targetDTO.getTaskStatusDTO().getConfigData();

            try {
                switch (configDTO.getTaskType()) {
                    case SIMULATION:
                        activeConfiguration = new ConfigurationSimulation((ConfigurationDTOSimulation) configDTO);
                        break;
                    case COMPILATION:
                        activeConfiguration = new ConfigurationCompilation((ConfigurationDTOCompilation) configDTO);
                        break;
                }
            } catch (Exception ignore) {
            }

            Task task = TaskFactory.getActualTask(
                    type,
                    target,
                    activeConfiguration);


//            executor.execute(task);
        });
        System.out.println("[ExecutionManager - acceptNewTargets()] - End.");
    }

    public int getAvailableThreads() {

        int activeThreads = executor.getActiveCount();

        int availableThreads = maxThreadCount - activeThreads;

        return availableThreads;
    }
}
