import { View, Text, StyleSheet, TouchableOpacity } from "react-native";
import { Link } from "expo-router";
import { RouterProps } from "@/lib/types";

export default function Header({ routes }: RouterProps) {
  return (
    <View style={styles.header} accessibilityLabel="Main navigation">
      <View style={styles.navbar}>
        {routes.map((item) => (
          <Link key={item.route} href={{ pathname: item.route }} asChild>
            <TouchableOpacity style={styles.link}>
              <Text style={styles.text}>{item.label}</Text>
            </TouchableOpacity>
          </Link>
        ))}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  header: {
    backgroundColor: "#f8f8f8",
    paddingVertical: 16,
    paddingHorizontal: 24,
    borderBottomWidth: 1,
    borderBottomColor: "#ccc",
  },
  navbar: {
    flexDirection: "row",
    justifyContent: "center",
    gap: 16,
  },
  link: {
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 8,
  },
  linkActive: {
    backgroundColor: "#ddd",
  },
  text: {
    fontSize: 16,
    fontWeight: "500",
    color: "#333",
  },
});
