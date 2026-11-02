# TP6 — Manipulation d'images

> 📄 Énoncé : [ENONCE.md](./ENONCE.md) — ce fichier-ci est **le README à rendre** sur GitLab (dossier `tp6/`). Complète-le au fur et à mesure.

## 👤 Auteur·e
- Nom Prénom : *à compléter*
- Groupe : *à compléter*

## 📝 Sujet
*Décris en 2-3 phrases ce que fait le programme (images en niveaux de gris, rectangles, négatif, contours).*

## ⚙️ Compilation
Depuis le dossier du TP (le dossier `images/` fourni doit être copié dans `classes/`) :
```bash
cp -r images classes/
javac -sourcepath src src/image/ImageMain.java -d classes
```

## ✅ Tests
```bash
javac -classpath junit-console.jar:classes -d test test/image/*Test.java test/image/color/*Test.java
java -jar junit-console.jar -classpath test:classes -scan-classpath
```

## 📚 Documentation
```bash
javadoc -sourcepath src -d docs image image.color
```

## ▶️ Exécution
```bash
java -classpath classes image.ImageMain /images/lamp.pgm 10
```
- 1ᵉʳ argument : *à compléter (nom du fichier image)*
- 2ᵉ argument : *à compléter (seuil des contours, valeur par défaut ?)*

## 📦 Archive exécutable
```bash
jar cvfe image.jar image.ImageMain images -C classes image
java -jar image.jar /images/lamp.pgm 10
```

### Ce qui s'affiche
*Décris les fenêtres obtenues (ou colle une capture).*

## 🤖 Intelligence Artificielle générative
*Indique si tu as utilisé une IAg, laquelle, et avec quels prompts — ou « Aucune ».*
