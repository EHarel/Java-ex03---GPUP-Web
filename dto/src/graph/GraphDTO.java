package graph;

import java.util.Collection;
import java.util.Set;

public class GraphDTO {
    private String graphName;
    private String uploadingUserName;
    private int priceSimulation;
    private int priceCompilation;

    private int countAllTargets;
    private int countLeaves;
    private int countMiddles;
    private int countRoots;
    private int countIndependents;
    private Collection<String> allTargetNames;
    private Set<TargetDTO> targetDTOs;


    // countAlLTargets getter-setter
    public int getCountAllTargets() {
        return countAllTargets;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    public String getUploadingUserName() {
        return uploadingUserName;
    }

    public void setUploadingUserName(String uploadingUserName) {
        this.uploadingUserName = uploadingUserName;
    }

    public int getPriceSimulation() {
        return priceSimulation;
    }

    public void setPriceSimulation(int priceSimulation) {
        this.priceSimulation = priceSimulation;
    }

    public int getPriceCompilation() {
        return priceCompilation;
    }

    public void setPriceCompilation(int priceCompilation) {
        this.priceCompilation = priceCompilation;
    }

    public Set<TargetDTO> getTargetDTOs() {
        return targetDTOs;
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
        this.allTargetNames = allTargetNames;
    }

    public Collection<String> getAllTargetNames() {
        return allTargetNames;
    }

    public void setTargetDTOs(Set<TargetDTO> targetDTOS) {
        this.targetDTOs = targetDTOS;
    }
}