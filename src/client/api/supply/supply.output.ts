type SupplyPreview = {
  id: number;
  name: string;
  type: SupplyType;
};

type SupplyInformation = {
  id: number;
  name: string;
  description?: string;
  imageUrl?: string;
};

type ShotSupplyInformation = SupplyInformation & {
  vialsPerBox: number;
  mlPerVial: number;
};

type LiquidSupplyInformation = SupplyInformation & {
  mlPerBottle: number;
  mlDosePerUse: number;
};

type PillSupplyInformation = SupplyInformation & {
  pillsPerBox: number;
  mgPerPill: number;
};

export {
  LiquidSupplyInformation,
  PillSupplyInformation,
  ShotSupplyInformation,
  SupplyInformation,
  SupplyPreview,
};
