import { useAuth } from "@/hooks/useAuth";
import colours from "@/theme/colours";
import size from "@/theme/size";
import { FontAwesome5 } from "@expo/vector-icons";
import {
  Image,
  Platform,
  Pressable,
  StyleSheet,
  useWindowDimensions,
  View,
} from "react-native";
type PrivateHeaderProps = {
  onToggleDrawer: () => void;
};

export default function PrivateHeader({ onToggleDrawer }: PrivateHeaderProps) {
  const { information } = useAuth();
  const { width } = useWindowDimensions();
  const isDesktop = Platform.OS === "web" && width >= 768;

  return (
    <View style={styles.headerContainer}>
      <View style={styles.search}>
        {!isDesktop && (
          <Pressable onPress={onToggleDrawer}>
            <FontAwesome5 name="list" size={size.icon.md} color="black" />
          </Pressable>
        )}
      </View>
      <View style={styles.profile}>
        <Pressable onPress={() => {}} style={styles.card}>
          <Image
            source={
              information?.imageUrl
                ? { uri: information.imageUrl }
                : require("@/assets/placeholder.png")
            }
            style={styles.image}
            resizeMode="cover"
          />
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  headerContainer: {
    flexDirection: "row",
    alignItems: "center",
    borderBottomWidth: 1,
    borderBottomColor: "#eee",
    backgroundColor: colours.secondaryBackground,
    width: "100%",
    height: 63,
    paddingHorizontal: 12,
  },
  search: {
    flex: 1,
    justifyContent: "center",
  },
  profile: {
    justifyContent: "center",
    alignItems: "center",
  },
  card: {
    width: 40,
    height: 40,
    borderRadius: 20,
    overflow: "hidden",
    borderWidth: 1,
    borderColor: "#ccc",
  },
  image: {
    width: "100%",
    height: "100%",
    borderRadius: 20,
  },
});
