import BasePage from "@/components/basic/base/BasePage";
import CustomButton from "@/components/basic/custom/CustomButton";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { Linking, Platform, StyleSheet, Text, View } from "react-native";

export default function Contact() {
  const { styles } = useThemedStyles();

  const handlePress = async () => {
    const webUrl = "https://github.com/cheestree";

    if (Platform.OS === "web") {
      window.open(webUrl, "_blank");
      return;
    }

    const appUrl = "github://";
    try {
      const supported = await Linking.canOpenURL(appUrl);
      if (supported) {
        await Linking.openURL(appUrl);
      } else {
        console.log("Cannot open GitHub app, opening web fallback.");
        await Linking.openURL(webUrl);
      }
    } catch (error) {
      console.error("Error opening URL:", error);
      await Linking.openURL(webUrl);
    }
  };

  return (
    <BasePage>
      <View style={styles.container}>
        <Text style={styles.header}>Contact</Text>
        <Text style={styles.paragraph}>
          If you wish to contact the author of this project, please use the
          following methods:
        </Text>
        <Text style={styles.info}>
          GitHub:
          <CustomButton onPress={handlePress} text="cheestree" />
        </Text>
      </View>
    </BasePage>
  );
}

const styles = StyleSheet.create({
  header: {
    fontSize: 28,
    fontWeight: "bold",
    marginBottom: 8,
  },
  paragraph: {
    fontSize: 16,
    marginBottom: 8,
  },
  info: {
    fontSize: 16,
    color: "#444",
  },
});
