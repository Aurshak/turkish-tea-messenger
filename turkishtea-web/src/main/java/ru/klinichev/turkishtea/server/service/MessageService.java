package ru.klinichev.turkishtea.server.service;

import ru.klinichev.turkishtea.shared.Message;

import java.util.List;

public interface MessageService {

    void addToDatabase(Message newMessage);

    List<Message> loadDatabase(long since, int thisId, int thatId);

}
