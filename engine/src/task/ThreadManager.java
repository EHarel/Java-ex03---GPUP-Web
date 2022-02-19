package task;

import logic.Engine;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager implements Serializable {
//    private static final long serialVersionUID = 1; // 09-Dec-2021 - Creation
    private static final long serialVersionUID = 2; // 13-Dec-2021 - Creation


    private int maxParallelism;
    private int currentParallelism;

    private transient ThreadPoolExecutor executor;
    private Integer remainingTasks;
    private Integer activeThreads;
    private Execution execution;



    /////////////////////////////


    public synchronized Boolean getPause() {
        return pause;
    }

    private void setPause(Boolean pause) {
        this.pause = pause;
    }

    private Boolean pause;





    /////////////////////////////


    public ThreadManager(int maxParallelism) {
        this.maxParallelism = maxParallelism;
        pause = Boolean.FALSE;

        prepareForNewExecution(1, null);
    }

    // TODO: should I add some validity checks and exceptions here?
    public void prepareForNewExecution(int threadCount, Execution execution) {
        setCurrentParallelism(threadCount);

        this.remainingTasks = 0;
        this.activeThreads = 0;
        this.execution = execution;
        this.pause = false;
    }

    public int getMaxParallelism() { return this.maxParallelism; }

    public synchronized void incrementRemainingTasks(long threadID) {
        synchronized (remainingTasks) {
            remainingTasks++;
            System.out.println("Remaining tasks incremented to " + remainingTasks + " by thread " + threadID + "...");
            System.out.println();
        }
//        System.out.println("Old incrementRemainingTasks - delete this");
    }

    public synchronized void decrementRemainingTasks(long threadID) {
        synchronized (remainingTasks) {
            remainingTasks--;
            System.out.println("Remaining tasks decremented to " + remainingTasks + " by thread " + threadID + "...");
            System.out.println();

        }

        checkEndState();
    }

    public synchronized void incrementActiveThreads(long threadID) {
        synchronized (activeThreads) {
            activeThreads++;
            System.out.println("Active threads incremented to " + activeThreads + " by thread " + threadID + "...");
            System.out.println();

        }
    }

    public synchronized void decrementActiveThreads(long threadID) {
        synchronized (activeThreads) {
            activeThreads--;
            System.out.println("Active threads decremented to " + activeThreads + " by thread " + threadID + "...");
            System.out.println();

        }

        checkEndState();
    }

    private void checkEndState() {
        synchronized (activeThreads) {
            synchronized (remainingTasks) {
                if (activeThreads == 0 && remainingTasks == 0 && executor.getQueue().isEmpty()) {
                    System.out.println(" ------------- DONE WORKING (thread manager print) ------------- ");
                    System.out.println();
                    Engine.getInstance().getTaskManager().getConsumerManager().getEndProcessConsumers().forEach(consumer -> consumer.accept(execution));

                }
            }
        }
    }

    /**
     * Adds a task to the queue and increments the remaining task count.
     */
    public void execute(Runnable command) {
        incrementRemainingTasks(Thread.currentThread().getId());

        executor.execute(command);
// TODO: old code. Delete? I tried analyzing the queue but it didn't work
//        synchronized (executor.getQueue()) {
//            long threadID = Thread.currentThread().getId();
//            remainingTasks = executor.getQueue().size();
//            System.out.println("Remaining tasks incremented to " + remainingTasks + " by thread " + threadID + "...");
//            System.out.println();
//        }
    }

    public void setExecutionData(Execution execution) {
        this.execution = execution;
    }

    public void pause(boolean isPause) {
        synchronized (pause) {
            if (!isPause) {
                pause.notifyAll();
            }

            pause = isPause;
        }
    }

    public int getCurrentParallelism() {
        return currentParallelism;
    }

    public void setCurrentParallelism(int threadCount) {
        if (validThreadCount(threadCount)) {
            if (executor == null) {
                executor = createNewExecutor(threadCount);
            } else {
                if (threadCount != executor.getCorePoolSize()) {
                    executor.setCorePoolSize(threadCount);
                    executor.setMaximumPoolSize(threadCount);
                }
            }

            currentParallelism = threadCount;
        }
    }

    private boolean validThreadCount(int threadCount) {
        boolean validThreadCount = true;

        if (threadCount < 1) {
            validThreadCount = false;
        }

        if (threadCount > maxParallelism) {
            validThreadCount = false;
        }

        return validThreadCount;
    }

    private ThreadPoolExecutor createNewExecutor(int threadCount) {
        ThreadPoolExecutor executor;

//        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
        executor = new ThreadPoolExecutor(threadCount,threadCount,60, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

        return executor;
    }
}
