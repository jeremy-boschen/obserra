import { Button } from "@/components/ui/button.tsx";
import { ChevronDown, ChevronUp, Link, RefreshCw } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card.tsx";
import React, { useCallback } from "react";
import { useToast } from "@/hooks/use-toast";
import { ServiceInfo } from "@/graphql/generated.ts";
import { Collapsible } from "@radix-ui/react-collapsible";
import { CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible.tsx";
import { useLocation } from "wouter";

export interface SectionProps {
  id: string;
  title: string;
  description: string;
  className?: string;

  open: boolean;
  setOpen: (open: boolean) => void;
  service: ServiceInfo;

  children: React.ReactNode;
}

export function Section({
  id,
  title,
  description,
  className = "px-0 py-0",
  open,
  setOpen,
  service,
  children,
}: SectionProps): React.ReactNode {
  const { toast } = useToast();

  const copyLink = useCallback(() => {
    const url = `${window.location.origin}/service/${service.id}/${id}`;
    navigator.clipboard
      .writeText(url)
      .then(() => {
        toast({
          title: "Link copied to clipboard",
          description: `${title} section link copied.`,
        });
      })
      .catch((err) => {
        console.error(`Failed to copy section link ${url}`, err);
        toast({
          title: "Failed to copy link",
          description: "Please try again or copy the URL manually.",
          variant: "destructive",
        });
      });
  }, [service.id, id, title, toast]);

  return (
    <>
      <Collapsible open={open} onOpenChange={setOpen} className="w-full relative" id={id}>
        <Card>
          <CardHeader className="px-6 py-5 flex flex-row items-center justify-between">
            <div>
              <div className="flex items-center">
                <Button
                  variant="ghost"
                  size="sm"
                  className="absolute top-0 right-0 mt-2 mr-2 h-8 w-8 p-0 text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-300"
                  title="Copy link to this section"
                  onClick={copyLink}
                >
                  <Link className="h-4 w-4" />
                </Button>

                <CardTitle className="text-lg leading-6 font-medium">{title}</CardTitle>

                <CollapsibleTrigger asChild>
                  <Button variant="ghost" size="sm" className="ml-2 h-8 w-8 p-0">
                    {open ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />}
                  </Button>
                </CollapsibleTrigger>
              </div>
              <p className="mt-1 max-w-2xl text-sm text-gray-500 dark:text-gray-400">
                {description}
              </p>
            </div>
            {/*<div className="flex space-x-2">*/}
            {/*  <Button*/}
            {/*    size="sm"*/}
            {/*    className="inline-flex items-center text-white bg-primary-600 hover:bg-primary-700"*/}
            {/*    onClick={async () => {*/}
            {/*      try {*/}
            {/*        // Show loading state*/}
            {/*        toast({*/}
            {/*          title: "Restarting service...",*/}
            {/*          description: "This may take a few moments.",*/}
            {/*        });*/}
            {/*        // Call the restart function*/}
            {/*        const result = await restartService();*/}
            {/*        // Show success message*/}
            {/*        toast({*/}
            {/*          title: "Service restarted successfully",*/}
            {/*          description:*/}
            {/*            result?.message ||*/}
            {/*            "The service is being restarted. Metrics and logs will refresh shortly.",*/}
            {/*          variant: "default",*/}
            {/*        });*/}
            {/*      } catch (error) {*/}
            {/*        // Show error message*/}
            {/*        toast({*/}
            {/*          title: "Failed to restart service",*/}
            {/*          description:*/}
            {/*            error instanceof Error ? error.message : "An unexpected error occurred",*/}
            {/*          variant: "destructive",*/}
            {/*        });*/}
            {/*      }*/}
            {/*    }}*/}
            {/*  >*/}
            {/*    <RefreshCw className="h-4 w-4 mr-1"/>*/}
            {/*    Restart*/}
            {/*  </Button>*/}
            {/*</div>*/}
          </CardHeader>

          <CollapsibleContent>
            <CardContent className={className}>{children}</CardContent>
          </CollapsibleContent>
        </Card>
      </Collapsible>
    </>
  );
}
