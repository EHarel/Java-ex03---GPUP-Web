package graph;

import java.io.Serializable;
import java.util.Collection;

public class SerialSetDTO implements Serializable {
    private static final long serialVersionUID = 1; // 08-Dec-2021 - Creation

    private final String name;
    private final Collection<String> targetNames;

    public SerialSetDTO(String name, Collection<String> targetNames) {
        this.name = name;
        this.targetNames = targetNames;
    }

    public String getName() {
        return name;
    }

    public Collection<String> getTargetNames() {
        return targetNames;
    }
}
