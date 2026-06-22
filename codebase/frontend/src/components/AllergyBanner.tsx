"use client";

import { useEffect, useState } from "react";
import { api } from "@/lib/api";
import { AlertTriangle } from "lucide-react";

interface AllergyBannerProps {
  patientId: string;
}

export function AllergyBanner({ patientId }: AllergyBannerProps) {
  const [allergies, setAllergies] = useState<any[]>([]);

  useEffect(() => {
    if (patientId) {
      api.get(`/allergies/patient/${patientId}`)
        .then((res) => {
          setAllergies(res.data);
        })
        .catch((err) => console.error("Failed to fetch allergies", err));
    }
  }, [patientId]);

  if (allergies.length === 0) return null;

  return (
    <div className="bg-error/10 border border-error/20 text-error p-3 rounded-md flex items-start gap-3 w-full mb-4">
      <AlertTriangle className="h-5 w-5 mt-0.5 shrink-0" />
      <div>
        <h4 className="font-semibold text-sm">Critical Patient Allergies</h4>
        <div className="text-sm mt-1">
          {allergies.map((allergy, idx) => (
            <span key={allergy.id}>
              <span className="font-medium">{allergy.allergen}</span> 
              <span className="opacity-80"> ({allergy.severity})</span>
              {idx < allergies.length - 1 ? ", " : ""}
            </span>
          ))}
        </div>
      </div>
    </div>
  );
}
