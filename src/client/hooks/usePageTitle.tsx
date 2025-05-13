import { useRoute } from "@react-navigation/native";
import { useEffect, useState } from "react";
import { Platform } from "react-native";

export const usePageTitle = (
  title: string,
  format: boolean = false,
  loadingTitle: string = "Loading...",
) => {
  const route = useRoute();
  const [currentTitle, setCurrentTitle] = useState<string>(loadingTitle);

  useEffect(() => {
    let formattedTitle = title;
    if (format) {
      formattedTitle = title.charAt(0).toUpperCase() + title.slice(1);
    }

    setCurrentTitle(formattedTitle);

    if (Platform.OS === "web") {
      document.title = formattedTitle;
    }
  }, [route.name, title, format]);

  return currentTitle;
};
