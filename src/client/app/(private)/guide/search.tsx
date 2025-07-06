import guideApi from "@/api/guide/guide.api";
import { GuideQueryParams } from "@/api/guide/guide.input";
import { GuidePreview } from "@/api/guide/guide.output";
import { RequestList } from "@/api/RequestList";
import { Role } from "@/api/user/user.output";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import { GuideSearchContent } from "@/components/guide/GuideSearchContent";
import { useAuth } from "@/hooks/useAuth";
import { router } from "expo-router";
import React, { useState } from "react";
import { Toast } from "toastify-react-native";

export default function GuideSearchScreen() {
  const { hasRoles } = useAuth();
  const accessButtons = hasRoles(Role.VETERINARIAN);

  const [modalVisible, setModalVisible] = useState(false);
  const [guides, setGuides] = useState<RequestList<GuidePreview>>();
  const [query, setQuery] = useState<GuideQueryParams>({});
  const [page, setPage] = useState(0);

  const handleSearch = async (params: GuideQueryParams, pageNum = 0) => {
    try {
      const data = await guideApi.getGuides({ ...params, page: pageNum });
      setGuides(data);
      setQuery(params);
      setPage(pageNum);
      setModalVisible(false);
    } catch (err) {
      Toast.error("Failed to fetch guides");
    }
  };

  const handleNext = () => {
    if (guides && page < guides.totalPages - 1) {
      handleSearch(query, page + 1);
    }
  };

  const handlePrev = () => {
    if (guides && page > 0) {
      handleSearch(query, page - 1);
    }
  };

  return (
    <BaseComponent title={"Guides"}>
      <PageHeader
        title={"Guides"}
        description={
          "Read up on the latest curated guides for your furry friend"
        }
        buttons={
          accessButtons
            ? [
                {
                  name: "New Guide",
                  icon: "plus",
                  operation: () => {
                    router.navigate({ pathname: "/guide/new" });
                  },
                },
              ]
            : []
        }
      />
      <GuideSearchContent
        guides={guides}
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
