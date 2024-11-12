package com.deliciouspizza.entity.user;

import com.deliciouspizza.utils.UserRights;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("employee")
public class Employee extends User {

    public Employee() {
        super("", "");
        this.userType = "employee";
    }

    public Employee(String username, String password) {
        super(username, password);
        rights = UserRights.ADMIN;
        this.userType = "employee";
    }
}
