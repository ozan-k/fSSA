package com.ozank;

import com.ozank.lexerParser.ReadFile;
import com.ozank.lexerParser.ModelBuilder;

public class Main {
    public static void main(String[] args) {
        String file =  ReadFile.readFile("src/main/resources/test.txt");
        ModelBuilder builder = new ModelBuilder(file);
        //System.out.println(file);
    }
}