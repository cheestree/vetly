import animalApi from "@/api/animal/animal.api";
import AnimalList from "@/components/animal/list/AnimalList";
import BaseComponent from "@/components/basic/BaseComponent";
import PageHeader from "@/components/basic/PageHeader";
import size from "@/theme/size";
import { FontAwesome5 } from "@expo/vector-icons";
import React, { useState } from "react";
import { Button } from "react-native-paper";
import { StyleSheet } from 'react-native'
import colours from "@/theme/colours";
import spacing from "@/theme/spacing";
import AnimalFilterModal from "@/components/animal/AnimalFilterModal";
import { useAuth } from "@/hooks/useAuth";
import { hasRole } from "@/lib/utils";

export default function PetSearchScreen() {
  const [modalVisible, setModalVisible] = useState(false);
  const [animals, setAnimals] = useState<
    RequestList<AnimalPreview> | undefined
  >(undefined);
  const [loading, setLoading] = useState(false)
  const { information } = useAuth()

  const handleSearch = async (params: AnimalQueryParams) => {
    setLoading(true)
    try {
      const data = await animalApi.getAllAnimals(params);
      setAnimals(data);
      setModalVisible(false);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false)
    }
  };
  
  return (
    <>
      <BaseComponent isLoading={false} title={"Search Pets"}>
        <PageHeader
          title={"Pets"}
          description={"Manage patients' pets"}
          buttons={[
            {
              name: "New Pet",
              icon: 'plus',
              operation: () => {},
            },
          ]}
        />
      
        {animals?.elements && <AnimalList animals={animals?.elements}/>}

        <Button onPress={() => setModalVisible(true)} style={style.filter}>
          <FontAwesome5 name="filter" size={size.icon.md} color="white" />
        </Button>

        <AnimalFilterModal
          visible={modalVisible}
          onDismiss={() => setModalVisible(false)}
          onSearch={async (params: AnimalQueryParams) => handleSearch(params)} 
          canSearchByUserId={hasRole(information?.roles || [], "VETERINARIAN")}
        />
      </BaseComponent>
    </>
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
  },
});