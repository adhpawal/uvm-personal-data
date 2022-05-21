package org.uvm.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PersonalDataValdiationCheck {

    private final static Logger LOGGER = Logger.getLogger(PersonalDataValdiationCheck.class.getName());

    public void test(){
        User user = new User();
        user.setSsn("1234567890");
        user.setFullName("John Doe");
        System.out.println(user.getSsn());
    }
}
