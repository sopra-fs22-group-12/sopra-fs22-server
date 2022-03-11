package ch.uzh.ifi.hase.soprafs22.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class UserGetDTO {

    private Long id;
    private String name;
    private String username;
    @JsonProperty("logged_in")
    private Boolean loggedIn;
    @JsonProperty("creation_date")
    private Date creationDate;
    private Date birthday;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }


    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }
}
