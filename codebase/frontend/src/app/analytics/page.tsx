"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { api } from "@/lib/api";

export default function AnalyticsDashboard() {
  const [data, setData] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const res = await api.get("/analytics/dashboard");
      setData(res.data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="p-8 text-center text-text-secondary">Loading Executive Dashboard...</div>;
  }

  if (!data) {
    return <div className="p-8 text-center text-error">Failed to load analytics data.</div>;
  }

  const { executive_overview: exec, ai_risk_alerts: alerts, quality_kpis: quality, revenue_by_department: revenue } = data;

  // For Revenue Chart
  const maxRevenue = Math.max(...revenue.map((r: any) => r.revenue));

  return (
    <div className="max-w-7xl mx-auto space-y-8 pb-12">
      <div className="flex justify-between items-center border-b border-border pb-4">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary">Executive Command Center</h2>
          <p className="text-text-secondary text-sm">Real-time hospital performance, revenue, and predictive analytics.</p>
        </div>
        <div className="text-sm text-text-secondary">
          Last Updated: {new Date().toLocaleTimeString()}
        </div>
      </div>

      {/* KPI Row 1: Executive Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card className="bg-primary text-white border-none shadow-lg">
          <CardContent className="p-6">
            <div className="text-sm opacity-80 mb-1">Total Revenue Today</div>
            <div className="text-3xl font-bold">₹{exec?.total_revenue_today?.toLocaleString() || '0'}</div>
            <div className="text-xs opacity-70 mt-2">↑ 12% vs Yesterday</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-6">
            <div className="text-sm text-text-secondary mb-1">Active IPD Census</div>
            <div className="text-3xl font-bold text-text-primary">{exec?.active_ipd_census || '0'}</div>
            <div className="text-xs text-success mt-2">Within Safe Limits</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-6">
            <div className="text-sm text-text-secondary mb-1">Bed Occupancy Rate</div>
            <div className="text-3xl font-bold text-warning">{exec?.bed_occupancy_percent || '0'}%</div>
            <div className="w-full bg-surface-hover h-2 rounded-full mt-2 overflow-hidden">
              <div className="bg-warning h-full" style={{ width: `${exec?.bed_occupancy_percent || 0}%` }}></div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-6">
            <div className="text-sm text-text-secondary mb-1">ER Waiting Time (Avg)</div>
            <div className="text-3xl font-bold text-error">{exec?.er_waiting_time_mins || '0'} mins</div>
            <div className="text-xs text-error mt-2">Requires Attention</div>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Left Col: Revenue & Quality */}
        <div className="lg:col-span-2 space-y-6">
          
          <Card>
            <CardHeader>
              <CardTitle>Revenue Distribution by Department</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {revenue?.map((dept: any, idx: number) => (
                  <div key={idx}>
                    <div className="flex justify-between items-end mb-1">
                      <span className="text-sm font-medium">{dept.department}</span>
                      <span className="text-sm font-bold">₹{dept.revenue.toLocaleString()}</span>
                    </div>
                    <div className="w-full bg-surface-hover h-4 rounded-full overflow-hidden">
                      <div 
                        className="bg-primary h-full transition-all duration-1000 ease-out" 
                        style={{ width: `${(dept.revenue / maxRevenue) * 100}%` }}
                      ></div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>NABH Quality & Compliance KPIs</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-2 gap-6">
                <div>
                  <div className="text-sm text-text-secondary mb-1">Average Length of Stay (ALOS)</div>
                  <div className="text-2xl font-bold">{quality?.average_length_of_stay} Days</div>
                  <div className="text-xs text-success mt-1">Target: &lt; 5 Days</div>
                </div>
                <div>
                  <div className="text-sm text-text-secondary mb-1">Patient Satisfaction Score</div>
                  <div className="text-2xl font-bold text-success">{quality?.patient_satisfaction_score} / 5.0</div>
                  <div className="text-xs text-success mt-1">Excellent</div>
                </div>
                <div>
                  <div className="text-sm text-text-secondary mb-1">Hospital Acquired Infections</div>
                  <div className="text-2xl font-bold text-warning">{quality?.hospital_acquired_infections}%</div>
                  <div className="text-xs text-text-secondary mt-1">Target: &lt; 2.0%</div>
                </div>
                <div>
                  <div className="text-sm text-text-secondary mb-1">Surgical Site Infections</div>
                  <div className="text-2xl font-bold text-success">{quality?.surgical_site_infections}%</div>
                  <div className="text-xs text-text-secondary mt-1">Target: &lt; 1.0%</div>
                </div>
              </div>
            </CardContent>
          </Card>

        </div>

        {/* Right Col: AI Risk Alerts */}
        <div className="lg:col-span-1">
          <Card className="h-full border-error">
            <CardHeader className="bg-error/10 border-b border-error/20">
              <CardTitle className="text-error flex items-center gap-2">
                <span>AI Clinical Risk Alerts</span>
              </CardTitle>
            </CardHeader>
            <CardContent className="p-0">
              <ul className="divide-y divide-border">
                {alerts?.map((alert: any, idx: number) => (
                  <li key={idx} className="p-4 hover:bg-surface-hover transition-colors">
                    <div className="flex justify-between items-start mb-2">
                      <div className="font-bold">{alert.name}</div>
                      <Badge variant={alert.status === 'CRITICAL' ? 'error' : (alert.status === 'HIGH' ? 'warning' : 'info')}>
                        {alert.status}
                      </Badge>
                    </div>
                    <div className="text-sm mb-2 text-error font-medium">Risk: {alert.alert_type}</div>
                    <div className="flex justify-between items-center text-xs text-text-secondary">
                      <span>{alert.patient_id} | {alert.ward}</span>
                      <span>Prob: {(alert.probability * 100).toFixed(0)}%</span>
                    </div>
                    <div className="w-full bg-surface-hover h-1.5 rounded-full mt-2 overflow-hidden">
                      <div 
                        className={`h-full ${alert.status === 'CRITICAL' ? 'bg-error' : (alert.status === 'HIGH' ? 'bg-warning' : 'bg-info')}`} 
                        style={{ width: `${alert.probability * 100}%` }}
                      ></div>
                    </div>
                  </li>
                ))}
              </ul>
            </CardContent>
          </Card>
        </div>

      </div>
    </div>
  );
}
