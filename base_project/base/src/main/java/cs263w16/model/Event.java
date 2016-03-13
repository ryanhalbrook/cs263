package cs263w16.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Map;

/**
 * Created by ryanhalbrook on 2/8/16.
 */
@XmlRootElement
public class Event {


    private String name;
    private String description;
    private String communityName;
    private Date eventDate;
    private boolean publiclyAvailable;

    public Event() {
        publiclyAvailable = false;
    }

    public Event(String name, String description, String communityName, boolean publiclyAvailable) {
        this.name = name;
        this.description = description;
        this.communityName = communityName;
        this.publiclyAvailable = publiclyAvailable;
    }

    public String getName() {return name;}
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getCommunityName() {
        return communityName;
    }
    public boolean isPubliclyAvailable() {
        return publiclyAvailable;
    }
    public Date getEventDate() {return eventDate;}
    public void setEventDate(Date eventDate) {this.eventDate = eventDate;}

}
