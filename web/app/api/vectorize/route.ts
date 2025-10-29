export const runtime = 'edge';

export async function POST(req: Request) {
  const body = await req.json().catch(() => ({}));
  const strokes = body?.strokes ?? [];
  // Placeholder: devolver SVG con polilíneas de los strokes
  const paths = strokes.map((s:any) => {
    const d = (s.points||[]).map((p:any,i:number) => `${i?'L':'M'}${p.x},${p.y}`).join(' ');
    return `<path d="${d}" fill="none" stroke="#0ff" stroke-width="2"/>`;
  }).join('\n');
  const svg = `<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" width="2000" height="1500" viewBox="0 0 2000 1500">
${paths}
</svg>`;
  return new Response(JSON.stringify({ svg }), { headers: { "Content-Type": "application/json" } });
}
