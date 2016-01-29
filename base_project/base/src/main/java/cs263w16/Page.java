package cs263w16;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by ryanhalbrook on 1/29/16.
 */
@XmlRootElement
public class Page {

    private String id;
    private String html;
    private Date creationDate;

    public Page() {
        this.creationDate = new Date();
    }

    public Page(String id, String html, Date creationDate) {
        this.id = id;
        this.html = html;
        this.creationDate = creationDate;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getHtml() {
        return html;
    }
    public void setHtml(String html) {
        this.html = html;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date date) {
        this.creationDate = date;
    }

}
