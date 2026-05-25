import userApi from "@/api/user/user.api";
import { Role, UserInformation } from "@/api/user/user.output";
import { safeCall } from "@/handlers/Handlers";
import { hasRole } from "@/lib/accessPolicy";
import authService from "@/lib/authService";
import firebase from "@/lib/firebase";
import { User } from "firebase/auth";
import {
  createContext,
  ReactNode,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";
import { Toast } from "toastify-react-native";

type AuthState = {
  user: User | null;
  information: UserInformation | null;
  loading: boolean;
  authenticated: boolean;
  roles: Role[];
};

type AuthActions = {
  signIn: () => Promise<void>;
  signOut: () => Promise<void>;
  signUpWithEmail: (email: string, password: string) => Promise<void>;
  signInWithEmail: (email: string, password: string) => Promise<void>;
  refreshProfile: () => Promise<UserInformation | null>;
};

type AuthContextType = AuthState &
  AuthActions & {
    hasRoles: (...allowedRoles: Role[]) => boolean;
  };

const AuthStateContext = createContext<AuthState | undefined>(undefined);
const AuthActionsContext = createContext<AuthActions | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [information, setInformation] = useState<UserInformation | null>(null);
  const [loading, setLoading] = useState(true);

  const refreshProfile = useCallback(async () => {
    const userInfo = await safeCall(() => userApi.getUserProfile(), {
      showToast: false,
    });
    setInformation(userInfo);
    return userInfo;
  }, []);

  const signUpWithEmail = useCallback(async (email: string, password: string) => {
    setLoading(true);
    try {
      const user = await authService.signUpWithEmailPassword(email, password);
      const information = await authService.loginBackendUser(user);

      setUser(user);
      setInformation(information);
    } catch (e) {
      console.error("Error during sign up:", e);
      Toast.error("Failed to sign up.");
    } finally {
      setLoading(false);
    }
  }, []);

  const signInWithEmail = useCallback(async (email: string, password: string) => {
    setLoading(true);
    try {
      const user = await authService.signInWithEmailPassword(email, password);
      const information = await authService.loginBackendUser(user);

      setUser(user);
      setInformation(information);
    } catch (e) {
      console.error("Error during sign in:", e);
      Toast.error("Failed to sign in.");
    } finally {
      setLoading(false);
    }
  }, []);

  const signIn = useCallback(async () => {
    setLoading(true);
    try {
      const user = await authService.signInWithGoogle();
      const information = await authService.loginBackendUser(user);

      setUser(user);
      setInformation(information);
    } catch (e) {
      console.error("Error during Google sign in:", e);
      Toast.error("Failed to sign in.");
    } finally {
      setLoading(false);
    }
  }, []);

  const signOut = useCallback(async () => {
    try {
      setLoading(true);
      await safeCall(() => userApi.logout(), {
        showToast: false,
        silent: true,
      });
      await authService.signOutFromProviders();

      setUser(null);
      setInformation(null);
    } catch (e) {
      console.error("Error during sign-out:", e);
      Toast.error("Failed to sign out.");
    } finally {
      setLoading(false);
    }
  }, []);

  const roles = useMemo(() => information?.roles ?? [], [information]);
  const state = useMemo<AuthState>(
    () => ({
      user,
      information,
      loading,
      authenticated: user != null && information != null,
      roles,
    }),
    [user, information, loading, roles],
  );

  const actions = useMemo<AuthActions>(
    () => ({
      signIn,
      signOut,
      signUpWithEmail,
      signInWithEmail,
      refreshProfile,
    }),
    [refreshProfile, signIn, signInWithEmail, signOut, signUpWithEmail],
  );

  useEffect(() => {
    const unsubscribe = firebase.auth.onIdTokenChanged(async (user) => {
      setLoading(true);

      if (user) {
        setUser(user);

        await refreshProfile();
      } else {
        setUser(null);
        setInformation(null);
      }

      setLoading(false);
    });

    return () => unsubscribe();
  }, [refreshProfile]);

  return (
    <AuthStateContext.Provider value={state}>
      <AuthActionsContext.Provider value={actions}>
        {children}
      </AuthActionsContext.Provider>
    </AuthStateContext.Provider>
  );
}

export function useAuthState() {
  const context = useContext(AuthStateContext);
  if (context === undefined) {
    throw new Error("useAuthState must be used within an AuthProvider");
  }
  return context;
}

export function useAuthActions() {
  const context = useContext(AuthActionsContext);
  if (context === undefined) {
    throw new Error("useAuthActions must be used within an AuthProvider");
  }
  return context;
}

export function usePermissions() {
  const { roles } = useAuthState();

  return useMemo(
    () => ({
      hasRoles: (...allowedRoles: Role[]) => hasRole(roles, ...allowedRoles),
      canCreateAnimal: hasRole(roles, Role.VETERINARIAN, Role.ADMIN),
      canManageCheckups: hasRole(roles, Role.VETERINARIAN, Role.ADMIN),
      canManageClinics: hasRole(roles, Role.ADMIN),
      canManageGuides: hasRole(roles, Role.VETERINARIAN, Role.ADMIN),
      canManageInventory: hasRole(roles, Role.VETERINARIAN, Role.ADMIN),
      canReviewRequests: hasRole(roles, Role.ADMIN),
    }),
    [roles],
  );
}

export function useAuth() {
  const state = useAuthState();
  const actions = useAuthActions();
  const permissions = usePermissions();

  return useMemo<AuthContextType>(
    () => ({
      ...state,
      ...actions,
      hasRoles: permissions.hasRoles,
    }),
    [actions, permissions.hasRoles, state],
  );
}
