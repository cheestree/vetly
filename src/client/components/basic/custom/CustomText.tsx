import { useThemedStyles } from "@/hooks/useThemedStyles";
import { FontAwesome5 } from "@expo/vector-icons";
import { Text, View, ViewStyle } from "react-native";

type CustomTextProps = {
  text: string;
  icon?: string;
  numberOfLines?: number;
  style?: ViewStyle;
};

export default function CustomText({
  icon,
  text,
  numberOfLines = 2,
}: CustomTextProps) {
  const { styles } = useThemedStyles();
  return (
    <View style={styles.metaContainer}>
      {icon && <FontAwesome5 name={icon} style={styles.icon} />}
      <Text
        ellipsizeMode="tail"
        numberOfLines={numberOfLines}
        style={styles.meta}
      >
        {text}
      </Text>
    </View>
  );
}
