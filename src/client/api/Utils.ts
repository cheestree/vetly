type QueryParams = Record<string, string | number | boolean | null | undefined>;

function buildQueryParams(params: QueryParams): string {
  const query = new URLSearchParams();

  for (const [key, value] of Object.entries(params)) {
    if (value !== null && value !== undefined) {
      query.append(key, String(value));
    }
  }

  const queryString = query.toString();
  return queryString ? `?${queryString}` : "";
}

function buildFetchOptions(
  method: string,
  body?: Record<string, unknown>,
  token?: string,
): RequestInit {
  const headers: HeadersInit = {
    "Content-Type": "application/json",
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  return {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  };
}

export { buildQueryParams, buildFetchOptions };
