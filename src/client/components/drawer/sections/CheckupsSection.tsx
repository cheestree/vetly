import { FontAwesome } from "@expo/vector-icons";
import { DrawerItem } from "@react-navigation/drawer";
import { Router } from "expo-router";
import { useState } from "react";

interface Props {
  router: Router;
  roles: string[];
}

export default function CheckupsSection({ router, roles }: Props) {
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
            onPress={() => router.navigate("/(private)/(drawer)/checkup")}
            style={{ marginLeft: 32 }}
          />
        </>
      )}
    </>
  );
}
