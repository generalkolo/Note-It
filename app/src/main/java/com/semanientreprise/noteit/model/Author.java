package com.semanientreprise.noteit.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Author extends RealmObject{
    @PrimaryKey
    @Required
    String id;
    @Required
    Date timeStamp;
    @Required
    String name;

    RealmList<Notes> notes;
}