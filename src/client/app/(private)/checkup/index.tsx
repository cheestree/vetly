import BaseComponent from "@/components/BaseComponent";
import React, { useState } from "react";
import { Text, StyleSheet } from "react-native";
import spacing from "@/theme/spacing";
import { Button } from "react-native-paper";
import { FontAwesome } from "@expo/vector-icons";
import layout from "@/theme/layout";
import FilterModal from "@/components/checkup/FilterModal";
import CheckupPreviewCard from "@/components/checkup/CheckupPreviewCard";
import CheckupServices from "@/api/services/CheckupServices";

interface RangeProps {
  startDate?: Date;
  endDate?: Date;
}

export default function CheckupSearchScreen() {
  const [modalVisible, setModalVisible] = useState(false);
  const [checkups, setCheckups] = useState<
    RequestList<CheckupPreview> | undefined
  >(undefined);
  const [mine, setMine] = useState(false);
  const [mineDisabled, setMineDisabled] = useState(false);

  const [range, setRange] = useState<RangeProps>({
    startDate: undefined,
    endDate: undefined,
  });

  const handleSearch = async () => {
    try {
      const data = await CheckupServices.getCheckups({
        dateTimeStart: range.startDate?.toDateString() || undefined,
        dateTimeEnd: range.endDate?.toDateString() || undefined,
      });
      setCheckups(data);
      setModalVisible(false);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <>
      <BaseComponent
        isLoading={false}
        title="Search Checkups"
        style={style.container}
      >
        {checkups?.elements.map((checkup) => (
          <CheckupPreviewCard key={checkup.id} checkup={checkup} />
        ))}

        <Button onPress={() => setModalVisible(true)} style={style.filter}>
          <FontAwesome name="filter" size={24} color="white" />
        </Button>

        <FilterModal
          visible={modalVisible}
          onDismiss={() => setModalVisible(false)}
          onSearch={handleSearch}
          range={range}
          setRange={setRange}
          mine={mine}
          setMine={setMine}
          mineDisabled={mineDisabled}
          setMineDisabled={setMineDisabled}
        />
      </BaseComponent>
    </>
  );
}
const style = StyleSheet.create({
  container: {
    ...layout.container,
    padding: spacing.md,
  },
  filter: {
    position: "absolute",
    bottom: spacing.md,
    right: spacing.md,
    padding: spacing.sm,
    borderRadius: 32,
    backgroundColor: "#6200ee",
    zIndex: 10,
  },
  modalFilters: {},
  modalContainer: {
    flex: 1,
    backgroundColor: "white",
    padding: spacing.md,
    justifyContent: "center",
    alignItems: "center",
  },
  modalButtons: {
    flexDirection: "row",
    justifyContent: "space-around",
    width: "100%",
    paddingHorizontal: spacing.md,
  },
  modalButton: {
    flex: 1,
    marginHorizontal: spacing.sm,
    borderColor: "#6200ee",
    backgroundColor: "#6200ee",
  },
  rangeText: {
    marginLeft: 8,
    fontSize: 14,
    color: "gray",
  },
});
