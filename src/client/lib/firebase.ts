import { getApp, getApps, initializeApp } from 'firebase/app';
import { GoogleSignin } from '@react-native-google-signin/google-signin'
import { getAuth, getReactNativePersistence } from 'firebase/auth';
import { initializeAuth  } from 'firebase/auth';
import ReactNativeAsyncStorage from '@react-native-async-storage/async-storage';
import { Platform } from 'react-native';

const firebaseConfig = {
    apiKey: "AIzaSyCTKhMkOgonrQ4etmcER8IfmBU4cw0umoc",
    authDomain: "vetly-ac89c.firebaseapp.com",
    projectId: "vetly-ac89c",
    storageBucket: "vetly-ac89c.firebasestorage.app",
    messagingSenderId: "285279634950",
    appId: "1:285279634950:web:509ea0617b4f83633d2587",
    measurementId: "G-S4DCBY7R55"
  };


GoogleSignin.configure({
    webClientId: '285279634950-85rhv94um27qqobdrcd9md235g9ehmut.apps.googleusercontent.com',
    offlineAccess: true
});

let app

if (!getApps().length) {
    initializeApp(firebaseConfig);
  } else {
    initializeApp(); // Use the default app
  }

app = getApp()

  let auth
  
  if (Platform.OS === 'web') {
    auth = getAuth(app)
  } else {
    try {
      auth = initializeAuth(app, {
        persistence: getReactNativePersistence(ReactNativeAsyncStorage)
      })
    } catch (e) {
      auth = getAuth(app)
    }
  }

export default { app, auth };
