import { FontAwesome } from "@expo/vector-icons";
import { DrawerItem } from "@react-navigation/drawer";
import { Router } from "expo-router";
import { useState } from "react";

interface Props {
  router: Router;
}

export default function AnimalsSection({ router }: Props) {
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
            onPress={() => router.navigate("/(private)/(drawer)/animal")}
            style={{ marginLeft: 32 }}
          />
        </>
      )}
    </>
  );
}
