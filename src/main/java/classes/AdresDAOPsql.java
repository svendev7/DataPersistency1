package classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdresDAOPsql implements AdresDAO {
    private Connection connection;

    public AdresDAOPsql(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Adres> findAll() throws SQLException {
        List<Adres> adressen = new ArrayList<>();
        String query = "SELECT * FROM adres";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int adres_id = resultSet.getInt("adres_id");
                String postcode = resultSet.getString("postcode");
                String huisnummer = resultSet.getString("huisnummer");
                String straat = resultSet.getString("straat");
                String woonplaats = resultSet.getString("woonplaats");
                int reiziger_id = resultSet.getInt("reiziger_id");

                Adres adres = new Adres(adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id);
                adressen.add(adres);
            }
        }

        return adressen;
    }

    @Override
    public Adres findById(int adres_id) throws SQLException {
        String query = "SELECT * FROM adres WHERE adres_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, adres_id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return createAdresFromResultSet(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public void save(Adres adres) throws SQLException {
        String query = "INSERT INTO adres (adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, adres.getAdres_id());
            statement.setString(2, adres.getPostcode());
            statement.setString(3, adres.getHuisnummer());
            statement.setString(4, adres.getStraat());
            statement.setString(5, adres.getWoonplaats());
            statement.setInt(6, adres.getReiziger_id());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(Adres adres) throws SQLException {
        String query = "UPDATE adres SET postcode = ?, huisnummer = ?, straat = ?, woonplaats = ?, reiziger_id = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, adres.getPostcode());
            statement.setString(2, adres.getHuisnummer());
            statement.setString(3, adres.getStraat());
            statement.setString(4, adres.getWoonplaats());
            statement.setInt(5, adres.getReiziger_id());
            statement.setInt(6, adres.getAdres_id());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(Adres adres) throws SQLException {
        String query = "DELETE FROM adres WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, adres.getAdres_id());
            statement.executeUpdate();
        }
    }

    @Override
    public Adres findByReiziger(int reiziger_id) throws SQLException {
        String query = "SELECT * FROM adres WHERE reiziger_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, reiziger_id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return createAdresFromResultSet(resultSet);
                }
            }
        }

        return null;
    }

    private Adres createAdresFromResultSet(ResultSet resultSet) throws SQLException {
        int adres_id = resultSet.getInt("adres_id");
        String postcode = resultSet.getString("postcode");
        String huisnummer = resultSet.getString("huisnummer");
        String straat = resultSet.getString("straat");
        String woonplaats = resultSet.getString("woonplaats");
        int reiziger_id = resultSet.getInt("reiziger_id");

        return new Adres(adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id);
    }
}