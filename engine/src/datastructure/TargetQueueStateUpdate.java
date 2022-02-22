package datastructure;

import graph.Target;
import graph.TargetDTO;

import java.time.Instant;

/**
 * This queue updates a target's state to "WAITING" upon enqueue.
 */
public class TargetQueueStateUpdate extends QueueLinkedList<Target> {

    @Override
    public void enqueue(Target target) {
        target.getTaskStatus().setTargetState(TargetDTO.TargetState.WAITING);
        target.setTimeOfEntryIntoTaskQueue(Instant.now());

        super.enqueue(target);
    }
}
