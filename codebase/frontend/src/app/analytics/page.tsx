"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";

export default function AnalyticsDashboard() {
  const [data, setData] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/v1/analytics/dashboard");
        if (res.ok) {
          const json = await res.json();
          setData(json);
        }
      } catch (error) {
        console.error("Failed to fetch analytics", error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  if (loading) {
    return <div className="flex justify-center items-center h-screen text-text-secondary">Loading Command Center...</div>;
  }

  if (!data) {
    return <div className="flex justify-center items-center h-screen text-error">Failed to load Command Center. Is the backend running?</div>;
  }

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-text-primary">OmniGrid Command Center</h1>
          <p className="text-text-secondary text-sm">Phase 5 Capstone: Advanced AI & Hospital Analytics</p>
        </div>
        <Badge variant="success" className="text-sm px-3 py-1">System Status: Optimal</Badge>
      </div>

      {/* Row 1: Executive Overview */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card className="bg-primary text-white border-none shadow-lg">
          <CardContent className="p-6">
            <div className="text-sm opacity-80 mb-1">Revenue Today</div>
            <div className="text-3xl font-bold">₹{data.executive_overview.total_revenue_today.toLocaleString()}</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-6">
            <div className="text-sm text-text-secondary mb-1">Active IPD Census</div>
            <div className="text-3xl font-bold text-text-primary">{data.executive_overview.active_ipd_census}</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-6">
            <div className="text-sm text-text-secondary mb-1">Bed Occupancy</div>
            <div className="text-3xl font-bold text-warning">{data.executive_overview.bed_occupancy_percent}%</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-6">
            <div className="text-sm text-text-secondary mb-1">Avg ER Wait Time</div>
            <div className="text-3xl font-bold text-success">{data.executive_overview.er_waiting_time_mins} mins</div>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Main Column: AI Predictors */}
        <div className="lg:col-span-2 space-y-6">
          <Card className="border-error border-2 shadow-sm">
            <CardHeader className="bg-error/10 pb-4">
              <CardTitle className="text-error flex items-center justify-between">
                <span>⚠️ AI Risk Predictors (Action Required)</span>
                <span className="text-xs font-normal text-text-secondary bg-surface px-2 py-1 rounded">Models: Sepsis v2, Readmission v1.4</span>
              </CardTitle>
            </CardHeader>
            <CardContent className="pt-4">
              <table className="w-full text-left text-sm">
                <thead className="bg-surface border-b border-border">
                  <tr>
                    <th className="p-3">Patient</th>
                    <th className="p-3">Location</th>
                    <th className="p-3">AI Flag</th>
                    <th className="p-3">Probability</th>
                    <th className="p-3">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {data.ai_risk_alerts.map((alert: any, idx: number) => (
                    <tr key={idx} className="border-b border-surface-hover">
                      <td className="p-3 font-bold">{alert.name} <span className="text-xs text-text-secondary block">{alert.patient_id}</span></td>
                      <td className="p-3">{alert.ward}</td>
                      <td className="p-3">
                        {alert.status === "CRITICAL" ? <Badge variant="error">{alert.alert_type}</Badge> : 
                         alert.status === "HIGH" ? <Badge variant="warning">{alert.alert_type}</Badge> : 
                         <Badge variant="info">{alert.alert_type}</Badge>}
                      </td>
                      <td className="p-3 font-mono">{(alert.probability * 100).toFixed(1)}%</td>
                      <td className="p-3"><Button variant="secondary" size="sm">Review Chart</Button></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Revenue by Department</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {data.revenue_by_department.map((dept: any, idx: number) => (
                  <div key={idx}>
                    <div className="flex justify-between text-sm mb-1">
                      <span className="font-medium">{dept.department}</span>
                      <span className="text-text-secondary">₹{dept.revenue.toLocaleString()}</span>
                    </div>
                    {/* Visual bar mock */}
                    <div className="w-full bg-surface-hover rounded-full h-2">
                      <div className="bg-primary h-2 rounded-full" style={{ width: `${(dept.revenue / 45000) * 100}%` }}></div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Side Column: Quality KPIs */}
        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>NABH Quality Indicators</CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
              
              <div>
                <div className="text-sm text-text-secondary mb-1 flex justify-between">
                  <span>Avg Length of Stay (ALOS)</span>
                  <span className="font-bold text-success">{data.quality_kpis.average_length_of_stay} Days</span>
                </div>
                <p className="text-xs text-text-secondary">Target: &lt; 5.0 Days</p>
              </div>
              
              <div className="border-t border-border pt-4">
                <div className="text-sm text-text-secondary mb-1 flex justify-between">
                  <span>Hospital Acquired Infections</span>
                  <span className="font-bold text-error">{data.quality_kpis.hospital_acquired_infections}%</span>
                </div>
                <p className="text-xs text-text-secondary">Target: &lt; 1.0%</p>
              </div>
              
              <div className="border-t border-border pt-4">
                <div className="text-sm text-text-secondary mb-1 flex justify-between">
                  <span>Surgical Site Infections</span>
                  <span className="font-bold text-warning">{data.quality_kpis.surgical_site_infections}%</span>
                </div>
                <p className="text-xs text-text-secondary">Target: &lt; 0.5%</p>
              </div>

              <div className="border-t border-border pt-4">
                <div className="text-sm text-text-secondary mb-1 flex justify-between">
                  <span>Patient Satisfaction Score</span>
                  <span className="font-bold text-primary">{data.quality_kpis.patient_satisfaction_score} / 5.0</span>
                </div>
                <p className="text-xs text-text-secondary">Based on Discharge Surveys</p>
              </div>

            </CardContent>
          </Card>
        </div>

      </div>
    </div>
  );
}
