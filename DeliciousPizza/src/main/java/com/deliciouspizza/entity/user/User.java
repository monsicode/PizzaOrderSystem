package com.deliciouspizza.entity.user;

import com.deliciouspizza.enums.UserRights;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "userType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Customer.class, name = "customer"),
    @JsonSubTypes.Type(value = Employee.class, name = "employee")
})
public abstract class User {

    protected final String username;

    @JsonProperty("hashedPassword")
    protected final String hashedPassword;

    protected UserRights rights;

    @JsonProperty("userType")
    protected String userType;

    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public String getUsername() {
        return username;
    }

    public UserRights getRights() {
        return rights;
    }

    public boolean checkPassword(String plainPassword) {
        return BCrypt.checkpw(plainPassword, this.hashedPassword);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
