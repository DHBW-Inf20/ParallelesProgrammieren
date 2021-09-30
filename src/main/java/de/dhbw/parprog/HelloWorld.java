package de.dhbw.parprog;

public class HelloWorld {

    public static void main(String[] args) {
        Lecture lecture = new Lecture("Paralleles Programmieren");
        System.out.println("Willkommen zur Vorlesung " + lecture.getName());
    }
}
