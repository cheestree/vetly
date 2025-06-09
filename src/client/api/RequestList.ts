type RequestList<Type> = {
  elements: Type[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
  hasNext: boolean;
  hasPrevious: boolean;
};
