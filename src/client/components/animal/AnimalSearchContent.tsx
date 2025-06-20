import animalApi from "@/api/animal/animal.api";
import { AnimalQueryParams } from "@/api/animal/animal.input";
import { AnimalPreview } from "@/api/animal/animal.output";
import { RequestList } from "@/api/RequestList";
import { Role } from "@/api/user/user.output";
import { useAuth } from "@/hooks/useAuth";
import { hasRole } from "@/lib/utils";
import { useState } from "react";
import CustomFilterButton from "../basic/custom/CustomFilterButton";
import AnimalFilterModal from "./AnimalFilterModal";
import AnimalList from "./list/AnimalList";

export default function AnimalSearchContent() {
  const { information } = useAuth();
  const [modalVisible, setModalVisible] = useState(false);
  const [animals, setAnimals] = useState<RequestList<AnimalPreview>>();

  const handleSearch = async (params: AnimalQueryParams) => {
    try {
      const data = await animalApi.getAllAnimals(params);
      setAnimals(data);
      setModalVisible(false);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <>
      {animals?.elements && <AnimalList animals={animals.elements} />}

      <CustomFilterButton onPress={() => setModalVisible(true)} />

      <AnimalFilterModal
        visible={modalVisible}
        onDismiss={() => setModalVisible(false)}
        onSearch={handleSearch}
        canSearchByUserId={hasRole(
          information.roles,
          Role.VETERINARIAN,
          Role.ADMIN,
        )}
      />
    </>
  );
}
