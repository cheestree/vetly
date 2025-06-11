import animalApi from "@/api/animal/animal.api";
import AnimalList from "@/components/animal/list/AnimalList";
import BaseComponent from "@/components/basic/BaseComponent";
import PageHeader from "@/components/basic/PageHeader";
import React, { useState } from "react";
import AnimalFilterModal from "@/components/animal/AnimalFilterModal";
import { useAuth } from "@/hooks/useAuth";
import { hasRole } from "@/lib/utils";
import FilterModelButton from "@/components/basic/FilterModelButton";

export default function PetSearchScreen() {
  const [modalVisible, setModalVisible] = useState(false);
  const [animals, setAnimals] = useState<
    RequestList<AnimalPreview> | undefined
  >(undefined);
  const [loading, setLoading] = useState(false);
  const { information } = useAuth();

  const handleSearch = async (params: AnimalQueryParams) => {
    setLoading(true);
    try {
      const data = await animalApi.getAllAnimals(params);
      setAnimals(data);
      setModalVisible(false);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
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
              icon: "plus",
              operation: () => {},
            },
          ]}
        />

        {animals?.elements && <AnimalList animals={animals?.elements} />}

        <FilterModelButton onPress={() => setModalVisible(true)} />

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
