package console.task.configuration;

import logic.Engine;
import task.configuration.ConfigurationDTO;

public abstract class ConfigUtils {
    static Engine engine = Engine.getInstance();

    public static void printConfig(ConfigurationDTO config) {
        System.out.println(engine.getFormalizedConfigurationString(config));
    }
}
