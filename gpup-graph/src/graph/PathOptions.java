package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum PathOptions {
    ANY {
        public Collection<List<TargetDTO>> returnPath(Collection<List<TargetDTO>> allPaths) {
            Collection<List<TargetDTO>> resPath = new ArrayList<>();

            if (allPaths != null) {
                resPath.add(allPaths.iterator().next());
            }

            return resPath;
        }
    },
    SHORTEST {
        public Collection<List<TargetDTO>> returnPath(Collection<List<TargetDTO>> allPaths) {
            Collection<List<TargetDTO>> resPath = new ArrayList<>();

            if (allPaths != null) {
                int minLength = getNumOfTargetsInPath(allPaths, false);
                resPath = addAllPathsByLength(allPaths, minLength);
            }

            return resPath;
        }
    },
    LONGEST {
        public Collection<List<TargetDTO>> returnPath(Collection<List<TargetDTO>> allPaths) {
            Collection<List<TargetDTO>> resPath = new ArrayList<>();

            if (allPaths != null) {
                int maxLength = getNumOfTargetsInPath(allPaths, true);
                resPath = addAllPathsByLength(allPaths, maxLength);
            }

            return resPath;
        }
    },
    ALL {
        public Collection<List<TargetDTO>> returnPath(Collection<List<TargetDTO>> allPaths) {
            Collection<List<TargetDTO>> resPath = allPaths;

            if (allPaths == null) {
                resPath = new ArrayList<>();
            }

            return resPath;
        }
    };

    public abstract Collection<List<TargetDTO>> returnPath(Collection<List<TargetDTO>> allPaths);

    public static int shortestPathLength(Collection<List<TargetDTO>> allPaths) {
        return getNumOfTargetsInPath(allPaths,false) - 1;
    }

    public static int longestPathLength(Collection<List<TargetDTO>> allPaths) {
        return getNumOfTargetsInPath(allPaths, true) - 1;
    }

    private static int getNumOfTargetsInPath(Collection<List<TargetDTO>> allPaths, boolean searchForMax) {
        if(allPaths == null || allPaths.size() == 0) {
            return 0;
        }

        int res = allPaths.iterator().next().size();

        for (Collection<TargetDTO> path : allPaths) {
            if (searchForMax) {
                if (path.size() > res) {
                    res = path.size();
                }
            } else {
                if (path.size() < res) {
                    res = path.size();
                }
            }
        }

        return res;
    }

    private static Collection<List<TargetDTO>> addAllPathsByLength(Collection<List<TargetDTO>> allPaths, int len) {
        Collection<List<TargetDTO>> resPaths = new ArrayList<>();

        for (List<TargetDTO> path : allPaths) {
            if (path.size() == len) {
                resPaths.add(path);
            }
        }

        return resPaths;
    }
}