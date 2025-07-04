import { QueryParams } from "../QueryParams";

type GuideCreate = {
  title: string;
  description: string;
  content: string;
};

type GuideUpdate = {
  title?: string;
  description?: string;
  content?: string;
};

type GuideQueryParams = QueryParams & {
  title?: string;
};

export { GuideCreate, GuideQueryParams, GuideUpdate };
