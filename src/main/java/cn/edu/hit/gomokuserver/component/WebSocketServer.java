package cn.edu.hit.gomokuserver.component;

import cn.edu.hit.gomokuserver.pojo.Game;
import cn.edu.hit.gomokuserver.pojo.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author SunDocker
 */
@Component
@ServerEndpoint("/gomoku/{username}")
public class WebSocketServer {
    public static Session waitingSession = null;

    public static final Map<Session, String> sessionUsername = new ConcurrentHashMap<>();

    public static final Map<Session, Game> sessionGame = new ConcurrentHashMap<>();


    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessionUsername.put(session, username);

        Session matchedSession = null;
        synchronized (Session.class) {
            if (waitingSession == null || !waitingSession.isOpen()) {
                waitingSession = session;
                return;
            }
            matchedSession = waitingSession;
            waitingSession = null;
        }

        Game game = new Game(session, matchedSession);
        sessionGame.put(session, game);
        sessionGame.put(matchedSession, game);

        // TODO: Problems about close
        if (!session.isOpen() || !matchedSession.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                matchedSession.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Message.ConnectMessage blkConnMsg = new Message.ConnectMessage(sessionUsername.get(matchedSession), -1);
        Message.ConnectMessage whtConnMsg = new Message.ConnectMessage(username, 1);
        ObjectMapper objMapper = new ObjectMapper();
        try {
            String blkMsgJson = objMapper.writeValueAsString(blkConnMsg);
            String whtMsgJson = objMapper.writeValueAsString(whtConnMsg);
            matchedSession.getBasicRemote().sendText(whtMsgJson);
            session.getBasicRemote().sendText(blkMsgJson);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnClose
    public void onClose(Session session) {
        Game game = sessionGame.remove(session);
        if (game == null) {
            return;
        }

        Session opponentSession = null;
        if (game.getBlackSession() == session) {
            opponentSession = game.getWhiteSession();
        } else if (game.getWhiteSession() == session) {
            opponentSession = game.getBlackSession();
        }
        if (opponentSession == null) {
            return;
        }

        sessionGame.remove(opponentSession);
        try {
            opponentSession.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("username") String username) {
        ObjectMapper objMapper = new ObjectMapper();
        Message.TryMessage tryMessage = null;
        try {
            tryMessage = objMapper.readValue(message, Message.TryMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (tryMessage == null) {
            return;
        }

        Game game = sessionGame.get(session);
        if (game == null) {
            return;
        }

        int curTurn = game.getTurn();
        Session blackSession = game.getBlackSession();
        Session whiteSession = game.getWhiteSession();
        if ((blackSession == session && curTurn == -1)
                || (whiteSession == session && curTurn == 1)) {
            int x = tryMessage.getX();
            int y = tryMessage.getY();
            Message.PlayMessage playMessage = new Message.PlayMessage(game.check(x, y), curTurn, x, y);
            try {
                String msgJson = objMapper.writeValueAsString(playMessage);
                blackSession.getBasicRemote().sendText(msgJson);
                whiteSession.getBasicRemote().sendText(msgJson);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @OnError
    public void onError(Throwable error) {
        throw new RuntimeException(error);
    }
}
