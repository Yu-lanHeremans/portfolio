# Alhambra - dobbelspel (Java)

Een Java-implementatie van een dobbelspel in de stijl van Alhambra. Het project focust op OOP, duidelijke domeinlogica en een gescheiden opbouw (GUI/CUI, domein en persistentie). Je kan het spel zowel via een console-interface als via een JavaFX-interface spelen.

## Overzicht

- Spel voor 3 tot 6 spelers met unieke kleuren
- Drie rondes met dobbelstenen en zetstenen
- Scoreberekening via bonusfiches en gebouwpunten
- Spelerregistratie met validatie en opslag in MySQL
- JavaFX GUI met startscherm, registratie, instellingen en spelbord

## Spelverloop (implementatie)

1. Registreer spelers (gebruikersnaam en geboortejaar).
2. Kies 3 tot 6 spelers en wijs kleuren toe.
3. Elke ronde werpt de huidige speler 8 dobbelstenen (max 3 worpen per beurt).
4. Zetstenen worden geplaatst op basis van de dobbelsteenwaarden.
5. Na elke ronde worden bonusfiches en gebouwpunten toegekend.
6. Na 3 rondes wordt de winnaar bepaald op basis van de score.

## Belangrijkste features

- Validatie van spelers (minimale gebruikersnaam en leeftijdscontrole)
- Aantal zetstenen afhankelijk van het aantal spelers (3 spelers: 5, 4 spelers: 4, anders: 3)
- Bonusfiches en startfiche per kolom
- Scoretabel per ronde met gebouwpunten
- Statistieken per speler (aantal gespeeld / gewonnen)
- JUnit tests voor de domeinlogica

## Architectuur

- **domein**: spelregels, spelers, rondes, spelbord en scoring
- **cui**: console-interface om het spel te spelen
- **gui**: JavaFX-interface met schermen en visuele assets
- **persistentie**: opslag van spelers via JDBC
- **dto / utils / exceptions**: ondersteunende klassen en helpers

## Benodigdheden

- JDK met JavaFX modules (javafx-controls, javafx-media)
- MySQL database voor speleropslag

## Database

De databaseverbinding staat hardcoded in [alhambra/src/persistentie/Connectie.java](alhambra/src/persistentie/Connectie.java). Pas deze aan naar je eigen schema en credentials.

De tabel bevat minimaal deze kolommen:

- gebruikersnaam
- geboortejaar
- aantalGewonnen
- aantalGespeeld

## Starten

- Console: run [alhambra/src/main/StartUp.java](alhambra/src/main/StartUp.java)
- GUI (JavaFX): run [alhambra/src/main/StartUpGUI.java](alhambra/src/main/StartUpGUI.java)

## Tests

JUnit tests controleren de domeinlogica. Een voorbeeld is [alhambra/src/testen/SpelTest.java](alhambra/src/testen/SpelTest.java).

## Assets

Afbeeldingen en audio worden via het classpath geladen door de JavaFX GUI.
