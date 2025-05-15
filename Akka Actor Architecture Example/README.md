

# Smart-Home & Order-Manager Docker Setup

## Voraussetzungen

- Docker & Docker Compose installiert  
- Die JAR-Artefakte beider Module sind über Gradle/Shadow-Jar im Verzeichnis `build/libs` verfügbar.

---

## 1. Order-Manager bauen & starten

1. **Image für Order-Manager bauen**  
   Aus dem Projekt-Root:
   ```bash
   docker build \
     -t akkaactorarchitectureexample-order-manager:latest \
     -f order-manager/Dockerfile \
     ./order-manager
   ```

2. **Order-Manager im Hintergrund starten**  
   ```bash
   docker compose up -d order-manager
   ```
   Dadurch bindet der Order-Manager seinen gRPC-Server an Port 50051 und meldet sich healthy.

---

## 2. Smart-Home interaktiv starten

1. **Image für Smart-Home bauen**  
   ```bash
   docker build \
     -t akkaactorarchitectureexample-smart-home:latest \
     -f smart-home/Dockerfile \
     ./smart-home
   ```

2. **Smart-Home-Container im interaktiven Modus starten**  
   ```bash
   docker run -it \
     --name smart-home-interactive \
     --network akkaactorarchitectureexample_default \
     -e ORDER_MANAGER_HOST=order-manager \
     -e ORDER_MANAGER_PORT=50051 \
     akkaactorarchitectureexample-smart-home:latest
   ```
   - `-it` öffnet ein Terminal (TTY) für die UI-CLI  
   - `--network akkaactorarchitectureexample_default` verbindet den Container ins Compose-Netzwerk  
   - Env-Vars `ORDER_MANAGER_HOST`/`ORDER_MANAGER_PORT` zeigen auf den Order-Manager

---

## 3. Verfügbare CLI-Befehle in der Smart-Home-UI

Sobald im Container erscheint:
```
Connection OK!
```
kannst du folgende Befehle eintippen:

| Befehl                         | Beschreibung                                                   |
| ------------------------------ | -------------------------------------------------------------- |
| `inventory`                    | Zeigt aktuelles Inventar (Produkt → Stückzahl)                 |
| `consume <produkt> [anzahl]`   | Konsumiere X Einheiten eines Produkts (Standard 1)             |
| `order <produkt> <anzahl>`     | Bestellt nach, fügt ins Inventar hinzu                         |
| `weight`                       | Zeigt das Gesamtgewicht aller Produkte (WeightSensor)          |
| `space`                        | Zeigt die belegten Slots/Kapazität (SpaceSensor)               |

**Beispiel:**
```text
# inventory
=== Aktuelles Inventar ===
• milk:   10 Stück
• eggs:    9 Stück
• cheese: 10 Stück
=========================

# consume eggs 2
Verbrauche: 2 x eggs

# weight
Aktuelles Gewicht: 2.40 kg

# space
Belegte Slots: 38

# order milk 5
Bestellt: milk x5 für 12.50€
```

---

## 4. Logs & Troubleshooting

- **Logs live mitlesen**  
  ```bash
  docker logs -f smart-home-interactive
  ```
- **Shell öffnen (falls im Hintergrund gestartet)**  
  ```bash
  docker attach smart-home-interactive
  ```
  Mit `Ctrl+P` `Ctrl+Q` wieder lösen, ohne den Container zu stoppen.