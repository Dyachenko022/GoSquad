package android.bignerdranch.com.tempproject.Objects;

public class GameObject {
    private String gameName;
    private String gameUrl;

    public GameObject(String gameName, String gameUrl)
    {
        this.gameName = gameName;
        this.gameUrl = gameUrl;
    }

    public String getGameName() {
        return gameName;
    }

    public String getGameUrl() {
        return gameUrl;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setGameUrl(String gameUrl) {
        this.gameUrl = gameUrl;
    }
}

