from fastapi import FastAPI
from pydantic import BaseModel
import spacy
from typing import List, Dict
import pandas as pd
from sklearn.linear_model import LogisticRegression
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
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
# Readmission Risk Model Training (Synthetic)
# ---------------------------------------------------------
def train_readmission_model():
    # Features: [Age, LengthOfStay, IsEmergency, ComorbiditiesCount]
    np.random.seed(42)
    # Low Risk (Class 0)
    low_age = np.random.normal(40, 15, 500)
    low_los = np.random.normal(2, 1, 500)
    low_emer = np.zeros(500) # Mostly non-emergency
    low_comorb = np.random.poisson(0.5, 500)
    
    # High Risk (Class 1)
    high_age = np.random.normal(70, 10, 500)
    high_los = np.random.normal(7, 3, 500)
    high_emer = np.ones(500)
    high_comorb = np.random.poisson(3, 500)

    X_low = np.column_stack((low_age, low_los, low_emer, low_comorb))
    y_low = np.zeros(500)

    X_high = np.column_stack((high_age, high_los, high_emer, high_comorb))
    y_high = np.ones(500)

    X = np.vstack((X_low, X_high))
    y = np.concatenate((y_low, y_high))

    model = LogisticRegression()
    model.fit(X, y)
    return model

readmission_model = train_readmission_model()

# ---------------------------------------------------------
# ICD-10 Semantic Database (TF-IDF setup)
# ---------------------------------------------------------
icd_catalog = [
    {"code": "E11.9", "desc": "Type 2 diabetes mellitus without complications"},
    {"code": "I10", "desc": "Essential (primary) hypertension"},
    {"code": "J45.909", "desc": "Unspecified asthma, uncomplicated"},
    {"code": "I21.9", "desc": "Acute myocardial infarction, unspecified"},
    {"code": "J01.90", "desc": "Acute sinusitis, unspecified"},
    {"code": "K21.9", "desc": "Gastro-esophageal reflux disease without esophagitis"},
    {"code": "M54.5", "desc": "Low back pain"},
    {"code": "N39.0", "desc": "Urinary tract infection, site not specified"},
    {"code": "R07.9", "desc": "Chest pain, unspecified"},
    {"code": "R50.9", "desc": "Fever, unspecified"},
    {"code": "A09", "desc": "Infectious gastroenteritis and colitis, unspecified"}
]
icd_descriptions = [item["desc"] for item in icd_catalog]
tfidf_vectorizer = TfidfVectorizer(stop_words='english')
tfidf_matrix = tfidf_vectorizer.fit_transform(icd_descriptions)

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

class ReadmissionRequest(BaseModel):
    patientAge: int
    lengthOfStayDays: int
    isEmergency: bool
    comorbiditiesCount: int

@app.post("/predict/readmission")
def predict_readmission(request: ReadmissionRequest):
    features = np.array([[request.patientAge, request.lengthOfStayDays, int(request.isEmergency), request.comorbiditiesCount]])
    prob = readmission_model.predict_proba(features)[0][1]
    
    risk_level = "LOW"
    if prob > 0.6:
        risk_level = "HIGH"
    elif prob > 0.3:
        risk_level = "MODERATE"
        
    return {
        "readmissionRiskScore": round(float(prob), 4),
        "riskLevel": risk_level
    }

class IcdRequest(BaseModel):
    clinicalText: str

@app.post("/icd-suggest")
def suggest_icd(request: IcdRequest):
    query_vec = tfidf_vectorizer.transform([request.clinicalText])
    cosine_similarities = cosine_similarity(query_vec, tfidf_matrix).flatten()
    
    # Get top 3 matches
    related_docs_indices = cosine_similarities.argsort()[:-4:-1]
    
    suggestions = []
    for i in related_docs_indices:
        score = cosine_similarities[i]
        if score > 0.1: # Only suggest if there is some relevance
            suggestions.append({
                "code": icd_catalog[i]["code"],
                "description": icd_catalog[i]["desc"],
                "confidence": round(float(score), 2)
            })
            
    if not suggestions:
        suggestions.append({"code": "R69", "description": "Illness, unspecified", "confidence": 0.50})
        
    return {"suggestions": suggestions}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
