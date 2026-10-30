import type { Metadata } from 'next';
import './style.css';
export const metadata: Metadata = { title: 'Diário Motorista', description: 'Um lugar simples para registrar cada turno, cuidar do veículo e acompanhar sua jornada.' };
export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) { return <html lang="pt-BR"><body>{children}</body></html>; }
