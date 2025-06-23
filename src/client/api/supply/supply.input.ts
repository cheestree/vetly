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
  PILL = "PILL",
  LIQUID = "LIQUID",
  SHOT = "SHOT",
  MISC = "MISC",
}

export { ClinicSupplyQueryParams, SupplyQueryParams, SupplyType, SupplyUpdate };
