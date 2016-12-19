package ru.klinichev.turkishtea.server.dao;

import ru.klinichev.turkishtea.shared.User;

import java.util.List;

public interface UserDAO {

    void addUser(User newUser);
    User getUserByName (String username);
    List<User> getAllUsers ();

}
