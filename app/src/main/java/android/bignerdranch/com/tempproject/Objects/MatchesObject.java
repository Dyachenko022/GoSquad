package android.bignerdranch.com.tempproject.Objects;

public class MatchesObject {
    private String userId;
    private String name;
    private String profileImage;
    public MatchesObject(String uid, String name, String profileImage)
    {
        this.userId = uid;
        this.name = name;
        this.profileImage = profileImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
