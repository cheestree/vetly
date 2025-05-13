import { useAuth } from "@/hooks/AuthContext";
import { FontAwesome } from "@expo/vector-icons";
import { DrawerContentScrollView, DrawerItem } from "@react-navigation/drawer";
import React, { useEffect, useState } from "react";
import { View, Text, Platform } from "react-native";
import AnimalsSection from "./sections/AnimalsSection";
import CheckupsSection from "./sections/CheckupsSection";

export default function CustomDrawerContent(props: any) {
  const { loading, information } = useAuth();
  const roles = information?.roles || [];

  const hasRole = (...allowedRoles: string[]) => {
    return allowedRoles.some((role) => roles.includes(role));
  };

  useEffect(() => {
    if (Platform.OS === "web") {
      const route = props.state.routes[props.state.index];
      const title = props.descriptors[route.key]?.options?.title || route.name;
      document.title = title;
    }
  }, [props.state.index]);

  if (loading) {
    return <></>;
  }

  return (
    <DrawerContentScrollView {...props}>
      <View style={{ padding: 16 }}>
        <Text style={{ fontSize: 18 }}>👋 Welcome, {information?.name}</Text>
      </View>

      {/* Dashboard (Always Visible) */}
      <DrawerItem
        label="Dashboard"
        onPress={() => props.navigation.navigate("dashboard")}
        icon={() => <FontAwesome name="home" size={20} />}
      />

      {/* Profile (Always Visible) */}
      <DrawerItem
        label="Profile"
        onPress={() => props.navigation.navigate("profile")}
        icon={() => <FontAwesome name="user" size={20} />}
      />

      {/* Settings (Always Visible) */}
      <DrawerItem
        label="Settings"
        onPress={() => props.navigation.navigate("settings")}
        icon={() => <FontAwesome name="gear" size={20} />}
      />

      {/* Checkups Section - Only for Veterinarians */}
      {hasRole("VETERINARIAN") && (
        <CheckupsSection navigation={props.navigation} />
      )}

      {/* Animals Section - Visible for Admin and Veterinarian */}
      {hasRole("ADMIN", "VETERINARIAN") && (
        <AnimalsSection navigation={props.navigation} />
      )}
    </DrawerContentScrollView>
  );
}
