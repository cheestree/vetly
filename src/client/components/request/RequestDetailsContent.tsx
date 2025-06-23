import { RequestInformation } from "@/api/request/request.output";
import { useThemedStyles } from "@/hooks/useThemedStyles";
import { View } from "react-native";
import CustomText from "../basic/custom/CustomText";

export default function RequestDetailsContent({
  request,
}: {
  request?: RequestInformation;
}) {
  const { styles } = useThemedStyles();
  if (!request) {
    return <View style={styles.container}></View>;
  }

  return (
    <View style={styles.container}>
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
      {request.files && request.files.length > 0 && (
        <CustomText text={`Files: ${request.files.join(", ")}`} />
      )}
      {request.extraData && (
        <CustomText text={`Extra Data: ${JSON.stringify(request.extraData)}`} />
      )}
    </View>
  );
}
