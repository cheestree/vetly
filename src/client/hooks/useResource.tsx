import { useCallback, useEffect, useState } from "react";
import { useAuth } from "./useAuth";

type UseResourceOptions = {
  redirectBasePath?: string;
  onStatusRedirect?: (status: number) => void;
  enabled?: boolean; // Keep this for manual control
};

export function useResource<T>(
  fetcher: () => Promise<T>,
  options: UseResourceOptions = {},
) {
  const [data, setData] = useState<T | null>(null);
  const [error, setError] = useState<Error | null>(null);
  const [loading, setLoading] = useState(true);
  const { loading: authLoading } = useAuth();
  const isEnabled = !authLoading && options.enabled !== false;

  const executeRequest = useCallback(() => {
    if (!isEnabled) {
      setLoading(authLoading);
      return;
    }

    let isMounted = true;
    setLoading(true);

    fetcher()
      .then((result) => {
        if (isMounted) {
          setData(result);
          setError(null);
          setLoading(false);
        }
      })
      .catch((err) => {
        if (isMounted) {
          setError(err);
          setLoading(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [fetcher, isEnabled, authLoading]);

  useEffect(() => {
    const cleanup = executeRequest();
    return cleanup;
  }, [executeRequest]);

  const refetch = useCallback(() => {
    executeRequest();
  }, [executeRequest]);

  return { data, error, loading, refetch };
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
