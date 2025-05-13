import { FontAwesome } from "@expo/vector-icons";
import { DrawerItem } from "@react-navigation/drawer";
import { useState } from "react";

export default function CheckupsSection({ navigation }: any) {
  const [expanded, setExpanded] = useState(false);

  return (
    <>
      <DrawerItem
        label="Checkups"
        onPress={() => setExpanded((prev) => !prev)}
        icon={() => <FontAwesome name="search" size={20} />}
      />
      {expanded && (
        <>
          <DrawerItem
            label="Search Checkups"
            onPress={() => navigation.navigate("checkup")}
            style={{ marginLeft: 32 }}
          />
          <DrawerItem
            label="Create Checkup"
            onPress={() => navigation.navigate("checkupCreate")}
            style={{ marginLeft: 32 }}
          />
        </>
      )}
    </>
  );
}
