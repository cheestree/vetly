import animalApi from "@/api/animal/animal.api";
import { AnimalQueryParams } from "@/api/animal/animal.input";
import { AnimalPreview } from "@/api/animal/animal.output";
import { GuideQueryParams } from "@/api/guide/guide.input";
import { RequestList } from "@/api/RequestList";
import { Role } from "@/api/user/user.output";
import AnimalSearchContent from "@/components/animal/AnimalSearchContent";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import { useAuth } from "@/hooks/useAuth";
import ROUTES from "@/lib/routes";
import { router } from "expo-router";
import React, { useState } from "react";
import { Toast } from "toastify-react-native";

export default function PetSearchScreen() {
  const { hasRoles } = useAuth();
  const accessButtons = hasRoles(Role.VETERINARIAN, Role.ADMIN);

  const [modalVisible, setModalVisible] = useState(false);
  const [animals, setAnimals] = useState<RequestList<AnimalPreview>>();
  const [query, setQuery] = useState<GuideQueryParams>({});
  const [page, setPage] = useState(0);

  const handleSearch = async (params: AnimalQueryParams, pageNum = 0) => {
    try {
      const data = await animalApi.getAllAnimals({ ...params, page: pageNum });
      setAnimals(data);
      setQuery(params);
      setPage(pageNum);
      setModalVisible(false);
    } catch (err) {
      Toast.error("Failed to fetch guides");
    }
  };

  const handleNext = () => {
    if (animals && page < animals.totalPages - 1) {
      handleSearch(query, page + 1);
    }
  };

  const handlePrev = () => {
    if (animals && page > 0) {
      handleSearch(query, page - 1);
    }
  };

  return (
    <BaseComponent title={"Search Pets"}>
      <PageHeader
        title={"Pets"}
        description={"Search patient's pets"}
        buttons={
          accessButtons
            ? [
                {
                  name: "New Pet",
                  icon: "plus",
                  operation: () => {
                    router.navigate({
                      pathname: ROUTES.PRIVATE.ANIMAL.CREATE,
                    });
                  },
                },
              ]
            : []
        }
      />
      <AnimalSearchContent
        animals={animals}
        query={query}
        page={page}
        modalVisible={modalVisible}
        setModalVisible={setModalVisible}
        onSearch={handleSearch}
        onNext={handleNext}
        onPrev={handlePrev}
      />
    </BaseComponent>
  );
}
