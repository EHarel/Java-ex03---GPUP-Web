package datastructure;

import graph.TargetDTO;
import task.OldCode.Task;
import task.consumer.ConsumerManager;

import java.time.Instant;

/**
 * This queue updates a target's state to "WAITING" upon enqueue.
 */
public class TaskQueueStateUpdate extends QueueLinkedList<Task> {
    ConsumerManager consumerManager;

    public TaskQueueStateUpdate(ConsumerManager taskManager) {
        super();

        this.consumerManager = taskManager;
    }

    @Override
    public void enqueue(Task newValue) {
        newValue.getTarget().getTaskStatus().setTargetState(TargetDTO.TargetState.WAITING);
        newValue.getTarget().setTimeOfEntryIntoTaskQueue(Instant.now());

        if (consumerManager != null) {
            consumerManager.getTargetStateChangedConsumers().forEach(targetDTOConsumer -> {
                targetDTOConsumer.accept(newValue.getTarget().toDTO());
            });
        }

        super.enqueue(newValue);
    }
}