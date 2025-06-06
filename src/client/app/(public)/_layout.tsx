import { MobileNavigator } from "@/components/navigators/MobileNavigator";
import { WebNavigator } from "@/components/navigators/WebNavigator";
import { useAuth } from "@/hooks/useAuth";
import { tabs } from "@/lib/types";
import { useWindowDimensions, Platform } from "react-native";

export default function Layout() {
  const { user } = useAuth();
  const { width } = useWindowDimensions();
  const isDesktop = Platform.OS === "web" && width >= 768;

  return isDesktop ? (
    <WebNavigator routes={tabs} authenticated={user != null} />
  ) : (
    <MobileNavigator routes={tabs} authenticated={user != null} />
  );
}
