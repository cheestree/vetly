import { darkColors, lightColors } from "@/theme/colours";
import { useColorScheme } from "react-native";

export const useColors = () => {
  const colorScheme = useColorScheme();
  return colorScheme === "dark" ? darkColors : lightColors;
};
