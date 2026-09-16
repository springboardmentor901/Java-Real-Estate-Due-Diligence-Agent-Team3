export type Role =
  | "BUYER"
  | "REAL_ESTATE_AGENT"
  | "LEGAL_REVIEWER"
  | "FINANCIAL_INSTITUTION"
  | "ADMINISTRATOR";

export type User = {
  id: number | string;
  fullName: string;
  email: string;
  role: Role;
  token?: string;
  createdAt?: string;
};

export type Property = {
  id: number;
  address: string;
  city?: string;
  state?: string;
  postalCode?: string;
  latitude?: number;
  longitude?: number;
  floodZone?: string;
  floodRiskRating?: string;
  zoningCompliance?: string;
  landUse?: string;
  setbackRequirements?: string;
  createdAt?: string;
};

export type SectionResult<T = unknown> = {
  status?: string;
  data?: T;
  error?: string;
};

export type DueDiligence = {
  propertyId: number;
  ownership?: SectionResult;
  taxHistory?: SectionResult;
  zoning?: SectionResult;
  floodZone?: SectionResult;
  permits?: SectionResult;
  environmental?: SectionResult;
  utilities?: SectionResult;
};

export type TimelineEntry = { date?: string; label: string; description?: string };
export type RiskAssessment = { id?: number; category?: string; indicator?: string; score?: number; notes?: string };
export type ReportStatus = "REQUESTED" | "IN_PROGRESS" | "COMPLETED" | "FAILED";
export type Report = {
  id: number;
  propertyId: number;
  propertyAddress?: string;
  requestedBy: number;
  status: ReportStatus;
  createdAt?: string;
  riskScore?: number;
  executiveSummary?: string;
  propertyTimeline?: TimelineEntry[];
  riskAssessments?: RiskAssessment[];
};

export type Notification = {
  id: string | number;
  title: string;
  message: string;
  read?: boolean;
  createdAt?: string;
  href?: string;
};

export type Utility = { id: number; utilityType: string; provider?: string; accountReference?: string; status?: string };
