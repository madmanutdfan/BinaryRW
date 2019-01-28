package com.visualsvn;

import java.io.*;
import java.util.*;

public class BinaryRW {

    private static RandomAccessFile inputFile;
    private static RandomAccessFile outputFile;
    private static Integer cntFiles = 0;
    private static List<RandomAccessFile> tempFiles = new ArrayList<>();

    private static void openFiles(String[] args){
        if(args.length != 2){
            System.out.println("ERROR::need 2 args");
            System.exit(1);
        }

        try {
            inputFile = new RandomAccessFile(args[0], "r");
        } catch (FileNotFoundException e) {
            System.out.println("ERROR::invalid 1 arg");
            System.exit(1);
        }

        if(args[1].split("\\.").length != 2){
            System.out.println("ERROR::invalid name of 2 file");
            System.exit(1);
        }

        try {
            outputFile = new RandomAccessFile(args[1], "rw");
        } catch (FileNotFoundException e) {
            System.out.println("Cannot create new file " + args[1]);
            System.exit(1);
        }
    }
    private static RandomAccessFile createNewFile(){
        RandomAccessFile newFile = null;
        String name = "file" + cntFiles.toString();
        cntFiles++;

        try {
            newFile = new RandomAccessFile(name, "rw");
        } catch (FileNotFoundException e) {
            System.out.println("ERROR::something's gone wrong");
            System.exit(1);
        }
        tempFiles.add(newFile);
        return newFile;
    }

    private static void readFile(){
        int maxLength = 0;
        RandomAccessFile curFile = createNewFile();

        try {
            int b = inputFile.read();
            while(b != -1){
                if(maxLength >= Integer.MAX_VALUE) {
                    curFile = createNewFile();
                    maxLength = 0;
                }
                curFile.write(b);
                maxLength++;

                b = inputFile.read();
            }
        } catch (Exception e) {
            System.out.println("ERROR::something's gone wrong");
            System.exit(1);
        }
    }
    public static void readCurFileAndWriteOutputFile(RandomAccessFile curFile){
        long length = 0;
        try {
            length = curFile.length();
            while(length > 0) {
                curFile.seek(length - 1);
                int b = curFile.read();
                outputFile.write(b);
                length--;
            }

            curFile.close();
            File cur = new File("file" + cntFiles.toString());
            cur.delete();
        } catch (IOException e) {
            System.out.println("ERROR::something's gone wrong");
            System.exit(1);
        }
    }

    private static void writeFile(){
        while(cntFiles > 0){
            RandomAccessFile curFile = tempFiles.get(--cntFiles);

            readCurFileAndWriteOutputFile(curFile);
        }
    }

    private static void closeFiles(){
        try {
            outputFile.close();
            inputFile.close();
        } catch (IOException e) {
            System.out.println("ERROR::something's gone wrong");
            System.exit(1);
        }
    }

    public static void main(String[] args){
        openFiles(args);
        readFile();
        writeFile();
        closeFiles();
    }
}