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
    private OVChipkaartDAO ovChipkaartDAO;
    private AdresDAO adresDAO;

    public ReizigerDAOPsql(Connection connection) {
        this.connection = connection;
        this.adresDAO = new AdresDAOPsql(connection);
        this.ovChipkaartDAO = new OVChipkaartDAOPsql(connection);
    }

    @Override
    public List<Reiziger> findAll() throws SQLException {
        List<Reiziger> reizigers = new ArrayList<>();
        String query = "SELECT r.reiziger_id, r.voorletters, r.tussenvoegsel, r.achternaam, r.geboortedatum, a.adres_id, a.postcode, a.huisnummer, a.straat, a.woonplaats " +
                "FROM reiziger r " +
                "LEFT JOIN adres a ON r.reiziger_id = a.reiziger_id";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Reiziger reiziger = createReizigerFromResultSet(resultSet);
                reizigers.add(reiziger);
            }
        }

        return reizigers;
    }
    @Override
    public Reiziger findById(int reiziger_id) throws SQLException {
        String query = "SELECT r.reiziger_id, r.voorletters, r.tussenvoegsel, r.achternaam, r.geboortedatum, a.adres_id, a.postcode, a.huisnummer, a.straat, a.woonplaats " +
                "FROM reiziger r " +
                "LEFT JOIN adres a ON r.reiziger_id = a.reiziger_id " +
                "WHERE r.reiziger_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, reiziger_id);
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
        String query = "SELECT r.reiziger_id, r.voorletters, r.tussenvoegsel, r.achternaam, r.geboortedatum, a.adres_id, a.postcode, a.huisnummer, a.straat, a.woonplaats " +
                "FROM reiziger r " +
                "LEFT JOIN adres a ON r.reiziger_id = a.reiziger_id " +
                "WHERE r.achternaam = ?";

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
            if (reiziger.getOvChipkaarten() != null) {
                for (OVChipkaart ovChipkaart : reiziger.getOvChipkaarten()) {
                    ovChipkaart.setReiziger(reiziger);
                    ovChipkaartDAO.save(ovChipkaart);
                }
            }
            if (reiziger.getAdres() != null) {
                reiziger.getAdres().setReiziger_id(reiziger.getId());
                adresDAO.save(reiziger.getAdres());
            }
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
            if (reiziger.getOvChipkaarten() != null) {
                for (OVChipkaart ovChipkaart : reiziger.getOvChipkaarten()) {
                    ovChipkaart.setReiziger(reiziger);
                    ovChipkaartDAO.update(ovChipkaart);
                }
            }

            if (reiziger.getAdres() != null) {
                reiziger.getAdres().setReiziger_id(reiziger.getId());
                adresDAO.update(reiziger.getAdres());
            }
        }
    }

    @Override
    public void delete(Reiziger reiziger) throws SQLException {
        if (reiziger.getAdres() != null) {
            adresDAO.delete(reiziger.getAdres());
        }

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

        String postcode = resultSet.getString("postcode");
        String huisnummer = resultSet.getString("huisnummer");
        String straat = resultSet.getString("straat");
        String woonplaats = resultSet.getString("woonplaats");
        int adres_id = resultSet.getInt("adres_id");

        Adres adres = new Adres(adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id);

        return new Reiziger(reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum, adres);
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
}
