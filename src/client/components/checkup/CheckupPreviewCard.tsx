import { FontAwesome5 } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { View, Text, StyleSheet, Pressable, Image } from "react-native";
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
  const router = useRouter()
  const { dateOnly, timeOnly } = splitDateTime(checkup.dateTime)

  return (
    <View style={styles.cardContainer}>
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

      <Text style={styles.description}>Description: {checkup.description}</Text>

      <Button 
        onPress={() => router.navigate({ 
          pathname: ROUTES.PRIVATE.CHECKUP.DETAILS, 
          params: { id: checkup.id } 
          })}
          style={styles.detailsButton}
      >
        <Text style={styles.detailsButtonText}>Details</Text>
      </Button>
    </View>
  )
}

const styles = StyleSheet.create({
  cardContainer: {
    padding: 16,
    backgroundColor: '#fff',
    borderRadius: 10,
    borderWidth: 1,
    borderColor: '#ccc',
    width: 256,
    alignSelf: 'center',
  },
  image: {
    width: '100%',
    height: 150,
    borderRadius: 8,
    marginBottom: 8,
  },
  animalName: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  title: {
    fontSize: 14,
    fontStyle: 'italic',
    marginBottom: 8,
  },
  scheduleContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 8,
  },
  dateTime: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  dateText: {
    marginLeft: 4,
    fontSize: 13,
  },
  description: {
    fontSize: 13,
    color: '#333',
  },
  detailsButton: {
    marginTop: 8,
    backgroundColor: colours.primary,
    borderRadius: 6
  },
  detailsButtonText: {
    color: colours.fontThirdiary,
  }
})