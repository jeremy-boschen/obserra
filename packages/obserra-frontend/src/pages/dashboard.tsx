import { useState } from "react";
import { Navbar } from "@/components/layout/navbar";
import { Sidebar } from "@/components/layout/sidebar";
import { useLocation } from "wouter";
import { ServicesProvider, useServicesContext } from "@/contexts/services-context.tsx";
import { ServiceDetail } from "@/components/services/service-detail";
import { ServiceCards } from "@/components/services/service-cards.tsx";

function View() {
  const { activeServiceId } = useServicesContext();

  return <div>{activeServiceId ? <ServiceDetail /> : <ServiceCards />}</div>;
}

export default function Dashboard() {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [, setLocation] = useLocation();

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  return (
    <ServicesProvider>
      <div className="min-h-screen antialiased bg-gray-50 text-gray-800 dark:bg-gray-900 dark:text-gray-200">
        <Navbar toggleSidebar={toggleSidebar} />

        <Sidebar sidebarOpen={sidebarOpen} />

        <main className={`pt-16 transition-all duration-300 ${sidebarOpen ? "ml-64" : "ml-0"}`}>
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
            <View />
          </div>
        </main>
      </div>
    </ServicesProvider>
  );
}
