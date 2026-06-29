"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/Button";
import { api } from "@/lib/api";

export default function TelemedicineCall() {
  const router = useRouter();
  const [status, setStatus] = useState("Connecting to secure signaling server...");
  const [inCall, setInCall] = useState(false);

  useEffect(() => {
    // Mocking the WebRTC connection sequence
    const timer1 = setTimeout(() => setStatus("Waiting for Doctor to join..."), 2000);
    const timer2 = setTimeout(() => {
      setStatus("Connected");
      setInCall(true);
    }, 5000);

    return () => {
      clearTimeout(timer1);
      clearTimeout(timer2);
    };
  }, []);

  const handleEndCall = () => {
    setInCall(false);
    setStatus("Call ended. Redirecting...");
    setTimeout(() => router.push("/portal"), 2000);
  };

  return (
    <div className="max-w-6xl mx-auto h-[80vh] flex flex-col bg-surface-hover rounded-xl overflow-hidden shadow-lg border border-border">
      
      <div className="bg-surface p-4 flex justify-between items-center border-b border-border shadow-sm z-10">
        <div>
          <h1 className="text-xl font-bold">Teleconsultation Room</h1>
          <p className="text-sm text-text-secondary">{status}</p>
        </div>
        <div className="text-sm font-semibold bg-primary/10 text-primary px-3 py-1 rounded">
          HIPAA Compliant E2EE
        </div>
      </div>

      <div className="flex-1 relative bg-black flex items-center justify-center">
        {inCall ? (
          <>
            {/* Mock Remote Video (Doctor) */}
            <div className="w-full h-full flex items-center justify-center relative overflow-hidden">
              <img src="https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=800&q=80" alt="Doctor Video Stream" className="w-full h-full object-cover opacity-80" />
              <div className="absolute bottom-4 left-4 bg-black/50 text-white px-2 py-1 rounded text-sm">
                Dr. Sarah Miller
              </div>
            </div>
            
            {/* Mock Local Video (Patient) */}
            <div className="absolute top-4 right-4 w-48 h-36 bg-gray-800 rounded-lg border-2 border-white overflow-hidden shadow-xl z-20">
              <img src="https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=300&q=80" alt="Self Video Stream" className="w-full h-full object-cover" />
            </div>
          </>
        ) : (
          <div className="text-white flex flex-col items-center">
            <div className="w-16 h-16 border-4 border-primary border-t-transparent rounded-full animate-spin mb-4"></div>
            <p className="text-lg">{status}</p>
          </div>
        )}
      </div>

      <div className="bg-surface p-4 flex justify-center gap-6 border-t border-border">
        <Button variant="secondary" className="rounded-full w-14 h-14 p-0 flex items-center justify-center">
          Mute
        </Button>
        <Button variant="secondary" className="rounded-full w-14 h-14 p-0 flex items-center justify-center">
          Video
        </Button>
        <Button variant="danger" className="rounded-full px-8 h-14 text-lg font-bold" onClick={handleEndCall}>
          End Call
        </Button>
      </div>

    </div>
  );
}
