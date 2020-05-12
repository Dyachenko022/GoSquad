package android.bignerdranch.com.tempproject.Objects;

public class Cards {

    private String userId;
    private String name;
    private String imageUrl;
    public Cards(String userId, String name, String imageUrl)
    {
        this.userId = userId;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }
}
