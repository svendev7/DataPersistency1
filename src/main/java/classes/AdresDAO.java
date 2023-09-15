package classes;
import java.sql.SQLException;
import java.util.List;
public interface AdresDAO {
    List<Adres> findAll() throws SQLException;
    Adres findById(int adres_id) throws SQLException;
    void save(Adres adres) throws SQLException;
    void update(Adres adres) throws SQLException;
    void delete(Adres adres) throws SQLException;
    Adres findByReiziger(int reiziger_id) throws SQLException;
}
