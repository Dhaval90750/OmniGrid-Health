"use client";

import { useState, useEffect } from "react";
import { Card, CardContent } from "@/components/ui/Card";
import { api } from "@/lib/api";

export default function PublicQueueBoard() {
  const [queues, setQueues] = useState<any[]>([]);

  useEffect(() => {
    // In a real implementation, we would fetch active doctors and their current processing token
    // Polling every 30 seconds
    setQueues([
      { doctorName: "Dr. Adams", department: "Cardiology", currentToken: 12, nextTokens: [13, 14, 15], room: "Room 101" },
      { doctorName: "Dr. Lee", department: "Orthopedics", currentToken: 5, nextTokens: [6, 7], room: "Room 105" },
      { doctorName: "Dr. Smith", department: "Neurology", currentToken: 22, nextTokens: [23, 24, 25], room: "Room 203" }
    ]);
  }, []);

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div className="text-center py-6 border-b border-border">
        <h1 className="text-4xl font-bold text-primary">Outpatient Department Queue</h1>
        <p className="text-lg text-text-secondary mt-2">Please wait in the designated area until your token number is called.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 pt-4">
        {queues.map((q, idx) => (
          <Card key={idx} className="overflow-hidden border-2 shadow-lg">
            <div className="bg-primary text-white p-4 text-center">
              <h2 className="text-2xl font-bold">{q.doctorName}</h2>
              <div className="text-primary-light font-medium">{q.department} | {q.room}</div>
            </div>
            <CardContent className="p-8 text-center space-y-6">
              <div>
                <div className="text-sm font-semibold text-text-secondary uppercase tracking-widest mb-2">Now Serving</div>
                <div className="text-7xl font-black text-text-primary tabular-nums">
                  {q.currentToken.toString().padStart(3, '0')}
                </div>
              </div>

              <div className="bg-surface p-4 rounded-lg border border-border">
                <div className="text-xs text-text-secondary uppercase font-semibold mb-2">Next in Queue</div>
                <div className="flex justify-center gap-4 text-xl font-bold tabular-nums text-text-primary opacity-80">
                  {q.nextTokens.map((t: number) => t.toString().padStart(3, '0')).join(", ")}
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
