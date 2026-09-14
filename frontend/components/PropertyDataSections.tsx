import PropertySection from "./PropertySection";
import type { ComponentProps, ReactNode } from "react";

function DataSection({ title, children, error, loading }: { title: string; children: ReactNode; error?: string; loading?: boolean }) {
  return <PropertySection title={title} error={error} loading={loading}>{children}</PropertySection>;
}

export const OwnershipSection = (props: Omit<ComponentProps<typeof DataSection>, "title">) => <DataSection title="Ownership" {...props} />;
export const TaxHistorySection = (props: Omit<ComponentProps<typeof DataSection>, "title">) => <DataSection title="Tax history" {...props} />;
export const ZoningSection = (props: Omit<ComponentProps<typeof DataSection>, "title">) => <DataSection title="Zoning" {...props} />;
export const FloodZoneSection = (props: Omit<ComponentProps<typeof DataSection>, "title">) => <DataSection title="Flood zone" {...props} />;
export const PermitsSection = (props: Omit<ComponentProps<typeof DataSection>, "title">) => <DataSection title="Permits" {...props} />;
export const EnvironmentalSection = (props: Omit<ComponentProps<typeof DataSection>, "title">) => <DataSection title="Environmental" {...props} />;
export const UtilitiesSection = (props: Omit<ComponentProps<typeof DataSection>, "title">) => <DataSection title="Utilities" {...props} />;
export const PropertyTimeline = (props: Omit<ComponentProps<typeof DataSection>, "title">) => <DataSection title="Property timeline" {...props} />;
