import { useThemedStyles } from "@/hooks/useThemedStyles";
import { View } from "react-native";

export default function RequestScreenContent() {
  const { styles } = useThemedStyles();

  return <View style={styles.container}></View>;
}
