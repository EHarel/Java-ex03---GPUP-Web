package task.consumer;

import graph.Target;
import graph.TargetDTO;

import java.util.function.Consumer;

public class ConsumerUpdateState implements Consumer<Target> {
    private final TargetDTO.TargetState stateToUpdate;

    public ConsumerUpdateState(TargetDTO.TargetState stateToUpdate) {
        this.stateToUpdate = stateToUpdate;
    }

    @Override
    public void accept(Target target) {
        if (target != null) {
            if (target.getTaskStatus().getTargetState() != TargetDTO.TargetState.FINISHED) {
                target.getTaskStatus().setTargetState(stateToUpdate);
            }
        }
    }
}