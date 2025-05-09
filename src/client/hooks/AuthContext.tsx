import { createContext, useContext, useEffect, useState } from "react";
import {
  getAuth,
  GoogleAuthProvider,
  signInWithCredential,
  signInWithPopup,
  User,
} from "firebase/auth";
import { GoogleSignin } from "@react-native-google-signin/google-signin";
import { Platform } from "react-native";
import firebase from "@/lib/firebase";
import UserServices from "@/api/services/UserServices";

type AuthContextType = {
  user: User | null;
  information: UserInformation | null;
  loading: boolean;
  signIn: () => Promise<void>;
  signOut: () => Promise<void>;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [information, setInformation] = useState<UserInformation | null>(null);
  const [loading, setLoading] = useState(true);
  const [roles, setRoles] = useState<string[]>([]);

  useEffect(() => {
    const unsubscribe = firebase.auth.onAuthStateChanged(async (user) => {
      setLoading(true)
  
      if (user) {
        setUser(user)
  
        try {
          const userInfo = await UserServices.getUserProfile()
          setInformation(userInfo)
        } catch (error) {
          console.error("Error fetching user profile:", error)
          setInformation(null)
        }
      } else {
        setUser(null)
        setInformation(null)
      }
  
      setLoading(false)
    })
  
    return () => unsubscribe()
  }, [])

  async function signIn() {
    if (Platform.OS === "web") {
      const provider = new GoogleAuthProvider();
      const result = await signInWithPopup(firebase.auth, provider);
      setUser(result.user);
    }
    try {
      if (Platform.OS === "android" || Platform.OS === "ios") {
        const rsp = await GoogleSignin.signIn();
        const credential = GoogleAuthProvider.credential(rsp.data?.idToken);
        const result = await signInWithCredential(firebase.auth, credential);
        setUser(result.user);
      }
    } catch (error: any) {
      console.error("Error during sign-in:", error);
      console.error("Error message:", error.stack);
    }
    if(user) {
      try {
        const UserInformation = await UserServices.getUserProfile();
        if (UserInformation) {
          setInformation(UserInformation);
        }
      } catch (error) {
        console.error("Error fetching user information:", error);
      }
    }else{
      console.error("User is null after sign-in");
    }
  }

  async function signOut() {
    try {
      await firebase.auth.signOut();

      if (Platform.OS === "android" || Platform.OS === "ios") {
        await GoogleSignin.signOut();
      }

      setUser(null);
      setInformation(null);
    } catch (error) {
      console.error("Error during sign-out:", error);
    }
  }

  return (
    <AuthContext.Provider value={{ user, information, loading, signIn, signOut }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
