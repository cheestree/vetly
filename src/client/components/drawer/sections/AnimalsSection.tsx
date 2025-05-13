import { FontAwesome } from "@expo/vector-icons";
import { DrawerItem } from "@react-navigation/drawer";
import { useState } from "react";

export default function AnimalsSection({ navigation }: any) {
  const [expanded, setExpanded] = useState(false);

  return (
    <>
      <DrawerItem
        label="Animals"
        onPress={() => setExpanded((prev) => !prev)}
        icon={() => <FontAwesome name="search" size={20} />}
      />
      {expanded && (
        <>
          <DrawerItem
            label="Search Animals"
            onPress={() => navigation.navigate("animal")}
            style={{ marginLeft: 32 }}
          />
        </>
      )}
    </>
  );
}
