import animalApi from "@/api/animal/animal.api";
import AnimalFilterModal from "@/components/animal/AnimalFilterModal";
import AnimalList from "@/components/animal/list/AnimalList";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import CustomFilterButton from "@/components/basic/custom/CustomFilterButton";
import { useAuth } from "@/hooks/useAuth";
import { hasRole } from "@/lib/utils";
import React, { useState } from "react";

export default function PetSearchScreen() {
  const { information } = useAuth();
  const [modalVisible, setModalVisible] = useState(false);
  const [animals, setAnimals] = useState<
    RequestList<AnimalPreview> | undefined
  >(undefined);
  const [loading, setLoading] = useState(false);

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
      <BaseComponent isLoading={loading} title={"Search Pets"}>
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

        <CustomFilterButton onPress={() => setModalVisible(true)} />

        <AnimalFilterModal
          visible={modalVisible}
          onDismiss={() => setModalVisible(false)}
          onSearch={async (params: AnimalQueryParams) => handleSearch(params)}
          canSearchByUserId={hasRole(
            information?.roles || [],
            "VETERINARIAN",
            "ADMIN",
          )}
        />
      </BaseComponent>
    </>
  );
}
