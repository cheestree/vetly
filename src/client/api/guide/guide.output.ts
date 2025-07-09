import { UserPreview } from "../user/user.output";

type GuidePreview = {
  id: number;
  title: string;
  imageUrl?: string;
  description: string;
  author: UserPreview;
  createdAt: string;
  updatedAt: string;
};

type GuideInformation = {
  id: number;
  title: string;
  imageUrl?: string;
  fileUrl?: string;
  description: string;
  content: string;
  author: UserPreview;
  createdAt: string;
  updatedAt: string;
};

export { GuideInformation, GuidePreview };
