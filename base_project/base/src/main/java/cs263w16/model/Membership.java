package cs263w16.model;

/**
 * Created by ryanhalbrook on 3/12/16.
 */
public class Membership {

    private String communityId;
    private String userId;

    public Membership(String communityId, String userId) {
        this.communityId = communityId;
        this.userId = userId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public String getUserId() {
        return userId;
    }

}
