"use client";

import { useState } from "react";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";

import { api } from "@/lib/api";

export default function AiScribeDashboard() {
  const [isRecording, setIsRecording] = useState(false);
  const [transcript, setTranscript] = useState("");
  const [isProcessing, setIsProcessing] = useState(false);
  const [aiResult, setAiResult] = useState<any>(null);

  const DUMMY_TRANSCRIPT = "Patient presents with a mild fever and severe headache for the past 2 days. Blood pressure is 120/80 mmHg. Heart rate is 85 bpm. Prescribing paracetamol for the fever.";

  const handleSimulateRecord = () => {
    setIsRecording(true);
    setTranscript("");
    setAiResult(null);
    
    // Simulate typing out the transcript via Voice
    let i = 0;
    const interval = setInterval(() => {
      setTranscript(DUMMY_TRANSCRIPT.substring(0, i));
      i += 3;
      if (i > DUMMY_TRANSCRIPT.length) {
        clearInterval(interval);
        setIsRecording(false);
      }
    }, 50);
  };

  const handleExtractEntities = async () => {
    setIsProcessing(true);
    try {
      const response = await api.post("/ai/extract", { transcript });
      setAiResult(response.data);
    } catch (error) {
      console.error("Failed to run AI", error);
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-text-primary">AI Clinical Scribe (Phase 4 MVP)</h1>
          <p className="text-text-secondary text-sm">Powered by mock Whisper V3 and Medical NER</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        
        {/* Left Side: Voice Input */}
        <Card className="h-[600px] flex flex-col">
          <CardHeader className="border-b border-border pb-4 flex flex-row justify-between items-center">
            <CardTitle>Voice Recording Interface</CardTitle>
            {isRecording && <Badge variant="error" className="animate-pulse">Recording...</Badge>}
          </CardHeader>
          <CardContent className="flex-1 flex flex-col p-4">
            
            <div className="flex-1 bg-surface-hover rounded-md p-4 mb-4 overflow-y-auto border border-border font-mono text-sm text-text-secondary">
              {transcript || "Click 'Start Recording' to dictate notes..."}
              {isRecording && <span className="animate-pulse ml-1">|</span>}
            </div>

            <div className="flex gap-4">
              <Button 
                variant={isRecording ? "secondary" : "danger"} 
                className="flex-1"
                onClick={handleSimulateRecord}
                disabled={isRecording}
              >
                {isRecording ? "Listening..." : "🎙 Simulate Voice Recording"}
              </Button>
              <Button 
                variant="primary" 
                className="flex-1"
                onClick={handleExtractEntities}
                disabled={!transcript || isRecording || isProcessing}
              >
                {isProcessing ? "Processing NLP..." : "🧠 Run Medical NER & SOAP"}
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* Right Side: AI Output */}
        <Card className="h-[600px] flex flex-col">
          <CardHeader className="border-b border-border pb-4">
            <CardTitle className="flex items-center gap-2">
              Structured Data Output
              {aiResult && <Badge variant="success">Confidence: {(aiResult.confidence_score * 100).toFixed(0)}%</Badge>}
            </CardTitle>
          </CardHeader>
          <CardContent className="flex-1 overflow-y-auto p-4 space-y-6">
            
            {!aiResult ? (
              <div className="h-full flex items-center justify-center text-text-secondary">
                Awaiting AI Processing...
              </div>
            ) : (
              <>
                <div>
                  <h3 className="text-sm font-bold text-text-primary mb-2 border-b border-border pb-1">Extracted Entities (NER)</h3>
                  <div className="grid grid-cols-2 gap-4 text-sm">
                    <div>
                      <span className="text-text-secondary">Symptoms:</span>
                      <div className="flex flex-wrap gap-1 mt-1">
                        {aiResult.extracted_symptoms?.map((s: string, i: number) => (
                          <Badge key={i} variant="warning">{s}</Badge>
                        ))}
                      </div>
                    </div>
                    <div>
                      <span className="text-text-secondary">Medications:</span>
                      <div className="flex flex-wrap gap-1 mt-1">
                        {aiResult.extracted_medications?.map((m: string, i: number) => (
                          <Badge key={i} variant="info">{m}</Badge>
                        ))}
                      </div>
                    </div>
                    <div className="col-span-2">
                      <span className="text-text-secondary">Vitals Detected:</span>
                      <div className="mt-1 font-medium">
                        BP: {aiResult.vitals?.blood_pressure || "—"} | HR: {aiResult.vitals?.heart_rate || "—"}
                      </div>
                    </div>
                  </div>
                </div>

                <div>
                  <h3 className="text-sm font-bold text-text-primary mb-2 border-b border-border pb-1">Generated SOAP Note</h3>
                  <div className="space-y-3 text-sm bg-surface p-3 rounded border border-border">
                    <div><span className="font-bold text-primary">S</span>ubjective:<br/>{aiResult.generated_soap_note?.Subjective || "—"}</div>
                    <div><span className="font-bold text-primary">O</span>bjective:<br/>{aiResult.generated_soap_note?.Objective || "—"}</div>
                    <div><span className="font-bold text-primary">A</span>ssessment:<br/>{aiResult.generated_soap_note?.Assessment || "—"}</div>
                    <div><span className="font-bold text-primary">P</span>lan:<br/>{aiResult.generated_soap_note?.Plan || "—"}</div>
                  </div>
                </div>

                <div className="pt-4 flex gap-3">
                  <Button variant="secondary" className="flex-1">Edit / Refine</Button>
                  <Button variant="primary" className="flex-1">Sign & Save to EMR</Button>
                </div>
              </>
            )}

          </CardContent>
        </Card>

      </div>
    </div>
  );
}
