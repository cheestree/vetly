import { useThemedStyles } from "@/hooks/useThemedStyles";
import { FontAwesome5 } from "@expo/vector-icons";
import { useState } from "react";
import { FlatList, Text, TouchableOpacity, View } from "react-native";
import CustomText from "./CustomText";

type CustomListItem = {
  label: string;
  key: string | number;
  value: any;
};

type CustomListProps = {
  list: CustomListItem[];
  selectedItem: string | number | undefined;
  onSelect: (value: any) => void;
  disabled?: boolean;
  // label is now optional
  label?: string;
};

export default function CustomList({
  label,
  list,
  selectedItem,
  onSelect,
  disabled,
}: CustomListProps) {
  const { styles } = useThemedStyles();

  const [expanded, setExpanded] = useState(false);
  const selected = list.find((e) => e.key === selectedItem) ||
    list[0] || { label: "None" };

  return (
    <>
      {label && <CustomText text={label} />}
      <View style={styles.customListContainer}>
        <TouchableOpacity
          style={styles.customListSelected}
          onPress={() => setExpanded((e) => !e)}
          disabled={disabled}
          accessibilityRole="button"
        >
          <Text style={styles.customListSelectedText}>{selected.label}</Text>
          <FontAwesome5
            name={expanded ? "chevron-up" : "chevron-down"}
            size={20}
            color="#888"
          />
        </TouchableOpacity>
        {expanded && (
          <View style={styles.customListList}>
            <FlatList
              data={list}
              keyExtractor={(item) => String(item.key)}
              renderItem={({ item }) => (
                <TouchableOpacity
                  style={[
                    styles.customListItem,
                    selectedItem === item.key && styles.customListSelectedItem,
                    disabled && styles.customListDisabledItem,
                  ]}
                  onPress={() => {
                    setExpanded(false);
                    onSelect(item.value);
                  }}
                  disabled={disabled}
                >
                  <Text
                    style={[
                      styles.customListItemText,
                      selectedItem === item.key &&
                        styles.customListSelectedItemText,
                    ]}
                  >
                    {item.label}
                  </Text>
                </TouchableOpacity>
              )}
            />
          </View>
        )}
      </View>
    </>
  );
}
