package nl.hu.dp;


import classes.Reiziger;
import classes.ReizigerDAO;
import classes.ReizigerDAOPsql;

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
        testReizigerDAO(rdao);
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
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", java.sql.Date.valueOf(gbdatum));
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        // Voeg aanvullende tests van de ontbrekende CRUD-operaties in.
        closeConnection();
    }
}