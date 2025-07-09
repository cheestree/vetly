import BasePage from "@/components/basic/base/BasePage";
import CustomButton from "@/components/basic/custom/CustomButton";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { Linking, Platform, Text, View } from "react-native";

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
    } catch (e) {
      console.error("Error opening URL:", e);
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
        <Text style={styles.container}>
          <CustomButton onPress={handlePress} icon={"github"} />
        </Text>
      </View>
    </BasePage>
  );
}
