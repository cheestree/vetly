import { QueryParams } from "../QueryParams";

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
  content?: string;
  file?: File;
};

type GuideQueryParams = QueryParams & {
  title?: string;
};

export { GuideCreate, GuideQueryParams, GuideUpdate };
