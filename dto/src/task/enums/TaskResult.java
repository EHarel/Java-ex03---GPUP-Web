package task.enums;

import java.io.Serializable;

public enum TaskResult implements Serializable, Cloneable {
    SUCCESS, SUCCESS_WITH_WARNINGS, FAILURE, UNPROCESSED
}
