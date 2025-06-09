type SupplyUpdate = {
    price?: number;
    quantity?: number;
}

type SupplyQueryParams = {
    name?: string;
    type?: SupplyType;
    page?: number;
    limit?: number;
    sortBy?: string;
    sortOrder?: "asc" | "desc";
};

type ClinicSupplyQueryParams = SupplyQueryParams & {
    clinicId?: number;
};

enum SupplyType {
    PILL,
    LIQUID,
    SHOT,
    MISC
}