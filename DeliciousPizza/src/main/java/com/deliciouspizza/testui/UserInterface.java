package com.deliciouspizza.testui;

import com.deliciouspizza.exception.ProductException;

public interface UserInterface {

    void displayMenu();

    void handleLogin(String username, String password) throws ProductException;

    void handleRegistration(String username, String password);

    void handleExit();

    void showMainMenuUser(String username) throws ProductException;

}
