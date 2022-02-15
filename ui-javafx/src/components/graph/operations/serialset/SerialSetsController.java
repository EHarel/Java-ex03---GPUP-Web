package components.graph.operations.serialset;

import graph.*;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Collection;

public class SerialSetsController {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------- FXML DATA MEMBERS ----------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @FXML private ScrollPane mainScene;

    @FXML private TableView<SerialSetDTO> tableViewSerialSets;
    @FXML private TableColumn<SerialSetDTO, String> tableColumnSerialSetName;
    @FXML private TableColumn<SerialSetDTO, Collection<String>> tableColumnTargetNames;

    @FXML
    public void initialize() {
        tableColumnSerialSetName.setCellValueFactory(new PropertyValueFactory<SerialSetDTO, String>("name"));
        tableColumnTargetNames.setCellValueFactory(new PropertyValueFactory<SerialSetDTO, Collection<String>>("targetNames"));



//        selectedTableColumn.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, String>("checkbox"));
//        targetNameTableColumn.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, String>("name"));
//        levelTableColumn.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, TargetDTO.Dependency>("dependency"));
//        userDataTableColumn.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, String>("userData"));
//        noRequireForTableColumn.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, Integer>("noTargetsThisDirectlyDependsOn"));
//        noDependsOnTableColumn.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, Integer>("noTargetsThisIsDirectlyRequiredFor"));
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public ScrollPane getMainScene() {
        return mainScene;
    }





    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ MISC. METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void populateData(DependenciesGraph graph) {
        if (graph != null) {
            Collection<SerialSetDTO> serialSets = graph.getSerialSetDTO();
            fillData(serialSets);
        }
    }

    private void fillData(Collection<SerialSetDTO> serialSets) {
        if (serialSets != null) {
            tableViewSerialSets.getItems().clear();

            serialSets.forEach(serialSetDTO -> {
                tableViewSerialSets.getItems().add(serialSetDTO);
            });
        }
    }
}


//    public void populateData(Graph graph) {
//        Collection<TargetDTO> targetDTOCollection = graph.getAllTargetData();
//
//        fillData(targetDTOCollection);
//    }
//
//    public void populateData(Collection<TargetDTO> targetDTOCollection) {
//        fillData(targetDTOCollection);
//    }
//
//    private void fillData(Collection<TargetDTO> targetDTOCollection) {
//        if (targetDTOCollection != null) {
//            targetTableView.getItems().clear();
//
//            targetDTOCollection.forEach(targetDTO -> {
//                addTarget(targetDTO);
//            });
//        }
//    }

//    private final String name;
//    private final Collection<String> targetNames;