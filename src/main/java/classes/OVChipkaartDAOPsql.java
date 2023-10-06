package classes;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {
    private Connection connection;

    public OVChipkaartDAOPsql(Connection connection) {
        this.connection = connection;
    }

    @Override
    public OVChipkaart save(OVChipkaart ovChipkaart) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO ov_chipkaart (kaart_nummer, geldig_tot, klasse, saldo, reiziger_id) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setInt(1, ovChipkaart.getKaartNummer());
            statement.setDate(2, new java.sql.Date(ovChipkaart.getGeldigTot().getTime()));
            statement.setInt(3, ovChipkaart.getKlasse());
            statement.setBigDecimal(4, ovChipkaart.getSaldo());
            statement.setInt(5, ovChipkaart.getReiziger().getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating OVChipkaart failed, no rows affected.");
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                ovChipkaart.setKaartNummer(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating OVChipkaart failed, no ID obtained.");
            }

            return ovChipkaart;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }



    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) {
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM ov_chipkaart WHERE reiziger_id = ?"
            );

            statement.setInt(1, reiziger.getId());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int kaartNummer = resultSet.getInt("kaart_nummer");
                Date geldigTot = resultSet.getDate("geldig_tot");
                int klasse = resultSet.getInt("klasse");
                BigDecimal saldo = resultSet.getBigDecimal("saldo");

                OVChipkaart ovChipkaart = new OVChipkaart(kaartNummer, geldigTot, klasse, saldo, reiziger);
                ovChipkaarten.add(ovChipkaart);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        return ovChipkaarten;
    }
    @Override
    public OVChipkaart update(OVChipkaart ovChipkaart) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE ov_chipkaart " +
                            "SET geldig_tot = ?, klasse = ?, saldo = ?, reiziger_id = ? " +
                            "WHERE kaart_nummer = ?"
            );

            statement.setDate(1, new java.sql.Date(ovChipkaart.getGeldigTot().getTime()));
            statement.setInt(2, ovChipkaart.getKlasse());
            statement.setBigDecimal(3, ovChipkaart.getSaldo());
            statement.setInt(4, ovChipkaart.getReiziger().getId());
            statement.setInt(5, ovChipkaart.getKaartNummer());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating OVChipkaart failed, no rows affected.");
            }

            return ovChipkaart;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(OVChipkaart ovChipkaart) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM ov_chipkaart WHERE kaart_nummer = ?"
            );

            statement.setInt(1, ovChipkaart.getKaartNummer());

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public OVChipkaart findByKaartNummer(int kaartNummer) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM ov_chipkaart WHERE kaart_nummer = ?"
            );

            statement.setInt(1, kaartNummer);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Date geldigTot = resultSet.getDate("geldig_tot");
                int klasse = resultSet.getInt("klasse");
                BigDecimal saldo = resultSet.getBigDecimal("saldo");
                int reizigerId = resultSet.getInt("reiziger_id");

                ReizigerDAO reizigerDAO = new ReizigerDAOPsql(connection);
                Reiziger reiziger = reizigerDAO.findById(reizigerId);

                return new OVChipkaart(kaartNummer, geldigTot, klasse, saldo, reiziger);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        return null;
    }
    @Override
    public List<OVChipkaart> findByProduct(Product product) throws SQLException {
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();
        String query = "SELECT o.* FROM ov_chipkaart o " +
                "INNER JOIN ov_chipkaart_product ocp ON o.kaart_nummer = ocp.kaart_nummer " +
                "WHERE ocp.product_nummer = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, product.getProduct_nummer());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    OVChipkaart ovChipkaart = createOVChipkaartFromResultSet(resultSet);
                    ovChipkaarten.add(ovChipkaart);
                }
            }
        }

        return ovChipkaarten;
    }
    private OVChipkaart createOVChipkaartFromResultSet(ResultSet resultSet) throws SQLException {
        int kaartNummer = resultSet.getInt("kaart_nummer");
        Date geldigTot = resultSet.getDate("geldig_tot");
        int klasse = resultSet.getInt("klasse");
        BigDecimal saldo = resultSet.getBigDecimal("saldo");
        int reizigerId = resultSet.getInt("reiziger_id");

        ReizigerDAO reizigerDAO = new ReizigerDAOPsql(connection);
        Reiziger reiziger = reizigerDAO.findById(reizigerId);

        return new OVChipkaart(kaartNummer, geldigTot, klasse, saldo, reiziger);
    }
}
