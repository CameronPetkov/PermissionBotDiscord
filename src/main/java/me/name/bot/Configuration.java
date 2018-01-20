package me.name.bot;

public class Configuration {
    String Token = null;
    String OwnerID = null;
    String Game = null;

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getOwnerID() {
        return OwnerID;
    }

    public void setOwnerID(String ownerID) {
        OwnerID = ownerID;
    }

    public String getGame() {
        return Game;
    }

    public void setGame(String game) {
        Game = game;
    }
}
