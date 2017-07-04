package com.teamtreehouse.worldbank;

import com.teamtreehouse.worldbank.model.Country;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Created by Rumy on 6/22/2017.
 */
public class Application {
    //Hold a reusable reference to a SessionFactory
    private static final SessionFactory sessionFactory=buildSessionFactory();


    private static SessionFactory buildSessionFactory(){
        //Create a StandardServiceRegistry
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static void main(String[] args) {

        UserMenu userMenu=new UserMenu();
        userMenu.run();
    //    fetchAllCounties().stream().forEach(System.out::println);

    }


    public static List<Country> fetchAllCounties(){
        //open a session
        Session session=sessionFactory.openSession();

        //  Create CriteriaBuilder
        CriteriaBuilder builder = session.getCriteriaBuilder();

        //  Create CriteriaQuery
        CriteriaQuery<Country> criteria = builder.createQuery(Country.class);

        // Specify criteria root
        criteria.from(Country.class);

        //  Execute query
        List<Country> countries = session.createQuery(criteria).getResultList();

        // Close the session
        session.close();

        return countries;
    }


    public static Country findCountryByCode(String code){
        //open a session
        Session session=sessionFactory.openSession();

        //retrieve the object
        Country country=session.get(Country.class,code);

        // Close the session
        session.close();
        return country;

    }


    public static void update(Country country){

        // open session
        Session session=sessionFactory.openSession();
        Transaction tx = null;

        try {
            //begin transaction
            tx = session.beginTransaction();
            //use session to update the contact
            session.update(country);
            //commit the transaction
            tx.commit();
        }catch  (RuntimeException e) {
            if (tx != null) tx.rollback();
            System.out.println("Problem updating!");
        }  finally {
            session.close();
        }

    }

   public static void save(Country country){

        //open a session
       Session session=sessionFactory.openSession();
       Transaction tx = null;

       try {
           //begin a transaction
           tx =session.beginTransaction();
           session.save(country);
           tx.commit();
       }catch (RuntimeException e) {
           if (tx != null) tx.rollback();
           System.out.println("This code already exists!!!");
       }  finally {
           session.close();
       }

    }

    public static void delete(Country country){

        // open session
        Session session=sessionFactory.openSession();

        //begin transaction
        session.beginTransaction();


        //use session to update the contact
        session.delete(country);

        //commit the transaction
        session.getTransaction().commit();

        //close the session
        session.close();

    }
}
