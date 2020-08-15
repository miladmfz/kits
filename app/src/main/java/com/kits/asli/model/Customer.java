package com.kits.asli.model;

import com.google.gson.annotations.SerializedName;

public class Customer {

    @SerializedName("CustomerCode")
    private Integer CustomerCode;
    @SerializedName("PriceTip")
    private Integer PriceTip;
    @SerializedName("CustomerName")
    private String CustomerName;
    @SerializedName("Address")
    private String Address;
    @SerializedName("Manager")
    private String Manager;
    @SerializedName("Mobile")
    private String Mobile;
    @SerializedName("Phone")
    private String Phone;
    @SerializedName("Delegacy")
    private String Delegacy;
    @SerializedName("CityName")
    private String CityName;
    @SerializedName("CityCode")
    private String CityCode;
    @SerializedName("Bestankar")
    private Integer Bestankar;
    @SerializedName("Active")
    private Integer Active;
    @SerializedName("CentralPrivateCode")
    private Integer CentralPrivateCode;
    @SerializedName("EtebarNaghd")
    private Integer EtebarNaghd;
    @SerializedName("EtebarCheck")
    private Integer EtebarCheck;
    @SerializedName("Takhfif")
    private Integer Takhfif;
    @SerializedName("MobileName")
    private String MobileName;
    @SerializedName("Email")
    private String Email;
    @SerializedName("Fax")
    private String Fax;
    @SerializedName("ZipCode")
    private String ZipCode;
    @SerializedName("PostCode")
    private String PostCode;
    @SerializedName("ErrCode")
    private String ErrCode;
    @SerializedName("ErrDesc")
    private String ErrDesc;
    @SerializedName("IsExist")
    private String IsExist;
    @SerializedName("KodeMelli")
    private String KodeMelli;


    public String getErrDesc() {
        return ErrDesc;
    }

    public void setErrDesc(String errDesc) {
        ErrDesc = errDesc;
    }

    public String getIsExist() {
        return IsExist;
    }

    public void setIsExist(String isExist) {
        IsExist = isExist;
    }

    public String getKodeMelli() {
        return KodeMelli;
    }

    public void setKodeMelli(String kodeMelli) {
        KodeMelli = kodeMelli;
    }

    public String getErrCode() {
        return ErrCode;
    }

    public void setErrCode(String errCode) {
        ErrCode = errCode;
    }

    public String getCityCode() {
        return CityCode;
    }

    public void setCityCode(String cityCode) {
        CityCode = cityCode;
    }

    public String getMobileName() {
        return MobileName;
    }

    void setMobileName(String mobileName) {
        MobileName = mobileName;
    }

    public String getEmail() {
        return Email;
    }

    void setEmail(String email) {
        Email = email;
    }

    public String getFax() {
        return Fax;
    }

    void setFax(String fax) {
        Fax = fax;
    }

    public String getZipCode() {
        return ZipCode;
    }

    void setZipCode(String zipCode) {
        ZipCode = zipCode;
    }

    public String getPostCode() {
        return PostCode;
    }

    void setPostCode(String postCode) {
        PostCode = postCode;
    }

    public Integer getBestankar() {
        return Bestankar;
    }

    void setBestankar(Integer bestankar) {
        Bestankar = bestankar;
    }

    public Integer getActive() {
        return Active;
    }

    void setActive(Integer active) {
        Active = active;
    }

    public Integer getCentralPrivateCode() {
        return CentralPrivateCode;
    }

    void setCentralPrivateCode(Integer centralPrivateCode) {
        CentralPrivateCode = centralPrivateCode;
    }

    public Integer getEtebarNaghd() {
        return EtebarNaghd;
    }

    void setEtebarNaghd(Integer etebarNaghd) {
        EtebarNaghd = etebarNaghd;
    }

    public Integer getEtebarCheck() {
        return EtebarCheck;
    }


    void setEtebarCheck(Integer etebarCheck) {
        EtebarCheck = etebarCheck;
    }

    public Integer getTakhfif() {
        return Takhfif;
    }

    void setTakhfif(Integer takhfif) {
        Takhfif = takhfif;
    }

    public String getDelegacy() {
        return Delegacy;
    }

    void setDelegacy(String delegacy) {
        Delegacy = delegacy;
    }

    public String getCityName() {
        return CityName;
    }

    void setCityName(String cityName) {
        CityName = cityName;
    }

    public Integer getCustomerCode() {
        return CustomerCode;
    }

    void setCustomerCode(Integer customerCode) {
        CustomerCode = customerCode;
    }

    public Integer getPriceTip() {
        return PriceTip;
    }

    void setPriceTip(Integer priceTip) {
        PriceTip = priceTip;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getAddress() {
        return Address;
    }

    void setAddress(String address) {
        Address = address;
    }

    public String getManager() {
        return Manager;
    }

    void setManager(String manager) {
        Manager = manager;
    }

    public String getMobile() {
        return Mobile;
    }

    void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getPhone() {
        return Phone;
    }

    void setPhone(String phone) {
        Phone = phone;
    }


}
