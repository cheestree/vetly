export type ColorScheme = {
  primary: string;
  secondary: string;
  primaryBackground: string;
  primaryBackgroundShadow: string;
  secondaryBackground: string;
  secondaryBackgroundShadow: string;
  thirdiaryBackground: string;
  thirdiaryBackgroundShadow: string;
  fontHeader: string;
  fontDescription: string;
  fontPrimary: string;
  fontSecondary: string;
  fontThirdiary: string;

  success: string;
  error: string;
  warning: string;

  buttonDisabled: string;

  border: string;

  inputBackground: string;
  inputBackgroundShadow: string;
  inputBorder: string;
  inputBorderFocus: string;

  cardBackground: string;
  cardBackgroundShadow: string;

  shadowColor: string;

  iconColour: string;

  activeDrawerItemBackground: string;
  activeDrawerItemLabel: string;
};

const lightColors: ColorScheme = {
  primary: "#2071f5",
  secondary: "#ffffff",
  primaryBackground: "#e6e7e8",
  primaryBackgroundShadow: "rgba(0, 0, 0, 0.6)",
  secondaryBackground: "#ffffff",
  secondaryBackgroundShadow: "rgba(0, 0, 0, 0.7)",
  thirdiaryBackground: "#cfcfcf",
  thirdiaryBackgroundShadow: "rgba(0, 0, 0, 0.5)",
  fontHeader: "#000000",
  fontDescription: "#8a8a8c",
  fontPrimary: "#ffffff",
  fontSecondary: "#8a8a8c",
  fontThirdiary: "#ffffff",

  success: "#28a745",
  error: "#dc3545",
  warning: "#ffc107",
  border: "#d0d1d2",
  cardBackground: "#ffffff",
  cardBackgroundShadow: "rgba(0, 0, 0, 0.6)",
  inputBackground: "#ffffff",
  inputBackgroundShadow: "rgba(0, 0, 0, 0.3)",
  inputBorder: "#c8c9ca",
  inputBorderFocus: "#2071f5",
  buttonDisabled: "#c8c9ca",
  shadowColor: "rgba(0, 0, 0, 0.1)",

  iconColour: "#000000",

  activeDrawerItemBackground: "#e0e7ff",
  activeDrawerItemLabel: "#2563eb",
};

const darkColors: ColorScheme = {
  primary: "#2071f5",
  secondary: "#1a1a1a",
  primaryBackground: "#2a2a2a",
  secondaryBackground: "#1a1a1a",
  thirdiaryBackground: "#3a3a3a",
  fontHeader: "#ffffff",
  fontDescription: "#b0b0b0",
  fontPrimary: "#ffffff",
  fontSecondary: "#b0b0b0",
  fontThirdiary: "#ffffff",

  success: "#28a745",
  error: "#dc3545",
  warning: "#ffc107",
  border: "#404040",
  cardBackground: "#2a2a2a",
  inputBackground: "#333333",
  inputBorder: "#505050",
  inputBorderFocus: "#2071f5",
  buttonDisabled: "#404040",
  shadowColor: "rgba(0, 0, 0, 0.3)",

  iconColour: "#ffffff",

  activeDrawerItemBackground: "#404040",
  activeDrawerItemLabel: "#b0b0b0",
};

export { darkColors, lightColors };
