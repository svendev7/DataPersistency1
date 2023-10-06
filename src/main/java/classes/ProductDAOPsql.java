package classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOPsql implements ProductDAO {
    private Connection connection;

    public ProductDAOPsql(Connection connection) {
        this.connection = connection;
    }
    @Override
    public void save(Product product) throws SQLException {
        String query = "INSERT INTO product (product_nummer, naam, beschrijving, prijs) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, product.getProduct_nummer());
            statement.setString(2,product.getNaam());
            statement.setString(3, product.getBeschrijving());
            statement.setInt(4, product.getPrijs());
            statement.executeUpdate();
            product.setProduct_nummer(product.getProduct_nummer());
        }
        if (!product.getOvChipkaarten().isEmpty()) {
            String junctionQuery = "INSERT INTO ov_chipkaart_product (kaart_nummer, product_nummer) VALUES (?, ?)";
            try (PreparedStatement junctionStatement = connection.prepareStatement(junctionQuery)) {
                for (OVChipkaart ovChipkaart : product.getOvChipkaarten()) {
                    junctionStatement.setInt(1, ovChipkaart.getKaartNummer());
                    junctionStatement.setInt(2, product.getProduct_nummer());
                    junctionStatement.executeUpdate();
                }
            }
        }
    }

    @Override
    public void update(Product product) throws SQLException {

        String query = "UPDATE product SET naam = ?, beschrijving = ?, prijs = ? WHERE product_nummer = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, product.getNaam());
            statement.setString(2, product.getBeschrijving());
            statement.setInt(3, product.getPrijs());
            statement.setInt(4, product.getProduct_nummer());
            statement.executeUpdate();
        }

        String clearJunctionQuery = "DELETE FROM ov_chipkaart_product WHERE product_nummer = ?";
        try (PreparedStatement clearJunctionStatement = connection.prepareStatement(clearJunctionQuery)) {
            clearJunctionStatement.setInt(1, product.getProduct_nummer());
            clearJunctionStatement.executeUpdate();
        }

        if (!product.getOvChipkaarten().isEmpty()) {
            String junctionQuery = "INSERT INTO ov_chipkaart_product (kaart_nummer, product_nummer) VALUES (?, ?)";
            try (PreparedStatement junctionStatement = connection.prepareStatement(junctionQuery)) {
                for (OVChipkaart ovChipkaart : product.getOvChipkaarten()) {
                    junctionStatement.setInt(1, ovChipkaart.getKaartNummer());
                    junctionStatement.setInt(2, product.getProduct_nummer());
                    junctionStatement.executeUpdate();
                }
            }
        }
    }

    @Override
    public void delete(Product product) throws SQLException {
        String junctionQuery = "DELETE FROM ov_chipkaart_product WHERE product_nummer = ?";
        try (PreparedStatement junctionStatement = connection.prepareStatement(junctionQuery)) {
            junctionStatement.setInt(1, product.getProduct_nummer());
            junctionStatement.executeUpdate();
        }
        String query = "DELETE FROM product WHERE product_nummer = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, product.getProduct_nummer());
            statement.executeUpdate();
        }
    }
    @Override
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart) throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.* FROM product p " +
                "INNER JOIN ov_chipkaart_product ocp ON p.product_nummer = ocp.product_nummer " +
                "WHERE ocp.kaart_nummer = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, ovChipkaart.getKaartNummer());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = createProductFromResultSet(resultSet);
                    products.add(product);
                }
            }
        }

        return products;
    }
    @Override
    public List<Product> findAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM product";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Product product = createProductFromResultSet(resultSet);
                products.add(product);
            }
        }

        return products;
    }
    private Product createProductFromResultSet(ResultSet resultSet) throws SQLException {
        int product_nummer = resultSet.getInt("product_nummer");
        String naam = resultSet.getString("naam");
        String beschrijving = resultSet.getString("beschrijving");
        int prijs = resultSet.getInt("prijs");

        return new Product(product_nummer, naam, beschrijving, prijs);
    }
}
