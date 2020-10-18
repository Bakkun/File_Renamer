package com.renamer;

import com.renamer.exception.EmptyListException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Renamer {

    private static final String CHARACTERS_FOR_GENERATION = "ABCDEFGHIJKLMNOP";
    private static final String NUMBERS_FOR_GENERATION = "0123456789";
    private static final Pattern EXTENSION_PATTERN = Pattern.compile("^\\.[\\w]+$");
    private static Random random = new Random();
    private static Set<String> extensions;
    private static List<String> files;

    private static void setExtensions(Set<String> extensions) {
        Renamer.extensions = new HashSet<>(extensions);
    }

    private static void setFiles(List<String> files) {
        Renamer.files = new ArrayList<>(files);
    }

    public static void main(String ...args) {
        Scanner in = new Scanner(System.in);

        System.out.print("Enter folder path: ");
        String path = in.nextLine();

        System.out.print("Enter the length of the file name: ");
        int nameLength = Integer.parseInt(in.nextLine());

        System.out.print("Enter a list of extensions with which you want to rename the files: ");
        String listOfExtensions = in.nextLine();

        in.close();

        if ("all".compareTo(listOfExtensions) == 0 || listOfExtensions.isEmpty()) {
            setFiles(getAllFiles(path));
        } else {
            addExtensions(listOfExtensions);
            setFiles(getAllFilesWithExtensions(path, extensions));
        }
        renameAll(files, nameLength);

        System.out.println("Files were renamed successfully!");
    }

    private static void addExtensions(String inputString) {
        Set<String> temp = new HashSet<>(Arrays.asList(inputString.split(", ")));
        setExtensions(temp.stream().filter(EXTENSION_PATTERN.asPredicate()).collect(Collectors.toSet()));
    }

    private static String generateRandomName(int length) {
        StringBuilder newName = new StringBuilder(length);
        int firstPart = length/2;

        if (length%2 != 0) {
            firstPart = length/2 + 1;
        }

        int secondPart = length - firstPart;

        for (int i = 0; i < firstPart; i++) {
            newName.append(CHARACTERS_FOR_GENERATION.charAt(random.nextInt(CHARACTERS_FOR_GENERATION.length())));
        }
        for (int i = 0; i < secondPart; i++) {
            newName.append(NUMBERS_FOR_GENERATION.charAt(random.nextInt(NUMBERS_FOR_GENERATION.length())));
        }

        return newName.toString();
    }

    public static void rename(String pathToFile, int length) {
        Path source = Paths.get(pathToFile);
        String fileName = source.getFileName().toString();

        try {
            Files.move(source, source.resolveSibling(generateRandomName(length) + fileName.substring(fileName.lastIndexOf('.'))));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void renameAll(List<String> files, int length) {
        for (String file : files) {
            rename(file, length);
        }
    }

    private static List<String> getAllFilesWithExtensions(String pathToDirectory, Set<String> setOfExtensions) {
        List<String> result = new ArrayList<>();

        try {
            result = Files.list(Paths.get(pathToDirectory))
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(extension -> setOfExtensions.contains(extension.substring(extension.lastIndexOf('.'))))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result.isEmpty()) {
            try {
                throw new EmptyListException();
            } catch (EmptyListException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private static List<String> getAllFiles(String pathToDirectory) {
        List<String> result = null;

        try {
            result = Files.list(Paths.get(pathToDirectory))
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
