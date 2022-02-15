package console.menu.system;

public class MenuAction extends MenuItem {
    private boolean backOnCompletion = false;
    private DoesAction action;

    public MenuAction(String name, SubMenu parentItem, DoesAction action) {
        super(name, parentItem, false);
        this.action = action;
    }

    @Override
    public String toString() {
        return addNumberAndName(1, name);
    }

    public MenuItem getItem(int index) {
        MenuItem res = null;

        if (index == 0) {
            res = this;
        }

        return res;
    }

    public void selected() {
        action.DoAction();
    }

    @Override
    public void updateMenuItemText() {

    }
}