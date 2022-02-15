package console.task.consumer;

import graph.TargetDTO;

import java.io.Serializable;
import java.util.function.Consumer;

public class ConsumerStartProcessingUI implements Consumer<TargetDTO>, Serializable {
    private static final long serialVersionUID = 1; // 13-Dec-2021, creation

    @Override
    public void accept(TargetDTO targetDTO) {
        System.out.println("Thread " + Thread.currentThread().getId() +
                ". Starting to process " + targetDTO.getName() + " . . . \n");
    }
}
