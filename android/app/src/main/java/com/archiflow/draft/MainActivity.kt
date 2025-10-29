package com.archiflow.draft

import android.graphics.pdf.PdfDocument
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File
import java.util.UUID

data class Pt(val x: Float, val y: Float)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(Modifier.fillMaxSize()) {
            DrawingScreen()
        }
    }
}

@Composable
fun DrawingScreen() {
    val scope = rememberCoroutineScope()
    val turquoise = Color(0xFF00E6B8)
    val bg = Color(0xFF1C1C1C)
    val grid = Color(0xFF2A2A2A)

    var strokes by remember { mutableStateOf(listOf<List<Pt>>()) }
    var current by remember { mutableStateOf(listOf<Pt>()) }
    val context = LocalContext.current

    Column(Modifier.fillMaxSize().background(bg)) {
        TopAppBar(title = { Text("ArchiFlow Draft") })
        Row(Modifier.fillMaxSize()) {
            // Lienzo
            Canvas(Modifier.weight(1f).fillMaxHeight()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> current = listOf(Pt(offset.x, offset.y)) },
                        onDrag = { change, _ ->
                            // Solo tomar stylus si está disponible; en móviles aceptar finger.
                            if (change.type == PointerType.Stylus || change.type == PointerType.Touch) {
                                current = current + Pt(change.position.x, change.position.y)
                            }
                        },
                        onDragEnd = { strokes = strokes + listOf(current); current = emptyList() }
                    )
                }
            ) {
                // grid
                val step = 32f
                for (x in 0..(size.width/step).toInt()) {
                    drawLine(grid, Offset(x*step,0f), Offset(x*step, size.height))
                }
                for (y in 0..(size.height/step).toInt()) {
                    drawLine(grid, Offset(0f, y*step), Offset(size.width, y*step))
                }
                // strokes
                strokes.forEach { s ->
                    for (i in 1 until s.size) {
                        drawLine(turquoise, Offset(s[i-1].x, s[i-1].y), Offset(s[i].x, s[i].y), strokeWidth = 3f)
                    }
                }
                for (i in 1 until current.size) {
                    drawLine(turquoise, Offset(current[i-1].x, current[i-1].y), Offset(current[i].x, current[i].y), strokeWidth = 3f)
                }
            }

            // Panel lateral simple
            Column(Modifier.width(220.dp).fillMaxHeight().padding(12.dp)) {
                Text("Herramientas", color = Color.White)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { strokes = emptyList(); current = emptyList() }) { Text("Nuevo") }
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    // Exportación PDF muy básica
                    val doc = PdfDocument()
                    val pageInfo = PdfDocument.PageInfo.Builder(1080, 1440, 1).create()
                    val page = doc.startPage(pageInfo)
                    val c = page.canvas
                    // Fondo
                    c.drawColor(android.graphics.Color.rgb(28,28,28))
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.CYAN
                        strokeWidth = 3f
                        style = android.graphics.Paint.Style.STROKE
                    }
                    strokes.forEach { s ->
                        for (i in 1 until s.size) {
                            c.drawLine(s[i-1].x, s[i-1].y, s[i].x, s[i].y, paint)
                        }
                    }
                    doc.finishPage(page)
                    val out = File(context.getExternalFilesDir(null), "sketch-${UUID.randomUUID()}.pdf")
                    doc.writeTo(out.outputStream())
                    doc.close()
                }) { Text("Exportar PDF") }

                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    scope.launch {
                        val file = Network.postVectorize(context, strokes)
                        if (file != null) {
                            Toast.makeText(context, "SVG guardado: ${file.name}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error al vectorizar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) { Text("Vectorizar (Vercel)") }
            }
        }
    }
}
