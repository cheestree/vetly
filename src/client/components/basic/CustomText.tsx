import { useThemedStyles } from "@/hooks/useThemedStyles";
import { FontAwesome5 } from "@expo/vector-icons";
import { Text, View } from "react-native";

type CustomTextProps = {
  text: string;
  icon?: string;
};

export default function CustomText({ icon, text }: CustomTextProps) {
  const { colours, styles } = useThemedStyles();
  return (
    <View style={styles.metaContainer}>
      {icon && <FontAwesome5 name={icon} style={styles.icon} />}
      <Text ellipsizeMode="tail" numberOfLines={2} style={styles.meta}>
        {text}
      </Text>
    </View>
  );
}
