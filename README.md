# PIT sur la factorielle

CPOO1 (INSA Rennes, 3INFO), CM3 : une suite de tests couvre tout le code, et
pourtant elle laisse passer un bug. PIT le montre, et dit quoi corriger.

```bash
mvn test                                    # les tests
mvn test-compile pitest:mutationCoverage    # la mutation : target/pit-reports/index.html
```

| Tag | Suite | Couverture de lignes | Mutants tués |
|---|---|---|---|
| `suite-faible` | deux tests | 7/7 (100 %) | 5/6 (83 %) |
| `suite-100` | la même, un test modifié | 7/7 (100 %) | 6/6 (100 %) |

```bash
git checkout suite-faible                   # puis la commande PIT
git checkout suite-100                      # idem
git diff suite-faible suite-100             # ce qui a changé dans la suite
```

## Le code testé

```java
public long fact(int n) {
    if (n == 0) {
        return 1;
    }
    long result = 1;
    for (int i = 2; i <= n; i++) {
        result = result * i;
    }
    return result;
}
```

## Avant : deux tests verts, toutes les lignes couvertes

```java
@Test
void factorialWith5Test() {
    long obs = f.fact(5);
    assertTrue(5 < obs);        // oracle (très) faible
}

@Test
void factorialWith0Test() {
    assertEquals(1, f.fact(0));
}
```

`fact(5)` passe par le `if`, la boucle et le dernier `return`. `fact(0)` passe
par `return 1`. Chaque ligne est donc exécutée, et la couverture est de 100 %.

PIT fabrique alors 6 **mutants**. Chacun est une copie de `fact` avec **une
seule** modification, un bug artificiel. PIT relance la suite sur chaque
mutant. Si un test échoue, le mutant est **tué** : la suite a vu le bug. Si
tous les tests passent, il **survit** : la suite ne l'a pas vu.

| Ligne d'origine | Mutant | `factorialWith5Test` | `factorialWith0Test` | Verdict |
|---|---|---|---|---|
| `if (n == 0)` | `n != 0` | échoue : `fact(5)` = 1 | passe : `fact(0)` = 1 | tué |
| `return 1;` | `return 0;` | passe : ligne non exécutée | échoue : `fact(0)` = 0 | tué |
| `i <= n` | `i > n` | échoue : `fact(5)` = 1 | passe : ligne non exécutée | tué |
| `i <= n` | `i < n` | **passe : `fact(5)` = 24, et `5 < 24`** | passe : ligne non exécutée | **survivant** |
| `result * i` | `result / i` | échoue : `fact(5)` = 0 | passe : ligne non exécutée | tué |
| `return result;` | `return 0;` | échoue : `fact(5)` = 0 | passe : ligne non exécutée | tué |

**Score de mutation = 5 tués / 6 mutants = 83 %.**

Le survivant `i < n` est un bug plausible : on oublie le dernier facteur, et
`fact(5)` renvoie 24 au lieu de 120. `factorialWith5Test` exécute bien la
ligne fautive, mais son oracle `5 < obs` accepte 24, comme il accepterait 121
ou un million. Le test **exécute** le code sans le **vérifier**. La couverture
ne voit pas la différence, la mutation si.

## Après : un oracle exact

On ne change qu'une ligne de la suite :

```diff
     @Test
     void factorialWith5Test() {
         long obs = f.fact(5);
-        assertTrue(5 < obs);        // oracle (très) faible
+        assertEquals(120, obs);     // oracle exact
     }
```

Le mutant `i < n` renvoie 24, et 24 ≠ 120 : le test échoue, le mutant est tué.
Les quatre autres mutants que tuait ce test renvoient 1 ou 0, et le nouveau
test les tue toujours.

**Score de mutation = 6 / 6 = 100 %**, pour une couverture inchangée (7/7).

## À retenir

- Une couverture de 100 % dit que chaque ligne a été **exécutée**. Elle ne dit
  pas que le résultat a été **vérifié**.
- Le score de mutation mesure les **assertions** : un survivant montre un bug
  que la suite laisserait passer, et donc l'assertion qui manque.
- 100 % de mutants tués ne prouve pas que le code est juste : PIT n'essaie que
  ses mutants. Ici, `fact(-1)` renvoie 1, et aucun test ne le dit.

Java 21, Maven.
