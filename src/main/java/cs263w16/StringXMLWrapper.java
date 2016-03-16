package cs263w16;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ryanhalbrook on 2/22/16.
 */
@XmlRootElement
public class StringXMLWrapper {

    private String string;

    public StringXMLWrapper() {}
    public StringXMLWrapper(String string) {this.string = string;}

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

}
