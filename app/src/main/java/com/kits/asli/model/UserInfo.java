package com.kits.asli.model;

public class UserInfo {

    private String Email;
    private String NameFamily;
    private String Address;
    private String Mobile;
    private String Phone;
    private String BirthDate;
    private String MelliCode;
    private String PostalCode;
    private String ActiveCode;

    public String getBrokerCode() {
        return BrokerCode;
    }

    public void setBrokerCode(String brokerCode) {
        BrokerCode = brokerCode;
    }

    private String BrokerCode;


    String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    String getNameFamily() {
        return NameFamily;
    }

    public void setNameFamily(String nameFamily) {
        NameFamily = nameFamily;
    }

    String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    String getPhone() {
        return Phone;
    }


    public void setPhone(String phone) {
        Phone = phone;
    }


    String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String birthDate) {
        BirthDate = birthDate;
    }

    String getMelliCode() {
        return MelliCode;
    }

    public void setMelliCode(String melliCode) {
        MelliCode = melliCode;
    }

    String getPostalCode() {
        return PostalCode;
    }

    public void setPostalCode(String postalCode) {
        PostalCode = postalCode;
    }


    public String getActiveCode() {
        return ActiveCode;
    }

    void setActiveCode(String activeCode) {
        ActiveCode = activeCode;
    }

}
