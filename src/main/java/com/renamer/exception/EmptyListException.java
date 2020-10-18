package com.renamer.exception;

public class EmptyListException extends Exception {
    public EmptyListException () {
        super("There are no files in the specified folder with the specified extensions!");
    }
}
