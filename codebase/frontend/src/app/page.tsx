import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { Badge } from "@/components/ui/Badge";

export default function Home() {
  return (
    <div className="max-w-5xl space-y-8">
      
      {/* Welcome Section */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-semibold text-text-primary mb-1">Welcome back, Dr. Smith</h2>
          <p className="text-text-secondary text-sm">Here is what&apos;s happening today in OmniGrid Health.</p>
        </div>
        <div className="flex gap-3">
          <Button variant="secondary">View Schedule</Button>
          <Button variant="primary">New Patient Registration</Button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        
        {/* Patient Registration Card */}
        <Card className="col-span-1 md:col-span-2">
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Quick Registration</CardTitle>
              <Badge variant="info">OPD Flow</Badge>
            </div>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <Input label="First Name" placeholder="Enter first name" />
              <Input label="Last Name" placeholder="Enter last name" />
            </div>
            <Input label="Mobile Number" placeholder="+91 9876543210" type="tel" />
            <Input label="Email Address (Optional)" placeholder="patient@example.com" type="email" />
          </CardContent>
          <CardFooter className="justify-end gap-3">
            <Button variant="secondary">Clear Form</Button>
            <Button variant="primary">Generate UHID & QR</Button>
          </CardFooter>
        </Card>

        {/* System Status Card */}
        <Card>
          <CardHeader>
            <CardTitle>System Status</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex justify-between items-center pb-2 border-b border-surface-hover">
              <span className="text-sm text-text-secondary">Core Database</span>
              <Badge variant="success">Online</Badge>
            </div>
            <div className="flex justify-between items-center pb-2 border-b border-surface-hover">
              <span className="text-sm text-text-secondary">Auth Service</span>
              <Badge variant="success">Online</Badge>
            </div>
            <div className="flex justify-between items-center pb-2 border-b border-surface-hover">
              <span className="text-sm text-text-secondary">Lab Integration</span>
              <Badge variant="warning">Sync Delayed</Badge>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-sm text-text-secondary">ICU Ventilators</span>
              <Badge variant="error">Critical Load</Badge>
            </div>
          </CardContent>
        </Card>

      </div>
    </div>
  );
}
