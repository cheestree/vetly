import guideApi from "@/api/guide/guide.api";
import BaseComponent from "@/components/basic/base/BaseComponent";
import PageHeader from "@/components/basic/base/PageHeader";
import CustomFilterButton from "@/components/basic/custom/CustomFilterButton";
import GuideFilterModal from "@/components/guide/GuideFilterModal";
import GuideList from "@/components/guide/list/GuideList";
import React, { useState } from "react";

export default function GuideScreen() {
  const [modalVisible, setModalVisible] = useState(false);
  const [guides, setGuides] = useState<RequestList<GuidePreview> | undefined>(
    undefined,
  );
  const [loading, setLoading] = useState(false);

  const handleSearch = async (params: CheckupQueryParams) => {
    setLoading(true);
    try {
      const data = await guideApi.getGuides(params);
      setGuides(data);
      setModalVisible(false);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <BaseComponent isLoading={loading} title={"Guides"}>
        <PageHeader
          title={"Guides"}
          description={
            "Read up on the latest curated guides for your furry friend"
          }
          buttons={[
            {
              name: "New Guide",
              icon: "plus",
              operation: () => {},
            },
          ]}
        />

        {guides?.elements && <GuideList guides={guides?.elements} />}

        <CustomFilterButton onPress={() => setModalVisible(true)} />

        <GuideFilterModal
          visible={modalVisible}
          onDismiss={() => setModalVisible(false)}
          onSearch={async (params: GuideQueryParams) => handleSearch(params)}
        />
      </BaseComponent>
    </>
  );
}
