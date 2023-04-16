package cn.edu.hit.gomokuserver.pojo;

import jakarta.websocket.Session;

import java.util.Arrays;

public class Game {
    int[][] gameBoard = new int[10][10];

    Session blackSession;

    Session whiteSession;

    /**
     * turn:
     * -2 - black won
     * -1 - black turn
     * 0 - no game
     * 1 - white turn
     * 2 - white won
     */
    int turn = -1;

    public Game(Session blackSession, Session whiteSession) {
        this.blackSession = blackSession;
        this.whiteSession = whiteSession;
    }

    public Session getBlackSession() {
        return blackSession;
    }


    public Session getWhiteSession() {
        return whiteSession;
    }


    public int getTurn() {
        return turn;
    }

    public int check(int x, int y) {
        this.gameBoard[x][y] = turn;
        if ((calContinue(x, y, -1, 0) + calContinue(x, y, 1, 0) >= 6)
                || (calContinue(x, y, 0, -1) + calContinue(x, y, 0, 1) >= 6)
                || (calContinue(x, y, -1, -1) + calContinue(x, y, 1, 1) >= 6)
                || (calContinue(x, y, -1, 1) + calContinue(x, y, 1, -1) >= 6)) {
            this.turn = this.turn > 0 ? this.turn + 1 : this.turn - 1;
        } else {
            this.turn = -this.turn;
        }
        return this.turn;
    }

    private int calContinue(int x, int y, int xv, int yv) {
        int ori = this.gameBoard[x][y];
        int continueCnt = 0;
        for (int i = x, j = y; 0 <= i && i < 10 && 0 <= j && j < 10 && ori == this.gameBoard[i][j]; i += xv, j += yv) {
            continueCnt++;
        }
        return continueCnt;

    }
}
