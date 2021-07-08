package com.example.javalibrar;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
//        GetFileName.run();
//        System.out.println(GetFileName.run()[0]);
//        System.out.println(Arrays.toString(GetFileName.run()));
//        String[] headingArray = GetFileName.run();
//        System.out.println(headingArray[1]);
//        System.out.println("first entry");
        String string = "123   42    23";
        System.out.println(Arrays.toString(string.split("\\s+")));
        System.out.println(string.split("\\s+")[2]);
        System.out.println((100 / 10.0));
        System.out.println("arsenalfans");
    }
}
