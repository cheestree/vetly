import { useThemedStyles } from "@/hooks/useThemedStyles";
import { ReactNode } from "react";
import { SafeAreaView } from "react-native-safe-area-context";

type BasePageProps = {
  children: ReactNode;
};

export default function BasePage({ children }: BasePageProps) {
  const { styles } = useThemedStyles();

  return <SafeAreaView style={styles.container}>{children}</SafeAreaView>;
}
