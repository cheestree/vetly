import { FontAwesome5 } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { View, Text, StyleSheet } from "react-native";
import { splitDateTime } from "@/lib/utils";

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
      <View style={styles.imageContainer}>
        <img style={styles.image} src={checkup.animal.imageUrl} />
        <View>
          <Text>{checkup.animal.name}</Text>
        </View>
      </View>
      <View style={styles.time}>
        <View>
          <FontAwesome5 name="calendar" size={24} />
          <Text>{dateOnly.toLocaleDateString()}</Text>
        </View>
        <View>
          <FontAwesome5 name="clock" size={24} />
          <Text>
            {timeOnly.hours}:{timeOnly.minutes}
          </Text>
        </View>
      </View>
      <View style={styles.description}>Description: {checkup.description}</View>
      <View></View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    padding: 16,
    backgroundColor: "#fff",
    borderRadius: 10,
    marginBottom: 12,
    shadowColor: "#000",
    shadowOpacity: 0.1,
    shadowRadius: 6,
    elevation: 2,
  },
  image: {
    width: 20,
  },
});
