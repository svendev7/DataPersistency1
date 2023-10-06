package classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        }
    }

    @Override
    public void update(Product product) throws SQLException {
        String query = "UPDATE product SET naam = ?, beschrijving = ?, prijs = ? WHERE product_nummer = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1,product.getNaam());
            statement.setString(2, product.getBeschrijving());
            statement.setInt(3, product.getPrijs());
            statement.setInt(4, product.getProduct_nummer());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(Product product) throws SQLException {
        String query = "DELETE FROM product WHERE product_nummer = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, product.getProduct_nummer());
            statement.executeUpdate();
        }
    }
}
