package edu.sabanciuniv.canberkkeleshomework3;

import androidx.annotation.NonNull;

public class Category {
    private String name;
    private int id;

    public Category(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Category() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
