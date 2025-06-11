type GuideCreate = {
  title: string;
  description: string;
  imageUrl?: string;
  content: string;
};

type GuideUpdate = {
  title?: string;
  description?: string;
  imageUrl?: string;
  content: string;
};

type GuideQueryParams = QueryParams & {
  title?: string;
};
