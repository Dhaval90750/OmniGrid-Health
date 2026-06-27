"use client";

import { useState, useRef } from "react";
import { useRouter } from "next/navigation";
import Webcam from "react-webcam";
import { Button } from "@/components/ui/Button";

export default function TelemedicineRoom() {
  const router = useRouter();
  const [joined, setJoined] = useState(false);
  const [micOn, setMicOn] = useState(true);
  const [camOn, setCamOn] = useState(true);

  return (
    <div className="max-w-6xl mx-auto h-[80vh] flex flex-col pt-8">
      <div className="flex justify-between items-center mb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Teleconsultation Room</h2>
          <p className="text-text-secondary text-sm">Consultation with Dr. Sarah Miller (Cardiology)</p>
        </div>
        {joined && (
          <div className="flex items-center gap-2">
            <span className="relative flex h-3 w-3">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-error opacity-75"></span>
              <span className="relative inline-flex rounded-full h-3 w-3 bg-error"></span>
            </span>
            <div className="text-error font-bold font-mono text-xl tracking-wider">05:42</div>
          </div>
        )}
      </div>

      <div className="flex-1 bg-black rounded-lg overflow-hidden relative flex items-center justify-center border-4 border-surface shadow-2xl">
        {!joined ? (
          <div className="text-center text-white space-y-6">
            <div className="w-24 h-24 bg-surface/20 rounded-full mx-auto flex items-center justify-center shadow-inner">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" /></svg>
            </div>
            <div>
              <h3 className="text-2xl font-bold mb-2">Ready to join?</h3>
              <p className="text-gray-400 text-sm max-w-md mx-auto">Please ensure your camera and microphone permissions are granted before joining the consultation.</p>
            </div>
            
            {/* Mini preview before joining */}
            <div className="w-64 h-48 bg-gray-900 mx-auto rounded-lg overflow-hidden border-2 border-surface flex items-center justify-center relative">
               <Webcam audio={false} mirrored={true} className="w-full h-full object-cover" />
               <div className="absolute bottom-2 left-2 bg-black/60 px-2 py-1 rounded text-xs">Preview</div>
            </div>

            <Button variant="primary" size="lg" onClick={() => setJoined(true)} className="px-8 mt-4">Join Consultation</Button>
          </div>
        ) : (
          <>
            {/* Main Video (Doctor Mock) */}
            <div className="absolute inset-0 bg-gray-900 flex items-center justify-center">
              <div className="text-center">
                <div className="w-32 h-32 bg-surface rounded-full flex items-center justify-center mx-auto mb-4 text-4xl shadow-lg border-4 border-surface-hover">👩‍⚕️</div>
                <div className="text-gray-400 font-medium text-lg">Dr. Sarah Miller</div>
                <div className="text-gray-600 text-sm">Receiving encrypted video stream...</div>
              </div>
            </div>
            
            {/* PIP Video (Patient Actual Webcam) */}
            <div className="absolute bottom-24 right-8 w-64 h-48 bg-gray-800 rounded-lg shadow-2xl border-2 border-surface overflow-hidden group">
              {camOn ? (
                <Webcam audio={false} mirrored={true} className="w-full h-full object-cover" />
              ) : (
                <div className="w-full h-full flex items-center justify-center bg-gray-900 text-gray-500">Camera Off</div>
              )}
              <div className="absolute bottom-2 left-2 bg-black/60 px-2 py-1 rounded text-xs text-white opacity-0 group-hover:opacity-100 transition-opacity">You</div>
              {!micOn && (
                <div className="absolute top-2 right-2 bg-error px-2 py-1 rounded-full text-xs text-white font-bold">Muted</div>
              )}
            </div>

            {/* Controls Bar */}
            <div className="absolute bottom-6 left-1/2 transform -translate-x-1/2 flex gap-6 bg-surface p-4 rounded-2xl shadow-2xl border border-border">
              <button 
                onClick={() => setMicOn(!micOn)}
                className={`w-14 h-14 rounded-full flex items-center justify-center text-white transition-all shadow-md ${micOn ? 'bg-surface-hover hover:bg-gray-600' : 'bg-error hover:bg-error-dark'}`}
                title={micOn ? "Mute Microphone" : "Unmute Microphone"}
              >
                {micOn ? '🎤' : '🔇'}
              </button>
              
              <button 
                onClick={() => setCamOn(!camOn)}
                className={`w-14 h-14 rounded-full flex items-center justify-center text-white transition-all shadow-md ${camOn ? 'bg-surface-hover hover:bg-gray-600' : 'bg-error hover:bg-error-dark'}`}
                title={camOn ? "Stop Camera" : "Start Camera"}
              >
                {camOn ? '📹' : '📷'}
              </button>
              
              <button 
                onClick={() => {
                  setJoined(false);
                  router.push('/portal');
                }} 
                className="w-20 h-14 rounded-full bg-error hover:bg-error-dark flex items-center justify-center text-white transition-colors shadow-lg font-bold"
                title="End Call"
              >
                END
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
