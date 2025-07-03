import BasePage from "@/components/basic/base/BasePage";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { Text } from "react-native";
import { ScrollView } from "react-native-gesture-handler";

export default function Index() {
  const { styles } = useThemedStyles();

  return (
    <BasePage>
      <ScrollView contentContainerStyle={styles.container}>
        <Text style={styles.header}>Vetly</Text>
      </ScrollView>
    </BasePage>
  );
}
