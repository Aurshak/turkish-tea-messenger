package ru.klinichev.turkishtea.server;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@ServerEndpoint(value="/add")
public class MessageWebsocket {

    private static final Logger logger = Logger.getLogger("MessageWebsocket");

    private static Set<Session> peers = new HashSet<Session>();

    @OnOpen
    public void onOpen( final Session session )
    {
        logger.info( "onOpen(" + session.getId() + ")" );
        peers.add( session );
    }

    @OnClose
    public void onClose( final Session session )
    {
        logger.info( "onClose(" + session.getId() + ")" );
        peers.remove( session );
    }

    @OnMessage
    public void onMessage( final String message, final Session session )
    {
        logger.info( "onMessage(" + message + "," + session.getId() + ")" );
        final String id = session.getId();
        for ( final Session peer : peers )
        {
            if ( peer.getId().equals( session.getId() ) )
            {
                peer.getAsyncRemote().sendText( "You said " + message );
            }
            else
            {
                peer.getAsyncRemote().sendText( id + " says " + message );
            }
        }
    }

}
