package android.bignerdranch.com.tempproject;

public class GamesChosenList {
    private String gameUid;
    private String gameName;

    public GamesChosenList(String gameUid, String gameName) {
        this.gameUid = gameUid;
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameUid() {
        return gameUid;
    }

    public void setGameUid(String gameUid) {
        this.gameUid = gameUid;
    }
}
