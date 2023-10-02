package nl.hu.dp;


import classes.*;

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
        testAdresDAO(adao, rdao);
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
            reiziger = new Reiziger(901, "S", "", "Boers", java.sql.Date.valueOf("1981-03-14"));
            rdao.save(reiziger);
        }

        Adres nieuwAdres = new Adres(21, postcode, huisnummer, straat, woonplaats, reiziger.getId());
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
}