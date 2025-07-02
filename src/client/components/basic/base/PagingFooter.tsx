import { useThemedStyles } from "@/hooks/useThemedStyles";
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
  const { styles } = useThemedStyles();

  return (
    <View style={styles.pagingFooter}>
      <CustomButton
        icon="chevron-left"
        onPress={onPrevious}
        disabled={disablePrevious}
      />
      <CustomButton
        icon="chevron-right"
        onPress={onNext}
        disabled={disableNext}
      />
    </View>
  );
}
