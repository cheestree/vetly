import { useThemedStyles } from "@/hooks/useThemedStyles";
import { RouterProps } from "@/lib/types";
import { FontAwesome5 } from "@expo/vector-icons";
import {
  DrawerContentComponentProps,
  DrawerContentScrollView,
} from "@react-navigation/drawer";
import { Href, usePathname, useRouter } from "expo-router";
import React from "react";
import {
  Platform,
  Text,
  TouchableOpacity,
  useWindowDimensions,
  View,
} from "react-native";
import SizedIcon from "../basic/SizedIcon";
import CustomDrawerItem from "./item/CustomDrawerItem";

type DrawerBehaviourProps = {
  isCollapsed: boolean;
  toggleCollapse: () => void;
};

type CustomDrawerProps = DrawerContentComponentProps &
  RouterProps &
  DrawerBehaviourProps;

export default function CustomDrawerContent({
  routes,
  isCollapsed,
  toggleCollapse,
  ...props
}: CustomDrawerProps) {
  const { colours, styles } = useThemedStyles();
  const pathname = usePathname();
  const router = useRouter();
  const { width } = useWindowDimensions();
  const isDesktop = Platform.OS === "web" && width >= 768;

  const shouldShowLabels = !isCollapsed || !isDesktop;

  return (
    <DrawerContentScrollView {...props} style={{ scrollbarWidth: "none" }}>
      <View
        style={[
          styles.drawerTop,
          { justifyContent: shouldShowLabels ? "space-between" : "center" },
        ]}
      >
        {shouldShowLabels && <Text style={styles.meta}>Vetly</Text>}
        {isDesktop && (
          <TouchableOpacity onPress={toggleCollapse}>
            <FontAwesome5 name={"list"} size={24} color={colours.iconColour} />
          </TouchableOpacity>
        )}
      </View>
      {routes.map((element) => (
        <CustomDrawerItem
          key={element.label}
          label={element.label}
          onPress={() => router.push(element.route as Href)}
          icon={<SizedIcon icon={element.icon} colour={colours.iconColour} />}
          style={[
            styles.drawerItem,
            pathname === element.route && {
              backgroundColor: colours.activeDrawerItemBackground,
            },
          ]}
          labelStyle={
            pathname === element.route && {
              color: colours.activeDrawerItemLabel,
              fontWeight: "600",
            }
          }
          showLabel={shouldShowLabels}
        />
      ))}
    </DrawerContentScrollView>
  );
}
