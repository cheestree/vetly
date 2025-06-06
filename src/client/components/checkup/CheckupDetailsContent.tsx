import { useRouter } from "expo-router";
import { Pressable, View, Image, Text } from "react-native";

export default function CheckupDetailsContent({
  checkup,
}: {
  checkup?: CheckupInformation;
}) {
  const router = useRouter();

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
        <Pressable onPress={() => router.push(`/animal/${checkup.animal.id}`)}>
          <Image
            source={{ uri: checkup.animal.imageUrl }}
            style={{ width: 200, height: 200, borderRadius: 8 }}
            resizeMode="cover"
          />
        </Pressable>
      )}
    </View>
  );
}
