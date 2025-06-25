import { Ionicons } from "@expo/vector-icons";
import { useState } from "react";
import {
  FlatList,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from "react-native";

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
  const [expanded, setExpanded] = useState(false);

  const selected = list.find((e) => e.key === selectedItem);

  return (
    <View style={styles.container}>
      <Text style={styles.label}>{label}</Text>
      <TouchableOpacity
        style={styles.selected}
        onPress={() => setExpanded((e) => !e)}
        disabled={disabled}
        accessibilityRole="button"
      >
        <Text style={styles.selectedText}>
          {selected ? selected.label : "None"}
        </Text>
        <Ionicons
          name={expanded ? "chevron-up" : "chevron-down"}
          size={20}
          color="#888"
        />
      </TouchableOpacity>
      {expanded && (
        <View style={styles.list}>
          <FlatList
            data={list}
            keyExtractor={(item) => String(item.key)}
            renderItem={({ item }) => (
              <TouchableOpacity
                style={[
                  styles.item,
                  selectedItem === item.key && styles.selectedItem,
                  disabled && styles.disabledItem,
                ]}
                onPress={() => {
                  setExpanded(false);
                  onSelect(item.value);
                }}
                disabled={disabled}
              >
                <Text
                  style={[
                    styles.itemText,
                    selectedItem === item.key && styles.selectedItemText,
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

const styles = StyleSheet.create({
  container: {
    marginBottom: 16,
    width: "100%",
  },
  label: {
    fontWeight: "bold",
    marginBottom: 8,
    fontSize: 16,
  },
  selected: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    padding: 12,
    borderRadius: 8,
    backgroundColor: "#f3f4f6",
    marginBottom: 4,
  },
  selectedText: {
    fontSize: 15,
    color: "#222",
  },
  list: {
    backgroundColor: "#f9fafb",
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#e5e7eb",
    maxHeight: 200,
    overflow: "hidden",
  },
  item: {
    padding: 12,
  },
  selectedItem: {
    backgroundColor: "#dbeafe",
  },
  disabledItem: {
    opacity: 0.5,
  },
  itemText: {
    fontSize: 15,
    color: "#222",
  },
  selectedItemText: {
    color: "#2563eb",
    fontWeight: "bold",
  },
});
