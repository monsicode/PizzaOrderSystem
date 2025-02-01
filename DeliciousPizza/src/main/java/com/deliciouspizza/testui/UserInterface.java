package com.deliciouspizza.testui;

public interface UserInterface {

    void displayMenu();

    void handleLogin(String username, String password);

    void handleRegistration(String username, String password);

    void handleExit();

    void showMainMenuUser(String username);

}
