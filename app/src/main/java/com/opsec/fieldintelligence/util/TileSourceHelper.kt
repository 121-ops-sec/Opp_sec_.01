package com.opsec.fieldintelligence.util

import android.content.Context
import org.osmdroid.tileprovider.tilesource.ITileSource
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.views.overlay.TilesOverlay
import java.io.File

object TileSourceHelper {

    /**
     * Standard OpenStreetMap tile source — used as base when online, also cached for offline.
     */
    fun buildOnlineTileSource(): ITileSource = TileSourceFactory.MAPNIK

    /**
     * ESRI World Imagery — overlaid as a semi-transparent second layer when satellite mode enabled.
     * No API key required for basic use.
     */
    fun buildSatelliteTileSource(): ITileSource = XYTileSource(
        "ESRI_World_Imagery",
        0, 19, 256, ".jpg",
        arrayOf(
            "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/"
        ),
        "Tiles \u00a9 Esri, DigitalGlobe, GeoEye, Earthstar Geographics, CNES/Airbus DS"
    )

    /**
     * Returns the directory where users should sideload offline tile archives (.zip or .mbtiles).
     * Path: Android/data/com.opsec.fieldintelligence/files/tiles/
     */
    fun getOfflineTilesDir(context: Context): File {
        return File(context.getExternalFilesDir(null), "tiles").also { it.mkdirs() }
    }

    /**
     * Returns a list of sideloaded tile archive files found in the offline tiles directory.
     */
    fun findOfflineArchives(context: Context): Array<File> {
        val dir = getOfflineTilesDir(context)
        return dir.listFiles { f -> f.extension in listOf("zip", "mbtiles") } ?: emptyArray()
    }

    /**
     * Returns the OSMDroid tile cache directory.
     */
    fun getTileCacheDir(context: Context): File =
        File(context.getExternalFilesDir(null), "osmdroid").also { it.mkdirs() }
}
