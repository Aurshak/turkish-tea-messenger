package ru.klinichev.turkishtea.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.klinichev.turkishtea.server.dao.UserDAO;
import ru.klinichev.turkishtea.shared.Session;
import ru.klinichev.turkishtea.shared.User;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    @Transactional
    public void addUser(User newUser) {
        userDAO.addUser(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByName(String name) {
        return userDAO.getUserByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

}
