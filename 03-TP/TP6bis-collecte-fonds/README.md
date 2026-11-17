# TP6bis — Collecte de fonds

> 📄 Énoncé : [ENONCE.md](./ENONCE.md) — ce fichier-ci est **le README à rendre** sur GitLab. Complète-le au fur et à mesure.

## 👤 Auteur·e
- Nom Prénom : *à compléter*
- Groupe : *à compléter*

## 📝 Sujet
*Décris en 2-3 phrases ce que fait le programme (actions caritatives, dons, gestionnaire `CharityActionManager`).*

## 📌 Choix de conception
*Indique le comportement choisi pour `donations()`, `donationsTotal()` et `closeAction()` quand l'action n'existe pas (le sujet ne le précise pas).*

## ⚙️ Compilation
Depuis le dossier du TP :
```bash
javac -sourcepath src src/fundraising/*.java -d classes
```

## ✅ Tests
```bash
javac -classpath junit-console.jar:classes -d test test/fundraising/*Test.java
java -jar junit-console.jar -classpath test:classes -scan-classpath
```

## 📚 Documentation
```bash
javadoc -sourcepath src -d docs fundraising fundraising.donation
```

## ▶️ Exécution
```bash
java -classpath classes fundraising.FundRaisingMain
```

### Exemple de trace
```text
(colle ici la sortie obtenue)
```

## 🤖 Intelligence Artificielle générative
*Indique si tu as utilisé une IAg, laquelle, et avec quels prompts — ou « Aucune ».*
