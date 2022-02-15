package console.task.analysis.report;

import console.menu.system.DoesAction;
import logic.Engine;
import task.ExecutionData;
import task.configuration.ConfigurationData;

public class ReportConfigurationData implements DoesAction {
    Engine engine = Engine.getInstance();
    ExecutionData executionData;

    public ReportConfigurationData(ExecutionData executionData) {
        this.executionData = executionData;
    }

    @Override
    public void DoAction() {
        printConfigData();
    }

    private void printConfigData() {
        ConfigurationData configData = executionData.getConfiguration().getData();
        System.out.println(engine.getFormalizedConfigurationString(configData));
        System.out.println();
    }
}
