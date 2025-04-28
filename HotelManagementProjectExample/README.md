# Systemarchitekturen

# Architektur

	Kernkomponenten:
      1.	Command-Side (Buchungs- und Rechnungsaggregate)
      2.	Event-Bus (Persistieren & Verteilen von Events)
      3.	Query-Side (Projektionen in eigene Read-Modelle)

    Kommunikation:
      – REST-Clients zwischen den Modulen
      – Event-Handler in Query-Side abonnieren Events

    Datenflüsse:
      1.	Client ➔ Command-API
      2.	Command erzeugt Event ➔ Event-Bus
      3.	Event-Bus persistiert & leitet weiter
      4.	Query-API-Projektionen aktualisieren Read-Models

# Einfaches End-to-End-Szenario

Im Folgenden wird ein typischer Durchlauf demonstriert – vom Anlegen eines Kunden bis hin zur Auswertung der erzeugten Events und Read-Models:

1. **Kunde anlegen**  
   Legt einen neuen Kunden an und sendet das `CustomerCreated`-Event über den Event-Bus.
   ```http
   POST http://localhost:8082/api/customer/create?name=Eray&email=eray@example.com
   ```

2. **Zimmer buchen**  
   Erzeugt eine neue Buchung und sendet das `RoomBooked`-Event.
   ```http
   POST http://localhost:8082/api/booking/create?roomId=9&customerId=<customer-id>&fromDate=2025-05-01&toDate=2025-05-05&guests=1
   ```

3. **Buchung stornieren (optional)**  
   Schickt ein `BookingCancelled`-Event und gibt das Zimmer wieder frei.
   ```http
   POST http://localhost:8082/api/booking/<booking-id>/cancel
   ```

4. **Rechnung (Invoice) erzeugen**  
   Auf Basis der bestehenden Buchung wird eine Rechnung erstellt, alle nötigen Daten (Zimmerpreis, Dauer, Standard-Zahlungsmethode „CARD“) kommen automatisch aus den bereits gespeicherten Modellen.
   ```http
   POST http://localhost:8082/api/invoice/create?bookingId=<booking-id>
   ```

5. **Rechnungen im Query-Side abfragen**  
   Liest das Read-Model `InvoiceQueryModel` aus der Query-Applikation aus.
   ```http
   GET http://localhost:8083/api/invoices?bookingId=<booking-id>
   ```

6. **Alle Events ansehen**  
   Über den Event-Bus können Sie alle gespeicherten und bereits deserialisierten Events einsehen.
   ```http
   GET http://localhost:8081/api/events/deserialized?bookingId=<booking-id>
   ```

7. **In-Memory-Daten (Debug) anzeigen**  
   (Nur in der Command-Side, z. B. zum Testen)
   ```http
   GET http://localhost:8082/api/customer/debug
   GET http://localhost:8082/api/booking/debug
   ```

8. **Alle Kunden im Query-Side**  
   Gibt das Read-Model aller Kunden zurück.
   ```http
   GET http://localhost:8083/api/customers
   ```

9. **Alle Buchungen im Query-Side**  
   Listet alle Buchungen im angegebenen Zeitraum auf.
   ```http
   GET http://localhost:8083/api/bookings?from=2025-05-01&to=2025-05-31
   ```
   
