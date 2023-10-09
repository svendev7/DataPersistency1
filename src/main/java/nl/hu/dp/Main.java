package nl.hu.dp;


import classes.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;


public class Main {
    private static Connection connection;
    /**
     * P2. Reiziger DAO: persistentie van een klasse
     * <p>
     * Deze methode test de CRUD-functionaliteit van de Reiziger DAO
     *
     * @throws SQLException
     */

    public static void main(String[] args) throws SQLException {
        getConnection();
        System.out.println("Hello world!");
        ReizigerDAO rdao = new ReizigerDAOPsql(connection);
        AdresDAO adao = new AdresDAOPsql(connection);
        OVChipkaartDAO ovdao = new OVChipkaartDAOPsql(connection);
        ProductDAO pdao = new ProductDAOPsql(connection);
//        testAdresDAO(adao, rdao);
        testReizigerOVChipkaartRelationship(rdao, ovdao);
//        testReizigerDAO(rdao);
//        testProductAndOVChipkaart(pdao, ovdao, rdao);
    }
    private static Connection getConnection() throws SQLException {
        if (connection == null) {
            String url =
                    "jdbc:postgresql://localhost:5432/ovchip?user=postgres&password=3609";
            connection = DriverManager.getConnection(url);
        }
        return connection;
    }
    private static void closeConnection() throws
            SQLException {

        if (connection != null) {
            connection.close();
            connection = null;
        }
    }
    private static void testAdresDAO(AdresDAO adao, ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test AdresDAO -------------");

        List<Adres> adressen = adao.findAll();
        System.out.println("[Test] AdresDAO.findAll() geeft de volgende adressen:");
        for (Adres a : adressen) {
            System.out.println(a);
        }
        System.out.println();

        String postcode = "1234 AB";
        String huisnummer = "42";
        String straat = "Main Street";
        String woonplaats = "City";

        Reiziger reiziger = rdao.findById(880);
        if (reiziger != null) {
            reiziger = new Reiziger(906, "S", "", "Boers", java.sql.Date.valueOf("1981-03-14"));
            rdao.save(reiziger);
        }

        Adres nieuwAdres = new Adres(26, postcode, huisnummer, straat, woonplaats, reiziger.getId());
        adao.save(nieuwAdres);

        // Update the Reiziger with the new Adres
        reiziger.setAdres(nieuwAdres);
        rdao.update(reiziger); // Update the Reiziger to associate it with the new Adres

        System.out.println("[Test] Reiziger met adres:");
        System.out.println(reiziger);

        System.out.println("\n[Test] Adressen na toevoegen van adres aan reiziger:");
        adressen = adao.findAll();
        for (Adres a : adressen) {
            System.out.println(a);
        }

        closeConnection();
    }
    private static void testReizigerDAO(ReizigerDAO rdao) throws SQLException {

        System.out.println("\n---------- Test ReizigerDAO -------------");

        // Haal alle reizigers op uit de database
        List<Reiziger> reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();

        // Maak een nieuwe reiziger aan en persisteer deze in de database
        String gbdatum = "1981-03-14";
        Reiziger sietske = new Reiziger(1005, "S", "", "Boers", java.sql.Date.valueOf(gbdatum));
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        // Voeg aanvullende tests van de ontbrekende CRUD-operaties in.
    }
    private static void testReizigerOVChipkaartRelationship(ReizigerDAO rdao, OVChipkaartDAO ovdao) throws SQLException {
        System.out.println("\n---------- Test Reiziger and OVChipkaart Relationship -------------");

        // Create a new Reiziger
        Reiziger reiziger = new Reiziger(1009, "T", "van", "Dijk", java.sql.Date.valueOf("1990-05-20"));
        rdao.save(reiziger);

        // Create a new OVChipkaart for the Reiziger
        OVChipkaart ovChipkaart = new OVChipkaart(12348, java.sql.Date.valueOf("2024-12-31"), 2, BigDecimal.valueOf(50.0), reiziger);
        ovdao.save(ovChipkaart);
        OVChipkaart ovChipkaart1 = new OVChipkaart(12349, java.sql.Date.valueOf("2024-12-31"), 2, BigDecimal.valueOf(50.0), reiziger);
        ovdao.save(ovChipkaart1);

        // Find Reiziger by OVChipkaart
        List<Reiziger> reizigers = rdao.findByOVChipkaart(ovChipkaart);
        System.out.println("[Test] Reizigers with OVChipkaart:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }

        // Find OVChipkaart by Reiziger

        List<OVChipkaart> foundOVChipkaarten = ovdao.findByReiziger(reiziger);
        System.out.println("[Test] OVChipkaart(s) for Reiziger:");
        for (OVChipkaart ov : foundOVChipkaarten) {
            System.out.println(ov);
        }

    }
    private static void testProductAndOVChipkaart(ProductDAO pdao, OVChipkaartDAO ovdao, ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test Product and OVChipkaart -------------");

        Product product1 = new Product(17, "Product A", "Description A", 10);
        pdao.save(product1);
        Reiziger reiziger = new Reiziger(669, "T", "van", "Dijk", java.sql.Date.valueOf("1990-05-20"));
        rdao.save(reiziger);
        OVChipkaart ovChipkaart1 = new OVChipkaart(28, java.sql.Date.valueOf("2024-12-31"), 2, BigDecimal.valueOf(50.0), reiziger);
        ovdao.save(ovChipkaart1);

        product1.addOVChipkaart(ovChipkaart1);
        pdao.update(product1);

        List<Product> productsForOVChipkaart1 = pdao.findByOVChipkaart(ovChipkaart1);
        System.out.println("[Test] Products associated with OVChipkaart:");
        for (Product product : productsForOVChipkaart1) {
            System.out.println(product);
        }

        List<OVChipkaart> ovChipkaartenForProduct1 = ovdao.findByProduct(product1);
        System.out.println("[Test] OVChipkaarten associated with Product 1:");
        for (OVChipkaart ovChipkaart : ovChipkaartenForProduct1) {
            System.out.println(ovChipkaart);
        }
    }
}