import guideApi from "@/api/guide/guide.api";
import BaseComponent from "@/components/basic/BaseComponent";
import PageHeader from "@/components/basic/PageHeader";
import GuideFilterModal from "@/components/guide/GuideFilterModal";
import GuidePreviewCard from "@/components/guide/GuidePreviewCard";
import GuideList from "@/components/guide/list/GuideList";
import colours from "@/theme/colours";
import size from "@/theme/size";
import spacing from "@/theme/spacing";
import { FontAwesome5 } from "@expo/vector-icons";
import React, { useState } from "react";
import { View, StyleSheet } from "react-native";
import { Button } from "react-native-paper";

export default function GuideScreen() {
  const [modalVisible, setModalVisible] = useState(false);
  const [guides, setGuides] = useState<
    RequestList<GuidePreview> | undefined
  >(undefined); 
  const [loading, setLoading] = useState(false)

  const handleSearch = async (params: CheckupQueryParams) => {
    setLoading(true)
    try {
      const data = await guideApi.getGuides(params);
      setGuides(data);
      setModalVisible(false);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false)
    }
  };

  return (
    <>
      <BaseComponent isLoading={false} title={"Guides"}>
        <PageHeader
          title={"Guides"}
          description={"Read up on the latest curated guides for your furry friend"}
          buttons={[
            {
              name: "New Guide",
              icon: 'plus',
              operation: () => {},
            },
          ]}
        />

        {guides?.elements && <GuideList guides={guides?.elements}/>}

        <Button onPress={() => setModalVisible(true)} style={style.filter}>
          <FontAwesome5 name="filter" size={size.icon.md} color="white" />
        </Button>

        <GuideFilterModal
          visible={modalVisible}
          onDismiss={() => setModalVisible(false)}
          onSearch={async (params: GuideQueryParams) => handleSearch(params)}
        />
      </BaseComponent>
    </>
  );
}

const style = StyleSheet.create({
  guideContainer: {
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
