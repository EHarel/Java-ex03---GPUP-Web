package graph;

import java.util.Collection;

public class GeneralData {
    private int countAllTargets;
    private int countLeaves;
    private int countMiddles;
    private int countRoots;
    private int countIndependents;
    private Collection<String> alLTargetNames;


    // countAlLTargets getter-setter
    public int getCountAllTargets() {
        return countAllTargets;
    }

    public boolean setCountAllTargets(int newCount) {
        boolean res = false;

        if (newCount > 0) {
            res = true;
            countAllTargets = newCount;
        }

        return res;
    }

    // countLeaves getter-setter
    public int getCountLeaves() {
        return countLeaves;
    }

    public boolean setCountLeaves(int countLeaves) {
        boolean res = false;

        if (countLeaves > 0) {
            res = true;
            this.countLeaves = countLeaves;
        }

        return res;
    }

    // countMiddles getter-setter
    public int getCountMiddles() {
        return countMiddles;
    }

    public boolean setCountMiddles(int newCount) {
        boolean res = false;

        if(newCount > 0) {
            res = true;
            this.countMiddles = newCount;
        }

        return res;
    }

    // countRoots getter-setter
    public int getCountRoots() {
        return countRoots;
    }

    public boolean setCountRoots(int newCount) {
        boolean res = false;

        if (newCount > 0) {
            res = true;
            this.countRoots = newCount;
        }

        return res;
    }

    // countIndependents getter-setter
    public int getCountIndependents() {
        return countIndependents;
    }

    public boolean setCountIndependents(int newCount) {
        boolean res = false;

        if(newCount > 0) {
            res = true;
            this.countIndependents = newCount;
        }

        return res;
    }

    public void setTargetNames(Collection<String> allTargetNames) {
        this.alLTargetNames = allTargetNames;
    }

    public Collection<String> getAllTargetNames() {
        return alLTargetNames;
    }
}