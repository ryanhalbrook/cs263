package cs263w16.model;

import java.util.Date;


/**
 * Created by ryanhalbrook on 3/15/16.
 */
public class MembershipEvent {

    private String userId;
    private String eventId;
    private String communityId;
    private Date eventDate;
    private Boolean hidden; // Whether the event should be hidden in the user feed.

    public MembershipEvent(String userId, String eventId, String communityId, Date eventDate, Boolean hidden) {
        this.userId = userId;
        this.eventId = eventId;
        this.communityId = communityId;
        this.eventDate = eventDate;
        this.hidden = hidden;
    }

    public String getId() {
        return userId + ":" + eventId;
    }

    public String getUserId() {
        return userId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public Boolean isHidden() {
        return hidden;
    }
}
