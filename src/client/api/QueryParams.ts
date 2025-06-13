type QueryParams = {
  page?: number;
  limit?: number;
  sortBy?: string;
  sortOrder?: SortOrder;
};

enum SortOrder {
  ASC,
  DESC,
}
