import { toAppError } from "@/lib/apiError";
import { filterRoutesByAccess } from "@/lib/accessPolicy";
import { Toast } from "toastify-react-native";

export { filterRoutesByAccess };

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
    const appError = toAppError(error);

    if (!options?.silent) {
      console.error("safeCall error:", error);
    }

    if (options?.showToast) {
      Toast.show({
        type: "error",
        text1: "Error",
        text2: appError.message,
        position: "bottom",
      });
    }

    return null;
  }
}

export default { safeCall, filterRoutesByAccess };
