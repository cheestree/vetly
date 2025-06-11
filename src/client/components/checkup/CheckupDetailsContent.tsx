import ROUTES from "@/lib/routes";
import { splitDateTime } from "@/lib/utils";
import { FontAwesome5 } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import React from "react";
import {
  Pressable,
  View,
  Image,
  Text,
  StyleSheet,
  TouchableOpacity,
  Linking,
} from "react-native";

export default function CheckupDetailsContent({
  checkup,
}: {
  checkup?: CheckupInformation;
}) {
  if (!checkup) {
    return (
      <View style={styles.container}>
        <Text style={styles.placeholder}>No checkup data found.</Text>
      </View>
    );
  }

  const router = useRouter();
  const { dateOnly, timeOnly } = splitDateTime(checkup.dateTime);

  return (
    <View style={styles.container}>
      <Text style={styles.heading}>{checkup.title}</Text>
      <Text style={styles.field}>{checkup.description}</Text>
      <Text style={styles.field}>Scheduled at: {checkup.clinic.name}</Text>
      <Text style={styles.field}>
        Scheduled for {dateOnly.toLocaleDateString()} at {timeOnly.hours}:
        {timeOnly.minutes}
      </Text>
      <Text style={styles.field}>Status: {checkup.status}</Text>

      {checkup.animal.imageUrl && (
        <Pressable
          onPress={() =>
            router.navigate({
              pathname: ROUTES.PRIVATE.ANIMAL.DETAILS,
              params: { id: checkup.animal.id },
            })
          }
        >
          <Image
            source={{ uri: checkup.animal.imageUrl }}
            style={styles.image}
            resizeMode="cover"
          />
        </Pressable>
      )}
      {checkup.files?.length > 0 && (
        <View style={styles.attachmentContainer}>
          <Text style={styles.sectionHeading}>Attachments</Text>
          {checkup.files.map((file) => (
            <TouchableOpacity
              key={file.uuid}
              onPress={() => Linking.openURL(file.url)}
              style={styles.attachment}
            >
              <FontAwesome5 size={20} name="file" style={{ marginRight: 8 }} />
              <View>
                <Text style={styles.fileDescription}>{file.description}</Text>
                <Text style={styles.fileDate}>
                  Uploaded: {new Date(file.createdAt).toLocaleDateString()}
                </Text>
              </View>
            </TouchableOpacity>
          ))}
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
  },
  placeholder: {
    fontSize: 16,
    color: "#999",
    textAlign: "center",
  },
  heading: {
    fontSize: 24,
    marginBottom: 16,
    fontWeight: "bold",
  },
  id: {
    fontSize: 18,
    marginBottom: 8,
  },
  field: {
    fontSize: 16,
    marginBottom: 8,
  },
  image: {
    width: 200,
    height: 200,
    borderRadius: 8,
    marginTop: 16,
  },
  attachmentContainer: {
    marginTop: 24,
  },

  sectionHeading: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 8,
  },

  attachment: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 12,
  },

  fileDescription: {
    fontSize: 16,
    color: "#007AFF",
    textDecorationLine: "underline",
  },

  fileDate: {
    fontSize: 12,
    color: "#666",
  },
});
