package com.archiflow.draft

import android.content.Context
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

object Network {
    // TODO: Reemplaza por tu dominio real de Vercel
    var baseUrl = "https://archiflow-draft.vercel.app"
    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    fun strokesToJson(strokes: List<List<Pt>>): String {
        val arr = JSONArray()
        for (s in strokes) {
            val points = JSONArray()
            for (p in s) {
                val obj = JSONObject()
                obj.put("x", p.x)
                obj.put("y", p.y)
                points.put(obj)
            }
            val stroke = JSONObject()
            stroke.put("tool", "pen")
            stroke.put("points", points)
            arr.put(stroke)
        }
        val root = JSONObject()
        root.put("strokes", arr)
        return root.toString()
    }

    fun postVectorize(context: Context, strokes: List<List<Pt>>): File? {
        val json = strokesToJson(strokes)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val reqBody = json.toRequestBody(mediaType)
        val req = Request.Builder()
            .url("$baseUrl/api/vectorize")
            .post(reqBody)
            .build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return null
            val body = resp.body?.string() ?: return null
            // Esperamos { svg: "<svg ...>" }
            val svg = JSONObject(body).optString("svg", "")
            if (svg.isBlank()) return null
            val out = File(context.getExternalFilesDir(null), "vectorized.svg")
            out.writeText(svg)
            return out
        }
    }
}
