package cs263w16.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by ryanhalbrook on 1/29/16.
 */
@XmlRootElement
public class Community {

    private String id;
    private String description;
    private Date creationDate;

    public Community() {
        this.creationDate = new Date();
    }

    public Community(String id, String description, Date creationDate) {
        this.id = id;
        this.description = description;
        this.creationDate = creationDate;
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

}
