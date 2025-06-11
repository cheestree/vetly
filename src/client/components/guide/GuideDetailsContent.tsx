import ROUTES from "@/lib/routes";
import { useRouter } from "expo-router";
import { View, StyleSheet, Image, ScrollView, Pressable } from "react-native";
import { Text } from "react-native-paper";

export default function GuideDetailsContent({
  guide,
}: {
  guide?: GuideInformation;
}) {
  const router = useRouter();

  if (!guide) {
    return (
      <View style={styles.cardContainer}>
        <Text style={styles.placeholder}>No guide information available.</Text>
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.cardContainer}>
      <Text style={styles.title}>{guide.title}</Text>

      {guide.imageUrl && (
        <Image source={{ uri: guide.imageUrl }} style={styles.image} />
      )}

      <Text style={styles.description}>{guide.description}</Text>

      <Text style={styles.content}>{guide.content}</Text>

      <View style={styles.metaContainer}>
        <Text style={styles.meta}>
          By{" "}
          <Pressable
            onPress={() => {
              console.log(guide.author.id);
              router.navigate({
                pathname: ROUTES.PRIVATE.USER.DETAILS,
                params: { id: guide.author.id },
              });
            }}
          >
            {guide.author.name}
          </Pressable>{" "}
          • {new Date(guide.createdAt).toLocaleDateString()}
        </Text>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  cardContainer: {
    padding: 16,
    backgroundColor: "#fff",
  },
  placeholder: {
    fontSize: 16,
    textAlign: "center",
    color: "#999",
  },
  title: {
    fontSize: 24,
    fontWeight: "bold",
    marginBottom: 12,
  },
  image: {
    width: "100%",
    height: 200,
    resizeMode: "cover",
    borderRadius: 8,
    marginBottom: 12,
  },
  description: {
    fontSize: 16,
    marginBottom: 8,
    color: "#555",
  },
  content: {
    fontSize: 16,
    color: "#333",
    marginBottom: 16,
  },
  metaContainer: {
    borderTopWidth: 1,
    borderTopColor: "#eee",
    paddingTop: 8,
  },
  meta: {
    fontSize: 14,
    color: "#777",
  },
});
