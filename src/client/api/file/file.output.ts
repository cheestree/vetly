type FileInformation = {
  id: string;
  url: string;
  name?: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
};

type FilePreview = {
  id: string;
  url: string;
};

export { FileInformation, FilePreview };

