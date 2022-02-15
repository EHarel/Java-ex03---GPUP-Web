package task.consumer;

import graph.DependenciesGraph;
import graph.Target;
import graph.TargetDTO;
import task.ExecutionData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * This class is responsible for collecting all the different consumers for the different stages of a task's life.
 * The different stages which allow a consumer are:
 *  (*) When the entire task process starts
 *  (*) When a target processing starts
 *  (*) When a target processing ends
 *  (*) When the entire task process ends
 */
public class ConsumerManager implements Serializable {
    private static final long serialVersionUID = 1; // 08-Dec-2021

    Collection<Consumer<Target>> startProcessConsumers; // TODO: change to null? String? Something else? Target makes no sense
    Collection<Consumer<TargetDTO>> startTargetConsumers;
    Collection<Consumer<TargetDTO>> endTargetConsumers;
    Collection<Consumer<ExecutionData>> endProcessConsumers;
    Collection<Consumer<TargetDTO>> targetStateChangedConsumers;

    Collection<Consumer<DependenciesGraph>> startTargetProcessingConsumers;

    public ConsumerManager() {
        startProcessConsumers = new ArrayList<>();
        startTargetConsumers = new ArrayList<>();
        endTargetConsumers = new ArrayList<>();
        endProcessConsumers = new ArrayList<>();

        targetStateChangedConsumers = new LinkedList<>();
        startTargetProcessingConsumers = new LinkedList<>();
    }

    public Collection<Consumer<Target>> getStartProcessConsumers() {
        return startProcessConsumers;
    }

    public void setStartProcessConsumers(Collection<Consumer<Target>> startProcessConsumers) {
        this.startProcessConsumers = startProcessConsumers;
    }

    public Collection<Consumer<TargetDTO>> getStartTargetConsumers() {
        return startTargetConsumers;
    }

    public void setStartTargetConsumers(Collection<Consumer<TargetDTO>> startTargetConsumers) {
        this.startTargetConsumers = startTargetConsumers;
    }

    public Collection<Consumer<TargetDTO>> getEndTargetConsumers() {
        return endTargetConsumers;
    }

    public void setEndTargetConsumers(Collection<Consumer<TargetDTO>> endTargetConsumers) {
        this.endTargetConsumers = endTargetConsumers;
    }

    public Collection<Consumer<ExecutionData>> getEndProcessConsumers() {
        return endProcessConsumers;
    }

    public void setEndProcessConsumers(Collection<Consumer<ExecutionData>> endProcessConsumers) {
        this.endProcessConsumers = endProcessConsumers;
    }

    public Collection<Consumer<TargetDTO>> getTargetStateChangedConsumers() {
        return targetStateChangedConsumers;
    }

    public Collection<Consumer<DependenciesGraph>> getStartTargetProcessingConsumers() {
        return startTargetProcessingConsumers;
    }
}
