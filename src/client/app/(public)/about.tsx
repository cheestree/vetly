import BasePage from "@/components/basic/base/BasePage";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { Text } from "react-native";
import { ScrollView } from "react-native-gesture-handler";

export default function AboutScreen() {
  const { styles } = useThemedStyles();

  return (
    <BasePage>
      <ScrollView contentContainerStyle={styles.container}>
        <Text style={styles.header}>About Vetly</Text>
        <Text style={styles.paragraph}>
          Vetly is a modern veterinary management platform designed to help
          clinics, veterinarians, and pet owners connect and manage animal
          health efficiently.
        </Text>
        <Text style={styles.header}>Our Mission</Text>
        <Text style={styles.paragraph}>
          To provide the best digital tools for animal healthcare professionals
          and pet owners.
        </Text>
      </ScrollView>
    </BasePage>
  );
}
