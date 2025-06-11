import { FontAwesome5 } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { View, Text, StyleSheet, Image } from "react-native";
import { splitDateTime } from "@/lib/utils";
import ROUTES from "@/lib/routes";
import { Button } from "react-native-paper";
import colours from "@/theme/colours";

interface CheckupPreviewCardProps {
  checkup: CheckupPreview;
}

export default function CheckupPreviewCard({
  checkup,
}: CheckupPreviewCardProps) {
  const router = useRouter();
  const { dateOnly, timeOnly } = splitDateTime(checkup.dateTime);

  return (
    <View style={styles.card}>
      <Image
        source={
          checkup.animal.imageUrl
            ? { uri: checkup.animal.imageUrl }
            : require("@/assets/placeholder.png")
        }
        style={styles.image}
        resizeMode="cover"
      />

      <View style={styles.textContainer}>
        <Text style={styles.animalName}>{checkup.animal.name}</Text>
        <Text style={styles.title}>{checkup.title}</Text>

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
          {checkup.description
            ? `Description: ${checkup.description}`
            : "No description"}
        </Text>

        <Button
          onPress={() =>
            router.navigate({
              pathname: ROUTES.PRIVATE.ANIMAL.DETAILS,
              params: { id: checkup.animal.id },
            })
          }
          style={styles.detailsButton}
        >
          <Text style={styles.detailsButtonText}>View Animal</Text>
        </Button>
        <Button
          onPress={() =>
            router.navigate({
              pathname: ROUTES.PRIVATE.CHECKUP.DETAILS,
              params: { id: checkup.id },
            })
          }
          style={styles.detailsButton}
        >
          <Text style={styles.detailsButtonText}>View Details</Text>
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
  detailsButton: {
    marginTop: 8,
    backgroundColor: colours.primary,
    borderRadius: 6,
  },
  detailsButtonText: {
    color: colours.fontThirdiary,
  },
});
