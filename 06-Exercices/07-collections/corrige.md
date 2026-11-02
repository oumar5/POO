# ✅ 07 — Collections : corrigé

> ⬅️ **Énoncé :** [./enonce.md](./enonce.md) · 🔗 **Cours :** [07 — Collections et tables de hachage](../../01-Cours/07-collections.md)
> 📌 Tout le code ci-dessous a été compilé (Java 21) et exécuté ; les affichages sont ceux obtenus réellement. Les classes fournies dans l'énoncé (`Song`, `Guest`, `Book`, `Room`…) ne sont pas recopiées.

---

## Exercice 1 — Quelle collection choisir ?

### Q1

| | Situation | Interface | Implémentation | Pourquoi |
|---|---|---|---|---|
| a | Liste de courses | `List<String>` | `ArrayList` | ordre + doublons possibles |
| b | Numéros sortis au loto | `Set<Integer>` | `HashSet` | pas de répétition, `contains` en temps constant |
| c | Répertoire | `Map<String,String>` | `HashMap` | clé (nom) → valeur (numéro) |
| d | File de la boulangerie | `List<String>` | `LinkedList` | nombreuses insertions/suppressions en tête |
| e | Inscrits triés sans doublon | `Set<String>` (`SortedSet`) | `TreeSet` | trié, `first()`, `last()` |
| f | Playlist, accès au 15ᵉ | `List<Song>` | `ArrayList` | `get(i)` en temps constant |
| g | Stock trié par produit | `Map<String,Integer>` | `TreeMap` | clés triées |
| h | Badges autorisés | `Set<String>` | `HashSet` | test d'appartenance rapide |

💡 Trois questions à se poser : *ordre / position ?* (`List`), *doublons interdits ?* (`Set`), *clé → valeur ?* (`Map`). Puis : *trié ?* (`Tree…`) ou *rapide sans ordre ?* (`Hash…`).

### Q2

| Ligne | Compile ? | Correction / raison |
|---|---|---|
| (1) | ❌ | `int` est primitif → `List<Integer> grades = new ArrayList<Integer>();` |
| (2) | ✅ | autoboxing ensuite : `grades.add(12);` |
| (3) | ❌ | `List` est une **interface** (pas de `new`) → `List<String> names = new ArrayList<String>();` |
| (4) | ✅ | |
| (5) | ❌ | une `Map` a **deux** paramètres de type → `Map<String,String> phoneBook = new HashMap<String,String>();` |
| (6) | ✅ | un `TreeSet` est un `Set`, donc une `Collection` (upcast) |

💡 Messages réels de `javac` : (1) `unexpected type … required: reference, found: int` ; (3) `List is abstract; cannot be instantiated` ; (5) `wrong number of type arguments; required 2`.

### Q3

On **programme avec l'interface** : le reste du code n'utilise que les méthodes de `List`. Si on veut passer à une `LinkedList`, on change **une seule ligne** (le `new`), et tout le reste compile toujours.

---

## Exercice 2 — Ma playlist

### Q1 à Q5

```java
// Fichier : Playlist.java
import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String name;
    private List<Song> songs;

    public Playlist(String name) {
        this.name = name;
        this.songs = new ArrayList<Song>();
    }

    public String getName() { return this.name; }

    // Q2 : délégation à la liste
    public void add(Song song) { this.songs.add(song); }
    public void insert(int position, Song song) { this.songs.add(position, song); }
    public Song get(int position) { return this.songs.get(position); }
    public Song remove(int position) { return this.songs.remove(position); }
    public int size() { return this.songs.size(); }

    // Q3
    public int totalDuration() {
        int total = 0;
        for (Song s : this.songs) {
            total = total + s.getDuration();
        }
        return total;
    }

    // Q4
    public List<Song> songsOf(String artist) {
        List<Song> result = new ArrayList<Song>();
        for (Song s : this.songs) {
            if (s.getArtist().equals(artist)) {
                result.add(s);
            }
        }
        return result;
    }

    // Q5
    public void display() {
        int number = 1;
        for (Song s : this.songs) {
            System.out.println(number + ". " + s);
            number++;
        }
    }
}
```

💡 `Playlist` ne réinvente rien : elle **délègue** à sa `List<Song>`. Les positions de la liste commencent à **0**, la numérotation affichée à **1** (d'où le compteur `number`).
💡 Comparer les artistes avec `equals`, jamais avec `==` (on compare des `String`, donc des objets).

### Q6

```java
// Fichier : PlaylistMain.java
public class PlaylistMain {
    public static void main(String[] args) {
        Playlist p = new Playlist("Road trip");
        Song a = new Song("Alors on danse", "Stromae", 206);
        Song b = new Song("Papaoutai", "Stromae", 232);
        Song c = new Song("Djadja", "Aya Nakamura", 171);
        p.add(a);
        p.add(b);
        p.insert(0, c);
        p.display();
        System.out.println(p.get(1));
        System.out.println(p.totalDuration());
        System.out.println(p.songsOf("Stromae"));
        p.remove(0);
        System.out.println(p.size() + " " + p.get(0));
        p.get(5);
    }
}
```

Affichage obtenu :

```text
1. Djadja (Aya Nakamura)
2. Alors on danse (Stromae)
3. Papaoutai (Stromae)
Alors on danse (Stromae)
609
[Alors on danse (Stromae), Papaoutai (Stromae)]
2 Alors on danse (Stromae)
Exception in thread "main" java.lang.IndexOutOfBoundsException: Index 5 out of bounds for length 2
```

💡 `insert(0, c)` place `c` **en tête** et décale les autres. `get(1)` = 2ᵉ élément. 206 + 232 + 171 = 609. Une liste s'affiche entre crochets grâce au `toString()` de chaque `Song`. Enfin `get(5)` sur une liste de taille 2 lève `IndexOutOfBoundsException`, qui arrête le programme.

### Q7

`get(i)` est en temps constant sur une `ArrayList`, mais **linéaire** sur une `LinkedList` : la boucle devient alors très lente (mesure du cours : 34 ms avec un itérateur contre 2315 ms avec `get`). Et si on change d'implémentation (§ Q3 de l'exercice 1), la boucle `for-each` reste efficace. ✅ Toujours parcourir avec for-each ou un itérateur.

---

## Exercice 3 — Liste d'invités sans doublon

### Q1

```java
// Fichier : GuestMain.java
import java.util.HashSet;
import java.util.Set;

public class GuestMain {
    public static void main(String[] args) {
        Set<Guest> guests = new HashSet<Guest>();
        Guest alice = new Guest("Alice", "Martin");
        System.out.println(guests.add(alice));
        System.out.println(guests.add(alice));
        System.out.println(guests.add(new Guest("Alice", "Martin")));
        System.out.println(guests.size());
        System.out.println(guests.contains(new Guest("Alice", "Martin")));
    }
}
```

Affichage obtenu avec la classe `Guest` de l'énoncé :

```text
true
false
true
2
false
```

💡 Ligne par ligne :
1. `true` : l'ensemble était vide.
2. `false` : **le même objet** (même référence) est déjà présent.
3. `true` : `new Guest(...)` est un **autre objet** ; `equals`/`hashCode` d'`Object` comparent les références ⇒ pas « égal » à `alice` ⇒ ajouté.
4. `2` : Alice est là deux fois, le « doublon » du cours (`Value`).
5. `false` : encore un nouvel objet, introuvable.

### Q2

```java
// Fichier : Guest.java
public class Guest {
    private String firstName;
    private String lastName;

    public Guest(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public String toString() { return this.firstName + " " + this.lastName; }

    public boolean equals(Object o) {                 // paramètre Object !
        if (!(o instanceof Guest)) {
            return false;
        }
        Guest other = (Guest) o;
        return this.firstName.equals(other.firstName) && this.lastName.equals(other.lastName);
    }

    public int hashCode() {                           // égaux ⇒ même hashCode
        return 31 * this.firstName.hashCode() + this.lastName.hashCode();
    }
}
```

💡 Même règle que `ValueOK` dans le cours : redéfinir **`equals(Object)` ET `hashCode()`**, de façon **cohérente** (deux invités égaux ont le même `hashCode`, car il est calculé à partir des mêmes attributs).

### Q3

Affichage obtenu avec la version Q2 :

```text
true
false
false
1
true
```

💡 Le 3ᵉ `add` trouve un invité **égal** (même alvéole grâce à `hashCode`, puis `equals` vrai) ⇒ refusé. `contains` le retrouve aussi.

### Q4

```java
// Fichier : Guest.java
public class Guest {
    private String firstName;
    private String lastName;

    public Guest(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public String toString() { return this.firstName + " " + this.lastName; }

    public boolean equals(Guest other) {              // ⚠️ SURCHARGE, pas redéfinition
        return this.firstName.equals(other.firstName) && this.lastName.equals(other.lastName);
    }

    public int hashCode() {
        return 31 * this.firstName.hashCode() + this.lastName.hashCode();
    }
}
```

Affichage obtenu : `true`, `false`, `true`, `2`, `false` (comme en Q1).

💡 `equals(Guest)` a une signature **différente** de `equals(Object)` : c'est une **surcharge**. Le `HashSet` appelle `equals(Object)`, qui est toujours celle d'`Object` (comparaison des références). Le `hashCode` amène bien dans la même alvéole… mais `equals(Object)` répond `false`.

### Q5

```java
// Fichier : Guest.java
public class Guest {
    private String firstName;
    private String lastName;

    public Guest(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public String toString() { return this.firstName + " " + this.lastName; }

    public boolean equals(Object o) {
        if (!(o instanceof Guest)) {
            return false;
        }
        Guest other = (Guest) o;
        return this.firstName.equals(other.firstName) && this.lastName.equals(other.lastName);
    }
    // pas de hashCode()
}
```

Affichage obtenu : `true`, `false`, `true`, `2`, `false`.

💡 Sans `hashCode`, les deux Alice ont (en pratique) des `hashCode` **différents** (ceux d'`Object`) : le `HashSet` regarde la mauvaise alvéole et n'appelle même pas `equals`. D'où la règle : **les deux** méthodes, toujours ensemble.

---

## Exercice 4 — Compter des mots, remplir un panier

### Q1

```java
// Fichier : WordCounter.java
import java.util.HashMap;
import java.util.Map;

public class WordCounter {
    public static Map<String,Integer> count(String[] words) {
        Map<String,Integer> occ = new HashMap<String,Integer>();
        for (String word : words) {
            if (occ.containsKey(word)) {
                occ.put(word, occ.get(word) + 1);   // remplace l'ancienne valeur
            } else {
                occ.put(word, 1);                   // première apparition
            }
        }
        return occ;
    }
}
```

💡 `put` sur une clé existante **remplace** la valeur : c'est exactement ce qu'on veut pour incrémenter. L'autoboxing gère `Integer` ↔ `int` (`occ.get(word) + 1`).

### Q2

```java
// Fichier : WordCounterMain.java
import java.util.Map;

public class WordCounterMain {
    public static void main(String[] args) {
        String[] words = {"le", "chat", "mange", "le", "poisson", "le", "chat", "dort"};
        Map<String,Integer> occ = WordCounter.count(words);
        System.out.println(occ.get("le") + " " + occ.get("chat") + " " + occ.get("chien"));
        System.out.println(occ.size());
    }
}
```

Affichage obtenu :

```text
3 2 null
5
```

💡 `get` d'une clé absente (`"chien"`) renvoie `null` (pas d'exception). Il y a 5 clés **différentes** : le, chat, mange, poisson, dort.

### Q3

On remplace `new HashMap<String,Integer>()` par `new TreeMap<String,Integer>()` : les clés sont alors **triées**. Le type déclaré (`Map`) ne change pas.

```java
// Fichier : WordCounter.java
import java.util.Map;
import java.util.TreeMap;

public class WordCounter {
    public static Map<String,Integer> count(String[] words) {
        Map<String,Integer> occ = new TreeMap<String,Integer>();   // seule ligne modifiée
        for (String word : words) {
            if (occ.containsKey(word)) {
                occ.put(word, occ.get(word) + 1);
            } else {
                occ.put(word, 1);
            }
        }
        return occ;
    }

    public static void display(Map<String,Integer> occ) {
        for (Map.Entry<String,Integer> entry : occ.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}
```

```java
// Fichier : WordCounterSortedMain.java
public class WordCounterSortedMain {
    public static void main(String[] args) {
        String[] words = {"le", "chat", "mange", "le", "poisson", "le", "chat", "dort"};
        WordCounter.display(WordCounter.count(words));
    }
}
```

Affichage obtenu :

```text
chat : 2
dort : 1
le : 3
mange : 1
poisson : 1
```

💡 C'est l'intérêt de **programmer avec l'interface** : une ligne change, le reste non. Avec une `HashMap`, l'ordre d'affichage n'est **pas garanti**.

### Q4

```java
// Fichier : Basket.java
import java.util.HashMap;
import java.util.Map;

public class Basket {
    private Map<String,Integer> items;

    public Basket() {
        this.items = new HashMap<String,Integer>();
    }

    public void add(String product, int quantity) {
        if (this.items.containsKey(product)) {
            this.items.put(product, this.items.get(product) + quantity);
        } else {
            this.items.put(product, quantity);
        }
    }

    public void remove(String product) {
        this.items.remove(product);
    }

    public int quantityOf(String product) {
        if (this.items.containsKey(product)) {
            return this.items.get(product);
        }
        return 0;
    }

    public int totalItems() {
        int total = 0;
        for (Integer quantity : this.items.values()) {
            total = total + quantity;
        }
        return total;
    }

    public void display() {
        for (Map.Entry<String,Integer> entry : this.items.entrySet()) {
            System.out.println(entry.getKey() + " x " + entry.getValue());
        }
    }
}
```

💡 `quantityOf` teste `containsKey` **avant** `get` : sinon `get` renverrait `null`, et la conversion automatique `null` → `int` lèverait une `NullPointerException`.

### Q5

```java
// Fichier : BasketMain.java
public class BasketMain {
    public static void main(String[] args) {
        Basket b = new Basket();
        b.add("pomme", 3);
        b.add("pain", 1);
        b.add("pomme", 2);
        System.out.println(b.quantityOf("pomme") + " " + b.quantityOf("lait"));
        System.out.println(b.totalItems());
        b.remove("pain");
        System.out.println(b.totalItems());
    }
}
```

Affichage obtenu :

```text
5 0
6
5
```

💡 3 + 2 pommes cumulées = 5 ; le lait est absent ⇒ 0 ; total 5 + 1 = 6 ; sans le pain : 5.

### Q6

`2` : `put("pomme", 2)` **remplace** la valeur 3 au lieu de l'ajouter (clés uniques). Le cumul doit être fait à la main.

---

## Exercice 5 — Rayer les articles achetés

```java
// Fichier : ShoppingList.java
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ShoppingList {

    // version naïve de l'énoncé (Q1, Q4)
    public static void removeBoughtBad(List<String> shoppingList, Set<String> bought) {
        for (String item : shoppingList) {
            if (bought.contains(item)) {
                shoppingList.remove(item);
            }
        }
    }

    // Q2 : version correcte
    public static void removeBought(List<String> shoppingList, Set<String> bought) {
        Iterator<String> it = shoppingList.iterator();
        while (it.hasNext()) {
            String item = it.next();
            if (bought.contains(item)) {
                it.remove();          // suppression PAR l'itérateur
            }
        }
    }

    // Q3 : version fausse par indice
    public static void removeBoughtIndex(List<String> shoppingList, Set<String> bought) {
        for (int i = 0; i < shoppingList.size(); i++) {
            if (bought.contains(shoppingList.get(i))) {
                shoppingList.remove(i);
            }
        }
    }
}
```

```java
// Fichier : ShoppingMain.java
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShoppingMain {
    public static List<String> newList() {
        List<String> l = new ArrayList<String>();
        l.add("pain"); l.add("lait"); l.add("oeufs"); l.add("beurre"); l.add("pommes");
        return l;
    }

    public static void main(String[] args) {
        // Q1
        List<String> l1 = newList();
        Set<String> bought1 = new HashSet<String>();
        bought1.add("lait");
        try {
            ShoppingList.removeBoughtBad(l1, bought1);
        } catch (ConcurrentModificationException e) {
            System.out.println("Q1 : ConcurrentModificationException !");
        }
        System.out.println("Q1 : " + l1);
        // Q2
        List<String> l2 = newList();
        Set<String> bought2 = new HashSet<String>();
        bought2.add("lait");
        bought2.add("oeufs");
        ShoppingList.removeBought(l2, bought2);
        System.out.println("Q2 : " + l2);
        // Q3
        List<String> l3 = newList();
        ShoppingList.removeBoughtIndex(l3, bought2);
        System.out.println("Q3 : " + l3);
        // Q4
        List<String> l4 = newList();
        Set<String> bought4 = new HashSet<String>();
        bought4.add("beurre");
        ShoppingList.removeBoughtBad(l4, bought4);
        System.out.println("Q4 : " + l4);
    }
}
```

Affichage obtenu :

```text
Q1 : ConcurrentModificationException !
Q1 : [pain, oeufs, beurre, pommes]
Q2 : [pain, beurre, pommes]
Q3 : [pain, oeufs, beurre, pommes]
Q4 : [pain, lait, oeufs, pommes]
```

### Q1

💡 Le for-each utilise un **itérateur caché**. `shoppingList.remove(item)` modifie la liste **sans passer par cet itérateur** ⇒ au `next()` suivant, l'itérateur **fail-fast** lève une `ConcurrentModificationException`. ⚠️ Remarque : `lait` a quand même été retiré avant l'exception, la liste est dans un état « à moitié traité ».

### Q2

💡 `it.remove()` retire le dernier élément renvoyé par `next()` **et** met l'itérateur à jour : pas d'exception, tous les éléments sont examinés.

### Q3

💡 Résultat faux : `oeufs` est **resté**. Quand on supprime `lait` (indice 1), `oeufs` recule à l'indice 1… mais `i` passe à 2 : `oeufs` n'est jamais examiné. En plus, `get(i)` dans une boucle est déconseillé (lent sur `LinkedList`).

### Q4

💡 Après la suppression de `beurre` (indice 3), la liste a 4 éléments et l'itérateur caché est « à la position 4 » : `hasNext()` répond `false`, il n'y a **plus de `next()`**, donc pas d'exception… et `pommes` n'a **jamais été examiné** ! Le code « marche » ici par hasard : avec un autre élément acheté (Q1), il plante. Un code correct doit l'être **dans tous les cas** ⇒ `it.remove()`.

---

## Exercice 6 — Classement du marathon

### Q1

```java
// Fichier : Runner.java
public class Runner implements Comparable<Runner> {
    private String name;
    private int time;   // en minutes

    public Runner(String name, int time) {
        this.name = name;
        this.time = time;
    }
    public String getName() { return this.name; }
    public int getTime() { return this.time; }

    public int compareTo(Runner other) {
        return this.time - other.time;    // < 0 si this est plus rapide
    }

    public String toString() { return this.name + " (" + this.time + " min)"; }
}
```

💡 Convention de `compareTo` : négatif si `this` vient **avant** `other`. Le plus rapide (plus petit temps) vient en premier ⇒ `this.time - other.time`.

### Q3

```java
// Fichier : NameComparator.java
import java.util.Comparator;

public class NameComparator implements Comparator<Runner> {
    public int compare(Runner r1, Runner r2) {
        return r1.getName().compareTo(r2.getName());   // compareTo de String
    }
}
```

### Q2, Q3, Q4 : exécution

```java
// Fichier : RunnerMain.java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class RunnerMain {
    public static void main(String[] args) {
        List<Runner> runners = new ArrayList<Runner>();
        runners.add(new Runner("Léa", 215));
        runners.add(new Runner("Karim", 189));
        runners.add(new Runner("Zoé", 240));
        runners.add(new Runner("Tom", 189));
        // Q2
        Collections.sort(runners);
        System.out.println(runners);
        System.out.println("Vainqueur : " + runners.get(0));
        // Q3
        Collections.sort(runners, new NameComparator());
        System.out.println(runners);
        // Q4
        Set<Runner> set = new TreeSet<Runner>();
        set.add(new Runner("Léa", 215));
        set.add(new Runner("Karim", 189));
        set.add(new Runner("Zoé", 240));
        set.add(new Runner("Tom", 189));
        System.out.println(set.size());
        System.out.println(set);
    }
}
```

Affichage obtenu :

```text
[Karim (189 min), Tom (189 min), Léa (215 min), Zoé (240 min)]
Vainqueur : Karim (189 min)
[Karim (189 min), Léa (215 min), Tom (189 min), Zoé (240 min)]
3
[Karim (189 min), Léa (215 min), Zoé (240 min)]
```

💡 **Q2** : tri par temps croissant. Karim et Tom sont ex æquo (`compareTo` = 0) : le tri par fusion de `Collections.sort` garde leur ordre d'origine (Karim a été ajouté avant Tom).
💡 **Q3** : même liste, autre ordre, sans toucher à `Runner` : on passe un `Comparator` en second argument.
💡 **Q4** : seulement **3** coureurs ! Un `TreeSet` considère que deux éléments sont « le même » quand `compareTo` renvoie **0**. Tom a le même temps que Karim ⇒ refusé comme doublon. ⚠️ Pour un `TreeSet`, l'ordre doit distinguer les éléments différents (par ex. départager par le nom).

### Q5

**Non**, erreur de compilation (`no suitable method found for sort(List<Runner>)`) : `Collections.sort(list)` exige des éléments **`Comparable`**. Sans `Comparable`, il faut utiliser la version avec `Comparator` : `Collections.sort(runners, new NameComparator());` compile.

---

## Exercice 7 — Une bibliothèque itérable

### Q1 et Q2

```java
// Fichier : Library.java
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Library implements Iterable<Book> {
    private List<Book> books;

    public Library() {
        this.books = new ArrayList<Book>();
    }

    public void addBook(Book book) {
        this.books.add(book);
    }

    // Q1 : on délègue à la collection interne (comme Agency dans le cours)
    public Iterator<Book> iterator() {
        return this.books.iterator();
    }

    // Q2
    public int removeBooksBy(String author) {
        int removed = 0;
        Iterator<Book> it = this.iterator();
        while (it.hasNext()) {
            Book b = it.next();
            if (b.getAuthor().equals(author)) {
                it.remove();
                removed++;
            }
        }
        return removed;
    }
}
```

💡 Implémenter `Iterable<Book>` = fournir **une** méthode, `iterator()`. Le for-each est alors utilisable **sur la bibliothèque** elle-même, sans exposer la liste interne.
💡 Pour supprimer pendant le parcours : `it.remove()` (exercice 5).

### Q3 et Q5

```java
// Fichier : Printer.java
import java.util.Collection;

public class Printer {
    // Q5 : « n'importe quelle collection »
    public static void printAll(Collection<?> c) {
        for (Object o : c) {
            System.out.println(o);
        }
    }
}
```

```java
// Fichier : LibraryMain.java
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class LibraryMain {
    public static void main(String[] args) {
        // Q3
        Library lib = new Library();
        lib.addBook(new Book("Le Hobbit", "Tolkien"));
        lib.addBook(new Book("Dune", "Herbert"));
        lib.addBook(new Book("Le Silmarillion", "Tolkien"));
        System.out.println(lib.removeBooksBy("Tolkien"));
        for (Book b : lib) {
            System.out.println(b);
        }
        // Q5
        List<Book> books = new ArrayList<Book>();
        books.add(new Book("Dune", "Herbert"));
        Set<String> authors = new TreeSet<String>();
        authors.add("Tolkien");
        authors.add("Herbert");
        Printer.printAll(books);
        Printer.printAll(authors);
    }
}
```

Affichage obtenu :

```text
2
Dune de Herbert
Dune de Herbert
Herbert
Tolkien
```

💡 Q3 : deux livres de Tolkien retirés, il reste Dune. Q5 : `Collection<?>` accepte une `List<Book>` **et** un `Set<String>` ; dans la boucle, la seule chose sûre est que les éléments sont des `Object` (le `toString()` appelé est celui de l'objet : late binding).

### Q4

| Ligne | Compile ? | Pourquoi |
|---|---|---|
| (a) | ✅ | `ArrayList<Book>` → `List<Book>` → `Collection<Book>` |
| (b) | ❌ | `Collection<Book>` n'est **pas** un sous-type de `Collection<Object>` (`incompatible types`) |
| (c) | ✅ | `Collection<?>` = « collection de n'importe quoi » |
| (d) | ❌ | type exact inconnu ⇒ on ne peut **rien ajouter** |
| (e) | ✅ | on peut toujours **lire** des `Object` |

💡 Sinon, on pourrait faire `c2.add("une String")` dans une liste de livres : Java l'interdit dès la compilation.

---

## Exercice 8 — L'hôtel du TP4 avec une `Map` (style DS)

### Q1, Q2 et Q4

`TreeMap<Integer,Room>` : les clés (numéros) sont **triées**, donc `values()` donne les chambres par numéro croissant.

```java
// Fichier : Hotel.java
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Hotel {
    private String name;
    private Map<Integer,Room> rooms;

    /**
     * Creates a hotel whose rooms are numbered from 1 to nbRooms, all free.
     * @param name the name of the hotel
     * @param nbRooms the number of rooms
     */
    public Hotel(String name, int nbRooms) {
        this.name = name;
        this.rooms = new TreeMap<Integer,Room>();
        for (int number = 1; number <= nbRooms; number++) {
            this.rooms.put(number, new Room(number));
        }
    }

    public String getName() { return this.name; }

    /**
     * @param number the number of a room
     * @return the room with this number, null if there is no such room
     */
    public Room getRoom(int number) {
        return this.rooms.get(number);
    }

    public int numberOfRooms() {
        return this.rooms.size();
    }

    /**
     * Rents the room with the given number.
     * @param number the number of the room to rent
     * @return the rented room
     * @throws RoomNotAvailableException if there is no room with this number
     *         or if this room is already rented
     */
    public Room rentRoom(int number) throws RoomNotAvailableException {
        Room room = this.getRoom(number);
        if (room == null) {
            throw new RoomNotAvailableException("no room number " + number);
        }
        if (!room.isFree()) {
            throw new RoomNotAvailableException("room " + number + " is already rented");
        }
        room.rent();
        return room;
    }

    /**
     * Frees the room with the given number (no effect if there is no such room).
     * @param number the number of the room
     */
    public void leaveRoom(int number) {
        Room room = this.getRoom(number);
        if (room != null) {
            room.leave();
        }
    }

    public int numberOfFreeRooms() {
        return this.freeRooms().size();
    }

    /**
     * @return the smallest number of a free room, 0 if all rooms are rented
     */
    public int firstFreeNumber() {
        for (Room room : this.rooms.values()) {    // par numéro croissant (TreeMap)
            if (room.isFree()) {
                return room.getNumber();
            }
        }
        return 0;
    }

    /**
     * @return the list of free rooms, sorted by increasing number
     */
    public List<Room> freeRooms() {
        List<Room> result = new ArrayList<Room>();
        for (Room room : this.rooms.values()) {
            if (room.isFree()) {
                result.add(room);
            }
        }
        return result;
    }
}
```

💡 `getRoom` se réduit à `get` : une clé absente donne `null`, plus de `try / catch` sur `ArrayIndexOutOfBoundsException`.
💡 `rentRoom` déclare `throws RoomNotAvailableException` (exception **contrôlée**, elle hérite de `Exception`) et la javadoc documente **les deux** cas de levée (`@throws`).

### Q3

```java
// Fichier : HotelTest.java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class HotelTest {

    @Test
    public void roomsAreCreatedAndFreeAtCreation() {
        Hotel hotel = new Hotel("California", 5);
        assertEquals(5, hotel.numberOfRooms());
        assertEquals(5, hotel.numberOfFreeRooms());
        for (int n = 1; n <= 5; n++) {
            assertEquals(n, hotel.getRoom(n).getNumber());
            assertTrue(hotel.getRoom(n).isFree());
        }
    }

    @Test
    public void getRoomReturnsNullIfNumberNotValid() {
        Hotel hotel = new Hotel("California", 5);
        assertNull(hotel.getRoom(0));
        assertNull(hotel.getRoom(6));
        assertNull(hotel.getRoom(-1));
    }

    @Test
    public void rentRoomReturnsTheRoomAndRentsIt() throws RoomNotAvailableException {
        Hotel hotel = new Hotel("California", 5);
        Room room = hotel.rentRoom(3);
        assertSame(hotel.getRoom(3), room);
        assertFalse(room.isFree());
        assertEquals(4, hotel.numberOfFreeRooms());
    }

    @Test
    public void rentRoomThrowsExceptionIfAlreadyRented() throws RoomNotAvailableException {
        Hotel hotel = new Hotel("California", 5);
        hotel.rentRoom(3);
        assertThrows(RoomNotAvailableException.class, () -> hotel.rentRoom(3));
    }

    @Test
    public void rentRoomThrowsExceptionIfNumberNotValid() {
        Hotel hotel = new Hotel("California", 5);
        assertThrows(RoomNotAvailableException.class, () -> hotel.rentRoom(0));
        assertThrows(RoomNotAvailableException.class, () -> hotel.rentRoom(6));
    }

    @Test
    public void leaveRoomFreesTheRoom() throws RoomNotAvailableException {
        Hotel hotel = new Hotel("California", 5);
        hotel.rentRoom(2);
        hotel.leaveRoom(2);
        assertTrue(hotel.getRoom(2).isFree());
        hotel.leaveRoom(42);                       // sans effet, pas d'exception
        assertEquals(5, hotel.numberOfFreeRooms());
    }

    @Test
    public void firstFreeNumberIsCorrect() throws RoomNotAvailableException {
        Hotel hotel = new Hotel("California", 3);
        assertEquals(1, hotel.firstFreeNumber());
        hotel.rentRoom(1);
        assertEquals(2, hotel.firstFreeNumber());
        hotel.rentRoom(2);
        hotel.rentRoom(3);
        assertEquals(0, hotel.firstFreeNumber());
    }

    @Test
    public void freeRoomsAreSortedByNumber() throws RoomNotAvailableException {
        Hotel hotel = new Hotel("California", 4);
        hotel.rentRoom(2);
        List<Room> free = hotel.freeRooms();
        assertEquals(3, free.size());
        assertEquals(1, free.get(0).getNumber());
        assertEquals(3, free.get(1).getNumber());
        assertEquals(4, free.get(2).getNumber());
    }
}
```

Résultat obtenu :

```text
[         8 tests successful      ]
[         0 tests failed          ]
```

💡 `assertSame` vérifie qu'on renvoie **la** chambre de l'hôtel (même référence). Les méthodes de test qui appellent `rentRoom` **hors** d'un `assertThrows` doivent déclarer `throws RoomNotAvailableException` (exception contrôlée).

### Q5

1. **Plus de décalage** numéro/indice (`rooms[number - 1]`) : on accède directement par le numéro.
2. **Numéros quelconques** possibles (101, 102, 201… pour les étages) et nombre de chambres modifiable, ce que ne permet pas un tableau de taille fixe indicé à partir de 0.
3. (bonus) Un numéro invalide donne `null` sans exception à capturer.

---

## ⚠️ Erreurs fréquentes

| Erreur | Pourquoi c'est faux | Correction |
|---|---|---|
| `List<int>` | une collection ne contient que des objets | `List<Integer>` (autoboxing) |
| `new List<String>()` | `List` est une interface | `new ArrayList<String>()` |
| Parcourir avec `for (i…) get(i)` | lent sur `LinkedList` ; décalage si on supprime | for-each ou itérateur |
| `list.remove(x)` dans un for-each | `ConcurrentModificationException` (fail-fast) | `Iterator` + `it.remove()` |
| `equals(Guest other)` | surcharge : le `HashSet` appelle `equals(Object)` | `public boolean equals(Object o)` |
| `equals` sans `hashCode` | les « égaux » tombent dans des alvéoles différentes | redéfinir les **deux** |
| `put` pour « ajouter » une quantité | `put` **remplace** la valeur existante | `containsKey` + `get` + `put(valeur cumulée)` |
| `int q = map.get(k)` avec `k` absent | `null` → `NullPointerException` à la conversion | tester `containsKey` d'abord |
| Compter sur l'ordre d'une `HashMap`/`HashSet` | aucun ordre garanti | `TreeMap` / `TreeSet` si on veut trier |
| `TreeSet` avec `compareTo` qui renvoie 0 pour des éléments différents | considérés comme doublons ⇒ perdus | `compareTo` qui départage |
| `Collections.sort(list)` sans `Comparable` | ne compile pas | implémenter `Comparable<T>` ou passer un `Comparator` |
| `Collection<Object>` pour « n'importe quelle collection » | `Collection<Book>` n'en est pas un sous-type | `Collection<?>` |

---

⬅️ [Retour à l'énoncé](./enonce.md) · 🔗 [Cours 07](../../01-Cours/07-collections.md)

🏠 [Sommaire des exercices](../README.md)
