export default function Home() {
  return (
    <main style={{minHeight:'100vh',background:'#0f0f0f',color:'#eaeaea',display:'grid',placeItems:'center'}}>
      <div style={{maxWidth:760,padding:24}}>
        <h1>ArchiFlow Draft</h1>
        <p>App Android para bocetar con stylus y convertir en plano técnico con IA.</p>
        <ul>
          <li>Sketch → Plan (cotas)</li>
          <li>Export PDF/SVG/DXF</li>
          <li>Preview 3D (roadmap)</li>
        </ul>
      </div>
    </main>
  )
}
