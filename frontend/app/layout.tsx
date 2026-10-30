import type { Metadata } from 'next';
import './style.css';
export const metadata: Metadata = { title: 'Rota — Diário de Bordo', description: 'Um lugar simples para registrar cada turno, cuidar do veículo e acompanhar o que acontece na rota.' };
export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) { return <html lang="pt-BR"><body>{children}</body></html>; }
