import { RequestPreview } from "@/api/request/request.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { View } from "react-native";

type RequestScreenProps = {
  requests: RequestPreview[];
};

export default function RequestScreenContent({ requests }: RequestScreenProps) {
  const { styles } = useThemedStyles();

  return <View style={styles.container}></View>;
}
