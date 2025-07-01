import BasePage from "@/components/basic/base/BasePage";
import { StyleSheet, Text } from "react-native";
import { ScrollView } from "react-native-gesture-handler";

export default function AboutScreen() {
  return (
    <BasePage>
      <ScrollView contentContainerStyle={styles.container}>
        <Text style={styles.header}>About Vetly</Text>
        <Text style={styles.paragraph}>
          Vetly is a modern veterinary management platform designed to help
          clinics, veterinarians, and pet owners connect and manage animal
          health efficiently.
        </Text>
        <Text style={styles.subheader}>Our Mission</Text>
        <Text style={styles.paragraph}>
          To provide the best digital tools for animal healthcare professionals
          and pet owners.
        </Text>
      </ScrollView>
    </BasePage>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 24,
    gap: 16,
  },
  header: {
    fontSize: 28,
    fontWeight: "bold",
    marginBottom: 8,
  },
  subheader: {
    fontSize: 20,
    fontWeight: "600",
    marginTop: 16,
    marginBottom: 4,
  },
  paragraph: {
    fontSize: 16,
    lineHeight: 22,
  },
});
