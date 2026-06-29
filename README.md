# Lagerverwaltungs-Webapp

Eine Spring-Boot-Webapp zur Lagerverwaltung mit klarer Trennung von Darstellung und Geschäftslogik.

## Funktionen

- Produkte anlegen
- Produktbilder hinterlegen (URL/Pfad) mit Vorschau in der Produktliste
- Produktliste anzeigen
- Verkäufe erfassen
- Nachbestellungen erfassen
- Verkaufsübersicht anzeigen
- Nachbestellübersicht anzeigen
- Dashboard mit Kennzahlen und Warnhinweisen bei niedrigem Bestand

## Technik

- Java 21
- Spring Boot 4.1.0
- Spring Data JPA (Hibernate 7)
- Thymeleaf 3.1
- MariaDB (Produktiv/Prüfungsprofil)
- H2 (Profil `dev` für lokalen Start ohne MariaDB)

## Lokaler Start

Standardmäßig startet die App jetzt mit dem Profil `dev` und einer persistenten H2-Datenbank.

1. Anwendung direkt starten (ohne MariaDB):

```powershell
./mvnw spring-boot:run
```

2. Optional: mit MariaDB starten (Profil `mariadb`):

```powershell
$env:SPRING_PROFILES_ACTIVE="mariadb"
$env:SPRING_DATASOURCE_URL="jdbc:mariadb://localhost:3306/lagerverwaltung"
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD="deinPasswort"
./mvnw spring-boot:run
```

2a. Optional: MariaDB schnell per Docker starten:

```powershell
docker compose up -d
```

Dann die App mit passenden Variablen starten:

```powershell
$env:SPRING_PROFILES_ACTIVE="mariadb"
$env:SPRING_DATASOURCE_URL="jdbc:mariadb://localhost:3306/lagerverwaltung"
$env:SPRING_DATASOURCE_USERNAME="lagerapp"
$env:SPRING_DATASOURCE_PASSWORD="lagerapp"
./mvnw spring-boot:run
```

3. Im Browser öffnen:

- `http://localhost:8080/`
- `http://localhost:8080/produkte`
- `http://localhost:8080/verkaeufe`
- `http://localhost:8080/nachbestellungen`

4. Optional für `dev`: H2-Konsole öffnen:

- `http://localhost:8080/h2-console`

Die `dev`-Datenbank liegt lokal unter `data/lagerverwaltung.*`. Wenn du die Daten zurücksetzen willst, lösche diese Datei(en) bei gestoppter Anwendung.

## Tests

```powershell
./mvnw test
```

## Datenbankschema (vereinfacht)

- `produkte`
  - `id` (PK)
  - `name` (eindeutig)
  - `kategorie`
  - `beschreibung`
  - `bild_url` (optional)
  - `preis`
  - `bestand`
  - `mindestbestand`
  - `erstellt_am`, `aktualisiert_am`
- `verkaeufe`
  - `id` (PK)
  - `produkt_id` (FK -> `produkte.id`)
  - `menge`
  - `kundenname` (optional)
  - `einzelpreis`, `gesamtpreis`
  - `verkauft_am`
- `nachbestellungen`
  - `id` (PK)
  - `produkt_id` (FK -> `produkte.id`)
  - `menge`
  - `lieferant` (optional)
  - `bemerkung` (optional)
  - `voraussichtliches_lieferdatum` (optional)
  - `tatsaechliches_lieferdatum` (optional)
  - `status`
  - `bestellt_am`

## Modellübersicht

- `Produkt`: Stammdaten, Kategorie, optionales Produktbild (URL/Pfad), Preis und Lagerbestand mit Nachzuliefern-Status bei negativem Bestand.
- `Verkauf`: Verkaufsvorgang mit Produktbezug, Menge, optionalem Kundenname, Preis und Datum.
- `Nachbestellung`: Nachbestellvorgang mit Lieferant, Status und Lieferdaten; Wareneingang erhöht den Bestand.

## Beispieldaten / Testszenarien

Für die LAP sollten mindestens 5 Produkte aus mehreren Kategorien angelegt werden, z. B.:

1. `Notebook Basic`, Kategorie `Elektronik`, Preis `799.00`, Bestand `10`
2. `USB-C Kabel`, Kategorie `Elektronik`, Preis `12.90`, Bestand `50`
3. `Bürostuhl Ergo`, Kategorie `Möbel`, Preis `249.00`, Bestand `5`
4. `Lagerbox XL`, Kategorie `Logistik`, Preis `19.90`, Bestand `25`
5. `Druckerpapier A4`, Kategorie `Bürobedarf`, Preis `6.50`, Bestand `100`

Empfohlene Prüfszenarien:

- Verkäufe erfassen (mit/ohne Kundenname, mit Rabatt-/Skontoauswahl und optionalem Datum)
- Negative Bestände erzeugen und Nachzuliefern-Markierung prüfen
- Nachbestellungen anlegen, als eingetroffen markieren und Bestandsänderung prüfen
- Löschen von Verkäufen (Bestandskorrektur) und Löschen von Nachbestellungen vor Wareneingang

