import ReactNativeAsyncStorage from "@react-native-async-storage/async-storage";
import { GoogleSignin } from "@react-native-google-signin/google-signin";
import { getApp, getApps, initializeApp } from "firebase/app";
import {
  getAuth,
  getReactNativePersistence,
  initializeAuth,
} from "firebase/auth";
import { Platform } from "react-native";
import firebaseConfig from "../firebaseConfig.json";

GoogleSignin.configure({
  webClientId: firebaseConfig.webClientId,
  offlineAccess: true,
});

let app;

if (!getApps().length) {
  initializeApp(firebaseConfig);
} else {
  initializeApp(); // Use the default app
}

app = getApp();

let auth;

if (Platform.OS === "web") {
  auth = getAuth(app);
} else {
  try {
    auth = initializeAuth(app, {
      persistence: getReactNativePersistence(ReactNativeAsyncStorage),
    });
  } catch (e) {
    auth = getAuth(app);
  }
}

export default { app, auth };
