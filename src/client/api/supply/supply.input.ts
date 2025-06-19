type SupplyUpdate = {
  price?: number;
  quantity?: number;
};

type SupplyQueryParams = QueryParams & {
  name?: string;
  type?: SupplyType;
};

type ClinicSupplyQueryParams = SupplyQueryParams & {
  clinicId?: number;
};

enum SupplyType {
  PILL,
  LIQUID,
  SHOT,
  MISC,
}

export { ClinicSupplyQueryParams, SupplyQueryParams, SupplyType, SupplyUpdate };
