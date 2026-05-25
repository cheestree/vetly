import userApi from "@/api/user/user.api";
import { UserInformation } from "@/api/user/user.output";
import firebase from "@/lib/firebase";
import { GoogleSignin } from "@react-native-google-signin/google-signin";
import {
  createUserWithEmailAndPassword,
  GoogleAuthProvider,
  signInWithCredential,
  signInWithEmailAndPassword,
  // @ts-ignore signInWithPopup is available on web builds.
  signInWithPopup,
  User,
} from "firebase/auth";
import { Platform } from "react-native";

async function loginBackendUser(user: User): Promise<UserInformation> {
  const idToken = await user.getIdToken();
  return (await userApi.login(idToken)).user;
}

async function signUpWithEmailPassword(email: string, password: string) {
  const credential = await createUserWithEmailAndPassword(
    firebase.auth,
    email,
    password,
  );
  return credential.user;
}

async function signInWithEmailPassword(email: string, password: string) {
  const credential = await signInWithEmailAndPassword(
    firebase.auth,
    email,
    password,
  );
  return credential.user;
}

async function signInWithGoogle() {
  if (Platform.OS === "web") {
    const provider = new GoogleAuthProvider();
    return (await signInWithPopup(firebase.auth, provider)).user;
  }

  const response = await GoogleSignin.signIn();
  const credential = GoogleAuthProvider.credential(response.data?.idToken);
  return (await signInWithCredential(firebase.auth, credential)).user;
}

async function signOutFromProviders() {
  await firebase.auth.signOut();

  if (Platform.OS === "android" || Platform.OS === "ios") {
    await GoogleSignin.signOut();
  }
}

export default {
  loginBackendUser,
  signInWithEmailPassword,
  signInWithGoogle,
  signOutFromProviders,
  signUpWithEmailPassword,
};
