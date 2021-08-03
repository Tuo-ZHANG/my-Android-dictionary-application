package com.example.javalibrar;

public class Test {
    public static void main(String[] args) {
//        GetFileName.run();
//        System.out.println(GetFileName.run()[0]);
//        System.out.println(Arrays.toString(GetFileName.run()));
        String[] headingArray = GetFileName.run();
        System.out.println(headingArray[0]);
//        System.out.println("first entry");

        System.out.println("success!");
        System.out.println(System.getProperty("java.library.path"));
    }


    private void show() {
        System.out.println("success!");
    }
}
