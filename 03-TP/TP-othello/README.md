# TP Othello

> 📄 Énoncé : [ENONCE.md](./ENONCE.md) — ce fichier-ci est **le README à rendre** sur GitLab. Complète-le au fur et à mesure.

## 👤 Auteur·e
- Nom Prénom : *à compléter*
- Groupe : *à compléter*

## 📝 Sujet
*Décris en 2-3 phrases ce que fait le programme.*

## ⚙️ Compilation
Depuis le dossier du TP :
```bash
javac -sourcepath src src/othello/*.java -d classes
```

## ✅ Tests
```bash
javac -classpath junit-console.jar:classes -d test test/othello/*Test.java
java -jar junit-console.jar -classpath test:classes -scan-classpath
```

## 📚 Documentation
```bash
javadoc -sourcepath src -d docs othello
```

## ▶️ Exécution
```bash
java -classpath classes othello.OthelloMain
# ou, avec le jar exécutable :
jar cvfe othello.jar othello.OthelloMain -C classes .
java -jar othello.jar
```

### Exemple de trace
```text
(colle ici la sortie obtenue)
```

## 🤖 Intelligence Artificielle générative
*Indique si tu as utilisé une IAg, laquelle, et avec quels prompts — ou « Aucune ».*
