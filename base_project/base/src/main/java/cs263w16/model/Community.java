package cs263w16.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * Created by ryanhalbrook on 1/29/16.
 */
@XmlRootElement
public class Community {

    private String id;
    private String description;
    private Date creationDate;
    private String adminUserId;

    public Community() {
        this.creationDate = new Date();
    }

    public Community(String id, String description, Date creationDate, String adminUserId) {
        this.id = id;
        this.description = description;
        this.creationDate = creationDate;
        this.adminUserId = adminUserId;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date date) {
        this.creationDate = date;
    }

    public String getAdminUserId() {
        return adminUserId;
    }
}
