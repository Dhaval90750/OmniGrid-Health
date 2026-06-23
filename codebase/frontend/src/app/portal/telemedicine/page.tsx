"use client";

import { useState } from "react";
import { Button } from "@/components/ui/Button";

export default function TelemedicineRoom() {
  const [joined, setJoined] = useState(false);

  return (
    <div className="max-w-6xl mx-auto h-[80vh] flex flex-col">
      <div className="flex justify-between items-center mb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Teleconsultation Room</h2>
          <p className="text-text-secondary text-sm">Consultation with Dr. Sarah Miller</p>
        </div>
        {joined && <div className="text-error font-bold animate-pulse">05:42</div>}
      </div>

      <div className="flex-1 bg-black rounded-lg overflow-hidden relative flex items-center justify-center border-4 border-surface shadow-2xl">
        {!joined ? (
          <div className="text-center text-white space-y-4">
            <div className="w-24 h-24 bg-surface/20 rounded-full mx-auto flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" /></svg>
            </div>
            <h3 className="text-xl font-bold">Ready to join?</h3>
            <p className="text-gray-400 text-sm max-w-md">Please ensure your camera and microphone are turned on before joining the consultation.</p>
            <Button variant="primary" onClick={() => setJoined(true)} className="mt-4">Join Consultation</Button>
          </div>
        ) : (
          <>
            {/* Main Video (Doctor) */}
            <div className="absolute inset-0 bg-gray-900 flex items-center justify-center">
              <div className="text-gray-500 font-mono">Dr. Miller's Video Stream (WebRTC Mock)</div>
            </div>
            
            {/* PIP Video (Patient) */}
            <div className="absolute bottom-6 right-6 w-48 h-32 bg-gray-800 rounded shadow-lg border-2 border-surface flex items-center justify-center">
              <div className="text-xs text-gray-400">Your Camera</div>
            </div>

            {/* Controls */}
            <div className="absolute bottom-6 left-1/2 transform -translate-x-1/2 flex gap-4 bg-black/50 p-3 rounded-full backdrop-blur-sm">
              <button className="w-12 h-12 rounded-full bg-surface/20 hover:bg-surface/40 flex items-center justify-center text-white transition-colors">
                🎤
              </button>
              <button className="w-12 h-12 rounded-full bg-surface/20 hover:bg-surface/40 flex items-center justify-center text-white transition-colors">
                📹
              </button>
              <button onClick={() => setJoined(false)} className="w-12 h-12 rounded-full bg-error hover:bg-error-dark flex items-center justify-center text-white transition-colors">
                ☎️
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
