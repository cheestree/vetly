import { FontAwesome5 } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { View, Text, StyleSheet, Pressable } from "react-native";
import { splitDateTime } from "@/lib/utils";
import ROUTES from "@/lib/routes";

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
        <Pressable onPress={() => router.navigate({pathname: ROUTES.PRIVATE.ANIMAL.DETAILS, params: { id: checkup.animal.id }})}>
          <img style={styles.image} src={checkup.animal.imageUrl} />
        </Pressable>
        <Text>{checkup.animal.name}</Text>
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
      <View style={styles.description}>
        <Text>Description: {checkup.description}</Text>
      </View>
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
    borderWidth: 1,
    borderColor: "#ccc"
  },
  image: {
    width: 20,
  },
});
