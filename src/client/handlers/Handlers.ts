import { Role } from "@/api/user/user.output";
import { Route } from "@/lib/types";
import { Toast } from "toastify-react-native";

type SafeCallOptions = {
  showToast?: boolean;
  silent?: boolean;
};

export async function safeCall<T>(
  fn: () => Promise<T>,
  options: SafeCallOptions = { showToast: true },
): Promise<T | null> {
  try {
    return await fn();
  } catch (error: unknown) {
    const message =
      error instanceof Error
        ? error.message
        : typeof error === "string"
          ? error
          : "An unexpected error occurred.";

    if (!options?.silent) {
      console.error("safeCall error:", error);
    }

    if (options?.showToast) {
      Toast.show({
        type: "error",
        text1: "Error",
        text2: message,
        position: "bottom",
      });
    }

    return null;
  }
}

export function filterRoutesByAccess(
  routes: Route[],
  authenticated: boolean,
  userRoles: Role[],
): Route[] {
  return routes.filter((route) => {
    if (route.route === "/login" && authenticated) return false;

    if (route.authenticated && !authenticated) return false;

    if (
      route.roles.length > 0 &&
      !route.roles.some((role) => userRoles.includes(role))
    )
      return false;

    return true;
  });
}

export default { safeCall, filterRoutesByAccess };
