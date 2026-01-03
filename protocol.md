# Media Rating Platform – Kurzprotokoll
Github-Link: https://github.com/jasimoj/MRP
### Architekturüberblick

HttpServer → Handler → MediaRatingApplication → Router → Controller → Service → Repository

Server: Java 21 mit eingebautem com.sun.net.httpserver.HttpServer (kein Spring).

Router: ordnet Pfade (z.B. /users, /media) den Controllern zu.

Controller: wertet HTTP-Methode + Pfad aus, ruft Service und baut Response.

Service: enthält Logik (z.B. Authentifizierung, Ratings, Media-CRUD).

Repository: führt Datenbank-Queries aus (z. B. Users, Media, Ratings, Favorites, Likes). Gibt Domänenobjekte zurück und übernimmt Mapping zwischen Tabellen/Joins (z. B. media_genres, rating_likes) und Java-Objekten.

### Authentifizierung

#### Stateless Token-Auth:

* Login → Token "username-mrpToken"
* Jeder Request (außer /register & /token) sendet
* Authorization: Bearer <token>.
* RequestMapper prüft Token über AuthService → holt User aus Repo → setzt
* Request.authUserId und Request.authUsername.

Es wird keine eigene Middleware für die Authentifizierung verwendet.
Stattdessen wird beim Starten des Servers entschieden, ob die Authentifizierung gebraucht wird oder nicht.

Der Server bekommt immer nur eine allgemeine Application. Mit instanceof wird dann geprüft, ob es sich um eine
MediaRatingApplication handelt. Nur in diesem Fall wird ein echter Authenticator verwendet.

Wenn die Application keine Authentifizierung braucht, wird ein Platzhalter verwendet, der einfach nur nicht angemeldet
zurückgibt. Dadurch muss der restliche Code nicht ständig prüfen, ob Authentifizierung existiert oder nicht.

Dadurch bleibt der Server allgemein einsetzbar, und die Authentifizierung ist optional, ohne den Ablauf der
Request-Verarbeitung unnötig kompliziert zu machen.

#### Controller-Checks:

* checkAuthorizationByUserId() prüft, ob der eingeloggte User auf die Ressource zugreifen darf.

* Keine Speicherung von Tokens → komplett stateless.

### Datenmodelle

| Entität             | Wichtige Felder                                                                                                                     |
|---------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| **User**            | `id`, `username`, `email`, `favoriteGenre`, `password`                                                                              |
| **Media**           | `id`, `title`, `description`, `mediaType`, `releaseYear`, `ageRestriction`, `genres`, `createdByUserId`, `avgStars`, `ratingsCount` |
| **Rating**          | `id`, `userId`, `mediaId`, `stars`, `comment`, `likesCount`, `confirmed`, `createdAt`                                               |
| **UserCredentials** | `username`, `password`                                                                                                              |
| **AuthPrincipal**   | `userId`, `username`                                                                                                                |
| **Recommendation**  | `id`, `title`, `avgScore`, `ratingsCount`                                                                                           |

### Fehlerbehandlung

#### Zentral in MediaRatingApplication über ExceptionMapper:

* EntityNotFoundException → 404 Not Found - Ressource existiert nicht (z. B. User/Media/Rating unbekannt).
* NotJsonBodyException → 400 Bad Request - Body fehlt oder ist kein gültiges JSON / DTO nicht parsbar.

* MissingRequiredFieldsException → 400 Bad Request - Erforderliche Felder im Request fehlen.

* CredentialMissmatchException → 401 Unauthorized - Fehlender/ungültiger Token oder falsche Login-Daten.

* ForbiddenException → 403 Forbidden - Authentifiziert, aber keine Berechtigung auf die Ressource (Owner-Check schlägt
  fehl).

* JsonConversionException → 500 Internal Server Error - Interner Serialisierungsfehler beim Antworten.

#### Aufgetretene Probleme
* Lesen und Verwenden eines Authentifizierungs-Tokens aus HTTP-Requests, ohne den Token zentral in einer Datenbank oder Session zu speichern.
**Lösung**: Der Token wird direkt aus dem Authorization-Header (Bearer Token) gelesen und stateless verifiziert. Dabei wird geprüft, ob der Token ein gültiges Format besitzt 
und ein existierender Benutzer zugeordnet werden kann. Die Benutzerinformation wird anschließend als AuthPrincipal im Request weitergereicht.
* Docker und Datenbankzugriff: Während der Implementierung kam es zu Login Problemen wodurch ich mich nicht über Dbeaver mit der Datenbank verbindet konnte. **Lösung:** Viel try & error, am Ende hat eine Kombination aus Port ändern, Volume und Datenbank neu aufsetzen geholfen.

### Beispiel-Flow

1. POST /users/register → legt User an
2. POST /users/token → liefert "username-mrpToken"
3. GET /users/<userId>/profile mit Authorization: Bearer username-mrpToken → Profil-JSON
4. POST /media → legt media an
5. PUT /users/<id>/profile → updated user Profil

### Unit Tests
Die Unit Tests konzentrieren sich hauptsächlich auf die Service-Schicht (AuthService, UserService, MediaService, RatingService), weil dort die meiste Business-Logik und die wichtigsten Regeln liegen. Die Repositories werden dabei mit Mocks (Mockito) ersetzt, damit die Tests unabhängig von der Datenbank sind und nur prüfen, ob die Services korrekt entscheiden und korrekt mit dem Repository interagieren.

**AuthServiceTest**:
Getestet wurden erfolgreiche und fehlerhafte Fälle bei Registrierung und Login (z. B. fehlende Eingaben, falsches Passwort, Benutzer nicht vorhanden). Zusätzlich wurde die Token-Verifizierung aus dem Authorization-Header getestet (gültiger Bearer-Token → AuthPrincipal, falsches Prefix/Suffix → Optional.empty()). Diese Logik ist sicherheitsrelevant und muss zuverlässig funktionieren.

**MediaServiceTest**:
Hier wurden typische CRUD-Fälle und Berechtigungen geprüft: Medien erstellen (inkl. korrektem createdByUserId) sowie das Löschen durch Nicht-Ersteller (soll ForbiddenException werfen). Außerdem wurden “Found/Not Found”-Fälle getestet, um sicherzustellen, dass bei fehlenden Datensätzen eine EntityNotFoundException ausgelöst wird.

**RatingServiceTest**:
Getestet wurden die wichtigsten Regeln rund um Ratings: vorhandene/nicht vorhandene Ratings, Listenabfrage, Update nur durch den Besitzer (sonst ForbiddenException), sowie Confirm/Delete ebenfalls nur durch den Besitzer. Zusätzlich wird geprüft, dass Rating-Erstellung scheitert, wenn das Medium nicht existiert (EntityNotFoundException). Diese Tests stellen sicher, dass die Zugriffsregeln (Owner-Checks) nicht umgangen werden können.

**UserServiceTest**:
Hier wurde geprüft, dass User/Profile korrekt geliefert werden, wenn vorhanden, und dass bei fehlenden Einträgen korrekt EntityNotFoundException geworfen wird. Damit ist das erwartete Verhalten der API bei “nicht gefunden” sauber abgesichert.

Insgesamt decken die Tests vor allem Validierung, Fehlerfälle und Berechtigungslogik ab, weil diese Bereiche am ehesten zu Bugs oder Sicherheitsproblemen führen und unabhängig von der Datenbank zuverlässig funktionieren müssen.

### Time-tracking
* Planung & Architektur: ~8 h

* Server & Routing: ~20 h

* Authentifizierung: ~20 h

* Datenbank & Repositories: ~35 h

* Business-Logik & Controller: ~35 h

* Tests & Dokumentation: ~8 h

* Gesamt: ~126h

