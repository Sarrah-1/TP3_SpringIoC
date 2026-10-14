# TP3 : Spring IoC & OCP - Stratégies d'injection et sélection d'implémentations

## 1. Objectif

Le principe Ouvert/Fermé (OCP) dit qu'une classe doit être ouverte à l'extension mais fermée à la modification.

Dans ce TP, la classe métier `MetierImpl` doit pouvoir utiliser une base de données, un fichier ou une API sans qu'on change son code. Elle dépend uniquement de l'interface `IDao`, et c'est Spring qui choisit l'implémentation à injecter.

## 2. Outils utilisés

- Java (JDK 21)
- Maven
- Spring Context
- JUnit
- IntelliJ IDEA

## 3. Structure du projet

```
TP3_SpringIoC
└── src/main/java
    ├── config
    │   ├── AppConfig
    │   ├── DaoAliasConfig
    │   └── DynamicDaoConfig
    ├── dao
    │   ├── IDao
    │   ├── DaoImpl
    │   ├── DaoImpl2
    │   ├── DaoImplProd
    │   ├── DaoFile
    │   └── DaoApi
    ├── metier
    │   ├── IMetier
    │   ├── MetierImpl
    │   └── MetierMultiple
    └── Main
```

<img width="245" height="443" alt="Capture d&#39;écran 2026-10-14 115520" src="https://github.com/user-attachments/assets/ffb72ab7-dabc-4054-a677-a94a5a69f918" />


## 4. Ce qu'on a fait

1. Création d'un projet Maven dans IntelliJ et ajout des dépendances dans le `pom.xml`.
2. Création de l'interface `IDao` avec une seule méthode, `getValue()`. C'est le contrat commun à toutes les sources de données.
3. Création des implémentations de `IDao`, déclarées avec `@Component` :

| Classe | Nom du bean | Source simulée | Valeur |
|--------|-------------|----------------|--------|
| `DaoImpl` | `dao` | Base de données (prod) | 100.0 |
| `DaoImpl2` | `dao2` | Deuxième implémentation | 150.0 |
| `DaoFile` | `daoFile` | Fichier local | 180.0 |
| `DaoApi` | `daoApi` | API externe | 220.0 |

4. Création de `MetierImpl`, déclarée avec `@Service`. Elle reçoit un `IDao` par le constructeur et retourne la valeur multipliée par 2. Cette classe ne change jamais pendant le TP.
5. Création de `AppConfig` pour activer le scan des composants (`@ComponentScan`) et charger le fichier `app.properties` (`@PropertySource`).
6. Mise en place de trois stratégies pour choisir le DAO à injecter (section suivante).
7. Ajout d'une trace `@PostConstruct` dans `MetierImpl` pour afficher le DAO injecté.
8. Écriture d'un test JUnit qui vérifie le résultat avec la configuration par défaut.

## 5. Les trois stratégies

### Stratégie A : `@Primary`

La classe `DaoImpl` est annotée `@Primary`, donc Spring la choisit en priorité parmi les implémentations de `IDao`. C'est simple, mais pour changer de DAO il faut modifier le code et recompiler.

<img width="527" height="266" alt="img1" src="https://github.com/user-attachments/assets/2db1dff6-5f00-4c34-90e5-eed876f9dc5f" />

<img width="620" height="142" alt="Capture d&#39;écran 2026-10-14 115645" src="https://github.com/user-attachments/assets/856397b4-bdc9-4f4f-8177-c174745519ea" />


### Stratégie B : alias avec `@Bean`

<img width="533" height="284" alt="img3" src="https://github.com/user-attachments/assets/62ecaa82-53a4-4fb0-bb85-2ff9d993c86a" />


### Stratégie C : propriétés externes

La classe `DynamicDaoConfig` lit la propriété `dao.target` dans `app.properties` avec `@Value` et crée le DAO correspondant avec un `switch`. Le bean retourné est déclaré `@Primary`.


Les valeurs possibles sont `dao`, `dao2`, `daoFile` et `daoApi`. On change de source sans modifier le code Java, mais il faut relancer l'application pour appliquer le choix. Dans la version actuelle, `Main` charge `AppConfig` et `DynamicDaoConfig`.

### Remarque

Les trois stratégies déclarent un bean `@Primary`. Elles ne doivent pas être chargées en même temps dans le contexte, sinon Spring échoue. Dans ce TP, c'est `Main` qui décide quelles classes de configuration sont chargées.

## 6. Résultats attendus

Le résultat est toujours la valeur du DAO multipliée par 2.

| Stratégie | Configuration | DAO injecté | Résultat |
|-----------|---------------|-------------|----------|
| A | `@Primary` sur `DaoImpl` | `DaoImpl` | 200.0 |
| B | `DaoAliasConfig` vers `DaoApi` | `DaoApi` | 440.0 |
| C | `dao.target=dao` | `DaoImpl` | 200.0 |
| C | `dao.target=dao2` | `DaoImpl2` | 300.0 |
| C | `dao.target=daoFile` | `DaoFile` | 360.0 |
| C | `dao.target=daoApi` | `DaoApi` | 440.0 |


## 9. Exécution finale de `Main`

<img width="620" height="142" alt="img2" src="https://github.com/user-attachments/assets/1a51223d-aa94-4dde-8fd1-ea4481431dbd" />

## 10. Conclusion

Le calcul de `MetierImpl` reste le même quand on change de source. Nous avons utilisé `@Primary`, un bean de configuration et une propriété pour choisir le DAO à injecter. Le choix de la dépendance reste dans la configuration, ce qui permet de respecter le principe OCP pour la classe métier.
