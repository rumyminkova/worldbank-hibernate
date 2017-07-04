package com.teamtreehouse.worldbank;

import com.teamtreehouse.worldbank.model.Country;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static com.teamtreehouse.worldbank.Application.*;

/**
 * Created by Rumy on 6/24/2017.
 */
public class UserMenu {
    private BufferedReader mReader;
    private Map<String, String> menu;


    public UserMenu() {
        menu = new HashMap<String, String>();
        menu.put("1", "View data table");
        menu.put("2", "View statistics");
        menu.put("3", "Add country");
        menu.put("4", "Edit country");
        menu.put("5", "Delete country");
        menu.put("9", "Exit");

        mReader = new BufferedReader(new InputStreamReader(System.in));

    }

    private String promptAction(Map<String, String> menu) throws IOException {
        System.out.println();
        for (Map.Entry<String, String> options : menu.entrySet()) {
            System.out.printf("%s. 	%s %n", options.getKey(), options.getValue());

        }
        System.out.println();
        System.out.print("What would you like to do: ");
        String choice = mReader.readLine();
        return choice.trim().toLowerCase();

    }

    public void run() {
        String choice = "";
        do {
            try {
                System.out.println();
                System.out.println("MAIN MENU");
                choice = promptAction(menu);
                switch (choice) {
                    case "1":
                        printData(fetchAllCounties());
                        break;
                    case "2":
                        stats(fetchAllCounties());
                        break;
                    case "3":
                        addNewCountry();
                        break;
                    case "4":
                        editCountry();
                        break;
                    case "5":
                        deleteCountry();
                        break;
                    case "9":

                        break;
                    default:
                        System.out.printf("Unknown choice. Try again %n%n%n");

                }

            } catch (IOException ioe) {
                System.out.println("Problem with input!");
                ioe.printStackTrace();
            }

        } while (!choice.equals("9"));

    }


    private void printData(List<Country> countries) {
       printHeader();
        for (Country country : countries) {
           System.out.printf(country.toString());
        }
     }


    private void addNewCountry() throws IOException {
        String newName="";
        BigDecimal newInternetUsers,newAdultLiteracyRate;

        System.out.print("Enter code for the country you want to add: ");
        String code = mReader.readLine().trim().toUpperCase();
        System.out.println("New name (max length 32)");
        newName = validStringEntry(32);
        System.out.println("InternetUsers ");
        newInternetUsers=validDecimalEntry();
        System.out.println("AdultLiteracyRate ");
        newAdultLiteracyRate=validDecimalEntry();

        Country c=new Country.CountryBuilder(code).withName(newName).withInternetUsers(newInternetUsers).withAdultLiteracyRate(newAdultLiteracyRate).build();
        save(c);
    }


    private void deleteCountry() throws IOException {
        System.out.print("Enter the code of the country you want to delete: ");
        String code = mReader.readLine().trim().toUpperCase();
        Country country = findCountryByCode(code);
        if (country == null){
            System.out.printf("Country not found");
        }else {
            printHeader();
            System.out.printf(country.toString());
            delete(country);
            System.out.println();
            System.out.println("DELETED!");
        }
  }


    private void editCountry() throws IOException {
        String newName="";
        BigDecimal newInternetUsers,newAdultLiteracyRate;

        System.out.print("Enter the code of the country you want to edit: ");
        String code = mReader.readLine().trim().toUpperCase();
        Country country = findCountryByCode(code);

        if (country == null){
            System.out.printf("Invalid code %n");
        }else {
            printHeader();
            System.out.printf(country.toString());
            System.out.println();

            System.out.println("New name (max length 32)");
            newName = validStringEntry(32);
            System.out.println("InternetUsers ");
            newInternetUsers=validDecimalEntry();
            System.out.println("AdultLiteracyRate ");
            newAdultLiteracyRate=validDecimalEntry();

            country.setName(newName);
            country.setInternetUsers(newInternetUsers);
            country.setAdultLiteracyRate(newAdultLiteracyRate);
            update(country);
        }

    }


    private void stats(List<Country> countries) {
        BigDecimal min,max;

        System.out.println();
        System.out.println("------- STATISTICS --------");
        System.out.println();

        final Comparator<Country> compInternetUsers = (p1, p2) -> p1.getInternetUsers().compareTo(p2.getInternetUsers());
        Country cMin = countries.stream().filter(c -> c.getInternetUsers() != null).min(compInternetUsers).get();
        Country cMax = countries.stream().filter(c -> c.getInternetUsers() != null).max(compInternetUsers).get();

        min=cMin.getInternetUsers().setScale(2,BigDecimal.ROUND_HALF_EVEN);
        max=cMax.getInternetUsers().setScale(2,BigDecimal.ROUND_HALF_EVEN);


        System.out.println("Internet users");
        System.out.printf(" Minimum = %s  country: %s %n",  min.toString(),cMin.getName());
        System.out.printf(" Maximum = %s  country: %s %n%n",max.toString(),cMax.getName());


        final Comparator<Country> compAdultLiteracyRate = (p1, p2) -> p1.getAdultLiteracyRate().compareTo(p2.getAdultLiteracyRate());
        Country cMinAL = countries.stream().filter(c -> c.getAdultLiteracyRate() != null).min(compAdultLiteracyRate).get();
        Country cMaxAL = countries.stream().filter(c -> c.getAdultLiteracyRate() != null).max(compAdultLiteracyRate).get();

        min=cMinAL.getAdultLiteracyRate().setScale(2,BigDecimal.ROUND_HALF_EVEN);
        max=cMaxAL.getAdultLiteracyRate().setScale(2,BigDecimal.ROUND_HALF_EVEN);

        System.out.println("Adult literacy rate");
        System.out.printf(" Minimum = %s  country: %s %n", min.toString(),cMinAL.getName());
        System.out.printf(" Maximum = %s  country: %s %n%n", max.toString(),cMaxAL.getName());

        BigDecimal cc=CalculateCorrelation(countries).setScale(2,BigDecimal.ROUND_HALF_EVEN);



        System.out.printf("Correlation coefficient = %s %n",cc.toString());
    }



    private BigDecimal CalculateCorrelation(List<Country> countries) {

        List<Country> countriesNonNullData = countries.stream()
                        .filter(c -> (c.getInternetUsers() != null) && (c.getAdultLiteracyRate() != null))
                        .collect(Collectors.toList());

        BigDecimal n=new BigDecimal(countriesNonNullData.size());

        BigDecimal sumX=BigDecimal.ZERO;
        BigDecimal sumY=BigDecimal.ZERO;
        BigDecimal sumX2=BigDecimal.ZERO;
        BigDecimal sumY2=BigDecimal.ZERO;
        BigDecimal sumXY=BigDecimal.ZERO;
        BigDecimal X,Y;

        for(Country country : countriesNonNullData){
            X=country.getInternetUsers();
            Y=country.getAdultLiteracyRate();
            sumX=sumX.add(X);
            sumY=sumY.add(Y);
            sumX2=sumX2.add(X.multiply(X));
            sumY2=sumY2.add(Y.multiply(Y));
            sumXY=sumXY.add(X.multiply(Y));
        }

        BigDecimal cCorrelation=(n.multiply(sumXY)).subtract(sumX.multiply(sumY));

        BigDecimal cCorrelation1=sqrt(((n.multiply(sumX2)).subtract(sumX.multiply(sumX))).multiply((n.multiply(sumY2)).subtract(sumY.multiply(sumY))));

        cCorrelation=cCorrelation.divide(cCorrelation1,8,BigDecimal.ROUND_HALF_EVEN);

        return cCorrelation;

    }


    public static BigDecimal sqrt(BigDecimal x) {

        return BigDecimal.valueOf(StrictMath.sqrt(x.doubleValue()));
    }


    private void printHeader(){
        System.out.println();
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("Code Country                               Internet Users        Literacy\n" +
                "--------------------------------------------------------------------------");
   }

   private String validStringEntry(int maxLength) throws IOException{
      String stringEntry;
      boolean validData=false;
      do {
         System.out.print(" Enter data: ");
         stringEntry = mReader.readLine();
          validData=(!stringEntry.equals(""))&&(stringEntry.length()<=maxLength);
          if (!validData) {
              System.out.printf("Invalid data! %n");
          }
     }while(!validData);

      return stringEntry;
   }


    private BigDecimal validDecimalEntry() throws IOException{
        BigDecimal result=null;
        boolean isNumber=false;

        while (!isNumber) {
            System.out.print("  Enter data: ");
            String input=mReader.readLine().trim();
            if (input.isEmpty()) break;
             try {
                    result = new BigDecimal(input);
                    result = result.setScale(8, BigDecimal.ROUND_HALF_EVEN);
                    if (result.precision() <= 11) {
                        isNumber = true;
                    } else {
                        System.out.println("The number is to big!");
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid data!");
                }

       }
      return result;
   }


}
