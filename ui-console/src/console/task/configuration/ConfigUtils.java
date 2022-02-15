package console.task.configuration;

import logic.Engine;
import task.configuration.ConfigurationData;

public abstract class ConfigUtils {
    static Engine engine = Engine.getInstance();

    public static void printConfig(ConfigurationData config) {
        System.out.println(engine.getFormalizedConfigurationString(config));
    }
}
