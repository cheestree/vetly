export type ColorScheme = {
  primary: string;
  secondary: string;

  primaryBackground: string;
  secondaryBackground: string;
  thirdiaryBackground: string;

  secondaryBackgroundShadow: string;
  primaryBackgroundShadow: string;
  thirdiaryBackgroundShadow: string;

  fontHeader: string;
  fontPrimary: string;
  fontSecondary: string;
  fontThirdiary: string;

  success: string;
  error: string;
  warning: string;
  border: string;

  cardBackground: string;
  cardBackgroundShadow: string;

  inputBackground: string;
  inputBackgroundShadow: string;
  inputBorder: string;
  inputBorderFocus: string;
  buttonDisabled: string;
  shadowColor: string;
  iconColour: string;
  inactiveDrawerItemBackground: string;
  activeDrawerItemBackground: string;
  activeDrawerItemLabel: string;
};

const lightColors: ColorScheme = {
  primary: "#6366f1",
  secondary: "#f8fafc",

  primaryBackground: "#f8fafc",
  secondaryBackground: "#f1f5f9",
  thirdiaryBackground: "#e2e8f0",

  primaryBackgroundShadow: "rgba(71, 85, 105, 0.1)",
  secondaryBackgroundShadow: "rgba(71, 85, 105, 0.08)",
  thirdiaryBackgroundShadow: "rgba(71, 85, 105, 0.05)",

  fontHeader: "#0f172a",
  fontPrimary: "#1e293b",
  fontSecondary: "#64748b",
  fontThirdiary: "#ffffff",

  success: "#10b981",
  error: "#ef4444",
  warning: "#f59e0b",
  border: "#cbd5e1",

  cardBackground: "#ffffff",
  cardBackgroundShadow: "rgba(71, 85, 105, 0.12)",

  inputBackground: "#ffffff",
  inputBackgroundShadow: "rgba(99, 102, 241, 0.05)",
  inputBorder: "#d1d5db",
  inputBorderFocus: "#6366f1",
  buttonDisabled: "#9ca3af",
  shadowColor: "rgba(71, 85, 105, 0.1)",
  iconColour: "#475569",
  inactiveDrawerItemBackground: "#f8fafc",
  activeDrawerItemBackground: "#eef2ff",
  activeDrawerItemLabel: "#4338ca",
};

const darkColors: ColorScheme = {
  primary: "#6366f1",
  secondary: "#1e293b",

  primaryBackground: "#0f172a",
  secondaryBackground: "#1e293b",
  thirdiaryBackground: "#334155",

  primaryBackgroundShadow: "rgba(0, 0, 0, 0.4)",
  secondaryBackgroundShadow: "rgba(0, 0, 0, 0.3)",
  thirdiaryBackgroundShadow: "rgba(0, 0, 0, 0.2)",

  fontHeader: "#f8fafc",
  fontPrimary: "#e2e8f0",
  fontSecondary: "#94a3b8",
  fontThirdiary: "#f8fafc",

  success: "#10b981",
  error: "#ef4444",
  warning: "#f59e0b",

  border: "#475569",

  cardBackground: "#1e293b",
  cardBackgroundShadow: "rgba(0, 0, 0, 0.6)",

  inputBackground: "#334155",
  inputBackgroundShadow: "rgba(0, 0, 0, 0.3)",
  inputBorder: "#64748b",
  inputBorderFocus: "#6366f1",

  buttonDisabled: "#64748b",
  shadowColor: "rgba(0, 0, 0, 0.4)",
  iconColour: "#cbd5e1",

  inactiveDrawerItemBackground: "transparent",
  activeDrawerItemBackground: "#3730a3",
  activeDrawerItemLabel: "#a5b4fc",
};

export { darkColors, lightColors };
