package com.tuo.mydictionary;

public class EntryInformationModel {
    private int id;
    private String entry;
    private int queriedTimes;
    private boolean hasRecord;

    public EntryInformationModel(int id, String entry, int queriedTimes, boolean hasRecord) {
        this.id = id;
        this.entry = entry;
        this.queriedTimes = queriedTimes;
        this.hasRecord = hasRecord;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public int getQueriedTimes() {
        return queriedTimes;
    }

    public void setQueriedTimes(int queriedTimes) {
        this.queriedTimes = queriedTimes;
    }

    public boolean isHasRecord() {
        return hasRecord;
    }

    public void setHasRecord(boolean hasRecord) {
        this.hasRecord = hasRecord;
    }

    @Override
    public String toString() {
        return "EntryInformationModel{" +
                "id=" + id +
                ", entry='" + entry + '\'' +
                ", queriedTimes=" + queriedTimes +
                ", hasRecord=" + hasRecord +
                '}';
    }
}
