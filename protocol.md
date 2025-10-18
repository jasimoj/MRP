# Media Rating Platform – Kurzprotokoll
### Architekturüberblick

HttpServer → Handler → MediaRatingApplication → Router → Controller → Service → Repository


Server: Java 21 mit eingebautem com.sun.net.httpserver.HttpServer (kein Spring).

Router: ordnet Pfade (z. B. /users, /media) den Controllern zu.

Controller: wertet HTTP-Methode + Pfad aus, ruft Service und baut Response.

Service: enthält Logik (z. B. Authentifizierung, Ratings, Media-CRUD).

Repository: aktuell noch In-Memory-Datenhaltung mit ID-Vergabe.

### Authentifizierung

#### Stateless Token-Auth:
* Login → Token "username-mrpToken"
* Jeder Request (außer /register & /token) sendet
* Authorization: Bearer <token>.
* RequestMapper prüft Token über AuthService → holt User aus Repo → setzt
* Request.authUserId und Request.authUsername.

#### Controller-Checks:
* checkAuthorizationByUserId() oder checkAuthorizationByUsername() prüfen, ob der eingeloggte User auf die Ressource zugreifen darf.

* Keine Speicherung von Tokens → komplett stateless.

### Datenmodelle

| Entität             | Wichtige Felder                                                                                                                                                |
| ------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **User**            | `id`, `username`, `email`, `favoriteGenre`, `favoriteMediaIds`                                                                                  |
| **Media**           | `id`, `title`, `description`, `mediaType`, `releaseYear`, `ageRestriction`, `genres`, `createdByUserId`, `avgScore`, `ratingsCount` |
| **Rating**          | `id`, `userId`, `mediaId`, `stars`, `comment`, `likesCount`, `confirmed`, `timestamp`                               |
| **UserCredentials** | `username`, `password`                                                                                                                                         |
| **AuthPrincipal**   | `userId`, `username`                                                                                                                                           |

### Fehlerbehandlung
#### Zentral in MediaRatingApplication über ExceptionMapper:

* EntityNotFoundException → 404 Not Found - Ressource existiert nicht (z. B. User/Media/Rating unbekannt).
* NotJsonBodyException → 400 Bad Request - Body fehlt oder ist kein gültiges JSON / DTO nicht parsbar.

* MissingRequiredFieldsException → 400 Bad Request - Erforderliche Felder im Request fehlen.

* CredentialMissmatchException → 401 Unauthorized - Fehlender/ungültiger Token oder falsche Login-Daten.

* ForbiddenException → 403 Forbidden - Authentifiziert, aber keine Berechtigung auf die Ressource (Owner-Check schlägt fehl).

* JsonConversionException → 500 Internal Server Error - Interner Serialisierungsfehler beim Antworten.

### Beispiel-Flow

1. POST /users/register → legt User an
2. POST /users/token → liefert "username-mrpToken"
3. GET /users/<userId>/profile mit Authorization: Bearer username-mrpToken → Profil-JSON
4. POST /media → legt media an 
5. PUT /users/<id>/profile → updated user Profil
