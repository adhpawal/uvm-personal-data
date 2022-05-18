package org.uvm.impl;

import org.uvm.PersonalData;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {

    private final static Logger LOGGER = Logger.getLogger(User.class.getName());

    @PersonalData
    private String ssn;

    private Date dateOfBirth;
    private String fullName;

    private Address address;

    private Contact contact;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        System.out.println("Pawal");
        this.contact = contact;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void test(){
        System.out.println(this.ssn);
        LOGGER.log(Level.INFO, this.ssn);
        LOGGER.log(Level.INFO, this.fullName);
    }
}
