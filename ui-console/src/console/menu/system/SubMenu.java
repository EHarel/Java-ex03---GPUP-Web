package console.menu.system;

import console.Utils;

import java.util.ArrayList;
import java.util.List;

public class SubMenu extends MenuItem {
    protected List<MenuItem> subMenuItems;

    public SubMenu(String subMenuName, SubMenu parentItem) {
        super(subMenuName, parentItem, true);
        subMenuItems = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder(name);
        int i = 1;

        for (MenuItem item : subMenuItems) {
            res.append("\n").append(addNumberAndName(i, item.name));

            if (!Utils.isNullOrEmptyStr(item.getComments())) {
                res.append("\n\t\t").append(item.getComments());
            }

            i++;
        }

        return res.toString();
    }


    @Override
    public int getOptionRange() {
        return subMenuItems.size();
    }

    public MenuItem getItem(int index) {
        MenuItem res = null;

        if (subMenuItems != null) {
            res = subMenuItems.get(index);
        }

        return res;
    }

    public boolean addItem(MenuItem newItem) {
        boolean res;

        res = subMenuItems.add(newItem);

        return res;
    }

    @Override
    public void updateMenuItemText() {
    }
}