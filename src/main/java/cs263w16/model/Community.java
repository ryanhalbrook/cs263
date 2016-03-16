package cs263w16.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * Created by ryanhalbrook on 1/29/16.
 */
@XmlRootElement
public class Community {


    // Immutable
    private String name;
    private Date creationDate;
    private String adminUserId;

    // Mutable
    private String description;

    public Community() {
        this.creationDate = new Date();
    }

    public Community(String name, String description, Date creationDate, String adminUserId) {
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
        this.adminUserId = adminUserId;
    }

    public String getId() {
        return name.replaceAll(" ", "_");
    }
    public String getName() { return name; }
    public String getDescription() {
        return description;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public String getAdminUserId() {
        return adminUserId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
