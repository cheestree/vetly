import { View, Text, Image } from "react-native";

export default function AnimalDetailsContent({
  animal,
}: {
  animal?: AnimalInformation;
}) {
  if (!animal) return <Text>No animal data found</Text>;

  return (
    <View style={{ flex: 1, padding: 16 }}>
      <Text style={{ fontSize: 24, marginBottom: 16 }}>Animal Details</Text>
      <Text style={{ fontSize: 18, marginBottom: 8 }}>#{animal.id}</Text>
      <Text style={{ marginBottom: 4 }}>Name: {animal.name}</Text>
      <Text style={{ marginBottom: 4 }}>Species: {animal.species}</Text>

      {animal.imageUrl && (
        <Image
          source={{ uri: animal.imageUrl }}
          style={{ width: 200, height: 200, borderRadius: 8 }}
          resizeMode="cover"
        />
      )}
    </View>
  );
}
