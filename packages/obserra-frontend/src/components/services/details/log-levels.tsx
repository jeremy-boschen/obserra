import React from "react";
import { Section, SectionProps } from "@/components/services/details/section.tsx";

interface LogLevelsProps extends Pick<SectionProps, "open" | "setOpen" | "service"> {}

export function LogLevels({ open, setOpen, service }: LogLevelsProps) {
  return (
    <>
      <Section
        id="loglevels"
        open={open}
        setOpen={setOpen}
        service={service}
        title="Log Level Management"
        description="Configure logging levels for this service."
      >
        <dl>
          <div className="bg-gray-50 dark:bg-gray-900 px-6 py-3 grid grid-cols-3 gap-4">
            <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">
              Service Name/ID
            </dt>
            <dd className="text-sm text-gray-900 dark:text-gray-300 col-span-2">
              {service.name}/{service.id}
            </dd>
          </div>
          <div className="bg-white dark:bg-gray-800 px-6 py-3 grid grid-cols-3 gap-4">
            <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">Version</dt>
            <dd className="text-sm text-gray-900 dark:text-gray-300 col-span-2">
              v{service.version}
            </dd>
          </div>
        </dl>
      </Section>
    </>
  );
}
