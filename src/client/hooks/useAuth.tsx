import userApi from "@/api/user/user.api";
import { Role, UserInformation } from "@/api/user/user.output";
import { safeCall } from "@/handlers/Handlers";
import firebase from "@/lib/firebase";
import { hasRole } from "@/lib/utils";
import { GoogleSignin } from "@react-native-google-signin/google-signin";
import {
  GoogleAuthProvider,
  signInWithCredential,
  //  @ts-ignore
  signInWithPopup,
  User,
} from "firebase/auth";
import {
  createContext,
  ReactNode,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";
import { Platform } from "react-native";

type AuthContextType = {
  user: User | null;
  information: UserInformation | null;
  loading: boolean;
  signIn: () => Promise<void>;
  signOut: () => Promise<void>;
  hasRoles: (...allowedRoles: Role[]) => boolean;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [information, setInformation] = useState<UserInformation | null>(null);
  const [loading, setLoading] = useState(true);
  const hasRoles = useCallback(
    (...allowedRoles: Role[]) => {
      return hasRole(information?.roles, ...allowedRoles);
    },
    [information],
  );
  const value = useMemo<AuthContextType>(
    () => ({
      user,
      information,
      loading,
      signIn,
      signOut,
      hasRoles,
    }),
    [user, information, loading, hasRoles],
  );

  useEffect(() => {
    const unsubscribe = firebase.auth.onIdTokenChanged(async (user) => {
      setLoading(true);

      if (user) {
        setUser(user);

        try {
          const userInfo = await userApi.getUserProfile();
          setInformation(userInfo);
        } catch (error) {
          console.error("Error fetching user profile:", error);
          setInformation(null);
        }
      } else {
        setUser(null);
        setInformation(null);
      }

      setLoading(false);
    });

    return () => unsubscribe();
  }, []);

  async function signIn() {
    setLoading(true);
    const user = await safeCall(async () => {
      if (Platform.OS === "web") {
        const provider = new GoogleAuthProvider();
        return (await signInWithPopup(firebase.auth, provider)).user;
      } else {
        const rsp = await GoogleSignin.signIn();
        const credential = GoogleAuthProvider.credential(rsp.data?.idToken);
        return (await signInWithCredential(firebase.auth, credential)).user;
      }
    });

    if (!user) return;

    const information = await safeCall(async () => {
      const idToken = await user.getIdToken();
      return (await userApi.login(idToken)).user;
    });

    if (!information) {
      setLoading(false);
      return;
    }

    setUser(user);
    setInformation(information);
    setLoading(false);
  }

  async function signOut() {
    try {
      setLoading(true);
      await firebase.auth.signOut();

      if (Platform.OS === "android" || Platform.OS === "ios") {
        await GoogleSignin.signOut();
      }

      setUser(null);
      setInformation(null);
    } catch (error) {
      console.error("Error during sign-out:", error);
    }
    setLoading(false);
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
