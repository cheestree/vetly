import { usePageTitle } from "@/hooks/usePageTitle";
import { useState } from "react";
import {
  ScrollView,
  TextInput,
  Button,
  Text,
  StyleSheet,
  Switch,
  View,
} from "react-native";
import AnimalServices from "@/api/services/AnimalServices";
import { useAuth } from "@/hooks/AuthContext";
import AnimalPreviewCard from "./AnimalPreviewCard";
export default function AnimalSearchScreen() {
  const [name, setName] = useState("");
  const [microchip, setMicrochip] = useState("");
  const [birthDate, setBirthDate] = useState<number | undefined>(undefined);
  const [species, setSpecies] = useState<number | undefined>(undefined);
  const [animals, setAnimals] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { token } = useAuth();
  usePageTitle("Search Animals");

  const [self, setSelf] = useState<boolean | null>(null);
  const [disableSelfFilter, setDisableSelfFilter] = useState(false);

  const handleDisableSwitchChange = (value: boolean) => {
    setDisableSelfFilter(value);
    if (value) {
      setSelf(null);
    }
  };

  const handleSearch = async () => {
    setLoading(true);
    setError(null);

    try {
      if (!token) {
        throw new Error("Authorization token is missing. Please log in again.");
      }

      const data = await AnimalServices.getAllAnimals(
        {
          name: name || undefined,
          microchip: microchip || undefined,
          birthDate,
          species,
          self: self || undefined,
          page: 0,
          limit: 10,
          sortBy: "name",
          sortOrder: "asc",
        },
        token,
      );
      setAnimals(data.elements);
    } catch (err) {
      console.error(err);
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("Failed to fetch animals. Please try again later.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <TextInput
        placeholder="Animal Name"
        style={styles.input}
        value={name}
        onChangeText={setName}
      />
      <TextInput
        placeholder="Microchip"
        style={styles.input}
        value={microchip}
        onChangeText={setMicrochip}
      />

      {/* Species Selection */}
      <Text>Select Species:</Text>
      <TextInput
        placeholder="Species"
        style={styles.input}
        value={species?.toString()}
        onChangeText={(text) => setSpecies(Number(text))}
      />
      <Text>Disable Ownership Filter</Text>
      <Switch
        onValueChange={handleDisableSwitchChange}
        value={disableSelfFilter}
      />
      {/* Self Filter */}
      <Text>Owned by me?</Text>
      <Switch
        onValueChange={() => {
          !disableSelfFilter && setSelf(self === true ? false : true);
        }}
        value={self === true}
        disabled={disableSelfFilter}
      />

      <Button
        title={loading ? "Searching..." : "Search"}
        onPress={handleSearch}
        disabled={loading}
      />

      {error && <Text style={styles.error}>{error}</Text>}

      {/* Card Wrapper with Flexbox for side-by-side layout */}
      <View style={styles.cardWrapper}>
        {animals.map((animal, index) => (
          <AnimalPreviewCard animal={animal} key={index} />
        ))}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 16,
  },
  input: {
    borderWidth: 1,
    borderColor: "#ccc",
    padding: 8,
    marginVertical: 8,
    borderRadius: 4,
  },
  error: {
    color: "red",
    marginTop: 8,
  },
  cardWrapper: {
    flexDirection: "row", // Align items horizontally
    flexWrap: "wrap", // Allow wrapping of items
    justifyContent: "space-between", // Space out cards
    marginTop: 16,
  },
  card: {
    width: "48%", // Each card takes up ~48% of the container width
    marginBottom: 16, // Space between rows
    padding: 16,
    backgroundColor: "#fff",
    borderRadius: 8,
    shadowColor: "#000",
    shadowOpacity: 0.1,
    shadowRadius: 5,
    elevation: 3, // For Android shadow
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 4,
  },
  cardId: {
    fontSize: 14,
    color: "#888",
  },
});
