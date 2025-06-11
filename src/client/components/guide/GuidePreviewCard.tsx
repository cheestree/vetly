import ROUTES from "@/lib/routes";
import { splitDateTime } from "@/lib/utils";
import colours from "@/theme/colours";
import { FontAwesome5 } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { View, StyleSheet, Text, Image } from "react-native";
import { Button } from "react-native-paper";

interface GuidePreviewCardProps {
  guide: GuidePreview;
}

export default function GuidePreviewCard({ guide }: GuidePreviewCardProps) {
  const router = useRouter();
  const { dateOnly, timeOnly } = splitDateTime(guide.createdAt);

  return (
    <View style={styles.card}>
      <Image
        source={
          guide.imageUrl
            ? { uri: guide.imageUrl }
            : require("@/assets/placeholder.png")
        }
        style={styles.image}
        resizeMode="cover"
      />

      <View style={styles.textContainer}>
        <Text style={styles.title}>{guide.title}</Text>

        <View style={styles.scheduleContainer}>
          <View style={styles.dateTime}>
            <FontAwesome5 name="calendar" size={16} />
            <Text style={styles.dateText}>{dateOnly.toLocaleDateString()}</Text>
          </View>
          <View style={styles.dateTime}>
            <FontAwesome5 name="clock" size={16} />
            <Text style={styles.dateText}>
              {timeOnly.hours}:{timeOnly.minutes}
            </Text>
          </View>
        </View>

        <Text style={styles.description}>
          {guide.description
            ? `Description: ${guide.description}`
            : "No description"}
        </Text>

        <Button
          onPress={() =>
            router.navigate({
              pathname: ROUTES.PRIVATE.GUIDE.DETAILS,
              params: { id: guide.id },
            })
          }
          style={styles.detailsButton}
        >
          <Text style={styles.detailsButtonText}>View Details</Text>
        </Button>
        <Button
          onPress={() =>
            router.navigate({
              pathname: ROUTES.PRIVATE.USER.DETAILS,
              params: { id: guide.author.id },
            })
          }
          style={styles.detailsButton}
        >
          <Text style={styles.detailsButtonText}>View Author</Text>
        </Button>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: "#fff",
    padding: 16,
    marginBottom: 12,
    borderRadius: 10,
    shadowColor: "#000",
    shadowOpacity: 0.08,
    shadowRadius: 6,
    shadowOffset: { width: 0, height: 2 },
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
  detailsButton: {
    marginTop: 8,
    backgroundColor: colours.primary,
    borderRadius: 6,
  },
  detailsButtonText: {
    color: colours.fontThirdiary,
  },
});
