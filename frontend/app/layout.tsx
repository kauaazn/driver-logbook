import type { Metadata } from 'next';
import './style.css';
export const metadata: Metadata = { title: 'Driver Logbook', description: 'A simple place to log each shift, care for your vehicle, and track your workday.' };
export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) { return <html lang="en"><body>{children}</body></html>; }
