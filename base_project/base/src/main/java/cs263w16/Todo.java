package cs263w16;

/*
 * Author: Lars Vogel, http://www.vogella.com/tutorials/REST/article.html
 * Modified: Ryan Halbrook 2016 - changed package to work for CS 263 assignment at UCSB.
 */

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Todo {
  private String id;
  private String summary;
  private String description;

  public Todo(){

  }
  public Todo (String id, String summary){
    this.id = id;
    this.summary = summary;
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getSummary() {
    return summary;
  }
  public void setSummary(String summary) {
    this.summary = summary;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }


}
