package classes;

import java.sql.SQLException;
import java.util.List;

public interface OVChipkaartDAO {
    OVChipkaart save(OVChipkaart ovChipkaart);
    OVChipkaart update(OVChipkaart ovChipkaart);
    boolean delete(OVChipkaart ovChipkaart);
    OVChipkaart findByKaartNummer(int kaartNummer);
    List<OVChipkaart> findByReiziger(Reiziger reiziger);
    List<OVChipkaart> findByProduct(Product product) throws SQLException;
}
