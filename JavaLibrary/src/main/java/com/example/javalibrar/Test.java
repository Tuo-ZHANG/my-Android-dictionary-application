package com.example.javalibrar;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


public class Test {
    public static void main(String[] args) {
//        GetFileName.run();
//        System.out.println(GetFileName.run()[0]);
//        System.out.println(Arrays.toString(GetFileName.run()));
//        String[] headingArray = GetFileName.run();
//        System.out.println(headingArray[0]);
//        System.out.println("first entry");

//        System.out.println("success!");
//        System.out.println(System.getProperty("java.library.path"));
//        String string = "s.css";
//        System.out.println(string.substring(string.length() - 4));
//        SortedMap<Integer, String> map_head = new TreeMap<Integer, String>();
//        ArrayList<Entry> entries = new ArrayList<>();
//        ArrayList<Entry> entriesBackup;
//        entries.add(new Entry("arsenal", "colins"));
//        entriesBackup = (ArrayList<Entry>) entries.clone();
//        entries.add(new Entry("deli", "colins"));
//        System.out.println(entries);
//        System.out.println(entriesBackup);
//        entries.clear();
//        System.out.println(entries);
//        System.out.println(entriesBackup);
        TreeMap<String, String> types = new TreeMap<>();
        types.put("1 gun", "collins");
        types.put("1 arsenal", "collins");
        types.put("2 arsenal", "collins");
        TreeMap<String, String> test = new TreeMap<>(types);
        TreeMap<String, String> testClone = (TreeMap<String, String>) types.clone();
        types.put("1 gun", "webster");
        System.out.println(types);
        System.out.println(testClone);
        System.out.println(test);
    }


    private void show() {
        System.out.println("success!");
    }
}
