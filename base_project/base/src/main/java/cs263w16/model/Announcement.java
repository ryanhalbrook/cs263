package cs263w16.model;

/**
 * Created by ryanhalbrook on 3/12/16.
 */
public class Announcement {
    private String eventId;
    private String title;
    private String description;

    public Announcement(String eventId, String title, String description) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
