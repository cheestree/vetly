import axios from "axios";

type ApiErrorKind =
  | "unauthorized"
  | "forbidden"
  | "validation"
  | "network"
  | "unknown";

export type AppError = Error & {
  kind: ApiErrorKind;
  status?: number;
};

function getKind(status?: number): ApiErrorKind {
  if (status === 401) return "unauthorized";
  if (status === 403) return "forbidden";
  if (status === 400 || status === 422) return "validation";
  return status ? "unknown" : "network";
}

function getResponseMessage(data: unknown) {
  if (!data || typeof data !== "object") return undefined;

  const responseData = data as { message?: unknown; error?: unknown };
  if (typeof responseData.message === "string") return responseData.message;
  if (typeof responseData.error === "string") return responseData.error;

  return undefined;
}

function toAppError(error: unknown): AppError {
  if (axios.isAxiosError(error)) {
    const status = error.response?.status;
    const message =
      getResponseMessage(error.response?.data) ||
      error.message ||
      "Request failed.";
    const appError = new Error(message) as AppError;

    appError.status = status;
    appError.kind = getKind(status);
    return appError;
  }

  if (error instanceof Error) {
    const appError = error as AppError;
    appError.kind = appError.kind ?? "unknown";
    return appError;
  }

  const appError = new Error(
    typeof error === "string" ? error : "An unexpected error occurred.",
  ) as AppError;
  appError.kind = "unknown";
  return appError;
}

export { toAppError };
