package task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AffectedTargetsData {
    private Set<String> skippedTargetsNames;
    private Set<String> openedTargetsNames;

    public AffectedTargetsData() {
        skippedTargetsNames = new HashSet<>();
        openedTargetsNames = new HashSet<>();
    }

    public Set<String> getSkippedTargetsNames() {
        return skippedTargetsNames;
    }

    public void setSkippedTargetsNames(Set<String> skippedTargetNames) {
        this.skippedTargetsNames = skippedTargetNames;
    }

    public Set<String> getOpenedTargetsNames() {
        return openedTargetsNames;
    }

    public void setOpenedTargetsNames(Set<String> openedTargetsNames) {
        this.openedTargetsNames = openedTargetsNames;
    }
}
