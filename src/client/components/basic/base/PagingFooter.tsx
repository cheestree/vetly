import { View } from "react-native";
import CustomButton from "../custom/CustomButton";

type PagingFooterProps = {
  onPrevious: () => void;
  onNext: () => void;
  disablePrevious?: boolean;
  disableNext?: boolean;
};

export default function PagingFooter({
  onPrevious,
  onNext,
  disablePrevious,
  disableNext,
}: PagingFooterProps) {
  return (
    <View
      style={{
        flexDirection: "row",
        justifyContent: "center",
        gap: 16,
        marginVertical: 8,
      }}
    >
      <CustomButton
        text="Previous"
        onPress={onPrevious}
        disabled={disablePrevious}
      />
      <CustomButton text="Next" onPress={onNext} disabled={disableNext} />
    </View>
  );
}
