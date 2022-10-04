package com.tuo.mydictionary;

public class Entry {
    private String entry;
    private String dictionary;

    public Entry(String entry, String dictionary) {
        this.entry = entry;
        this.dictionary = dictionary;
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }
}
