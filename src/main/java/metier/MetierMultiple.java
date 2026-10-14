package metier;

import dao.IDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MetierMultiple implements IMetier {
    private final List<IDao> daos;
    private final Map<String, IDao> daoMap;

    // Spring injecte automatiquement tous les beans de type IDao dans la liste,
    // et les associe par leurs noms de composants dans la map.
    public MetierMultiple(List<IDao> daos, Map<String, IDao> daoMap) {
        this.daos = daos;
        this.daoMap = daoMap;
    }

    @Override
    public double calcul() {
        // Exemple d'utilisation : somme de toutes les valeurs de tous les DAO
        double somme = daos.stream().mapToDouble(IDao::getValue).sum();

        System.out.println("[MetierMultiple] Nombre de DAO injectés : " + daos.size());
        System.out.println("[MetierMultiple] Clés disponibles dans la Map : " + daoMap.keySet());

        return somme * 2.0;
    }
}