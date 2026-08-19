"use client";

import { useState } from 'react';
import { LayoutDashboard, Calendar, Users, Activity, Settings, Zap } from 'lucide-react';

export default function CoordinatorDashboard() {
  const [activeTab, setActiveTab] = useState('overview');

  return (
    <div className="min-h-screen bg-background text-textPrimary font-sans">
      {/* Sidebar Navigation */}
      <nav className="fixed left-0 top-0 h-full w-64 bg-surface border-r border-surfaceElevated p-4">
        <div className="flex items-center gap-3 mb-10 mt-2">
          <div className="w-8 h-8 rounded bg-primary flex items-center justify-center">
            <Zap className="text-surface w-5 h-5" />
          </div>
          <h1 className="text-lg font-bold text-primary tracking-wide">IntelliSchedule</h1>
        </div>

        <div className="space-y-2">
          <NavItem icon={<LayoutDashboard />} label="Dashboard" active={activeTab === 'overview'} onClick={() => setActiveTab('overview')} />
          <NavItem icon={<Calendar />} label="Timetable Studio" active={activeTab === 'studio'} onClick={() => setActiveTab('studio')} />
          <NavItem icon={<Users />} label="Faculty & Batches" active={activeTab === 'management'} onClick={() => setActiveTab('management')} />
          <NavItem icon={<Activity />} label="Recovery Hub" active={activeTab === 'recovery'} onClick={() => setActiveTab('recovery')} />
          <NavItem icon={<Settings />} label="System Rules" active={activeTab === 'rules'} onClick={() => setActiveTab('rules')} />
        </div>
      </nav>

      {/* Main Content Area */}
      <main className="ml-64 p-8">
        <header className="mb-8 flex justify-between items-center">
          <div>
            <h2 className="text-2xl font-bold">Coordinator Workspace</h2>
            <p className="text-textSecondary text-sm mt-1">Manage academic schedules, resources, and self-healing operations.</p>
          </div>
          <button className="bg-primary hover:bg-primaryDark text-background font-bold py-2 px-4 rounded flex items-center gap-2 transition-colors">
            <Zap className="w-4 h-4" />
            Run Global Optimizer
          </button>
        </header>

        {/* Dashboard Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <StatCard title="Timetable Health" value="98%" trend="+2.4%" status="good" />
          <StatCard title="Pending Makeups" value="3" trend="Action Required" status="warning" />
          <StatCard title="Active Conflicts" value="0" trend="All clear" status="good" />
        </div>

        {/* Studio Section */}
        <div className="bg-surface border border-surfaceElevated rounded-xl p-6">
          <h3 className="text-lg font-bold mb-4">Live Academic Sessions (Today)</h3>
          <div className="space-y-3">
            <SessionRow course="Database Management" faculty="Dr. Sharma" room="Lab 3" time="10:00 AM - 12:00 PM" status="Ongoing" />
            <SessionRow course="Software Engineering" faculty="Prof. Gupta" room="Room 204" time="01:00 PM - 02:00 PM" status="Upcoming" />
            <SessionRow course="Cloud Computing" faculty="Dr. Verma" room="Lab 1" time="02:30 PM - 04:30 PM" status="Upcoming" />
          </div>
        </div>
      </main>
    </div>
  );
}

function NavItem({ icon, label, active, onClick }: { icon: React.ReactNode, label: string, active: boolean, onClick: () => void }) {
  return (
    <button 
      onClick={onClick}
      className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
        active ? 'bg-primary/10 text-primary border border-primary/20' : 'text-textSecondary hover:bg-surfaceElevated hover:text-textPrimary'
      }`}
    >
      <div className="w-5 h-5">{icon}</div>
      <span className="font-medium text-sm">{label}</span>
    </button>
  );
}

function StatCard({ title, value, trend, status }: { title: string, value: string, trend: string, status: 'good' | 'warning' | 'danger' }) {
  const statusColors = {
    good: 'text-accent',
    warning: 'text-warning',
    danger: 'text-danger'
  };

  return (
    <div className="bg-surface border border-surfaceElevated p-6 rounded-xl">
      <h4 className="text-textSecondary text-sm font-medium">{title}</h4>
      <div className="mt-2 flex items-end justify-between">
        <span className="text-3xl font-bold">{value}</span>
        <span className={`text-xs font-bold ${statusColors[status]}`}>{trend}</span>
      </div>
    </div>
  );
}

function SessionRow({ course, faculty, room, time, status }: { course: string, faculty: string, room: string, time: string, status: string }) {
  return (
    <div className="flex items-center justify-between p-4 bg-background/50 border border-surfaceElevated rounded-lg">
      <div className="flex items-center gap-4">
        <div className="w-10 h-10 rounded-full bg-surfaceElevated flex items-center justify-center text-primary font-bold text-sm">
          {course.substring(0, 2).toUpperCase()}
        </div>
        <div>
          <h5 className="font-bold text-sm">{course}</h5>
          <p className="text-textSecondary text-xs">{faculty} • {room}</p>
        </div>
      </div>
      <div className="flex items-center gap-4">
        <span className="text-sm font-medium">{time}</span>
        <span className={`px-2 py-1 rounded text-xs font-bold ${status === 'Ongoing' ? 'bg-accent/20 text-accent' : 'bg-surfaceElevated text-textSecondary'}`}>
          {status}
        </span>
      </div>
    </div>
  );
}
