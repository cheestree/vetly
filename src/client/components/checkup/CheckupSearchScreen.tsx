import React, { useState } from "react";
import { TextInput, Button, Text, StyleSheet, ScrollView } from "react-native";
import CheckupServices from "../../api/services/CheckupServices";
import DatePickerComponent from "../date_pickers/DatePickerComponent";
import CheckupPreviewCard from "./CheckupPreviewCard";
import { usePageTitle } from "@/hooks/usePageTitle";
import { useAuth } from "@/hooks/AuthContext";

export default function CheckupSearchScreen() {
  const [veterinarianName, setVeterinarianName] = useState("");
  const [animalName, setAnimalName] = useState("");
  const [dateTimeStart, setDateTimeStart] = useState<string | null>(null);
  const [dateTimeEnd, setDateTimeEnd] = useState<string | null>(null);
  const [checkups, setCheckups] = useState<
    RequestList<CheckupPreview> | undefined
  >(undefined);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { token } = useAuth();
  usePageTitle("Search Checkups");

  const handleSearch = async () => {
    setLoading(true);
    setError(null);

    try {
      if (!token) {
        throw new Error("Authorization token is missing. Please log in again.");
      }

      const data = await CheckupServices.getCheckups(
        {
          veterinarianName: veterinarianName || undefined,
          animalName: animalName || undefined,
          dateTimeStart: dateTimeStart || undefined,
          dateTimeEnd: dateTimeEnd || undefined,
        },
        token,
      );
      setCheckups(data);
    } catch (err) {
      console.error(err);
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("Failed to fetch checkups. Please try again later.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <TextInput
        placeholder="Veterinarian Name"
        style={styles.input}
        value={veterinarianName}
        onChangeText={setVeterinarianName}
      />
      <TextInput
        placeholder="Animal Name"
        style={styles.input}
        value={animalName}
        onChangeText={setAnimalName}
      />

      {/* Start Date Picker */}
      <Text>Select Start Date:</Text>
      <DatePickerComponent
        selectedDate={dateTimeStart}
        onDateChange={setDateTimeStart}
      />

      {/* End Date Picker */}
      <Text>Select End Date:</Text>
      <DatePickerComponent
        selectedDate={dateTimeEnd}
        onDateChange={setDateTimeEnd}
      />

      <Button
        title={loading ? "Searching..." : "Search"}
        onPress={handleSearch}
        disabled={loading}
      />

      {error && <Text style={styles.error}>{error}</Text>}

      {checkups?.elements.map((checkup, index) => (
        <CheckupPreviewCard checkup={checkup} key={index} />
      ))}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 16,
    gap: 12,
  },
  input: {
    borderWidth: 1,
    borderColor: "#ccc",
    padding: 10,
    borderRadius: 8,
  },
  checkupItem: {
    marginTop: 12,
    padding: 10,
    backgroundColor: "#f5f5f5",
    borderRadius: 8,
  },
  error: {
    color: "red",
  },
});
