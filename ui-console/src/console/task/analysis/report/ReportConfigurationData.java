package console.task.analysis.report;

import console.menu.system.DoesAction;
import logic.Engine;
import task.Execution;
import task.configuration.ConfigurationDTO;

public class ReportConfigurationData implements DoesAction {
    Engine engine = Engine.getInstance();
    Execution execution;

    public ReportConfigurationData(Execution execution) {
        this.execution = execution;
    }

    @Override
    public void DoAction() {
        printConfigData();
    }

    private void printConfigData() {
        ConfigurationDTO configData = execution.getConfiguration().toDTO();
        System.out.println(engine.getFormalizedConfigurationString(configData));
        System.out.println();
    }
}
