import { useAuth } from "@/hooks/useAuth";
import { FontAwesome, FontAwesome5 } from "@expo/vector-icons";
import { DrawerContentScrollView, DrawerItem } from "@react-navigation/drawer";
import React from "react";
import { View, Text } from "react-native";
import { useRouter } from "expo-router";
import AnimalSection from "./sections/AnimalSection";
import CheckupSection from "./sections/CheckupSection";
import ClinicSection from "./sections/ClinicSection";
import ROUTES from "@/lib/routes";

export default function CustomDrawerContent(props: any) {
  const { loading, information } = useAuth();
  const router = useRouter();
  const roles = information?.roles || [];

  return (
    <DrawerContentScrollView {...props}>
      <View style={{ padding: 16 }}>
        <Text style={{ fontSize: 18 }}>👋 Welcome, {information?.name}</Text>
      </View>

      {/* Dashboard (Always Visible) */}
      <DrawerItem
        label="Dashboard"
        onPress={() => router.push(ROUTES.PRIVATE.ME.DASHBOARD)}
        icon={() => <FontAwesome5 name="home" size={20} />}
      />

      {/* Profile (Always Visible) */}
      <DrawerItem
        label="Profile"
        onPress={() => router.navigate(ROUTES.PRIVATE.ME.PROFILE)}
        icon={() => <FontAwesome5 name="user-circle" size={20} />}
      />

      {/* Settings (Always Visible) */}
      <DrawerItem
        label="Settings"
        onPress={() => router.navigate(ROUTES.PRIVATE.ME.SETTINGS)}
        icon={() => <FontAwesome5 name="wrench" size={20} />}
      />

      {/* My Pets (Always Visible) */}
      <DrawerItem
        label="My Pets"
        onPress={() => router.navigate(ROUTES.PRIVATE.ME.PETS)}
        icon={() => <FontAwesome5 name="bone" size={20} />}
      />

      <CheckupSection roles={roles} />
      <AnimalSection roles={roles} />
      <ClinicSection roles={roles} />
    </DrawerContentScrollView>
  );
}
