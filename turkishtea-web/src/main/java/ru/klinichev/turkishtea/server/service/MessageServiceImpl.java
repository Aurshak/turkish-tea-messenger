package ru.klinichev.turkishtea.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.klinichev.turkishtea.server.dao.MessageDAO;
import ru.klinichev.turkishtea.shared.Message;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageDAO messageDAO;

    public void setMessageDAO(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    @Override
    @Transactional
    public void addToDatabase(Message newMessage) {
        messageDAO.addToDatabase(newMessage);
    }

    @Override
    @Transactional
    public List<Message> loadDatabase(long since, int thisId, int thatId) {
        return messageDAO.loadDatabase(since, thisId, thatId);
    }
}
