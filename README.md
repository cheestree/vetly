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
- **Frontend** - Expo (React w/Typescript).
- **Backend** - Spring MVC (Kotlin).
- **Database** - PostgreSQL (SQL), Firebase (NoSQL), Firestore (storage).
- **APIs** - .

## 🚀 Getting Started
###  Prerequisites
- Docker and Docker Compose installed
- Node.js (for Expo development)
- Git

###  Environment Setup
1. Clone the repository

   ```bash
   git clone https://github.com/cheestree/Vetly
   cd vetly

2. Create a .env file in the /src directory with the following variables
    # Database Configuration
   
    ```bash
    POSTGRES_DB=vetly_db
    POSTGRES_USER=vetly_user
    POSTGRES_PASSWORD=your_password
    POSTGRES_HOST=database
    POSTGRES_PORT=5432
    
    # Spring Boot Configuration
    SPRING_PROFILES_ACTIVE=dev
    PORT=8080
    JAVA_OPTS=-Xmx512m
    
    # CORS Configuration
    CORS_ALLOWED_ORIGINS=http://localhost:19006,http://localhost:3000
    
    # Firebase Configuration
    FIREBASE_CREDENTIALS_PATH=/app/config/serviceAccount.json
    
    # Expo Configuration
    EXPO_PUBLIC_API_URL=http://localhost:8080

4. Place your Firebase service account JSON file at:
   ```bash
   src/server/src/main/resources

# :moyai: Running the application
##  Option 1: Full Stack (Recommended for development)
1. Start the backend services:
   ```bash
   cd src
   docker-compose up
   
2. Start the Expo development server (in a new terminal):
   
    ```bash
    cd src/client
    npm install
    npx expo start

3. Access the application
  - **Web**: Open http://localhost:19006 in your browser
  - **Mobile**: Scan the QR code with Expo Go app
  - **Backend API**: Available at http://localhost:8080

##  Option 2: Backend Only
    cd src
    docker-compose up database web

##  Option 2: Database Only
    cd src
    docker-compose up database

#  Development Workflow

1. **Backend changes**: Restart the web service:
    ```bash
    cd src
    docker-compose restart web

2. **Frontend changes**: Hot reload is automatic when running Expo locally
3. **Database changes**: Modify SQL files in the sql/ directory and recreate containers:

    ```bash
    cd src
    docker-compose down -v
    docker-compose up database web

## 💻 Developers
- [Daniel Carvalho](https://github.com/cheestree)

## 🧭 Supervisors
- [Diego Passos](https://github.com/diegogpassos)

## ⚖️ License
This project is licensed under GPL-3.0.
