package ru.klinichev.turkishtea.server;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;
import ru.klinichev.turkishtea.shared.Session;

@Component
public class SessionManager {

    @Lookup
    Session getSession() {
        return null;
    }

}
