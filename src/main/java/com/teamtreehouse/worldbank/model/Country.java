package com.teamtreehouse.worldbank.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by Rumy on 6/22/2017.
 */

@Entity
public class Country {

    @Id
    @Column(columnDefinition="VARCHAR(3)")
    private String code;

    @Column(columnDefinition="VARCHAR(32)")
    private String name;

    @Column(length = 11,precision = 8)
    private BigDecimal internetUsers;

    @Column(length = 11,precision = 8)
    private BigDecimal adultLiteracyRate;

    public Country(){}

    public Country(CountryBuilder countryBuilder) {
        this.code=countryBuilder.code;
        this.name=countryBuilder.name;
        this.internetUsers=countryBuilder.internetUsers;
        this.adultLiteracyRate=countryBuilder.adultLiteracyRate;
    }

    @Override
    public String toString() {
        String internetUsersString,adultLiteracyRateString;
        if (internetUsers == null) {
            internetUsersString = "--";
        } else {
            internetUsersString = (internetUsers.setScale(2,BigDecimal.ROUND_HALF_EVEN)).toString();
        }
        if (adultLiteracyRate == null) {
            adultLiteracyRateString = "--";
        } else {
            adultLiteracyRateString = (adultLiteracyRate.setScale(2,BigDecimal.ROUND_HALF_EVEN)).toString();
        }
        return String.format("%-3s  %-35s %15s %15s %n",code, name, internetUsersString, adultLiteracyRateString);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getInternetUsers() {
        return internetUsers;
    }

    public void setInternetUsers(BigDecimal internetUsers) {
        this.internetUsers = internetUsers;
    }

    public BigDecimal getAdultLiteracyRate() {
        return adultLiteracyRate;
    }

    public void setAdultLiteracyRate(BigDecimal adultLiteracyRate) {
        this.adultLiteracyRate = adultLiteracyRate;
    }



    public static class CountryBuilder {

        private String code;
        private String name;
        private BigDecimal internetUsers;
        private BigDecimal adultLiteracyRate;

        public CountryBuilder(String code){
            this.code=code;
        }

        public CountryBuilder withName(String name){
            this.name=name;
            return this;
        }

        public CountryBuilder withInternetUsers(BigDecimal internetUsers){
            this.internetUsers=internetUsers;
            return this;

        }

        public CountryBuilder withAdultLiteracyRate(BigDecimal adultLiteracyRate){
            this.adultLiteracyRate=adultLiteracyRate;
            return this;

        }

        public Country build(){
            return new Country(this);

        }

    }
}
