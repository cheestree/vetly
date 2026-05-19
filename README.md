# 🐈 Vetly

An all-in-one veterinarian clinic manager — built as the Final Project for a BSc at Instituto Superior de Engenharia de Lisboa (Summer Semester 2024/2025).

Vetly connects pet owners and clinics through a single platform: pet health records, inventory management, care guides, and emergency clinic lookup via Google Maps.

## Technologies

| Layer | Stack |
|---|---|
| Frontend | Expo (React Native + TypeScript) |
| Backend | Spring MVC (Kotlin) |
| Database | PostgreSQL, Firebase Firestore, Firebase Storage |
| Auth & Messaging | Firebase Authentication |
| Infrastructure | Docker & Docker Compose |

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) and Docker Compose
- [Node.js](https://nodejs.org/) (latest LTS)
- [Git](https://git-scm.com/)
- A Firebase project with a service account JSON (Project Settings → Service accounts → Generate new private key)

> **Optional:** [Expo Go](https://expo.dev/client) on a physical device for mobile development. Android Studio and Xcode are only needed for native builds.

## ⚙️ Setup

### 1. Clone the repository

```bash
git clone https://github.com/cheestree/Vetly
cd vetly
```

### 2. Configure environment variables

Create `src/.env`:

```env
# Spring
SPRING_PROFILES_ACTIVE=dev
JAVA_OPTS=-Xmx512m
CORS_ALLOWED_ORIGINS=http://localhost:8081
BUCKET_NAME=<your-firestore-bucket-name>

# PostgreSQL
POSTGRES_DB=postgres
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_HOST=localhost

# Ports
DB_HOST_PORT=4343
WEB_HOST_PORT=8080
PORT=8080
```

Create `src/client/.env`:

```env
EXPO_PUBLIC_API_URL=http://<your-lan-ip>:8080/api
```

### 3. Place Firebase config files

| File | Location |
|---|---|
| Backend service account (`serviceAccount.json`) | `src/server/src/main/resources/` |
| Frontend Firebase config (`firebaseConfig.json`) | `src/client/` |
| Android config (`google-services.json`) | `src/client/` |
| iOS config (`GoogleService-Info.plist`) *(optional)* | `src/client/` |

`firebaseConfig.json` must include a `webClientId` field. Find it in Google Cloud Console → Credentials → OAuth 2.0 Client IDs (type: Web application).

```json
{
  "apiKey": "...",
  "authDomain": "...",
  "projectId": "...",
  "storageBucket": "...",
  "messagingSenderId": "...",
  "appId": "...",
  "measurementId": "...",
  "webClientId": "..."
}
```

## 🚀 Running

### Full stack (recommended)

```bash
# Terminal 1 — backend
cd src
docker-compose up

# Terminal 2 — frontend
cd src/client
npm install
npm run start
```

- **Web:** `http://localhost:8081` (or as shown in the Expo terminal)
- **API:** `http://localhost:8080`
- **Mobile:** Scan the QR code in the Expo terminal with Expo Go

> **Physical device:** Set `EXPO_PUBLIC_API_URL` to your machine's LAN IP, not `localhost`. If the device is on a different network, use [ngrok](https://ngrok.com/): `ngrok http 8080`.

### Backend only

```bash
cd src && docker-compose up database web
```

### Database only

```bash
cd src && docker-compose up database
```

> A default admin account is available at `admin@gmail.com` / `adminadmin`.

## Development Workflow

**Backend changes** — restart the service:
```bash
cd src && docker-compose restart web
```

**Frontend changes** — hot reload is automatic. Restart only when changing env vars or native code:
```bash
npm run start
```

**Database schema changes** — ⚠️ this destroys all data:
```bash
cd src
docker-compose down -v
docker-compose up database web
```

## Developers

- [Daniel Carvalho](https://github.com/cheestree)

## Supervisors

- [Diego Passos](https://github.com/diegogpassos)

## License

[GPL-3.0](LICENSE)