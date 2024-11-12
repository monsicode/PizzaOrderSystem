package com.deliciouspizza.entity.user;

import java.util.Objects;
import java.util.UUID;

public abstract class User {

    //to remove id , username will be unique
    private final String id;
    protected final String username;
    protected final String password;
    protected int age;
   // Set<Orders> orderHistory;

    //to think about
    public User(String username, String password) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, int age) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
