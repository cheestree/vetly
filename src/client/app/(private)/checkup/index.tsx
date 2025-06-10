import BaseComponent from "@/components/basic/BaseComponent";
import React, { useState } from "react";
import { StyleSheet } from "react-native";
import spacing from "@/theme/spacing";
import { Button } from "react-native-paper";
import { FontAwesome5 } from "@expo/vector-icons";
import checkupApi from "@/api/checkup/checkup.api";
import PageHeader from "@/components/basic/PageHeader";
import colours from "@/theme/colours";
import size from "@/theme/size";
import CheckupList from "@/components/checkup/list/CheckupList";
import CheckupFilterModal from "@/components/checkup/CheckupFilterModal";

export default function CheckupSearchScreen() {
  const [modalVisible, setModalVisible] = useState(false);
  const [checkups, setCheckups] = useState<
    RequestList<CheckupPreview> | undefined
  >(undefined);
  const [loading, setLoading] = useState(false)

  const handleSearch = async (params: CheckupQueryParams) => {
    setLoading(true)
    try {
      const data = await checkupApi.getCheckups(params);
      setCheckups(data);
      setModalVisible(false);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false)
    }
  };

  return (
    <BaseComponent
      isLoading={loading}
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
      
      {checkups?.elements && <CheckupList checkups={checkups?.elements}/>}

      <Button onPress={() => setModalVisible(true)} style={style.filter}>
        <FontAwesome5 name="filter" size={size.icon.md} color="white" />
      </Button>

      <CheckupFilterModal
        visible={modalVisible}
        onDismiss={() => setModalVisible(false)}
        onSearch={async (params: CheckupQueryParams) => handleSearch(params)}
      />
    </BaseComponent>
  );
}

const style = StyleSheet.create({
  checkupContainer: {
    width: '100%',
    height: '90%',
    flexDirection: 'row',
    justifyContent: 'space-between',
    display: 'flex',
    borderWidth: 1,
    borderColor: "#ccc",
    borderRadius: size.border.sm,
    padding: size.padding.sm,
  },
  filter: {
    position: "absolute",
    bottom: spacing.md,
    right: spacing.md,
    justifyContent: 'center',
    width: 64,
    height: 64,
    borderRadius: size.border.md,
    backgroundColor: colours.primary,
    zIndex: 10,
  }
});
