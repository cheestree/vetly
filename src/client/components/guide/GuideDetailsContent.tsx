import { GuideInformation } from "@/api/guide/guide.output";
import ROUTES from "@/lib/routes";
import { useRouter } from "expo-router";
import { Image, Pressable, ScrollView, StyleSheet, View } from "react-native";
import { Text } from "react-native-paper";

export default function GuideDetailsContent({
  guide,
}: {
  guide?: GuideInformation;
}) {
  const router = useRouter();

  if (!guide) {
    return (
      <View style={extra.cardContainer}>
        <Text style={extra.placeholder}>No guide information available.</Text>
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={extra.cardContainer}>
      <Text style={extra.title}>{guide.title}</Text>

      {guide.imageUrl && (
        <Image source={{ uri: guide.imageUrl }} style={extra.image} />
      )}

      <Text style={extra.description}>{guide.description}</Text>

      <Text style={extra.content}>{guide.content}</Text>

      <View style={extra.metaContainer}>
        <Text style={extra.meta}>
          By{" "}
          <Pressable
            onPress={() => {
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

const extra = StyleSheet.create({
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
