import { Link } from "expo-router";
import { View, Text, Image } from "react-native";

export default function CheckupDetailsContent({
  checkup,
}: {
  checkup?: CheckupInformation;
}) {
  if (!checkup) return <Text>No checkup data found</Text>;

  return (
    <View style={{ flex: 1, padding: 16 }}>
      <Text style={{ fontSize: 24, marginBottom: 16 }}>Checkup Details</Text>
      <Text style={{ fontSize: 18, marginBottom: 8 }}>#{checkup.id}</Text>
      <Text style={{ marginBottom: 4 }}>Clinic: {checkup.clinic.name}</Text>
      <Text style={{ marginBottom: 12 }}>
        Description: {checkup.description}
      </Text>

      {checkup.animal.imageUrl && (
        <Link
          href={{
            pathname: `/animal/[animalId]`,
            params: { animalId: checkup.animal.id },
          }}
        >
          <Image
            source={{ uri: checkup.animal.imageUrl }}
            style={{ width: 200, height: 200, borderRadius: 8 }}
            resizeMode="cover"
          />
        </Link>
      )}
    </View>
  );
}
