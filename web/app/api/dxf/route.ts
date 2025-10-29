export const runtime = 'edge';

function dxfHeader(){
  return ["0","SECTION","2","ENTITIES"].join("\n");
}
function dxfFooter(){
  return ["0","ENDSEC","0","EOF"].join("\n");
}

export async function POST(req: Request) {
  const { plan } = await req.json().catch(()=>({plan:{walls:[]}}));
  const lines: string[] = [];
  (plan?.walls||[]).forEach((w:any) => {
    lines.push("0","LINE","8","Walls","10",String(w.a.x),"20",String(w.a.y),"11",String(w.b.x),"21",String(w.b.y));
  });
  const dxf = [dxfHeader(), ...lines, dxfFooter()].join("\n");
  return new Response(dxf, { headers: { "Content-Type": "application/dxf" } });
}
