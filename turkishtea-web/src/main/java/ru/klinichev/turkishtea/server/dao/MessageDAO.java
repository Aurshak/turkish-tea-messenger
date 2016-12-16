package ru.klinichev.turkishtea.server.dao;

import ru.klinichev.turkishtea.shared.Message;

import java.util.List;

public interface MessageDAO {

    void addToDatabase(Message newMessage);

    List<Message> loadDatabase(long since, int thisId, int thatId);

}
