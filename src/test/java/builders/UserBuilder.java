package builders;

public class UserBuilder {
    private String id;
    private String email;
    private String username;
    private String password;

    public UserBuilder id(String id) {
        this.id = id;
        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder username(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    public User build() {
        return new User(id, email, username, password);
    }
}
