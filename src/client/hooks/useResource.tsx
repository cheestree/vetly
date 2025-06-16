import { useRouter } from "expo-router";
import { useEffect, useState } from "react";

type UseResourceOptions = {
  redirectBasePath?: string;
  onStatusRedirect?: (status: number) => void;
};

export function useResource<T>(
  fetchFn: () => Promise<T | undefined>,
  deps: any[] = [],
  options?: UseResourceOptions,
) {
  const router = useRouter();
  const [data, setData] = useState<T | undefined>(undefined);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let isCancelled = false;

    const fetchData = async () => {
      try {
        const result = await fetchFn();
        if (!result) {
          if (!isCancelled) {
            router.replace({
              pathname: "/error/[code]",
              params: { code: "404", message: "Resource not found" },
            });
          }
        } else if (!isCancelled) {
          setData(result);
        }
      } catch (err: any) {
        if (isCancelled) return;

        const status = err?.response?.status;

        if (status === 401) {
          router.replace({
            pathname: "/error/[code]",
            params: { code: "401", message: "Unauthorized access" },
          });
        } else {
          router.replace({
            pathname: "/error/[code]",
            params: { code: "500", message: "Unexpected error occurred" },
          });
        }

        options?.onStatusRedirect?.(status);
      } finally {
        if (!isCancelled) setLoading(false);
      }
    };

    fetchData();

    return () => {
      isCancelled = true;
    };
  }, [fetchFn, options, router]);

  return { data, loading };
}

export function useOptionalResource<T>(
  fetchFn: () => Promise<T | undefined>,
  deps: any[] = [],
  shouldFetch: boolean = true,
) {
  const [data, setData] = useState<T | undefined>(undefined);
  const [loading, setLoading] = useState(shouldFetch);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!shouldFetch) {
      setLoading(false);
      return;
    }

    let isCancelled = false;
    const fetchData = async () => {
      try {
        setError(null);
        const result = await fetchFn();
        if (!isCancelled) {
          setData(result);
        }
      } catch (err: any) {
        if (!isCancelled) {
          setError(err.message || "An error occurred");
        }
      } finally {
        if (!isCancelled) setLoading(false);
      }
    };

    fetchData();
    return () => {
      isCancelled = true;
    };
  }, [...deps, shouldFetch]);

  return { data, loading, error };
}
