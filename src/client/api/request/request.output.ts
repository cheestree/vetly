type RequestPreview = {
  id: string;
  user: UserPreview;
  target: string;
  action: string;
  status: string;
  justification?: string;
  createdAt: string;
};

type RequestInformation = {
  id: string;
  user: UserInformation;
  target: string;
  action: string;
  status: string;
  justification?: string;
  files: string[];
  extraData: JSON;
  createdAt: string;
};
