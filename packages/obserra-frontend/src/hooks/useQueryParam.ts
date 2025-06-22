import { useState, useEffect } from "react";

export function useQueryParam(key: string): string | null {
  // read the current value once
  const getParam = () => new URLSearchParams(window.location.search).get(key);

  const [value, setValue] = useState<string | null>(getParam);

  useEffect(() => {
    const onPop = () => setValue(getParam());
    window.addEventListener("popstate", onPop);
    return () => window.removeEventListener("popstate", onPop);
  }, [key]);

  return value;
}
