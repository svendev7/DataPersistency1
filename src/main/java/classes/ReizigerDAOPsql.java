package classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
public class ReizigerDAOPsql implements ReizigerDAO {
    private Connection connection;

    public ReizigerDAOPsql(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Reiziger> findAll() throws SQLException {
        List<Reiziger> reizigers = new ArrayList<>();
        String query = "SELECT reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum FROM reiziger";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Reiziger reiziger = createReizigerFromResultSet(resultSet);
                AdresDAO adresDAO = new AdresDAOPsql(connection);
                Adres adres = adresDAO.findByReiziger(reiziger.getId());
                reiziger.setAdres(adres);
                reizigers.add(reiziger);
            }
        }

        return reizigers;
    }

    @Override
    public Reiziger findById(int id) throws SQLException {
        String query = "SELECT reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum FROM reiziger WHERE reiziger_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return createReizigerFromResultSet(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<Reiziger> findByAchternaam(String achternaam) throws SQLException {
        List<Reiziger> reizigers = new ArrayList<>();
        String query = "SELECT * FROM reiziger WHERE achternaam = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, achternaam);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Reiziger reiziger = createReizigerFromResultSet(resultSet);
                    reizigers.add(reiziger);
                }
            }
        }

        return reizigers;
    }

    @Override
    public void save(Reiziger reiziger) throws SQLException {
        String query = "INSERT INTO reiziger (reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, reiziger.getId());
            statement.setString(2, reiziger.getVoorletters());
            statement.setString(3, reiziger.getTussenvoegsel());
            statement.setString(4, reiziger.getAchternaam());
            statement.setDate(5, reiziger.getGeboortedatum());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(Reiziger reiziger) throws SQLException {
        String query = "UPDATE reiziger SET voorletters = ?, tussenvoegsel = ?, achternaam = ?, geboortedatum = ? WHERE reiziger_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, reiziger.getVoorletters());
            statement.setString(2, reiziger.getTussenvoegsel());
            statement.setString(3, reiziger.getAchternaam());
            statement.setDate(4, reiziger.getGeboortedatum());
            statement.setInt(5, reiziger.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(Reiziger reiziger) throws SQLException {
        String query = "DELETE FROM reiziger WHERE reiziger_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, reiziger.getId());
            statement.executeUpdate();
        }
    }

    private Reiziger createReizigerFromResultSet(ResultSet resultSet) throws SQLException {
        int reiziger_id = resultSet.getInt("reiziger_id");
        String voorletters = resultSet.getString("voorletters");
        String tussenvoegsel = resultSet.getString("tussenvoegsel");
        String achternaam = resultSet.getString("achternaam");
        Date geboortedatum = resultSet.getDate("geboortedatum");


        return new Reiziger(reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum);
    }
    @Override
    public List<Reiziger> findByOVChipkaart(OVChipkaart ovChipkaart) {
        List<Reiziger> reizigers = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM reiziger " +
                            "WHERE reiziger_id IN (SELECT reiziger_id FROM ov_chipkaart WHERE kaart_nummer = ?)"
            );

            statement.setInt(1, ovChipkaart.getKaartNummer());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int reizigerId = resultSet.getInt("reiziger_id");
                String voorletters = resultSet.getString("voorletters");
                String tussenvoegsel = resultSet.getString("tussenvoegsel");
                String achternaam = resultSet.getString("achternaam");
                Date geboortedatum = resultSet.getDate("geboortedatum");

                Reiziger reiziger = new Reiziger(reizigerId, voorletters, tussenvoegsel, achternaam, geboortedatum);
                reizigers.add(reiziger);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle errors appropriately
        }

        return reizigers;
    }
    @Override
    public void saveAdres(Reiziger reiziger) throws SQLException {
        if (reiziger.getAdres() != null) {
            String query = "INSERT INTO adres (postcode, huisnummer, straat, woonplaats, reiziger_id) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                Adres adres = reiziger.getAdres();
                statement.setString(1, adres.getPostcode());
                statement.setString(2, adres.getHuisnummer());
                statement.setString(3, adres.getStraat());
                statement.setString(4, adres.getWoonplaats());
                statement.setInt(5, reiziger.getId()); // Assuming reiziger_id is the primary key of the reiziger table
                statement.executeUpdate();
            }
        }
    }

    @Override
    public void updateAdres(Reiziger reiziger) throws SQLException {
        if (reiziger.getAdres() != null) {
            String query = "UPDATE adres SET postcode = ?, huisnummer = ?, straat = ?, woonplaats = " +
                    "WHERE reiziger_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                Adres adres = reiziger.getAdres();
                statement.setString(1, adres.getPostcode());
                statement.setString(2, adres.getHuisnummer());
                statement.setString(3, adres.getStraat());
                statement.setString(4, adres.getWoonplaats());
                statement.setInt(5, reiziger.getId()); // Assuming reiziger_id is the primary key of the reiziger table
                statement.executeUpdate();
            }
        }
    }
}
