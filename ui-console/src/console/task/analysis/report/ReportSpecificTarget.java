package console.task.analysis.report;

import console.Utils;
import console.menu.system.DoesAction;
import graph.TargetDTO;
import logic.Engine;
import task.Execution;

import java.util.Optional;

public class ReportSpecificTarget implements DoesAction {
    private Execution execution;

    public ReportSpecificTarget(Execution execution) { this.execution = execution;}

    @Override
    public void DoAction() {
        reportSpecificTarget();
    }

    private void reportSpecificTarget() {
        Optional<String> targetName = Utils.getTargetName(execution.getStartingGraph());

        targetName.ifPresent(name ->{
            TargetDTO targetDTO = execution.getSpecificTargetData(name);

            if (targetDTO == null) {
                System.out.println("Whoops, couldn't find the target!");
                return;
            }

            System.out.println();
            System.out.println(Engine.getInstance().getFormalizedTargetDataString(targetDTO));
        });
    }
}
