package com.example.javalibrar;

import java.io.File;


public class GetFileName {

    public static String[] run() {
        File folder = new File("E:/study/cs/MyApplication/app/src/main/assets/conjugation");
        File[] listOfFiles = folder.listFiles();
        String[] headingArray = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                headingArray[i] = listOfFiles[i].getName().substring(0, listOfFiles[i].getName().length() - 5);
//                System.out.println(listOfFiles[i].getName().substring(0, listOfFiles[i].getName().length() - 5));
            } else if (listOfFiles[i].isDirectory()) {
//                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
        return headingArray;
    }
}
