import BaseComponent from "@/components/basic/BaseComponent";
import React, { useState } from "react";
import { StyleSheet, View } from "react-native";
import spacing from "@/theme/spacing";
import { Button } from "react-native-paper";
import { FontAwesome5 } from "@expo/vector-icons";
import layout from "@/theme/layout";
import FilterModal from "@/components/checkup/FilterModal";
import CheckupPreviewCard from "@/components/checkup/CheckupPreviewCard";
import CheckupServices from "@/api/services/CheckupServices";
import { RangeProps } from "@/lib/types";
import PageHeader from "@/components/basic/PageHeader";

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
    <BaseComponent
      isLoading={false}
      title="Search Checkups"
    >
      <PageHeader
        title={"Checkups"}
        description={"Manage and schedule checkups for your patients"}
        buttons={[
          {
            name: "Add Checkup",
            icon: "plus",
            operation: () => {},
          },
        ]}
      />
      <View style={style.checkupContainer}>
        {checkups?.elements.map((checkup) => (
          <CheckupPreviewCard key={checkup.id} checkup={checkup} />
        ))}
      </View>

      <Button onPress={() => setModalVisible(true)} style={style.filter}>
        <FontAwesome5 name="filter" size={24} color="white" />
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
  );
}

const style = StyleSheet.create({
  checkupContainer: {
    width: "100%",
    height: "90%",
    borderWidth: 1,
    borderColor: "#ccc",
    borderRadius: 8,
    padding: 8
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
