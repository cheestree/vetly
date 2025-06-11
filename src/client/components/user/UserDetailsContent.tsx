import { View, Text, Image, StyleSheet } from "react-native";

export default function UserDetailsContent({
  user,
}: {
  user?: UserInformation;
}) {
  if (!user) {
    return (
      <View style={styles.container}>
        <Text style={styles.placeholder}>No user data found.</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {user.imageUrl && (
        <Image source={{ uri: user.imageUrl }} style={styles.avatar} />
      )}

      <Text style={styles.name}>{user.name}</Text>
      <Text style={styles.email}>{user.email}</Text>
      <Text style={styles.meta}>Role: {user.roles}</Text>
      <Text style={styles.meta}>
        Joined: {new Date(user.joinedAt).toLocaleDateString()}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 16,
    alignItems: "center",
    backgroundColor: "#fff",
  },
  placeholder: {
    fontSize: 16,
    color: "#999",
    textAlign: "center",
  },
  avatar: {
    width: 100,
    height: 100,
    borderRadius: 50,
    marginBottom: 12,
  },
  name: {
    fontSize: 22,
    fontWeight: "bold",
    marginBottom: 4,
  },
  email: {
    fontSize: 16,
    color: "#666",
    marginBottom: 8,
  },
  meta: {
    fontSize: 14,
    color: "#444",
    marginBottom: 2,
  },
});
