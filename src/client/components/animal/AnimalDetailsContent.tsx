import { Alert, Image, Platform, StyleSheet, Text, View } from "react-native";
import CustomButton from "../basic/CustomButton";

export default function AnimalDetailsContent({
  animal,
  deleteAnimal,
  editAnimal,
}: {
  animal?: AnimalInformation;
  deleteAnimal: () => void;
  editAnimal: () => void;
}) {
  if (!animal) {
    return (
      <View style={extras.container}>
        <Text style={extras.placeholder}>No animal data found.</Text>
      </View>
    );
  }

  const handleDelete = () => {
    if (Platform.OS === "web") {
      const confirmed = window.confirm(
        "Are you sure you want to delete this animal?",
      );
      if (confirmed) {
        deleteAnimal();
      }
    } else {
      Alert.alert(
        "Delete Animal",
        "Are you sure you want to delete this animal?",
        [
          { text: "Cancel", style: "cancel" },
          {
            text: "Delete",
            style: "destructive",
            onPress: () => deleteAnimal(),
          },
        ],
      );
    }
  };

  return (
    <View style={extras.container}>
      <Text style={extras.heading}>Animal Details</Text>
      <Text style={extras.id}>#{animal.id}</Text>
      <Text style={extras.field}>Name: {animal.name}</Text>
      <Text style={extras.field}>Species: {animal.species}</Text>

      {animal.imageUrl && (
        <Image
          source={{ uri: animal.imageUrl }}
          style={extras.image}
          resizeMode="cover"
        />
      )}

      <View style={extras.actionButtons}>
        <CustomButton onPress={editAnimal} text="Edit" />
        <CustomButton onPress={handleDelete} text="Delete" />
      </View>
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
