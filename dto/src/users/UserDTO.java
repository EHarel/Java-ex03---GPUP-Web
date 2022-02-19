package users;

import utilsharedall.UserType;

public class UserDTO {
    private String name;
    private UserType type;

    public UserDTO(String name, UserType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }

    public UserType getType() {
        return type;
    }
}
