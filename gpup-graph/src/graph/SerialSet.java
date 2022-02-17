package graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public class SerialSet implements Serializable {
    private static final long serialVersionUID = 1; // 08-Dec-2021, creation

    private String name;
    private ReentrantLock lock;
    private Collection<Target> targets;

    private boolean isLocked;

    public SerialSet(String setName) {
        this.name = setName;
        this.lock = new ReentrantLock();
        this.targets = new ArrayList<>();
    }

    public String getName() {
        return name;
    }




//    public synchronized boolean tryLock() {
//        boolean locked = false;
//
//        if (!isLocked) {
//            locked = lock.tryLock();
//
//        }
//
//        return locked;
//    }
//
//    public synchronized void unlock() {
//        lock.unlock();
//    }


    public ReentrantLock getLock() {
        return lock;
    }

    public Collection<Target> getTargets() { return  this.targets; }

    public void addTarget(Target newTarget) {
        if (newTarget != null) {
            for (Target target : targets) {
                if (target.getName().equalsIgnoreCase(newTarget.getName())) {
                    return;
                }
            }

            targets.add(newTarget);
            newTarget.addSet(this);
        }
    }

    public void removeTarget(String targetName) {
        for (Target target : targets) {
            if (target.getName().equalsIgnoreCase(targetName)) {
                targets.remove(target);
                target.removeSet(this.name);
                break;
            }
        }
    }

    public SerialSetDTO toData() {
        Collection<String> targetNames = new LinkedList<>();

        targets.forEach(target -> targetNames.add(target.getName()));

        return new SerialSetDTO(this.name, targetNames);
    }

    /**
     * Returns a new object, identical to THIS, with the SAME LOCK! But without the targets.
     * They need to be added manually.
     */
    SerialSet cloneWithoutTargets() {
        SerialSet clone = new SerialSet(this.name);
        clone.lock = this.lock;

        return clone;
    }
}
