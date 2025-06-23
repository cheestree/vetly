import { RequestPreview } from "@/api/request/request.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { View } from "react-native";
import CustomText from "../basic/custom/CustomText";

export default function RequestPreviewCard({
  request,
}: {
  request: RequestPreview;
}) {
  const { styles } = useThemedStyles();

  return (
    <View style={styles.cardContainer}>
      <CustomText text={`User: ${request.user?.name ?? "—"}`} />
      <CustomText text={`Action: ${request.action}`} />
      <CustomText text={`Target: ${request.target}`} />
      <CustomText text={`Status: ${request.status}`} />
      {request.justification && (
        <CustomText text={`Justification: ${request.justification}`} />
      )}
      <CustomText
        text={`Created: ${new Date(request.createdAt).toLocaleString()}`}
      />
    </View>
  );
}
