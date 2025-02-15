package com.alxsshv.journal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "verification_journals")
public class Journal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "number")
    private String number;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;

    public void updateFrom(Journal journal) {
        this.title = journal.getTitle();
        this.description = journal.getDescription();
    }

}