import { ApiPaths } from "@/api/Path";
import api from "@/lib/axios";

async function getSupply(
  id: number
): Promise<GuideInformation> {
  const response = await api.get(ApiPaths.supplies.get_supply(id));
  return response.data;
}

async function getSupplies(
  queryParams: SupplyQueryParams = {},
): Promise<RequestList<GuidePreview>> {
  const response = await api.get(ApiPaths.supplies.get_all, {
    params: queryParams,
  });
  return response.data;
}

async function getClinicSupplies(
  id: number,
  queryParams: ClinicSupplyQueryParams = {},
): Promise<RequestList<GuidePreview>> {
  const response = await api.get(ApiPaths.supplies.get_clinic_supplies(id), {
    params: queryParams,
  });
  return response.data;
}

async function updateSupply(
  clinicId: number,
  supplyId: number,
  input: SupplyUpdate
): Promise<void> {
  const response = await api.put(ApiPaths.supplies.update(clinicId, supplyId), input)
  return response.data
}

async function deleteSupply(
  clinicId: number,
  supplyId: number
): Promise<void> {
  const response = await api.delete(ApiPaths.supplies.delete(clinicId, supplyId))
  return response.data
}

export default { getSupply, getSupplies, getClinicSupplies, updateSupply, deleteSupply };
