import { Image, StyleSheet, Text, View } from "react-native";

export default function AnimalDetailsContent({
  animal,
}: {
  animal?: AnimalInformation;
}) {
  if (!animal) {
    return (
      <View style={extras.container}>
        <Text style={extras.placeholder}>No animal data found.</Text>
      </View>
    );
  }

  return (
    <View style={extras.container}>
      <Text style={extras.field}>Name: {animal.name}</Text>
      <Text style={extras.field}>Species: {animal.species}</Text>

      {animal.imageUrl && (
        <Image
          source={{ uri: animal.imageUrl }}
          style={extras.image}
          resizeMode="cover"
        />
      )}
    </View>
  );
}

const extras = StyleSheet.create({
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
    marginBottom: 4,
  },
  image: {
    width: 200,
    height: 200,
    borderRadius: 8,
    marginTop: 16,
  },
});
