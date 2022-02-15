package console.graph;

import console.menu.system.DoesAction;
import graph.GeneralData;
import graph.Graph;
import graph.SerialSetDTO;

import java.util.Collection;

public class TargetsDataGeneral implements DoesAction {
    private final Graph graph;

    public TargetsDataGeneral(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void DoAction() {
        printAllTargetBasicDetails();
    }

    private void printAllTargetBasicDetails() {
        System.out.println("[Graph \"" + graph.getName() + "\" general details]");
        GeneralData generalData = graph.getGeneralDataAllTargets();

        if (generalData == null) {
            System.out.println("Whoa there, graph isn't loaded yet!");
            /* TODO
             * (1) This shouldn't even be allowed. Is there a way to avoid this section?
             * (2) If user reaches this part anyway, should we offer an option to load a file here?
             *
             */
        } else {
            System.out.println("Number of all targets: " + generalData.getCountAllTargets());
            System.out.println("Number of leaves: " + generalData.getCountLeaves());
            System.out.println("Number of middles: " + generalData.getCountMiddles());
            System.out.println("Number of roots: " + generalData.getCountRoots());
            System.out.println("Number of independents: " + generalData.getCountIndependents());
            System.out.println("All targets: " + generalData.getAllTargetNames());
            printSerialSets();
        }
    }

    private void printSerialSets() {
        Collection<SerialSetDTO> serialSetDTOS = graph.getSerialSetDTO();

        if (serialSetDTOS.isEmpty()) {
            System.out.println("Serial sets: None");
        } else {
            StringBuilder serialSetsStr = new StringBuilder();
            serialSetsStr.append("Serial sets:");

            for (SerialSetDTO serialSetDTO : serialSetDTOS) {
                serialSetsStr.append("\n\t").append(serialSetDTO.getName()).append(" ").append(serialSetDTO.getTargetNames());
            }

            System.out.println(serialSetsStr);
        }
    }
}
