package components.dashboard.users;

import components.app.AppMainController;
import components.graph.targettable.TargetDTOTable;
import graph.TargetDTO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import users.UserDTO;
import utilshared.UserType;

import java.util.Collection;
import java.util.LinkedList;

public class UserTableController {

    @FXML
    private ScrollPane MainPane;

    @FXML
    private TableView<UserDTO> tableView_Users;

    @FXML
    private TableColumn<UserDTO, String> tableColumn_UserName;

    @FXML
    private TableColumn<UserDTO, UserType> tableColumn_UserType;

    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */



    /* ---------------------------------------------------------------------------------------------------- */
    /* ----------------------------------- CONSTRUCTOR AND INITIALIZER ------------------------------------ */
    /* ---------------------------------------------------------------------------------------------------- */
    @FXML
    public void initialize() {
        tableColumn_UserName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("name"));
        tableColumn_UserType.setCellValueFactory(new PropertyValueFactory<UserDTO, UserType>("type"));
    }





    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
        public TableView<UserDTO> getTableView() {
        return this.tableView_Users;
    }

    // ----------------- Target Table Code: -----------------
//    public void setMainController(AppMainController mainController) {
//        this.mainController = mainController;
//    }
//
//
//    public Parent getScene() {
//        return scene;
//    }
//
//    public void setScene(Parent parent) {
//        this.scene = parent;
//    }
//
//    public Collection<TargetDTO> getAllTargets() {
//        ObservableList<TargetDTOTable> allItems = tableViewTargets.getItems();
//        Collection<TargetDTO> allTargets = new LinkedList<>();
//
//        if (allItems != null) {
//            allItems.forEach(targetDTOTable -> {
//                allTargets.add(targetDTOTable);
//            });
//        }
//
//        return allTargets;
//    }
//
//    public Collection<TargetDTO> getSelectedTargets() {
//        ObservableList<TargetDTOTable> allItems = tableViewTargets.getItems();
//        Collection<TargetDTO> selectedTargets = new LinkedList<>();
//
//        if (allItems != null) {
//            allItems.forEach(targetDTOTable -> {
//                if (targetDTOTable.getCheckbox().isSelected()) {
//                    selectedTargets.add(targetDTOTable);
//                }
//            });
//        }
//
//        return selectedTargets;
//    }
//
//    public TargetDTOTable getSelectedTarget() {
//        return tableViewTargets.getSelectionModel().getSelectedItem();
//    }



    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* -------------------------------------- POPULATE DATA METHODS --------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void clear() {
        tableView_Users.getItems().clear();
    }

    /**
     * This method completely replaces the current users listed in the table by those given in the parameter.
     * @param users A collection of users to replace the old ones.
     */
    public void fillUsers(Collection<UserDTO> users) {
        clear();

        users.forEach(userDTO -> {
            UserDTO newDTO = new UserDTO(userDTO.getName(), userDTO.getType());

            tableView_Users.getItems().add(newDTO);
        });
    }
}
