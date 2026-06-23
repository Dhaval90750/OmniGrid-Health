package com.medcore.his.service;

import org.springframework.stereotype.Service;

/**
 * Service to simulate PACS storage configurations (e.g., S3 or MinIO buckets).
 * In production, this would manage signed URLs or binary stream proxies.
 */
@Service
public class PacsStorageService {

    public String getWadoUrl(String studyUid) {
        // Web Access to DICOM Persistent Objects (WADO) mock URL
        return "https://pacs.medcore.local/wado?requestType=WADO&studyUID=" + studyUid;
    }
    
    public String generateCornerstoneViewerUrl(String studyUid) {
        return "https://viewer.medcore.local/viewer?StudyInstanceUIDs=" + studyUid;
    }
}
