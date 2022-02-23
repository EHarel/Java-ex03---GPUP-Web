package components.execution;

import com.google.gson.GsonBuilder;
import com.sun.org.apache.xerces.internal.xs.LSInputList;
import components.app.AppMainController;
import components.app.FileSystemUtils;
import components.execution.receivedtargets.TargetDTOWorkerDetails;
import components.execution.task.Task;
import components.execution.task.TaskFactory;
import components.execution.task.TaskUtils;
import graph.Target;
import graph.TargetDTO;
import httpclient.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import task.enums.TaskResult;
import task.enums.TaskType;
import task.configuration.*;
import utilsharedall.ConstantsAll;
import utilsharedall.UserType;
import utilsharedclient.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
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

        for (int i = 0; i < allTargetsUserWorkedOn.size(); i++) {
            TargetDTOWorkerDetails targetDTO = allTargetsUserWorkedOn.get(i);
            if (targetDTO.getName().equals(target.getName())) {
                if (targetDTO.getExecutionName().equals(target.getTaskStatus().getExecutionName())) {
                    TargetDTOWorkerDetails updatedDTO = new TargetDTOWorkerDetails(target.toDTO());
                    allTargetsUserWorkedOn.set(i, updatedDTO);
//                    updatedTarget = targetDTO;
//                    update(targetDTO, target);

                    mainController.targetHistoryListUpdated();
                    break;
                }
            }
        }

//        mainController.updateSpecificTarget(updatedTarget);
    }

    private void update(TargetDTOWorkerDetails targetDTO, Target target, String resLog) {
        targetDTO.setTaskResult(target.getTaskStatus().getTaskResult());
        targetDTO.setResLog(resLog);
    }

    public void doneTask(Target target, String formalizedTargetStr) {
        String fullLogPathStr = writeToFile(target.toDTO());
        updateTarget(target, formalizedTargetStr);
        mainController.decrementActiveThreadCount();
        updateServerAboutTaskResult(target, formalizedTargetStr, fullLogPathStr);
    }


    private String writeToFile(TargetDTO targetDTO) {
        Path fullDirPath = FileSystemUtils.getExecutionPath(targetDTO.getExecutionName());

        String fullLogPath = fullDirPath.toString() + "/" + targetDTO.getName() + ".log";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fullLogPath));
            writer.write(TaskUtils.getFormalizedTargetDataString(targetDTO));
            writer.close();
        } catch (IOException ignore) {
        } // Can't stop the execution for this

        return fullLogPath;
    }

    private void updateServerAboutTaskResult(Target target, String targetLog, String fullLogPathStr) {
        TaskResult taskResult = target.getTaskStatus().getTaskResult();
        String executionName = target.getTaskStatus().getExecutionName();
        String targetName = target.getName();

//        String bodyStr =
//                        ConstantsAll.BP_TASK_RESULT + "=" + taskResult + ConstantsAll.LINE_SEPARATOR +
//                        ConstantsAll.BP_EXECUTION_NAME + "=" + executionName + ConstantsAll.LINE_SEPARATOR +
//                        ConstantsAll.BP_TARGET_NAME + "=" + targetName + ConstantsAll.LINE_SEPARATOR +
//                        ConstantsAll.BP_TARGET_LOG + "=" + targetLog + ConstantsAll.LINE_SEPARATOR;

//        String bodyStr = targetLog;
        File selectedFile = new File(fullLogPathStr);
        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart("file1", selectedFile.getName(), RequestBody.create(selectedFile, MediaType.parse("text/plain")))
                        .build();

        String finalUrl = HttpUrl
                .parse(ConstantsAll.EXECUTION_TARGET_UPDATE)
                .newBuilder()
                .addQueryParameter(ConstantsAll.QP_TASK_RESULT, String.valueOf(taskResult))
                .addQueryParameter(ConstantsAll.QP_EXECUTION_NAME, executionName)
                .addQueryParameter(ConstantsAll.QP_TARGET_NAME, targetName)
                .build()
                .toString();

//        updateHttpStatusLine("New request is launched for: " + finalUrl); // Aviad Code


//        int taskPayment = getTaskPayment(target);


        HttpClientUtil.runAsync(finalUrl, body, new Callback() {
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
                String responseBody = response.body().string();
                if (response.code() != 200) {
//                    Platform.runLater(() ->
//                            executionSubmissionFailure());
                } else {

//                    String responseString = response.body().string();
//                    Integer payment = Integer.valueOf(responseString);

                    int payment = 5;

                    Platform.runLater(() -> {
                        addPaymentToTarget(target, payment);
                    });
                }
            }
        });
    }

    private void addPaymentToTarget(Target target, int payment) {
        if (targetFinishedWithSuccessOrWarnings(target)) {
            for (TargetDTOWorkerDetails targetDTOWorker :
                    allTargetsUserWorkedOn) {
                if (isSameExecutionTarget(targetDTOWorker, target)) {
                    targetDTOWorker.setPaycheck(payment);
                    mainController.addPayment(payment);
                    mainController.targetHistoryListUpdated();
                }
            }
        }
    }

    private boolean targetFinishedWithSuccessOrWarnings(Target target) {
        boolean finishedWithSuccessOrWarnings = false;

        if (target != null) {
            TaskResult taskResult = target.getTaskStatus().getTaskResult();

            switch (taskResult) {
                case SUCCESS:
                case SUCCESS_WITH_WARNINGS:
                    finishedWithSuccessOrWarnings = true;
                    break;
                case FAILURE:
                case UNPROCESSED:
                default:
                    finishedWithSuccessOrWarnings = false;
            }
        }

        return finishedWithSuccessOrWarnings;
    }

    private boolean isSameExecutionTarget(TargetDTOWorkerDetails targetDTOWorker, Target target) {
        if (targetDTOWorker == null || target == null) {
            return false;
        }

        if (!targetDTOWorker.getName().equals(target.getName())) {
            return false;
        }

        if (!targetDTOWorker.getExecutionName().equals(target.getTaskStatus().getExecutionName())) {
            return false;
        }

        return true;
    }

    private int getTaskPayment(Target target) {
        int payment = 0;

        if (targetFinishedWithSuccessOrWarnings(target)) {

        }


        return payment;
    }
}
