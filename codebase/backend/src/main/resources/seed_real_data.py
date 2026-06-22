import urllib.request
import json
import csv
import ssl

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

# 1. Fetch real ICD-10 from NIH API
print("Fetching real ICD-10 codes from NIH API...")
icd_codes = []
# We'll fetch the first 500 codes
url = "https://clinicaltables.nlm.nih.gov/api/icd10cm/v3/search?terms=a&sf=code,name&maxList=500"
try:
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    response = urllib.request.urlopen(req, context=ctx)
    data = json.loads(response.read().decode('utf-8'))
    # data format: [count, [codes...], None, [[code, description]...]]
    results = data[3]
    for row in results:
        icd_codes.append([row[0], row[1], "General"])
except Exception as e:
    print(f"Error fetching NIH ICD-10: {e}")
    # Fallback to some common ones if network fails
    icd_codes = [["J45.909", "Unspecified asthma, uncomplicated", "Respiratory"]]

with open("icd10.csv", "w", newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(["code", "description", "category"])
    writer.writerows(icd_codes)
print(f"Saved {len(icd_codes)} ICD-10 codes to icd10.csv")

# 2. Fetch real Drug dataset from OpenFDA (Generics/Brands)
print("Fetching real Drugs from OpenFDA API...")
drug_list = []
drug_url = "https://api.fda.gov/drug/ndc.json?limit=1000"
try:
    req = urllib.request.Request(drug_url, headers={'User-Agent': 'Mozilla/5.0'})
    response = urllib.request.urlopen(req, context=ctx)
    data = json.loads(response.read().decode('utf-8'))
    for item in data.get("results", []):
        generic_name = item.get("generic_name", "Unknown")
        brand_name = item.get("brand_name", generic_name)
        dosage_form = item.get("dosage_form", "Tablet")
        
        active_ingredients = item.get("active_ingredients", [])
        strength = active_ingredients[0].get("strength", "N/A") if active_ingredients else "N/A"
        
        drug_list.append([generic_name, brand_name, dosage_form, strength, "General"])
except Exception as e:
    print(f"Error fetching FDA Drugs: {e}")

with open("drugs.csv", "w", newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(["genericName", "brandName", "dosageForm", "strength", "classification"])
    writer.writerows(drug_list)
print(f"Saved {len(drug_list)} Drugs to drugs.csv")
