import { useThemedStyles } from "@/hooks/useThemedStyles";
import { Ionicons } from "@expo/vector-icons";
import { useState } from "react";
import { FlatList, Text, TouchableOpacity, View } from "react-native";

type CustomListItem = {
  label: string;
  key: any;
  value: any;
};

type CustomListProps = {
  label: string;
  list: CustomListItem[];
  selectedItem?: any;
  onSelect: (value: any) => void;
  disabled?: boolean;
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
  const selected = list.find((e) => e.key === selectedItem);

  return (
    <View style={styles.customListContainer}>
      <Text style={styles.customListLabel}>{label}</Text>
      <TouchableOpacity
        style={styles.customListSelected}
        onPress={() => setExpanded((e) => !e)}
        disabled={disabled}
        accessibilityRole="button"
      >
        <Text style={styles.customListSelectedText}>
          {selected ? selected.label : "None"}
        </Text>
        <Ionicons
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
  );
}
