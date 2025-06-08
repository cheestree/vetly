import { useNavigation } from "expo-router";
import { useEffect } from "react";
import { Platform } from "react-native";

export function useDocumentTitle(title: string) {
  const navigation = useNavigation();

  useEffect(() => {
    if (Platform.OS === "web") {
      const previousTitle = document.title;
      document.title = title;

      return () => {
        document.title = previousTitle;
      };
    }

    navigation.setOptions({ title: title });
  }, [title]);
}
