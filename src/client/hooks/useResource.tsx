import { toAppError } from "@/lib/apiError";
import { type Href, router } from "expo-router";
import { useCallback, useEffect, useRef, useState } from "react";
import { useAuthState } from "./useAuth";

type UseResourceOptions = {
  redirectBasePath?: string;
  onStatusRedirect?: (status: number) => void;
  enabled?: boolean;
};

export function useResource<T>(
  fetcher: () => Promise<T>,
  options: UseResourceOptions = {},
) {
  const {
    enabled = true,
    onStatusRedirect,
    redirectBasePath,
  } = options;
  const [data, setData] = useState<T | undefined>(undefined);
  const [error, setError] = useState<Error | undefined>(undefined);
  const [loading, setLoading] = useState(true);
  const { loading: authLoading } = useAuthState();
  const latestRequestId = useRef(0);
  const isEnabled = !authLoading && enabled;

  const executeRequest = useCallback(async () => {
    if (!isEnabled) {
      setLoading(authLoading);
      return;
    }

    const requestId = latestRequestId.current + 1;
    latestRequestId.current = requestId;
    setLoading(true);

    try {
      const result = await fetcher();

      if (latestRequestId.current === requestId) {
        setData(result);
        setError(undefined);
        setLoading(false);
      }

      return result;
    } catch (err) {
      const requestError = toAppError(err);
      const status = requestError.status;

      if (latestRequestId.current === requestId) {
        setError(requestError);
        setLoading(false);
      }

      if (status) {
        onStatusRedirect?.(status);

        if (redirectBasePath) {
          const href = {
            pathname: `${redirectBasePath.replace(/\/$/, "")}/${status}`,
            params: { message: requestError.message },
          } as Href;

          router.replace(href);
        }
      }

      return undefined;
    }
  }, [authLoading, fetcher, isEnabled, onStatusRedirect, redirectBasePath]);

  useEffect(() => {
    void executeRequest();
  }, [executeRequest]);

  const refetch = useCallback(() => {
    return executeRequest();
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
