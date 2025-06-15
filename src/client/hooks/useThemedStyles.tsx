import { darkColors, lightColors } from "@/theme/colours";
import layout from "@/theme/layout";
import { useMemo } from "react";
import { useColorScheme } from "react-native";

export const useThemedStyles = () => {
  const colorScheme = useColorScheme();

  return useMemo(() => {
    const colours = colorScheme === "dark" ? darkColors : lightColors;
    const styles = layout({ colours });

    return { colours, styles };
  }, [colorScheme]);
};
