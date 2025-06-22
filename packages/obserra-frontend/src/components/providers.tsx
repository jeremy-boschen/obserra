import React from "react";
import { ThemeProvider } from "@/components/theme-provider";
import { Toaster } from "@/components/ui/toaster";
import { TooltipProvider } from "@/components/ui/tooltip";
import { Provider as UrqlClientProvider } from "urql";
import { client } from "@/graphql/client.ts";

interface ProvidersProps {
  children: React.ReactNode;
}

export function Providers({ children }: ProvidersProps) {
  return (
    <UrqlClientProvider value={client}>
      <ThemeProvider defaultTheme="system" storageKey="theme">
        <TooltipProvider>
          <Toaster />
          {children}
        </TooltipProvider>
      </ThemeProvider>
    </UrqlClientProvider>
  );
}
