package cs263w16.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Map;

/**
 * Created by ryanhalbrook on 2/8/16.
 */
@XmlRootElement
public class Event {


    // Immutable.
    private String name;
    private String communityName;

    // Mutable.
    private String description;
    private Date date;


    public Event(String name, String description, String communityName) {
        this.name = name;
        this.description = description;
        this.communityName = communityName;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return communityName + ":" + name.replaceAll(" ", "_");
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
