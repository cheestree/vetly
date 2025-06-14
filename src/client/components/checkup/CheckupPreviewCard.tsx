import { useThemedStyles } from "@/hooks/useThemedStyles";
import ROUTES from "@/lib/routes";
import { splitDateTime } from "@/lib/utils";
import { FontAwesome5 } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { Image, Pressable, StyleSheet, Text, View } from "react-native";

interface CheckupPreviewCardProps {
  checkup: CheckupPreview;
}

export default function CheckupPreviewCard({
  checkup,
}: CheckupPreviewCardProps) {
  const { styles } = useThemedStyles();
  const router = useRouter();
  const { dateOnly, timeOnly } = splitDateTime(checkup.dateTime);

  return (
    <View style={extra.card}>
      <Image
        source={
          checkup.animal.imageUrl
            ? { uri: checkup.animal.imageUrl }
            : require("@/assets/placeholder.png")
        }
        style={styles.image}
        resizeMode="cover"
      />

      <View style={extra.textContainer}>
        <Text style={extra.animalName}>{checkup.animal.name}</Text>
        <Text style={styles.title}>{checkup.title}</Text>

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

        <Text style={styles.description}>
          {checkup.description
            ? `Description: ${checkup.description}`
            : "No description"}
        </Text>

        <Pressable
          onPress={() =>
            router.navigate({
              pathname: ROUTES.PRIVATE.ANIMAL.DETAILS,
              params: { id: checkup.animal.id },
            })
          }
          style={styles.button}
        >
          <Text style={styles.buttonText}>View Animal</Text>
        </Pressable>
        <Pressable
          onPress={() =>
            router.navigate({
              pathname: ROUTES.PRIVATE.CHECKUP.DETAILS,
              params: { id: checkup.id },
            })
          }
          style={styles.button}
        >
          <Text style={styles.buttonText}>View Details</Text>
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
  animalName: {
    fontSize: 18,
    fontWeight: "bold",
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
