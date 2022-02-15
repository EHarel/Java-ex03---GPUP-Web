package console.task.analysis.report;

import console.Utils;
import console.menu.system.DoesAction;
import graph.TargetDTO;
import logic.Engine;
import task.ExecutionData;

import java.util.Optional;

public class ReportSpecificTarget implements DoesAction {
    private ExecutionData executionData;

    public ReportSpecificTarget(ExecutionData executionData) { this.executionData = executionData;}

    @Override
    public void DoAction() {
        reportSpecificTarget();
    }

    private void reportSpecificTarget() {
        Optional<String> targetName = Utils.getTargetName(executionData.getStartingGraph());

        targetName.ifPresent(name ->{
            TargetDTO targetDTO = executionData.getSpecificTargetData(name);

            if (targetDTO == null) {
                System.out.println("Whoops, couldn't find the target!");
                return;
            }

            System.out.println();
            System.out.println(Engine.getInstance().getFormalizedTargetDataString(targetDTO));
        });
    }
}
