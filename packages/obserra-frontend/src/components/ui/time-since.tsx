import React, { useState, useEffect } from "react";

interface TimeSinceProps {
  value: Date;
  interval?: number;
}

export function TimeSince({ value, interval = 1_000 }: TimeSinceProps) {
  const [now, setNow] = useState(Date.now());

  // 1) install your interval once on mount
  useEffect(() => {
    // kick off an immediate update
    setNow(Date.now());

    const id = setInterval(() => {
      setNow(Date.now());
    }, interval);

    return () => clearInterval(id);
    // empty deps → never re-run, so `interval` is fixed
  }, []);

  // 2) compute elapsed
  const secondsElapsed = Math.max(0, Math.floor((now - value.getTime()) / 1000));

  let displayValue: number;
  let unit: "s" | "m" | "h";

  if (secondsElapsed < 60) {
    displayValue = secondsElapsed;
    unit = "s";
  } else if (secondsElapsed < 3600) {
    displayValue = Math.floor(secondsElapsed / 60);
    unit = "m";
  } else {
    displayValue = Math.floor(secondsElapsed / 3600);
    unit = "h";
  }

  return (
    <span>
      {displayValue}
      {unit}
    </span>
  );
}
