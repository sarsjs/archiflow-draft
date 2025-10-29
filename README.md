# ArchiFlow Draft — Monorepo

Android (Kotlin/Compose) + Web/API (Vercel).

## Estructura
```
archiflow-draft/
├─ android/                 # App Android (Kotlin/Compose, Jetpack Compose)
├─ api/                     # Rutas serverless para Vercel (Next.js App Router style)
├─ web/                     # Landing/visor simple
├─ .github/workflows/       # CI para Android
└─ README.md
```

## Pasos rápidos

### 1) Android
- Abre `android/` en Android Studio (Arctic Fox+).
- Sincroniza Gradle; ejecuta la app en un emulador o dispositivo.
- El módulo muestra un lienzo con captura de trazos y exportación PDF básica.

### 2) GitHub Actions (CI)
- Sube el repo a GitHub.
- El workflow en `.github/workflows/android-ci.yml` construye el APK `debug` en cada push y, si etiquetas `v*`, genera AAB para release.
- Añade secretos si deseas firmar el release: `SIGNING_*` (ver archivo).

### 3) Vercel
- Conecta el repo en vercel.com y despliega.
- Las rutas:
  - `POST /api/vectorize` → devuelve SVG simplificado (placeholder)
  - `POST /api/dxf` → devuelve DXF mínimo (placeholder)
- La landing en `web/` se sirve en `/`.

> Nota: El código de vectores/DXF es de demostración; sustitúyelo por tu lógica real.
