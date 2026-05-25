import { useLocalSearchParams } from "expo-router";

function firstParamValue(value: string | string[] | undefined) {
  return Array.isArray(value) ? value[0] : value;
}

function useRequiredRouteParam(name: string) {
  const params = useLocalSearchParams();
  const value = firstParamValue(params[name]);

  if (!value) {
    throw new Error(`Missing route param: ${name}`);
  }

  return value;
}

function useNumericRouteParam(name: string) {
  const value = useRequiredRouteParam(name);
  const numericValue = Number(value);

  if (!Number.isFinite(numericValue)) {
    throw new Error(`Invalid numeric route param: ${name}`);
  }

  return numericValue;
}

export { useNumericRouteParam, useRequiredRouteParam };
