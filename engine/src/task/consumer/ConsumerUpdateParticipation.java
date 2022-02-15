package task.consumer;

import graph.Target;

import java.util.function.Consumer;

public class ConsumerUpdateParticipation implements Consumer<Target> {
    private final boolean participates;

    public ConsumerUpdateParticipation(boolean participates) {
        this.participates = participates;
    }

    @Override
    public void accept(Target target) {
        if (target != null) {
            target.setParticipatesInExecution(participates);
        }
    }
}
