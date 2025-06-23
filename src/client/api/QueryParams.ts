type QueryParams = {
  page?: number;
  limit?: number;
  sortBy?: string;
  sortOrder?: SortOrder;
};

enum SortOrder {
  ASC = "ASC",
  DESC = "DESC",
}

export { QueryParams, SortOrder };
