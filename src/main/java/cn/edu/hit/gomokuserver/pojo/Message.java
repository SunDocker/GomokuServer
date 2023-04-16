package cn.edu.hit.gomokuserver.pojo;

public abstract class Message {
    public static final String MESSAGE_CONNECT = "connect";
    public static final String MESSAGE_PLAY = "play";
    public static final String MESSAGE_TRY = "try";
    String type;

    /**
     * role:
     * -1 - black
     * 0 - no game
     * 1 - white
     */
    int role;

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class ConnectMessage extends Message {
        String username;

        public ConnectMessage(String username, int role) {
            super.type = MESSAGE_CONNECT;
            this.username = username;
            super.role = role;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

    }

    public static class PlayMessage extends Message {
        /**
         * turn:
         * -2 - black won
         * -1 - black turn
         * 0 - no game
         * 1 - white turn
         * 2 - white won
         */
        int nextTurn;

        /**
         * role:
         * -1 - black
         * 0 - no game
         * 1 - white
         */
        int role;

        int x;
        int y;

        @Override
        public int getRole() {
            return role;
        }

        @Override
        public void setRole(int role) {
            this.role = role;
        }

        public int getNextTurn() {
            return nextTurn;
        }

        public void setNextTurn(int nextTurn) {
            this.nextTurn = nextTurn;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public PlayMessage(int nextTurn, int role, int x, int y) {
            super.type = MESSAGE_PLAY;
            this.nextTurn = nextTurn;
            this.role = role;
            this.x = x;
            this.y = y;
        }
    }

    public static class TryMessage extends Message {
        int x;
        int y;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
