package cs263w16.model;

import com.google.appengine.api.datastore.Key;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ryanhalbrook on 2/8/16.
 */
@XmlRootElement
public class Event {


    private String name;
    private String description;
    private String communityName;

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

    public String getName() {
        return name;
    }

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

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public boolean isPubliclyAvailable() {
        return publiclyAvailable;
    }

    public void setPubliclyAvailable(boolean publiclyAvailable) {
        this.publiclyAvailable = publiclyAvailable;
    }

}
