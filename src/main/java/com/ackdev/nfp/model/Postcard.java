package com.ackdev.nfp.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="nfp_epostcard")
public class Postcard {

    public Postcard() {

    }

    @Id
    @Column
    private
    String id;
    @Column
    private
    Integer year;
    @Column
    private
    String company_name;
    @Column
    private
    String GrossReceiptsUnder25k;
    @Column
    private
    String Terminated;
    @Column
    private
    String TaxPeriodBeginDate;
    @Column
    private
    String TaxPeriodEndDate;
    @Column
    private
    String WebsiteURL;
    @Column
    private
    String OfficerName;
    @Column
    private
    String OfficerAddressLine1;
    @Column
    private
    String OfficerAddressLine2;
    @Column
    private
    String OfficerAddressCity;
    @Column
    private
    String Province;
    @Column
    private
    String State;
    @Column
    private
    String PostalCode;
    @Column
    private
    String Country;
    @Column
    private
    String OrgAddressLine1;
    @Column
    private
    String OrgAddressLine2;
    @Column
    private
    String OrgCity;
    @Column
    private
    String OrgProvince;
    @Column
    private
    String OrgState;
    @Column
    private
    String OrgPostalCode;
    @Column
    private
    String OrgCountry;
    @Column
    private
    String DBA1;
    @Column
    private
    String DBA2;
    @Column
    private
    String DBA3;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getGrossReceiptsUnder25k() {
        return GrossReceiptsUnder25k;
    }

    public void setGrossReceiptsUnder25k(String grossReceiptsUnder25k) {
        GrossReceiptsUnder25k = grossReceiptsUnder25k;
    }

    public String getTerminated() {
        return Terminated;
    }

    public void setTerminated(String terminated) {
        Terminated = terminated;
    }

    public String getTaxPeriodBeginDate() {
        return TaxPeriodBeginDate;
    }

    public void setTaxPeriodBeginDate(String taxPeriodBeginDate) {
        TaxPeriodBeginDate = taxPeriodBeginDate;
    }

    public String getTaxPeriodEndDate() {
        return TaxPeriodEndDate;
    }

    public void setTaxPeriodEndDate(String taxPeriodEndDate) {
        TaxPeriodEndDate = taxPeriodEndDate;
    }

    public String getWebsiteURL() {
        return WebsiteURL;
    }

    public void setWebsiteURL(String websiteURL) {
        WebsiteURL = websiteURL;
    }

    public String getOfficerName() {
        return OfficerName;
    }

    public void setOfficerName(String officerName) {
        OfficerName = officerName;
    }

    public String getOfficerAddressLine1() {
        return OfficerAddressLine1;
    }

    public void setOfficerAddressLine1(String officerAddressLine1) {
        OfficerAddressLine1 = officerAddressLine1;
    }

    public String getOfficerAddressLine2() {
        return OfficerAddressLine2;
    }

    public void setOfficerAddressLine2(String officerAddressLine2) {
        OfficerAddressLine2 = officerAddressLine2;
    }

    public String getOfficerAddressCity() {
        return OfficerAddressCity;
    }

    public void setOfficerAddressCity(String officerAddressCity) {
        OfficerAddressCity = officerAddressCity;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getPostalCode() {
        return PostalCode;
    }

    public void setPostalCode(String postalCode) {
        PostalCode = postalCode;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getOrgAddressLine1() {
        return OrgAddressLine1;
    }

    public void setOrgAddressLine1(String orgAddressLine1) {
        OrgAddressLine1 = orgAddressLine1;
    }

    public String getOrgAddressLine2() {
        return OrgAddressLine2;
    }

    public void setOrgAddressLine2(String orgAddressLine2) {
        OrgAddressLine2 = orgAddressLine2;
    }

    public String getOrgCity() {
        return OrgCity;
    }

    public void setOrgCity(String orgCity) {
        OrgCity = orgCity;
    }

    public String getOrgProvince() {
        return OrgProvince;
    }

    public void setOrgProvince(String orgProvince) {
        OrgProvince = orgProvince;
    }

    public String getOrgState() {
        return OrgState;
    }

    public void setOrgState(String orgState) {
        OrgState = orgState;
    }

    public String getOrgPostalCode() {
        return OrgPostalCode;
    }

    public void setOrgPostalCode(String orgPostalCode) {
        OrgPostalCode = orgPostalCode;
    }

    public String getOrgCountry() {
        return OrgCountry;
    }

    public void setOrgCountry(String orgCountry) {
        OrgCountry = orgCountry;
    }

    public String getDBA1() {
        return DBA1;
    }

    public void setDBA1(String DBA1) {
        this.DBA1 = DBA1;
    }

    public String getDBA2() {
        return DBA2;
    }

    public void setDBA2(String DBA2) {
        this.DBA2 = DBA2;
    }

    public String getDBA3() {
        return DBA3;
    }

    public void setDBA3(String DBA3) {
        this.DBA3 = DBA3;
    }
}
