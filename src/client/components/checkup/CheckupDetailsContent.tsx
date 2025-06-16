import { useThemedStyles } from "@/hooks/useThemedStyles";
import ROUTES from "@/lib/routes";
import { splitDateTime } from "@/lib/utils";
import { FontAwesome5 } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import React from "react";
import {
  Image,
  Linking,
  Pressable,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from "react-native";

export default function CheckupDetailsContent({
  checkup,
}: {
  checkup?: CheckupInformation;
}) {
  const { styles } = useThemedStyles();
  const router = useRouter();
  if (!checkup) {
    return (
      <View style={styles.container}>
        <Text style={extras.placeholder}>No checkup data found.</Text>
      </View>
    );
  }
  const { dateOnly, timeOnly } = splitDateTime(checkup.dateTime);

  return (
    <View style={styles.container}>
      <Text style={extras.heading}>{checkup.title}</Text>
      <Text style={extras.field}>{checkup.description}</Text>
      <Text style={extras.field}>Scheduled at: {checkup.clinic.name}</Text>
      <Text style={extras.field}>
        Scheduled for {dateOnly.toLocaleDateString()} at {timeOnly.hours}:
        {timeOnly.minutes}
      </Text>
      <Text style={extras.field}>Status: {checkup.status}</Text>

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
            style={extras.image}
            resizeMode="cover"
          />
        </Pressable>
      )}
      {checkup.files?.length > 0 && (
        <View style={extras.attachmentContainer}>
          <Text style={extras.sectionHeading}>Attachments</Text>
          {checkup.files.map((file) => (
            <TouchableOpacity
              key={file.uuid}
              onPress={() => Linking.openURL(file.url)}
              style={extras.attachment}
            >
              <FontAwesome5 size={20} name="file" style={{ marginRight: 8 }} />
              <View>
                <Text style={extras.fileDescription}>{file.description}</Text>
                <Text style={extras.fileDate}>
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

const extras = StyleSheet.create({
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
