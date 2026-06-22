import urllib.request
import os
import json

base_dir = r"e:\Personal\PMS\codebase\backend\src\main\resources"

print("Downloading ICD-10 dataset...")
try:
    urllib.request.urlretrieve("https://raw.githubusercontent.com/kamillamamedova/icd10/master/icd10.csv", os.path.join(base_dir, "icd10.csv"))
    print("ICD-10 downloaded.")
except Exception as e:
    print(f"Failed ICD-10 URL 1: {e}")
    try:
        urllib.request.urlretrieve("https://raw.githubusercontent.com/k4m1113/ICD-10-CSV/master/icd10.csv", os.path.join(base_dir, "icd10.csv"))
        print("ICD-10 downloaded from fallback.")
    except Exception as e2:
        print(f"Failed ICD-10 URL 2: {e2}")

print("Downloading Drugs dataset...")
try:
    # A known Kaggle dataset exported to Github or similar.
    urllib.request.urlretrieve("https://raw.githubusercontent.com/junioralive/Indian-Medicine-Dataset/main/medicine_dataset.csv", os.path.join(base_dir, "drugs.csv"))
    print("Drugs downloaded.")
except Exception as e:
    print(f"Failed Drugs URL 1: {e}")
    try:
        # Let's try another source if that fails
        urllib.request.urlretrieve("https://raw.githubusercontent.com/611noorsaeed/Medicine-Recommendation-System-Personalized-Medical-Recommendation-System-with-Machine-Learning/main/medications.csv", os.path.join(base_dir, "drugs.csv"))
        print("Drugs downloaded from fallback.")
    except Exception as e2:
        print(f"Failed Drugs URL 2: {e2}")

print("Done.")
