package com.semanientreprise.noteit.model;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notes extends RealmObject{
    @PrimaryKey
    @Required
    private String noteId;
    @Required
    private String note;
    @Required
    private String noteTitle;
    @Required
    private Boolean isSaved;
    @Required
    private Date timestamp;

    public Notes() {
        this.noteId = UUID.randomUUID().toString();
        this.noteTitle = "";
        this.note = "";
        this.isSaved = false;
        this.timestamp = new Date();
    }
}