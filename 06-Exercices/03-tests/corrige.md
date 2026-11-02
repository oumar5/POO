# ✅ 03 — Tests unitaires : corrigé

> ⬅️ **Énoncé :** [./enonce.md](./enonce.md) · 🔗 **Cours :** [03 — Tests unitaires (JUnit)](../../01-Cours/03-tests.md)
> 📌 Tout le code a été **compilé** (Java 21) et les tests **exécutés** avec JUnit 5 (`junit-platform-console-standalone-1.9.3`). Les rapports ci-dessous sont réels.
> Chaque classe de test commence par `import org.junit.jupiter.api.Test;` et `import static org.junit.jupiter.api.Assertions.*;`.

---

## Exercice 1 — La consigne à bagages

### Q1 — Les assertions

| n° | Vérification | Assertion |
|---|---|---|
| a | numéro 12 | `assertEquals(12, locker.getNumber());` |
| b | casier neuf libre | `assertTrue(locker.isFree());` |
| c | aucun sac | `assertNull(locker.getBag());` |
| d | plus libre après `put(b)` | `assertFalse(locker.isFree());` |
| e | le sac est **b lui-même** | `assertSame(b, locker.getBag());` |
| f | propriétaire `"Léa"` | `assertEquals("Léa", locker.getBag().getOwner());` |
| g | le sac n'est **pas** `b2` | `assertNotSame(b2, locker.getBag());` |
| h | `takeBag()` renvoie `b` | `assertSame(b, locker.takeBag());` |

💡 Booléen → `assertTrue` / `assertFalse` ; valeur (nombre, `String`) → `assertEquals` ; **identité** d'un objet → `assertSame` / `assertNotSame` ; absence d'objet → `assertNull`.

### Q2 — `LockerTest` (4 tests réussis)

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LockerTest {

    @Test
    public void newLockerIsFreeAndHasNoBag() {
        Locker locker = new Locker(12);
        assertEquals(12, locker.getNumber());
        assertTrue(locker.isFree());
        assertNull(locker.getBag());
    }

    @Test
    public void putBagInFreeLocker() {
        Locker locker = new Locker(12);
        Bag b = new Bag("Léa");
        assertTrue(locker.isFree());
        locker.put(b);
        assertFalse(locker.isFree());
        assertSame(b, locker.getBag());
        assertEquals("Léa", locker.getBag().getOwner());
    }

    @Test
    public void cannotPutBagInOccupiedLocker() {
        Locker locker = new Locker(12);
        Bag b1 = new Bag("Léa");
        Bag b2 = new Bag("Tom");
        locker.put(b1);
        assertSame(b1, locker.getBag());
        locker.put(b2);
        assertSame(b1, locker.getBag());
        assertNotSame(b2, locker.getBag());
    }

    @Test
    public void takeBagReturnsTheBagAndFreesTheLocker() {
        Locker locker = new Locker(12);
        Bag b = new Bag("Léa");
        locker.put(b);
        assertFalse(locker.isFree());
        Bag taken = locker.takeBag();
        assertSame(b, taken);
        assertTrue(locker.isFree());
        assertNull(locker.getBag());
    }
}
```

```text
[         4 tests successful      ]
[         0 tests failed          ]
```

### Q3 — Pourquoi `assertEquals` sur le propriétaire ne suffit pas

Deux sacs différents peuvent appartenir à « Léa » (ou deux personnes s'appeler Léa) : le test passerait même si le casier rendait **un autre** sac. Seul `assertSame(b, locker.getBag())` garantit que c'est **le même objet**, comme `robbie.getCarriedBox()` dans le cours.

---

## Exercice 2 — Le podomètre qui se trompe

### Q1 — Bilan

**5** tests exécutés : **2** réussis (`newCounterHasZeroSteps`, `walkAddsSteps`), **3** en échec.

### Q2 + Q3 — Analyse des échecs

| Test | Attendu | Obtenu | Ligne | Coupable | Justification |
|---|---|---|---|---|---|
| `goalReachedWhenExactlyTheGoal` | `true` | `false` | 39 | **code** | javadoc : « **at least** goal steps » → `>=` et non `>` |
| `kilometersAfter5000Steps` | `3.5` | `3.0` | 32 | **code** | javadoc : « 5000 steps give 3.5 km » ; `350000 / 100000` est une division **entière** (= 3) |
| `negativeStepsAreIgnored` | `70` | `100` | 25 | **test** | javadoc : « a negative number of steps is **ignored** » → 100 pas, pas 70 |

💡 Toujours relire la **javadoc** avant de corriger : c'est elle le contrat. Ici, un des trois échecs vient d'un test faux.

### Q4 — Corrections

```java
public class StepCounter {
    public static final int STEP_LENGTH_CM = 70;
    private int steps;

    /** build a step counter with 0 steps */
    public StepCounter() {
        this.steps = 0;
    }

    /** @return the number of steps walked */
    public int getSteps() {
        return this.steps;
    }

    /** add steps to the counter; a negative number of steps is ignored
     * @param nbSteps the number of steps walked
     */
    public void walk(int nbSteps) {
        if (nbSteps > 0) {
            this.steps = this.steps + nbSteps;
        }
    }

    /** @return the distance walked, in kilometers (5000 steps give 3.5 km) */
    public double getKilometers() {
        return this.steps * StepCounter.STEP_LENGTH_CM / 100000.0;
    }

    /** @param goal a number of steps
     * @return true if at least goal steps have been walked
     */
    public boolean goalReached(int goal) {
        return this.steps >= goal;
    }
}
```

et dans le test :

```java
    @Test
    public void negativeStepsAreIgnored() {
        StepCounter c = new StepCounter();
        c.walk(100);
        c.walk(-30);
        assertEquals(100, c.getSteps());
    }
```

```text
[         5 tests successful      ]
[         0 tests failed          ]
```

---

## Exercice 3 — La batterie du téléphone

### Q1 — Les cas

| Méthode | Cas normal | Cas limite(s) |
|---|---|---|
| constructeur + `getLevel()` | `new PhoneBattery(42)` → 42 | — |
| `charge` | 30 + 25 → 55 | 70 + 30 → **pile 100** ; 90 + 50 → **plafonné à 100** |
| `use` | 80 − 30 → 50 | 10 − 25 → **0** (jamais négatif) |
| `isEmpty` | à 1 % → `false` | à **0** → `true` |
| `isLow` | à 25 % → `false`, puis −6 → `true` | **19** → `true` ; **20** → `false` |

### Q2 — `PhoneBatteryTest` (10 tests réussis)

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PhoneBatteryTest {

    @Test
    public void newBatteryHasGivenLevel() {
        PhoneBattery battery = new PhoneBattery(42);
        assertEquals(42, battery.getLevel());
    }

    @Test
    public void chargeAddsLevel() {
        PhoneBattery battery = new PhoneBattery(30);
        battery.charge(25);
        assertEquals(55, battery.getLevel());
    }

    @Test
    public void chargeUpToExactly100() {
        PhoneBattery battery = new PhoneBattery(70);
        battery.charge(30);
        assertEquals(100, battery.getLevel());
    }

    @Test
    public void chargeNeverGoesAbove100() {
        PhoneBattery battery = new PhoneBattery(90);
        battery.charge(50);
        assertEquals(100, battery.getLevel());
    }

    @Test
    public void useRemovesLevel() {
        PhoneBattery battery = new PhoneBattery(80);
        battery.use(30);
        assertEquals(50, battery.getLevel());
    }

    @Test
    public void useNeverGoesBelow0() {
        PhoneBattery battery = new PhoneBattery(10);
        battery.use(25);
        assertEquals(0, battery.getLevel());
        assertTrue(battery.isEmpty());
    }

    @Test
    public void batteryIsEmptyOnlyAt0() {
        PhoneBattery battery = new PhoneBattery(1);
        assertFalse(battery.isEmpty());
        battery.use(1);
        assertTrue(battery.isEmpty());
    }

    @Test
    public void batteryIsLowBelow20() {
        PhoneBattery battery = new PhoneBattery(19);
        assertTrue(battery.isLow());
    }

    @Test
    public void batteryIsNotLowAtExactly20() {
        PhoneBattery battery = new PhoneBattery(20);
        assertFalse(battery.isLow());
    }

    @Test
    public void batteryBecomesLowAfterUse() {
        PhoneBattery battery = new PhoneBattery(25);
        assertFalse(battery.isLow());
        battery.use(6);
        assertTrue(battery.isLow());
    }
}
```

```text
[        10 tests successful      ]
[         0 tests failed          ]
```

### Q3 — Quel test attrape quelle erreur ? (vérifié en modifiant le code)

| Code modifié | Test en échec | Message |
|---|---|---|
| `this.level <= PhoneBattery.LOW_LEVEL` | `batteryIsNotLowAtExactly20` | `expected: <false> but was: <true>` |
| `if` de `charge` oublié | `chargeNeverGoesAbove100` | `expected: <100> but was: <140>` |

💡 Sans les tests **aux limites** (20 pile, dépassement de 100), ces deux bugs passeraient inaperçus : `chargeUpToExactly100` réussit même sans le `if` !

---

## Exercice 4 — Le parking qui déborde

### Q1 — Ce qui n'est jamais testé

- un parking **plein** : `isFull()` n'est jamais vérifié à `true` ;
- une voiture qui tente d'entrer dans un parking **plein** (`enter()` → `false`) ;
- une voiture qui « sort » d'un parking **vide** (« nothing happens if it is empty »).

### Q2 — Les 3 tests révélateurs

```java
    @Test
    public void parkingLotIsFullWhenAllPlacesAreTaken() {
        ParkingLot p = new ParkingLot(2);
        p.enter();
        p.enter();
        assertTrue(p.isFull());
    }

    @Test
    public void carCannotEnterFullParkingLot() {
        ParkingLot p = new ParkingLot(2);
        p.enter();
        p.enter();
        assertFalse(p.enter());
        assertEquals(2, p.getNbCars());
    }

    @Test
    public void leavingEmptyParkingLotDoesNothing() {
        ParkingLot p = new ParkingLot(2);
        assertEquals(0, p.getNbCars());
        p.leave();
        assertEquals(0, p.getNbCars());
    }
```

Avec le code **bugué** (exécution réelle) :

```text
│     ├─ carCannotEnterFullParkingLot() ✘ expected: <false> but was: <true>
│     ├─ leavingEmptyParkingLotDoesNothing() ✘ expected: <0> but was: <-1>
│     ├─ parkingLotIsFullWhenAllPlacesAreTaken() ✘ expected: <true> but was: <false>
[         3 tests successful      ]
[         3 tests failed          ]
```

💡 Ce sont exactement les plaintes du gardien : une 3ᵉ voiture entre (`true` au lieu de `false`) et le compteur passe à `-1`.

### Q3 — Code corrigé

```java
public class ParkingLot {
    private int capacity;
    private int nbCars;

    /** build an empty parking lot
     * @param capacity the number of places
     */
    public ParkingLot(int capacity) {
        this.capacity = capacity;
        this.nbCars = 0;
    }

    /** @return the number of cars in the parking lot */
    public int getNbCars() {
        return this.nbCars;
    }

    /** @return true if all the places are taken */
    public boolean isFull() {
        return this.nbCars >= this.capacity;
    }

    /** a car enters, only if the parking lot is not full
     * @return true if the car could enter, false otherwise
     */
    public boolean enter() {
        if (!this.isFull()) {
            this.nbCars = this.nbCars + 1;
            return true;
        }
        return false;
    }

    /** a car leaves the parking lot (nothing happens if it is empty) */
    public void leave() {
        if (this.nbCars > 0) {
            this.nbCars = this.nbCars - 1;
        }
    }
}
```

La classe de test complète = les 3 tests de l'énoncé + les 3 nouveaux :

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParkingLotTest {

    @Test
    public void newParkingLotIsEmpty() {
        ParkingLot p = new ParkingLot(3);
        assertEquals(0, p.getNbCars());
        assertFalse(p.isFull());
    }

    @Test
    public void carCanEnterEmptyParkingLot() {
        ParkingLot p = new ParkingLot(3);
        assertTrue(p.enter());
        assertEquals(1, p.getNbCars());
    }

    @Test
    public void carLeavingDecreasesNbCars() {
        ParkingLot p = new ParkingLot(3);
        p.enter();
        p.enter();
        p.leave();
        assertEquals(1, p.getNbCars());
    }

    @Test
    public void parkingLotIsFullWhenAllPlacesAreTaken() {
        ParkingLot p = new ParkingLot(2);
        p.enter();
        p.enter();
        assertTrue(p.isFull());
    }

    @Test
    public void carCannotEnterFullParkingLot() {
        ParkingLot p = new ParkingLot(2);
        p.enter();
        p.enter();
        assertFalse(p.enter());
        assertEquals(2, p.getNbCars());
    }

    @Test
    public void leavingEmptyParkingLotDoesNothing() {
        ParkingLot p = new ParkingLot(2);
        assertEquals(0, p.getNbCars());
        p.leave();
        assertEquals(0, p.getNbCars());
    }
}
```

```text
[         6 tests successful      ]
[         0 tests failed          ]
```

### Q4 — Non-régression

Corriger `isFull` ou `leave` pourrait **casser** un comportement qui marchait (par exemple `carLeavingDecreasesNbCars`). Relancer **tous** les tests (`-scan-classpath`) vérifie que les anciens tests réussissent **encore** : c'est le test de **non-régression**.

---

## Exercice 5 — La tirelire

### Q1 — Javadoc

```java
    /**
     * Insert money in this piggy bank.
     * Nothing happens if the piggy bank is broken or if cents is not positive.
     * @param cents the amount of money to insert, in cents
     */

    /**
     * Smash this piggy bank to get all its money.
     * After that, the piggy bank is broken and contains 0 cent.
     * @return the money that was in the piggy bank, in cents
     *         (0 if it was already broken)
     */
```

### Q2 — Du cahier des charges aux tests

| Phrase du cahier des charges | Test |
|---|---|
| « créée vide et intacte » | `newPiggyBankIsEmptyAndNotBroken` |
| « on peut y insérer une somme » | `insertAddsMoney` |
| « une somme nulle ou négative est ignorée » | `insertIgnoresNegativeOrZeroAmount` |
| « on récupère tout son contenu, et elle est vide » | `smashGivesAllTheMoneyAndBreaksThePiggyBank` |
| « casser une tirelire déjà cassée ne rapporte rien » | `smashTwiceGivesNothingTheSecondTime` |
| « on ne peut plus rien insérer dans une tirelire cassée » | `cannotInsertInBrokenPiggyBank` |

### Q3 — `PiggyBankTest` (6 tests réussis)

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PiggyBankTest {

    @Test
    public void newPiggyBankIsEmptyAndNotBroken() {
        PiggyBank pig = new PiggyBank();
        assertEquals(0, pig.getAmount());
        assertFalse(pig.isBroken());
    }

    @Test
    public void insertAddsMoney() {
        PiggyBank pig = new PiggyBank();
        pig.insert(200);
        pig.insert(50);
        assertEquals(250, pig.getAmount());
    }

    @Test
    public void insertIgnoresNegativeOrZeroAmount() {
        PiggyBank pig = new PiggyBank();
        pig.insert(100);
        pig.insert(-20);
        pig.insert(0);
        assertEquals(100, pig.getAmount());
    }

    @Test
    public void smashGivesAllTheMoneyAndBreaksThePiggyBank() {
        PiggyBank pig = new PiggyBank();
        pig.insert(150);
        assertFalse(pig.isBroken());
        assertEquals(150, pig.smash());
        assertTrue(pig.isBroken());
        assertEquals(0, pig.getAmount());
    }

    @Test
    public void smashTwiceGivesNothingTheSecondTime() {
        PiggyBank pig = new PiggyBank();
        pig.insert(150);
        pig.smash();
        assertTrue(pig.isBroken());
        assertEquals(0, pig.smash());
    }

    @Test
    public void cannotInsertInBrokenPiggyBank() {
        PiggyBank pig = new PiggyBank();
        pig.smash();
        assertTrue(pig.isBroken());
        pig.insert(100);
        assertEquals(0, pig.getAmount());
    }
}
```

### Q4 — `PiggyBank`

```java
public class PiggyBank {
    private int amount;       // en centimes
    private boolean broken;

    /** build an empty piggy bank, not broken */
    public PiggyBank() {
        this.amount = 0;
        this.broken = false;
    }

    /** @return the amount of money in this piggy bank, in cents */
    public int getAmount() {
        return this.amount;
    }

    /** @return true if this piggy bank has been smashed */
    public boolean isBroken() {
        return this.broken;
    }

    /**
     * Insert money in this piggy bank.
     * Nothing happens if the piggy bank is broken or if cents is not positive.
     * @param cents the amount of money to insert, in cents
     */
    public void insert(int cents) {
        if (!this.broken && cents > 0) {
            this.amount = this.amount + cents;
        }
    }

    /**
     * Smash this piggy bank to get all its money.
     * After that, the piggy bank is broken and contains 0 cent.
     * @return the money that was in the piggy bank, in cents
     *         (0 if it was already broken)
     */
    public int smash() {
        int money = this.amount;
        this.amount = 0;
        this.broken = true;
        return money;
    }
}
```

💡 Pas besoin d'un `if` dans `smash()` : si la tirelire est déjà cassée, `amount` vaut déjà 0, donc elle renvoie 0. Les tests le confirment.

### Q5 — Commandes et rapport

```bash
javac -d classes src/PiggyBank.java
javac -classpath junit-console.jar:classes test/PiggyBankTest.java
java -jar junit-console.jar -classpath test:classes -scan-classpath
```

```text
[         6 tests found           ]
[         6 tests successful      ]
[         0 tests failed          ]
```

---

## Exercice 6 — Le distributeur de boissons (style DS)

### Q1 + Q2 — `Drink.java`

```java
public enum Drink {
    COFFEE(50), TEA(40), CHOCOLATE(60);

    private final int price;   // en centimes

    private Drink(int price) {
        this.price = price;
    }

    /** @return the price of this drink, in cents */
    public int getPrice() {
        return this.price;
    }

    /** @return the cheapest drink */
    public static Drink cheapest() {
        Drink best = Drink.values()[0];
        for (Drink d : Drink.values()) {
            if (d.getPrice() < best.getPrice()) {
                best = d;
            }
        }
        return best;
    }
}
```

### Q3 à Q5 — `VendingMachine.java`

```java
public class VendingMachine {
    private int credit;         // en centimes
    private int nbDrinksSold;

    /** build a machine with no credit and no drink sold */
    public VendingMachine() {
        this.credit = 0;
        this.nbDrinksSold = 0;
    }

    /** @return the current credit, in cents */
    public int getCredit() {
        return this.credit;
    }

    /** @return the number of drinks sold since the creation of the machine */
    public int getNbDrinksSold() {
        return this.nbDrinksSold;
    }

    /** insert a coin (nothing happens if cents is not positive)
     * @param cents the value of the coin, in cents
     */
    public void insertCoin(int cents) {
        if (cents > 0) {
            this.credit = this.credit + cents;
        }
    }

    /**
     * Order a drink. If the credit is sufficient, the price of the drink is
     * removed from the credit and the number of drinks sold is increased by 1.
     * Otherwise, nothing changes.
     * @param d the ordered drink
     * @return true if the drink has been served, false if the credit is too low
     */
    public boolean order(Drink d) {
        if (this.credit >= d.getPrice()) {
            this.credit = this.credit - d.getPrice();
            this.nbDrinksSold = this.nbDrinksSold + 1;
            return true;
        }
        return false;
    }

    /**
     * Give back the remaining credit. The credit becomes 0.
     * @return the money given back, in cents
     */
    public int giveChange() {
        int change = this.credit;
        this.credit = 0;
        return change;
    }
}
```

💡 Q4.1 : la javadoc dit **tout** ce que les tests vont vérifier : condition (crédit suffisant), effets (crédit, nombre de ventes), valeur renvoyée, et le cas d'échec (« nothing changes »).

### Q4.2 + Q5 — Tests

| Cas testé | Attendu |
|---|---|
| machine neuve | crédit 0, 0 vente |
| `insertCoin` | crédit cumulé |
| crédit 100, café (50) | `true`, crédit 50, 1 vente |
| crédit **pile** 40, thé (40) — limite | `true`, crédit 0, 1 vente |
| crédit 50, chocolat (60) | `false`, crédit **inchangé** 50, 0 vente |
| crédit 0 | `false`, 0 vente |
| crédit 100, trois thés | `true`, `true`, `false` ; crédit 20, 2 ventes |
| `giveChange` après un achat | rend 40, crédit 0 |
| `giveChange` sans crédit — limite | rend 0 |

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VendingMachineTest {

    @Test
    public void newMachineHasNoCreditAndNoDrinkSold() {
        VendingMachine m = new VendingMachine();
        assertEquals(0, m.getCredit());
        assertEquals(0, m.getNbDrinksSold());
    }

    @Test
    public void insertCoinIncreasesCredit() {
        VendingMachine m = new VendingMachine();
        m.insertCoin(20);
        m.insertCoin(50);
        assertEquals(70, m.getCredit());
    }

    @Test
    public void orderWithMoreThanEnoughCredit() {
        VendingMachine m = new VendingMachine();
        m.insertCoin(100);
        assertEquals(100, m.getCredit());
        assertTrue(m.order(Drink.COFFEE));
        assertEquals(50, m.getCredit());
        assertEquals(1, m.getNbDrinksSold());
    }

    @Test
    public void orderWithExactCredit() {
        VendingMachine m = new VendingMachine();
        m.insertCoin(40);
        assertTrue(m.order(Drink.TEA));
        assertEquals(0, m.getCredit());
        assertEquals(1, m.getNbDrinksSold());
    }

    @Test
    public void orderFailsWhenCreditIsTooLow() {
        VendingMachine m = new VendingMachine();
        m.insertCoin(50);
        assertFalse(m.order(Drink.CHOCOLATE));
        assertEquals(50, m.getCredit());
        assertEquals(0, m.getNbDrinksSold());
    }

    @Test
    public void orderFailsWithNoCredit() {
        VendingMachine m = new VendingMachine();
        assertFalse(m.order(Drink.TEA));
        assertEquals(0, m.getNbDrinksSold());
    }

    @Test
    public void severalOrdersWithTheSameCredit() {
        VendingMachine m = new VendingMachine();
        m.insertCoin(100);
        assertTrue(m.order(Drink.TEA));
        assertTrue(m.order(Drink.TEA));
        assertFalse(m.order(Drink.TEA));
        assertEquals(20, m.getCredit());
        assertEquals(2, m.getNbDrinksSold());
    }

    @Test
    public void giveChangeReturnsCreditAndResetsIt() {
        VendingMachine m = new VendingMachine();
        m.insertCoin(100);
        m.order(Drink.CHOCOLATE);
        assertEquals(40, m.giveChange());
        assertEquals(0, m.getCredit());
    }

    @Test
    public void giveChangeWithNoCreditGivesNothing() {
        VendingMachine m = new VendingMachine();
        assertEquals(0, m.giveChange());
    }
}
```

Et pour `Drink` :

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DrinkTest {

    @Test
    public void pricesOfDrinks() {
        assertEquals(50, Drink.COFFEE.getPrice());
        assertEquals(40, Drink.TEA.getPrice());
        assertEquals(60, Drink.CHOCOLATE.getPrice());
    }

    @Test
    public void cheapestDrinkIsTea() {
        assertSame(Drink.TEA, Drink.cheapest());
    }
}
```

```text
[        11 tests successful      ]
[         0 tests failed          ]
```

💡 Le test « pile le prix » (`orderWithExactCredit`) attrape l'erreur classique `>` au lieu de `>=`. Le test d'échec vérifie **aussi** que rien n'a changé (crédit et ventes) : sans ça, un code qui retire le prix avant de vérifier passerait.

---

## ⚠️ Erreurs fréquentes

| Erreur | Pourquoi c'est faux | Correction |
|---|---|---|
| `assertEquals(obtenu, attendu)` | message `expected/but was` inversé, trompeur | **attendu en premier** |
| `assertEquals(b, locker.getBag())` pour vérifier « c'est b » | compare avec `equals`, pas l'identité | `assertSame(b, locker.getBag())` |
| `assertTrue(x == null)` | marche, mais message d'échec peu clair | `assertNull(x)` |
| Corriger le code dès qu'un test échoue | le test peut contredire la javadoc (`70` au lieu de `100`) | relire la **javadoc** d'abord |
| Ne tester que des cas « au milieu » | les bugs `>` / `>=` se cachent aux **limites** | tester pile sur la limite **et** juste à côté |
| Tester un échec sans vérifier que rien n'a changé | un code qui modifie l'état avant d'échouer passerait | vérifier aussi l'état (crédit, compteur…) |
| Oublier les préconditions | le test peut réussir « par hasard » | `assertFalse(pig.isBroken())` avant `smash()` |
| Un seul gros test pour tout | on ne sait pas quel comportement est cassé | **un test par comportement**, nom = phrase |
| Ne relancer que le nouveau test | une régression passe inaperçue | `-scan-classpath` : relancer **tous** les tests |
| `public int testX()` / oubli de `@Test` | JUnit ignore ou refuse la méthode | `@Test public void testX()` |

---

⬅️ [Retour à l'énoncé](./enonce.md) · 🏠 [Sommaire des exercices](../README.md)
