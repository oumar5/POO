# ✅ 09 — Classes abstraites et `final` : corrigé

> ⬅️ **Énoncé :** [./enonce.md](./enonce.md) · 🔗 **Cours :** [09 — Classes abstraites et final](../../01-Cours/09-classes-abstraites.md)
> 📌 Tout le code ci-dessous a été compilé (Java 21) et exécuté ; les affichages et les erreurs de compilation indiqués sont ceux obtenus réellement. Les classes fournies dans l'énoncé ne sont pas recopiées.

---

## Exercice 1 — Interface, classe abstraite ou classe concrète ?

### Q1

| | Choix | Justification |
|---|---|---|
| a | **interface** `Payable` | seulement un contrat (`amount()`), rien à factoriser |
| b | **classe abstraite** `Shape` | code commun (`name`, `describe()`) + `area()` impossible à écrire en général |
| c | **classe concrète** `Circle` | tout est défini, on veut des instances |
| d | **classe abstraite** `Instrument` | `play()` commune, `sound()` abstraite |
| e | **interface** `Comparable<T>` | un contrat que des classes sans rapport (`String`, `Person`, `Carrot`) respectent |
| f | **classe concrète** `Hotel` | on crée des hôtels |
| g | **classe abstraite** `GameCharacter` | attributs + `attack`, `isDead`… communs ; `interactWith` dépend du personnage |
| h | **interfaces** `LandVehicle` et `Boat` | une classe n'hérite que d'**une** classe, mais implémente **plusieurs** interfaces |

💡 Arbre de décision du cours : *du code ou des attributs à factoriser ?* Non → interface. Oui → *des méthodes impossibles à écrire au niveau général ?* Oui → abstraite ; non → concrète.

### Q2

| | Réponse | Explication |
|---|---|---|
| 1 | **Vrai** | appelé via `super(...)` pour initialiser la partie commune |
| 2 | **Faux** | `abstract` interdit `new`, même sans méthode abstraite (`… is abstract; cannot be instantiated`) |
| 3 | **Vrai** | sinon : `… is not abstract and does not override abstract method …` |
| 4 | **Faux** | `public abstract class A { }` compile : on veut juste interdire les instances |
| 5 | **Vrai** | `interface MarinePredator extends Fish, Predator` |
| 6 | **Faux** | une seule super-classe en Java |
| 7 | **Vrai** | `Shape s = new Circle(1);` |
| 8 | **Faux** | `illegal combination of modifiers: abstract and private` : une méthode `private` ne peut pas être redéfinie |

---

## Exercice 2 — Formes géométriques

### Q1

```java
// Fichier : Shape.java
public abstract class Shape {
    protected String name;

    public Shape(String name) {
        this.name = name;
    }

    public abstract double area();
    public abstract double perimeter();

    // méthodes CONCRÈTES qui utilisent les méthodes abstraites
    public String describe() {
        return this.name + " : aire = " + this.area() + ", périmètre = " + this.perimeter();
    }

    public boolean isBiggerThan(Shape other) {
        return this.area() > other.area();
    }
}
```

💡 `describe()` est écrite **une seule fois**, sans savoir comment on calcule l'aire : `this.area()` sera résolue à l'exécution (late binding) dans la classe de l'objet.

### Q2

```java
// Fichier : Rectangle.java
public class Rectangle extends Shape {
    protected double width;
    protected double height;

    public Rectangle(double width, double height) {
        this("rectangle", width, height);
    }

    protected Rectangle(String name, double width, double height) {   // pour Square
        super(name);
        this.width = width;
        this.height = height;
    }

    public double area() { return this.width * this.height; }
    public double perimeter() { return 2 * (this.width + this.height); }
}
```

```java
// Fichier : Square.java
public class Square extends Rectangle {
    public Square(double side) {
        super("carré", side, side);       // réutilise tout le code de Rectangle
    }
}
```

```java
// Fichier : Circle.java
public class Circle extends Shape {
    private double radius;

    public Circle(double radius) {
        super("cercle");
        this.radius = radius;
    }

    public double area() { return Math.PI * this.radius * this.radius; }
    public double perimeter() { return 2 * Math.PI * this.radius; }
}
```

💡 Comme `Square extends Rectangle` dans le cours, `Square` réutilise `area()` et `perimeter()` de `Rectangle`. Le constructeur `protected` n'est utilisable que par les sous-classes (et le paquetage).

### Q3

| | Compile ? | Pourquoi |
|---|---|---|
| (a) | ❌ `Shape is abstract; cannot be instantiated` | classe abstraite, même avec un constructeur |
| (b) | ✅ | upcast `Square` → `Shape` |
| (c) | ✅ | un carré est un rectangle |
| (d) | ❌ `incompatible types: Rectangle cannot be converted to Square` | un rectangle n'est pas forcément un carré |

### Q4 et Q5

```java
// Fichier : RightTriangle.java
public class RightTriangle extends Shape {
    private double a;
    private double b;

    public RightTriangle(double a, double b) {
        super("triangle rectangle");
        this.a = a;
        this.b = b;
    }

    public double area() { return this.a * this.b / 2; }
    public double perimeter() { return this.a + this.b + Math.sqrt(this.a * this.a + this.b * this.b); }
}
```

```java
// Fichier : ShapeMain.java
import java.util.ArrayList;
import java.util.List;

public class ShapeMain {
    public static void main(String[] args) {
        List<Shape> shapes = new ArrayList<Shape>();
        shapes.add(new Rectangle(3, 4));
        shapes.add(new Square(5));
        shapes.add(new Circle(1));
        for (Shape s : shapes) {
            System.out.println(s.describe());
        }
        System.out.println(shapes.get(1).isBiggerThan(shapes.get(0)));
        // Q5
        System.out.println(new RightTriangle(3, 4).describe());
    }
}
```

Affichage obtenu :

```text
rectangle : aire = 12.0, périmètre = 14.0
carré : aire = 25.0, périmètre = 20.0
cercle : aire = 3.141592653589793, périmètre = 6.283185307179586
true
triangle rectangle : aire = 6.0, périmètre = 12.0
```

💡 Q4 : chaque `describe()` (héritée de `Shape`) appelle l'`area()` de la vraie forme. Le carré (25) est plus grand que le rectangle (12) ⇒ `true`.
💡 Q5 : **aucune** classe existante modifiée, on a seulement **ajouté** `RightTriangle` ⇒ principe **ouvert/fermé (OCP)**, comme l'ajout d'un `Hexagon` dans le cours.

---

## Exercice 3 — L'orchestre

### Q1 et Q2

```java
// Fichier : StringInstrument.java
public abstract class StringInstrument extends Instrument {
    protected int nbStrings;

    public StringInstrument(String name, int nbStrings) {
        super(name);
        this.nbStrings = nbStrings;
    }

    public String toString() {
        return super.toString() + " (" + this.nbStrings + " cordes)";
    }
}
```

💡 `StringInstrument` est **abstraite** : elle a le droit de ne pas définir `sound()`. Ce sont ses sous-classes concrètes (`Guitar`, `Violin`) qui le feront.

```java
// Fichier : Guitar.java
public class Guitar extends StringInstrument {
    public Guitar() { super("guitare", 6); }
    public String sound() { return "Gling"; }
}
```

```java
// Fichier : Violin.java
public class Violin extends StringInstrument {
    public Violin() { super("violon", 4); }
    public String sound() { return "Vziii"; }
}
```

```java
// Fichier : Drum.java
public class Drum extends Instrument {
    public Drum() { super("batterie"); }
    public String sound() { return "Boum"; }
}
```

### Q3

```java
// Fichier : OrchestraMain.java
import java.util.ArrayList;
import java.util.List;

public class OrchestraMain {
    public static void main(String[] args) {
        List<Instrument> orchestra = new ArrayList<Instrument>();
        orchestra.add(new Guitar());
        orchestra.add(new Violin());
        orchestra.add(new Drum());
        for (Instrument i : orchestra) {
            System.out.println(i + " -> " + i.play(2));
        }
    }
}
```

Affichage obtenu :

```text
guitare (6 cordes) -> guitare : Gling Gling
violon (4 cordes) -> violon : Vziii Vziii
batterie -> batterie : Boum Boum
```

💡 `toString()` : version de `StringInstrument` pour la guitare et le violon, version d'`Instrument` pour la batterie. `play` (écrite une fois dans `Instrument`) appelle le `sound()` de chaque objet.

### Q4

| | Erreur de compilation obtenue | Correction |
|---|---|---|
| (a) | `Instrument is not abstract and does not override abstract method sound() in Instrument` | `public abstract class Instrument` |
| (b) | `abstract methods cannot have a body` | `public abstract String sound();` |
| (c) | `Piano is not abstract and does not override abstract method sound() in Instrument` | définir `sound()` dans `Piano` (ou déclarer `Piano` abstraite) |
| (d) | `StringInstrument is abstract; cannot be instantiated` | créer une sous-classe concrète (`Harp`) |

---

## Exercice 4 — Personnages de jeu

### Q1 à Q3

```java
// Fichier : Orc.java
public class Orc extends GameCharacter {
    protected static final int ORC_LIFEPOINTS = 10;

    public Orc(String name, int strength) {
        super(name, ORC_LIFEPOINTS, strength);   // constructeur de la classe abstraite
    }

    public void interactWith(GameCharacter other) {
        this.attack(other);                      // moi vois, moi tue
    }
}
```

```java
// Fichier : CarefulCharacter.java
public class CarefulCharacter extends GameCharacter {
    protected int cautionThreshold;

    public CarefulCharacter(String name, int strength, int limit) {
        super(name, GameCharacter.DEFAULT_LIFEPOINTS, strength);
        this.cautionThreshold = limit;
    }

    public void interactWith(GameCharacter other) {
        if (other.strength() - this.strength > this.cautionThreshold) {
            this.flee();
        } else {
            this.attack(other);
        }
    }
}
```

```java
// Fichier : Healer.java
public class Healer extends GameCharacter {
    private static final int HEAL = 3;

    public Healer(String name) {
        super(name);                             // 12 PV, force 8 par défaut
    }

    public void interactWith(GameCharacter other) {
        System.out.println(this.name + " soigne " + other.name);
        other.removeLifePoints(-HEAL);
    }
}
```

💡 Chaque sous-classe n'écrit **que** son constructeur (qui délègue à `super(...)`) et **sa** version de `interactWith`. `attack`, `flee`, `isDead`… sont écrites une seule fois dans `GameCharacter`.

### Q4

```java
// Fichier : GameMain.java
public class GameMain {
    public static void main(String[] args) {
        GameCharacter orc = new Orc("Grishnak", 9);
        GameCharacter careful = new CarefulCharacter("Bilbo", 5, 3);
        GameCharacter healer = new Healer("Elrond");
        orc.interactWith(careful);
        careful.interactWith(orc);
        healer.interactWith(careful);
        careful.interactWith(healer);
        System.out.println(orc + " " + careful + " " + healer);
        orc.interactWith(careful);
        System.out.println(careful.isDead());
    }
}
```

Affichage obtenu :

```text
Grishnak attaque Bilbo
Bilbo s'enfuit
Elrond soigne Bilbo
Bilbo attaque Elrond
Grishnak (10 PV) Bilbo (6 PV) Elrond (7 PV)
Grishnak attaque Bilbo
true
```

💡 Déroulé : Bilbo 12 → 3 (force 9). Face à l'orc : 9 − 5 = 4 > 3 ⇒ fuite. Soigné : 3 → 6. Face au soigneur : 8 − 5 = 3, **pas** > 3 ⇒ attaque, Elrond 12 → 7. Enfin 6 − 9 = −3 ⇒ mort. Les références sont de type `GameCharacter`, mais chaque `interactWith` exécuté est celui de l'objet (late binding).

### Q5

❌ `GameCharacter is abstract; cannot be instantiated`. Les constructeurs d'une classe abstraite ne servent pas à `new` : ils **initialisent la partie commune** (nom, points de vie, force) quand une sous-classe appelle `super(...)`.

---

## Exercice 5 — `final` : compile ou pas ?

### Q1

| | Compile ? | Message de `javac` / explication |
|---|---|---|
| (1) | ❌ | `cannot inherit from final Euro` : classe `final` ⇒ pas de sous-classe |
| (2) | ❌ | `vote() in Referendum cannot override vote() in Poll` / `overridden method is final` |
| (3) | ✅ | une méthode `final` est **héritée** et utilisable ; seule sa redéfinition est interdite |
| (4) | ❌ | `cannot assign a value to final variable number` : déjà initialisé dans le constructeur |
| (5) | ❌ | `variable number might not have been initialized` : un attribut `final` doit recevoir une valeur |
| (6) | ❌ | `illegal combination of modifiers: abstract and final` : abstraite = à étendre ; `final` = interdit d'étendre |
| (7) | ❌ | `illegal combination of modifiers: abstract and final` : abstraite = à redéfinir ; `final` = interdit de redéfinir |
| (8) | ❌ | `cannot assign a value to static final variable MAX` : une constante ne change pas |
| (9) | ✅ | c'est la **référence** `names` qui est `final` (on ne peut pas écrire `names = …`), pas l'objet liste : on peut y ajouter des éléments |

💡 `final` ≈ « constant » : pour une **classe**, la hiérarchie est figée ; pour une **méthode**, son code est figé ; pour un **attribut / une variable**, la valeur (ou la **référence**) est figée.

### Q2

`vote` est `final` pour **garantir le code exécuté** quel que soit le type de scrutin : aucune sous-classe de `Poll` ne peut la redéfinir pour mémoriser qui a voté quoi (anonymat). Autre exemple : une méthode `payWithCard(code)` d'une banque qui vérifie **toujours** le code avant de débiter ; une sous-classe « carte premium » ne doit pas pouvoir sauter la vérification.

---

## Exercice 6 — La machine à boissons chaudes (style DS)

### Q1 et Q2

```java
// Fichier : HotDrink.java
import java.util.ArrayList;
import java.util.List;

public abstract class HotDrink {

    /**
     * Prepares this drink, always following the same recipe:
     * boil water, brew, pour in cup, then add condiments if wanted.
     * This method is final: subclasses cannot change the order of the steps.
     * @return the list of the steps done, in order
     */
    public final List<String> prepare() {
        List<String> steps = new ArrayList<String>();
        steps.add(this.boilWater());
        steps.add(this.brew());
        steps.add(this.pourInCup());
        if (this.wantsCondiments()) {
            steps.add(this.addCondiments());
        }
        return steps;
    }

    protected String boilWater() { return "faire bouillir l'eau"; }
    protected String pourInCup() { return "verser dans la tasse"; }

    protected abstract String brew();
    protected abstract String addCondiments();

    protected boolean wantsCondiments() { return true; }   // « crochet » redéfinissable
}
```

💡 `prepare()` est le **patron de méthode** : l'algorithme (l'ordre des étapes) est **fermé** (`final`), les étapes variables sont **ouvertes** (abstraites ou redéfinissables). Chaque `this.xxx()` est résolu par lookup depuis la classe de l'objet.

### Q3

```java
// Fichier : Tea.java
public class Tea extends HotDrink {
    protected String brew() { return "faire infuser le thé"; }
    protected String addCondiments() { return "ajouter du citron"; }
}
```

```java
// Fichier : Coffee.java
public class Coffee extends HotDrink {
    protected String brew() { return "passer le café"; }
    protected String addCondiments() { return "ajouter du sucre"; }
}
```

```java
// Fichier : BlackCoffee.java
public class BlackCoffee extends Coffee {
    protected boolean wantsCondiments() { return false; }   // seule redéfinition
}
```

### Q4

```java
// Fichier : HotDrinkTest.java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class HotDrinkTest {

    @Test
    public void teaStepsAreCorrect() {
        List<String> expected = new ArrayList<String>();
        expected.add("faire bouillir l'eau");
        expected.add("faire infuser le thé");
        expected.add("verser dans la tasse");
        expected.add("ajouter du citron");
        assertEquals(expected, new Tea().prepare());
    }

    @Test
    public void coffeeStepsAreCorrect() {
        List<String> steps = new Coffee().prepare();
        assertEquals(4, steps.size());
        assertEquals("passer le café", steps.get(1));
        assertEquals("ajouter du sucre", steps.get(3));
    }

    @Test
    public void blackCoffeeHasNoCondiments() {
        List<String> steps = new BlackCoffee().prepare();
        assertEquals(3, steps.size());
        assertFalse(steps.contains("ajouter du sucre"));
        assertEquals("verser dans la tasse", steps.get(2));
    }

    @Test
    public void firstStepIsAlwaysBoilingWater() {
        HotDrink[] drinks = {new Tea(), new Coffee(), new BlackCoffee()};
        for (HotDrink d : drinks) {
            assertEquals("faire bouillir l'eau", d.prepare().get(0));
        }
    }
}
```

Résultat obtenu :

```text
[         4 tests successful      ]
[         0 tests failed          ]
```

💡 `assertEquals` sur deux listes compare leurs éléments **un à un** avec `equals` (même taille, même ordre).

### Q5

```java
// Fichier : DrinkMain.java
import java.util.ArrayList;
import java.util.List;

public class DrinkMain {
    public static void main(String[] args) {
        List<HotDrink> order = new ArrayList<HotDrink>();
        order.add(new Tea());
        order.add(new BlackCoffee());
        for (HotDrink d : order) {
            System.out.println(d.prepare());
        }
    }
}
```

Affichage obtenu :

```text
[faire bouillir l'eau, faire infuser le thé, verser dans la tasse, ajouter du citron]
[faire bouillir l'eau, passer le café, verser dans la tasse]
```

💡 Pour le café noir : `brew()` de `Coffee` (héritée), `wantsCondiments()` de `BlackCoffee` ⇒ pas de 4ᵉ étape.

### Q6

❌ Erreur de compilation : `prepare() in Tea cannot override prepare() in HotDrink` / `overridden method is final`. C'est voulu : `final` **garantit** que, quelle que soit la boisson (même écrite plus tard par quelqu'un d'autre), la recette respecte l'ordre des étapes — exactement comme `vote` garantit l'anonymat dans `Poll`.

---

## Exercice 7 — Objets connectés

### Q1

**5 méthodes** : `category()` (abstraite dans `Device`), `batteryLevel()` et `charge()` (de `Rechargeable`), `isOnline()` (de `Connected`), `appName()` (de `SmartDevice`). Comme `MarinePredator` dans le cours : `SmartDevice` hérite des méthodes de **ses deux** super-interfaces.

### Q2

```java
// Fichier : Smartwatch.java
public class Smartwatch extends Device implements SmartDevice {
    private int battery;

    public Smartwatch(String brand) {
        super(brand);
        this.battery = 20;
    }

    public String category() { return "montre"; }
    public int batteryLevel() { return this.battery; }
    public void charge() { this.battery = 100; }
    public boolean isOnline() { return true; }
    public String appName() { return "WatchApp"; }
}
```

```java
// Fichier : PowerBank.java
public class PowerBank extends Device implements Rechargeable {
    private int battery;

    public PowerBank(String brand) {
        super(brand);
        this.battery = 50;
    }

    public String category() { return "batterie"; }
    public int batteryLevel() { return this.battery; }
    public void charge() { this.battery = 100; }
}
```

```java
// Fichier : Lamp.java
public class Lamp extends Device {
    public Lamp(String brand) { super(brand); }
    public String category() { return "lampe"; }
}
```

### Q3

```java
// Fichier : ChargingStation.java
import java.util.ArrayList;
import java.util.List;

public class ChargingStation {
    private List<Rechargeable> devices;

    public ChargingStation() {
        this.devices = new ArrayList<Rechargeable>();
    }

    public void plug(Rechargeable r) {
        this.devices.add(r);
    }

    public void chargeAll() {
        for (Rechargeable r : this.devices) {
            r.charge();
        }
    }
}
```

💡 La station ne dépend que de l'interface `Rechargeable` : montres, batteries externes… et tout futur appareil rechargeable, sans modification (OCP).

### Q4

| | Compile ? | Pourquoi |
|---|---|---|
| (a) | ❌ `Device is abstract; cannot be instantiated` | classe abstraite |
| (b) | ✅ | une `Smartwatch` est un `SmartDevice`, donc un `Rechargeable` |
| (c) | ❌ `cannot find symbol … method isOnline()` | le type de la référence (`Rechargeable`) ne connaît pas `isOnline` |
| (d) | ❌ `incompatible types: PowerBank cannot be converted to SmartDevice` | une batterie externe n'est pas connectée |
| (e) | ✅ | `SmartDevice extends Connected` |
| (f) | ❌ `incompatible types: Lamp cannot be converted to Rechargeable` | une lampe n'est pas rechargeable |
| (g) | ✅ | `PowerBank implements Rechargeable` |

### Q5

```java
// Fichier : HomeMain.java
public class HomeMain {
    public static void main(String[] args) {
        Smartwatch watch = new Smartwatch("Garmin");
        PowerBank bank = new PowerBank("Anker");
        ChargingStation station = new ChargingStation();
        station.plug(watch);
        station.plug(bank);
        System.out.println(watch + " " + watch.batteryLevel() + "% / " + bank + " " + bank.batteryLevel() + "%");
        station.chargeAll();
        System.out.println(watch.batteryLevel() + " " + bank.batteryLevel() + " " + watch.isOnline());
        System.out.println(new Lamp("Ikea"));
    }
}
```

Affichage obtenu :

```text
montre Garmin 20% / batterie Anker 50%
100 100 true
lampe Ikea
```

💡 `toString()` est écrite dans `Device` (classe abstraite) et appelle la `category()` de chaque objet.

### Q6

- `Device` a un **attribut** (`brand`), un **constructeur** et du **code** commun (`getBrand`, `toString`) : impossible dans une interface ⇒ classe abstraite.
- `Smartwatch` **hérite déjà** de `Device` ; une classe n'a **qu'une** super-classe. Si `Rechargeable` était une classe, `Smartwatch` ne pourrait pas être à la fois un `Device` et un `Rechargeable`. Les interfaces, elles, se cumulent.

---

## ⚠️ Erreurs fréquentes

| Erreur | Pourquoi c'est faux | Correction |
|---|---|---|
| `new Shape(...)` sur une classe abstraite | pas d'instance directe, même avec un constructeur | `Shape s = new Circle(1);` |
| `public abstract double area() { … }` | une méthode abstraite n'a **pas de corps** | `public abstract double area();` |
| Méthode abstraite dans une classe non `abstract` | `… is not abstract and does not override abstract method …` | `public abstract class …` |
| Sous-classe concrète qui oublie une méthode abstraite | elle hérite d'une méthode sans corps | la définir, ou rendre la sous-classe abstraite |
| Croire qu'une classe abstraite n'a pas de constructeur | elle en a, appelés via `super(...)` | initialiser la partie commune dans la classe abstraite |
| Croire qu'une classe abstraite doit avoir une méthode abstraite | zéro méthode abstraite est permis | `abstract` sert aussi juste à interdire `new` |
| `abstract` + `final` (ou + `private`) | combinaisons contradictoires | choisir : à compléter **ou** figé |
| Redéfinir une méthode `final` | `overridden method is final` | redéfinir les méthodes « étapes », pas la méthode patron |
| Réaffecter un attribut `final` | `cannot assign a value to final variable` | une seule affectation (déclaration ou constructeur) |
| Croire qu'une liste `final` est non modifiable | seule la **référence** est figée | ne pas réaffecter la référence ; le contenu peut changer |
| `class A extends B, C` | une seule super-classe | `extends B implements I1, I2` |
| Appeler une méthode absente du type de la référence (`r.isOnline()`) | le type de la référence fixe les appels légaux | référence du bon type (`SmartDevice`, `Connected`) |

---

⬅️ [Retour à l'énoncé](./enonce.md) · 🔗 [Cours 09](../../01-Cours/09-classes-abstraites.md)

🏠 [Sommaire des exercices](../README.md)
