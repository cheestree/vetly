import animalApi from "@/api/animal/animal.api";
import { AnimalQueryParams } from "@/api/animal/animal.input";
import { AnimalPreview } from "@/api/animal/animal.output";
import { RequestList } from "@/api/RequestList";
import AnimalFilterModal from "@/components/animal/AnimalFilterModal";
import AnimalList from "@/components/animal/list/AnimalList";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import CustomFilterButton from "@/components/basic/custom/CustomFilterButton";
import { useAuth } from "@/hooks/useAuth";
import ROUTES from "@/lib/routes";
import { hasRole } from "@/lib/utils";
import { router } from "expo-router";
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
          description={"Search patient's pets"}
          buttons={[
            {
              name: "New Pet",
              icon: "plus",
              operation: () => {
                router.navigate({
                  pathname: ROUTES.PRIVATE.ANIMAL.CREATE,
                });
              },
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
