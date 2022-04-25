package com.example.interviewcoach;

/**
 * Contact model for displaying user Contacts
 *
 * @author Emmy
 */
public class Contact {
    String name;
    String number;


    Contact() {

    }

    Contact(String name, String number) {
        this.name = name;
        this.number = number;
    }


    //Getters & setters.
    public String setName(String name) {
        return this.name = name;
    }

    public String setNumber(String number) {
        return this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }


    //Override toString method to display contact name & number.
    @Override
    public String toString() {
        return String.format("Contact name: " + name + "\n" + "Contact number: " + number + "\n\n");
    }
}
