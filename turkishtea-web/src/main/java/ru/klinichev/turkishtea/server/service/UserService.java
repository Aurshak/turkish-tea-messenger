package ru.klinichev.turkishtea.server.service;

import ru.klinichev.turkishtea.shared.User;

import java.util.List;

public interface UserService {

    void addUser(User newUser);
    User getUserByName (String name);
    List<User> getAllUsers ();

}
