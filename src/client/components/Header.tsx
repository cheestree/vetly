import { View, Text, StyleSheet, Platform } from "react-native";
import { Link } from "expo-router";

export default function Header() {
  const navItems = [
    { label: "Vetly", route: "/" as const },
    { label: "About", route: "/about" as const },
    { label: "Contacts", route: "/contact" as const },
    { label: "Login", route: "/login" as const },
  ];

  return (
    <View style={styles.header}>
      <View style={styles.navbar}>
        {navItems.map((item) => (
          <Link key={item.route} href={item.route} style={styles.link}>
            <Text style={styles.text}>{item.label}</Text>
          </Link>
        ))}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  header: {
    backgroundColor: "#f8f8f8",
    padding: Platform.OS === "ios" ? 32 : 16,
    borderRadius: 16,
    borderBottomColor: "#ccc",
    borderBottomWidth: 1,
  },
  navbar: {
    flexDirection: "row",
    justifyContent: "space-around",
    alignItems: "center",
    marginStart: 16,
  },
  link: {
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 16,
  },
  linkHover: {
    backgroundColor: "#e0e0e0",
  },
  text: {
    fontSize: 16,
    fontWeight: "500",
  },
});
