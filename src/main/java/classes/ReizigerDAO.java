package classes;

import java.sql.SQLException;
import java.util.List;

public interface ReizigerDAO {
    List<Reiziger> findAll() throws SQLException;
    Reiziger findById(int reiziger_id) throws SQLException;
    List<Reiziger> findByAchternaam(String achternaam) throws SQLException;
    void save(Reiziger reiziger) throws SQLException;
    void update(Reiziger reiziger) throws SQLException;
    void delete(Reiziger reiziger) throws SQLException;
}