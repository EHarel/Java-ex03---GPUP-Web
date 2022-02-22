package components.execution;

import com.google.gson.GsonBuilder;
import components.app.AppMainController;
import components.execution.receivedtargets.TargetDTOWorkerDetails;
import components.execution.task.Task;
import components.execution.task.TaskFactory;
import graph.Target;
import graph.TargetDTO;
import httpclient.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import task.enums.TaskType;
import task.configuration.*;
import utilsharedall.ConstantsAll;
import utilsharedclient.Constants;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ExecutionManager {
    private final AppMainController mainController;
    private final int maxThreadCount;
    List<TargetDTOWorkerDetails> allTargetsUserWorkedOn;
    TargetFetcher targetFetcher;
    private BooleanProperty autoUpdate;
    private Timer timer;
    ThreadPoolExecutor executor;


    public ExecutionManager(int threadCount, AppMainController mainController) {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);

        this.maxThreadCount = threadCount;

        this.allTargetsUserWorkedOn = new LinkedList<>();

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
            allTargetsUserWorkedOn.add(new TargetDTOWorkerDetails(targetDTO));
        });

        mainController.targetHistoryListUpdated();

        targetDTOs.forEach(targetDTO -> {
            TaskType type = targetDTO.getTaskStatusDTO().getConfigData().getTaskType();
            Target target = Target.recreateTargetWithDependencyNamesOnly(targetDTO);

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
                    activeConfiguration,
                    this,
                    mainController);


            executor.execute(task);
        });
        System.out.println("[ExecutionManager - acceptNewTargets()] - End.");
    }

    public int getAvailableThreads() {

        int activeThreads = executor.getActiveCount();

        int availableThreads = maxThreadCount - activeThreads;

        return availableThreads;
    }

    public List<TargetDTOWorkerDetails> getTargets() {
        return allTargetsUserWorkedOn;
    }

    public synchronized void updateTarget(Target target, String formalizedTargetStr) {
        TargetDTOWorkerDetails updatedTarget = null;

        for (TargetDTOWorkerDetails targetDTO :
                allTargetsUserWorkedOn) {
            if (targetDTO.getName().equals(target.getName())) {
                if (targetDTO.getExecutionName().equals(target.getTaskStatus().getExecutionName())) {
                    updatedTarget = targetDTO;
                    update(targetDTO, target);
                    break;
                }
            }
        }

//        mainController.updateSpecificTarget(updatedTarget);
        mainController.targetHistoryListUpdated();
    }

    private void update(TargetDTOWorkerDetails targetDTO, Target target) {
        targetDTO.setTaskResult(target.getTaskStatus().getTaskResult());
    }

    public void doneTask(Target target, String formalizedTargetStr) {
        updateTarget(target, formalizedTargetStr);
        mainController.decrementActiveThreadCount();
        updateServerAboutTaskResult(target, formalizedTargetStr);
    }

    private void updateServerAboutTaskResult(Target target, String formalizedTargetStr) {
        String bodyStr =
                        ConstantsAll.BP_TASK_RESULT + "=" + target.getTaskStatus().getTaskResult() + ConstantsAll.LINE_SEPARATOR +
                        ConstantsAll.BP_EXECUTION_NAME + "=" + target.getTaskStatus().getExecutionName() + ConstantsAll.LINE_SEPARATOR +
                        ConstantsAll.BP_TARGET_NAME + "=" + target.getName() + ConstantsAll.LINE_SEPARATOR;

        String finalUrl = HttpUrl
                .parse(ConstantsAll.EXECUTION_TARGET_UPDATE)
                .newBuilder()
                .build()
                .toString();

//        updateHttpStatusLine("New request is launched for: " + finalUrl); // Aviad Code

        HttpClientUtil.runAsync(finalUrl, bodyStr, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                showFileLoadingErrorMessage("Something went wrong with the file upload!");

//                        Platform.runLater(() ->
//                                resultMessageProperty.set("Something went wrong: " + e.getMessage()));
//                Platform.runLater(() ->
//                        executionSubmissionFailure());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                String responseBody = response.body().string();
//                if (response.code() != 200) {
//                    Platform.runLater(() ->
//                            executionSubmissionFailure());
//                } else {
//                    Platform.runLater(() -> {
//                        executionSubmissionSuccess();
//                    });
//                }
            }
        });
    }
}
