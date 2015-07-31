package com.fortitudetec.example.todo.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "todos")
public class Todo {

    private Long _id;
    private String _item;
    private String _itemNotes;
    private boolean _completed;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    @NotBlank
    public String getItem() {
        return _item;
    }

    public void setItem(String item) {
        _item = item;
    }

    public String getItemNotes() {
        return _itemNotes;
    }

    public void setItemNotes(String itemNotes) {
        _itemNotes = itemNotes;
    }

    public boolean isCompleted() {
        return _completed;
    }

    public void setCompleted(boolean completed) {
        _completed = completed;
    }
}
