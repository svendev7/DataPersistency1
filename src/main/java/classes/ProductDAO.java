package classes;

import java.sql.SQLException;
import java.util.List;

public interface ProductDAO {
    void save(Product product) throws SQLException;
    void update(Product product) throws SQLException;
    void delete(Product product) throws SQLException;
    List<Product> findByOVChipkaart(OVChipkaart ovChipkaart) throws SQLException;
    List<Product> findAll() throws SQLException;
}
