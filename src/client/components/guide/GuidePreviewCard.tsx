import { useThemedStyles } from "@/hooks/useThemedStyles";
import ROUTES from "@/lib/routes";
import { splitDateTime } from "@/lib/utils";
import { FontAwesome5 } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { Image, Pressable, StyleSheet, Text, View } from "react-native";

interface GuidePreviewCardProps {
  guide: GuidePreview;
}

export default function GuidePreviewCard({ guide }: GuidePreviewCardProps) {
  const router = useRouter();
  const { dateOnly, timeOnly } = splitDateTime(guide.createdAt);
  const style = useThemedStyles();

  return (
    <View style={extra.card}>
      <Image
        source={
          guide.imageUrl
            ? { uri: guide.imageUrl }
            : require("@/assets/placeholder.png")
        }
        style={extra.image}
        resizeMode="cover"
      />

      <View style={extra.textContainer}>
        <Text style={extra.title}>{guide.title}</Text>

        <View style={extra.scheduleContainer}>
          <View style={extra.dateTime}>
            <FontAwesome5 name="calendar" size={16} />
            <Text style={extra.dateText}>{dateOnly.toLocaleDateString()}</Text>
          </View>
          <View style={extra.dateTime}>
            <FontAwesome5 name="clock" size={16} />
            <Text style={extra.dateText}>
              {timeOnly.hours}:{timeOnly.minutes}
            </Text>
          </View>
        </View>

        <Text style={extra.description}>
          {guide.description
            ? `Description: ${guide.description}`
            : "No description"}
        </Text>

        <Pressable
          onPress={() =>
            router.navigate({
              pathname: ROUTES.PRIVATE.GUIDE.DETAILS,
              params: { id: guide.id },
            })
          }
          style={style.button}
        >
          <Text style={style.buttonText}>View Details</Text>
        </Pressable>
        <Pressable
          onPress={() =>
            router.navigate({
              pathname: ROUTES.PRIVATE.USER.DETAILS,
              params: { id: guide.author.id },
            })
          }
          style={style.button}
        >
          <Text style={style.buttonText}>View Author</Text>
        </Pressable>
      </View>
    </View>
  );
}

const extra = StyleSheet.create({
  card: {
    backgroundColor: "#fff",
    padding: 16,
    marginBottom: 12,
    borderRadius: 10,
    boxShadow: "0px 2px 6px rgba(0, 0, 0, 0.08)",
    elevation: 3,
  },
  image: {
    width: "100%",
    height: 200,
    borderRadius: 8,
    backgroundColor: "#e0e0e0",
  },
  textContainer: {
    marginTop: 12,
  },
  title: {
    fontSize: 14,
    fontStyle: "italic",
    marginBottom: 8,
  },
  scheduleContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 8,
  },
  dateTime: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
  },
  dateText: {
    marginLeft: 4,
    fontSize: 13,
  },
  description: {
    fontSize: 13,
    color: "#333",
    marginBottom: 8,
  },
});
