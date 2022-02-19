package users;

import utilsharedall.UserType;

public class User {
    private String name;
    private UserType userType;

    public User(String name, UserType userType) {
        this.name = name;
        this.userType = userType;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public UserType getUserType() {
        return userType;
    }

    public UserDTO toDTO() {
        return new UserDTO(name, userType);
    }
}
