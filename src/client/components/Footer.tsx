import { Link } from "expo-router";
import React from "react";
import { Text, View, StyleSheet } from "react-native";

export default function Header(params) {
  return <View style={styles.footer}></View>;
}

const styles = StyleSheet.create({
  footer: {
    backgroundColor: "#f8f8f8",
    padding: 16,
    borderTopColor: "#ccc",
    borderTopWidth: 1,
  },
});
