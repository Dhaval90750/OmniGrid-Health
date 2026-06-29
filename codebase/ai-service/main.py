from fastapi import FastAPI
from pydantic import BaseModel
import spacy
from typing import List, Dict
import pandas as pd
from sklearn.linear_model import LogisticRegression
import numpy as np

app = FastAPI(title="MedCore AI Service", version="1.0")

# Load Spacy Model (Lightweight English model)
try:
    nlp = spacy.load("en_core_web_sm")
except OSError:
    print("Downloading language model for the spacy POS tagger")
    from spacy.cli import download
    download("en_core_web_sm")
    nlp = spacy.load("en_core_web_sm")


# ---------------------------------------------------------
# Sepsis Prediction Model Training (Synthetic on startup)
# ---------------------------------------------------------
def train_sepsis_model():
    # Generate synthetic data for Sepsis prediction
    # Features: [HeartRate, SystolicBP, Temperature, SpO2]
    np.random.seed(42)
    # Normal vitals (Class 0)
    normal_hr = np.random.normal(75, 10, 500)
    normal_sbp = np.random.normal(120, 15, 500)
    normal_temp = np.random.normal(37.0, 0.4, 500)
    normal_spo2 = np.random.normal(98, 1, 500)
    
    # Sepsis vitals (Class 1) - Tachycardia, Hypotension, Fever/Hypothermia, Hypoxia
    sepsis_hr = np.random.normal(110, 15, 500)
    sepsis_sbp = np.random.normal(90, 15, 500)
    sepsis_temp = np.random.normal(38.5, 0.8, 500)
    sepsis_spo2 = np.random.normal(92, 3, 500)

    X_normal = np.column_stack((normal_hr, normal_sbp, normal_temp, normal_spo2))
    y_normal = np.zeros(500)

    X_sepsis = np.column_stack((sepsis_hr, sepsis_sbp, sepsis_temp, sepsis_spo2))
    y_sepsis = np.ones(500)

    X = np.vstack((X_normal, X_sepsis))
    y = np.concatenate((y_normal, y_sepsis))

    model = LogisticRegression()
    model.fit(X, y)
    return model

sepsis_model = train_sepsis_model()

# ---------------------------------------------------------
# Request Models
# ---------------------------------------------------------
class ScribeRequest(BaseModel):
    text: str

class VitalsRequest(BaseModel):
    heartRate: float
    systolicBp: float
    temperature: float
    spo2: float

# ---------------------------------------------------------
# API Endpoints
# ---------------------------------------------------------

@app.get("/health")
def health_check():
    return {"status": "ok", "message": "AI Service is running"}

@app.post("/extract")
def extract_entities(request: ScribeRequest):
    """
    Extracts basic medical entities (heuristic-based using base Spacy POS/NER)
    """
    doc = nlp(request.text)
    
    symptoms = []
    medications = []
    diagnoses = []
    
    # A very basic heuristic extractor using base Spacy models since we aren't using specialized SciSpacy for performance/size reasons.
    # In a real production system, this would use SciSpacy (en_core_sci_sm)
    
    for ent in doc.ents:
        # Just map base entities to our categories for demonstration
        if ent.label_ in ["DISEASE", "SYMPTOM", "ORG"]: 
            symptoms.append(ent.text)
        elif ent.label_ in ["CHEMICAL", "PRODUCT"]:
            medications.append(ent.text)
            
    # Fallback to noun chunks if NER misses
    for chunk in doc.noun_chunks:
        text_lower = chunk.text.lower()
        if any(word in text_lower for word in ["pain", "ache", "fever", "cough", "nausea"]):
            if chunk.text not in symptoms:
                symptoms.append(chunk.text)
        if any(word in text_lower for word in ["mg", "tablet", "syrup", "ml", "dose", "paracetamol", "aspirin"]):
            if chunk.text not in medications:
                medications.append(chunk.text)
        if any(word in text_lower for word in ["disease", "infection", "syndrome", "disorder", "diabetes", "hypertension"]):
            if chunk.text not in diagnoses:
                diagnoses.append(chunk.text)
                
    # Deduplicate
    return {
        "symptoms": list(set(symptoms)),
        "medications": list(set(medications)),
        "diagnoses": list(set(diagnoses))
    }

@app.post("/predict/sepsis")
def predict_sepsis(request: VitalsRequest):
    """
    Predicts sepsis risk based on vitals using the locally trained scikit-learn model.
    """
    # Features: [HeartRate, SystolicBP, Temperature, SpO2]
    features = np.array([[request.heartRate, request.systolicBp, request.temperature, request.spo2]])
    
    # Get probability of class 1 (Sepsis)
    prob = sepsis_model.predict_proba(features)[0][1]
    
    risk_level = "LOW"
    if prob > 0.7:
        risk_level = "CRITICAL"
    elif prob > 0.4:
        risk_level = "HIGH"
    elif prob > 0.2:
        risk_level = "MODERATE"
        
    return {
        "sepsisRiskScore": round(float(prob), 4),
        "riskLevel": risk_level,
        "contributingFactors": [
            "Tachycardia" if request.heartRate > 100 else "",
            "Hypotension" if request.systolicBp < 100 else "",
            "Fever/Hypothermia" if request.temperature > 38.0 or request.temperature < 36.0 else "",
            "Hypoxia" if request.spo2 < 95 else ""
        ]
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
