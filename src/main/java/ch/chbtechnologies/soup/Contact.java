package ch.chbtechnologies.soup;

/**
 * Created by chb on 28.04.2015.
 */
public class Contact {
    private String name;
    private String phoneNumber;
    private String Address;

    public Contact(String name, String address, String phoneNumber) {
        this.name = name;
        Address = address;
        this.phoneNumber = phoneNumber;
    }

    public Contact(String phoneNumber, String name) {
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public Contact() {

    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
