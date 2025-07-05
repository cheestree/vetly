# 🐈 Vetly
An all-in-one multiplatform veterinarian clinic manager web application.

Made for the Final Project of a BSc's in Instituto Superior de Engenharia de Lisboa (Summer Semester 2024/2025).

## ⁉️ But why?
Most companies nowadays tend to focus on creating one thing and one thing only, but improve it as best they can. Some have dedicated apps for each functionality. That's where _Vetly_ comes in.

_Vetly_ tries to fill in this gap of functionality and connectivity by giving both ends new ways to communicate and connect. Need to know what food you should give your dog when he's sick? You can get such info from guides or directly from a checkup with a vet. Managing your clinics inventory with multiple apps is tiresome? It takes care of it with a centralized system. Your parrot is having issues breathing and needs immediate attention? Some clinics might be available in your area to aid you.

## ✨Features
- **Multiplatform** - no need for native apps, simply use your device's browser and use it seamlessly.
- **Cloud-hosted information** - access your pet and/or clinics information from anywhere, with an internet connection.
- **Tight-knit community** - our experience veterinarians can give you tips and guides on how to best take care of your pet.
- **Emergency checkups** - using Google Maps' integration, if you have an emergency, you can head to a emergency-capable clinic.

## ⚙️ Technologies
- **Frontend** - Expo (React Native with TypeScript).
- **Backend** - Spring MVC (Kotlin).
- **Database** - PostgreSQL (SQL), Firebase Firestore (NoSQL), [Firebase Storage](https://firebase.google.com/docs/storage).
- **Containerization** - Docker & Docker Compose.
- **Authentication & Messaging** - Firebase Authentication.
- **Optional Tools**:
  - EAS Build (for building standalone mobile apps)

## 🚀 Getting Started
### 🛠️ Prerequisites

- **Required for development (Expo + web)**
  - [Docker](https://docs.docker.com/get-docker/) and Docker Compose installed
  - [Node.js](https://nodejs.org/) (recommended: latest LTS)
  - [Git](https://git-scm.com/)
  - **Firebase service account**:
    - Required for the backend to authenticate with Firebase services.
    - Obtain a service account JSON file from your Firebase console (under Project Settings → Service accounts → Generate new private key).

- **Recommended for better Expo development**
  - [Expo Go](https://expo.dev/client) installed on your physical device (iOS/Android) to run the app during development.

- **Optional: Android native builds**
  - Android Studio (with Android SDK, platform tools, and emulator installed)
  - Java Development Kit (JDK) 11 or newer (required by Android builds)

- **Optional: iOS native builds (macOS only)**
  - macOS with Xcode installed (required to build or test iOS apps locally)
  - Xcode Command Line Tools (`xcode-select --install`)

- **Optional: EAS (Expo Application Services) builds**
  - Expo CLI installed globally (`npm install -g expo-cli`)
  - An Expo account (free) to sign in with `npx expo login`

> 📝 **Notes**
> - You don’t need Android Studio or Xcode if you only plan to use Expo Go during development.
> - EAS Build allows you to build standalone apps in the cloud without local Android/iOS toolchains, but you still need the correct credentials for signing apps.

###  Environment Setup
1. Clone the repository
    ```
    git clone https://github.com/cheestree/Vetly
    cd vetly
    ```

2. Create a .env file in the `/src` directory with the following variables
    # Database Configuration
   
    ```env
    #   Spring Application configuration
    SPRING_PROFILES_ACTIVE=dev
    JAVA_OPTS=-Xmx512m
    CORS_ALLOWED_ORIGINS=http://localhost:8081
    FIREBASE_CREDENTIALS_PATH=/app/config/serviceAccount.json

    #   Database configuration
    POSTGRES_DB=postgres
    POSTGRES_USER=postgres
    POSTGRES_PASSWORD=postgres
    POSTGRES_HOST=localhost

    #   Port mappings (host machine ports)
    DB_HOST_PORT=4343
    WEB_HOST_PORT=8080

    #   Internal application port
    PORT=8080
    ```

3. In your frontend project (`src/client`), create a separate `.env` with your API endpoint:
    ```env
    EXPO_PUBLIC_API_URL=http://<your-lan-ip>:8080/api
    ```

4. Place your Firebase service account file for the backend (admin SDK) at:
    ```
    src/server/src/main/resources/serviceAccount.json
    ```

5. Place your Firebase Android configuration file (`google-services.json`) at:
    ```
    src/client
    ```

6. (Optional) Place your Firebase iOS configuration file (`GoogleService-Info.plist`) at:
    ```
    src/client
    ```

✅ **Notes**
- The backend service account JSON is different from the mobile app Firebase config files.
- Update `EXPO_PUBLIC_API_URL` in your client `.env` to point to your machine's LAN IP or ngrok URL.

# 🗿 Running the application
##  Option 1: Full Stack (Recommended for development)
1. Start the backend services:
    ```
    cd src
    docker-compose up
    ```
   
2. Start the Expo development server (in a new terminal):
    ```
    cd src/client
    npm install
    npm run start
    ```

**📱 Important: Physical Device Setup**
- If you're using a physical device (not an emulator/simulator), you must ensure your device can reach the API server:
  - If your device is on the same Wi-Fi network as your development machine, set your machine's local IP in `src/client/.env`, e.g.:
    ```
    EXPO_PUBLIC_API_URL=http://192.168.x.x:8080/api
    ```
  - If your device is **not on the same network**, or you run into connectivity issues, use [ngrok](https://ngrok.com/) to expose your backend:
    ```
    ngrok http 8080
    ```
    Then copy the HTTPS URL from ngrok into your `.env`:
    ```
    EXPO_PUBLIC_API_URL=https://your-ngrok-id.ngrok.io/api
    ```

- After updating `.env`, restart the Expo server to pick up changes:
    ```
    npm run start
    ```
    ✅ This ensures your mobile device can communicate with your API reliably.


3. Access the application
  - **Web**: Open the web app in your browser at the URL shown in your Expo terminal (commonly http://localhost:8081 or http://localhost:19006)
  - **Mobile**: Scan the QR code in the Expo terminal with the Expo Go app (for development), or install the standalone build once you generate it by running `npm run android` (build located at `src/client/android/app/build/outputs/apk/debug`).
  - **Backend API**: Runs locally at http://localhost:8080 on your development machine.  
  ⚠️ **Note**: Physical devices cannot reach `localhost` directly — ensure `EXPO_PUBLIC_API_URL` in your `.env` points to your machine’s LAN IP or an ngrok URL.
##  Option 2: Backend Only
```
cd src
docker-compose up database web
```

##  Option 2: Database Only
```
cd src
docker-compose up database
```

#  Development Workflow

1. **Backend changes**: Restart the backend service to pick up code or config updates:
    ```
    cd src
    docker-compose restart web
    ```

2. **Frontend changes**: Hot reload (Fast Refresh) is automatic when running Expo locally.
    - If you change environment variables or native code, restart the Expo server:
      ```
      npm run start
      ```
3. **Database changes**: Modify SQL files in the sql/ directory and recreate containers: 
    
    ⚠️ This will destroy all data in your database.
    ```
    cd src
    docker-compose down -v
    docker-compose up database web
    ```

## 💻 Developers
- [Daniel Carvalho](https://github.com/cheestree)

## 🧭 Supervisors
- [Diego Passos](https://github.com/diegogpassos)

## ⚖️ License
This project is licensed under GPL-3.0.
