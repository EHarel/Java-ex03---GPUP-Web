package task;

import java.io.Serializable;

/**
 * This class determines if a specific target is part of an execution,
 * and which targets related to it are also affected - targets it is required for, targets it is dependent on,
 * or both.
 */
public class ChosenTarget implements Serializable {
    private static final long serialVersionUID = 1; // 11-Dec-2021 Creation


    public enum RelationshipDirection implements Serializable {
        DEPENDENT_ON, REQUIRED_FOR, BOTH
    }

    public String getName() {
        return name;
    }

    public RelationshipDirection getRelationshipDirection() {
        return relationshipDirection;
    }

    String name;
    RelationshipDirection relationshipDirection;

    public ChosenTarget(String name, RelationshipDirection relationshipDirection) {
        this.name = name;
        this.relationshipDirection = relationshipDirection;
    }
}
