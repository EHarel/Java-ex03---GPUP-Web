package console.task.consumer;

import graph.TargetDTO;
import logic.Engine;

import java.io.Serializable;
import java.util.function.Consumer;

public class ConsumerPrintTarget implements Consumer<TargetDTO>, Serializable {
//    private static final long serialVersionUID = 2;
//    private static final long serialVersionUID = 3; // 24-Nov-2021
    private static final long serialVersionUID = 4; // 08-Dec-2021, name change

    @Override
    public void accept(TargetDTO targetDTO) {
        System.out.println(Engine.getInstance().getFormalizedTargetDataString(targetDTO) + System.lineSeparator());
    }
}