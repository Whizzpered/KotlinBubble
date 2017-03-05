package com.whizzpered.bubbleshooter.desktop

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.whizzpered.bubbleshooter.engine.graphics.Atlas
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.*
import javax.imageio.ImageIO
import java.awt.RenderingHints
import com.badlogic.gdx.tools.texturepacker.*
import java.nio.file.FileSystems
import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.TranscoderInput
import java.io.*
import java.nio.file.Paths







class AtlasProcessor {
    data class Picture(val path: String, val type: String, val file: File)

    init {
        val spriteDir = File("../../ToAtlas/Sprites")
        val listFile = File("../../ToAtlas/files.list")

        val list = listPicturesIn(Picture("", "dir", spriteDir))
        val sb = StringBuilder()
        list.forEach {
            sb.append(it.path)
            sb.append('\t')
            sb.append(it.file.lastModified())
            sb.append('\n')
        }
        Atlas.Quality.values().forEach {
            sb.append(it.name)
            sb.append('\t')
            sb.append(it.atlasPath)
            sb.append('\t')
            sb.append(it.scale)
            sb.append('\t')
            sb.append(it.antialiasing)
            sb.append('\n')
        }
        val s = sb.toString()
        if (s != readListFromFile(listFile))
        try {
            Atlas.Quality.values().forEach {
                makeAtlas(list, it.atlasPath, it.scale, it.antialiasing)
            }
            writeLiestToFile(s, listFile)
        } catch (e: Exception) {

        }
    }

    private fun normalizePath(path: String): String {
        val basePath = FileSystems.getDefault().getPath(File("assets").absolutePath)
        val resolvedPath = basePath.parent.resolve(path)
        val abolutePath = resolvedPath.normalize()
        return abolutePath.toString()
    }

    @Throws(IOException::class)
    private fun delete(f: File) {
        if (f.isDirectory) {
            for (c in f.listFiles()!!)
                delete(c)
        }
        if (!f.delete())
            throw FileNotFoundException("Failed to delete file: " + f)
    }

    private fun makeAtlas(list: List<Picture>, name: String, scale: Float, antialiasing: Boolean) {
        val dir = File(normalizePath("../../ToAtlas/Generated/$name"))
        println(dir.absolutePath)
        if (dir.exists())
            delete(dir)
        dir.mkdirs()
        list.forEach {
            try {
                if (it.type == "svg") {
                    val svg_URI_input = normalizePath(it.file.absolutePath)
                    val input_svg_image = TranscoderInput(svg_URI_input)
                    val png_ostream = FileOutputStream(normalizePath("../../ToAtlas/Sprites/${it.path}.png"))
                    val output_png_image = TranscoderOutput(png_ostream)
                    val my_converter = PNGTranscoder()
                    my_converter.transcode(input_svg_image, output_png_image)
                    png_ostream.flush()
                    png_ostream.close()
                }

                var i = ImageIO.read(File(normalizePath("../../ToAtlas/Sprites/${it.path}.png")))
                val b = BufferedImage(
                        (i.width * scale).toInt(),
                        (i.height * scale).toInt(),
                        BufferedImage.TYPE_INT_ARGB
                )
                val g: Graphics2D = b.createGraphics()
                val rh = RenderingHints(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
                g.setRenderingHints(rh)
                g.drawImage(i, 0, 0, b.width, b.height, null)
                g.dispose()
                val f = File(normalizePath("../../ToAtlas/Generated/$name/${it.path.replace("/", "__")}.png"))
                ImageIO.write(b, "png", f)
                if (it.type == "svg")
                    File(normalizePath("../../ToAtlas/Generated/$name/${it.path}.png")).delete()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val settings = TexturePacker.Settings()
        if (antialiasing) {
            settings.filterMag = Texture.TextureFilter.Linear
            settings.filterMin = Texture.TextureFilter.MipMap
        }
        TexturePacker.process(
                settings,
                normalizePath("../../ToAtlas/Generated/$name"),
                normalizePath("../assets"),
                "$name"
        )
    }

    private fun readListFromFile(file: File): String {
        if (!file.exists())
            return ""
        val sb = StringBuilder()
        val s = Scanner(file)
        while (s.hasNextLine()) {
            sb.append(s.nextLine())
            sb.append('\n')
        }
        return sb.toString()
    }

    private fun writeLiestToFile(list: String, file: File) {
        val pw = PrintWriter(file)
        pw.print(list)
        pw.close()
    }

    private fun String.removeLastChars(chars: Int): String {
        return this.substring(0, this.length - chars)
    }

    private fun listPicturesIn(dir: Picture): List<Picture> {
        val ml = mutableListOf<Picture>()
        dir.file.listFiles().forEach {
            if (it.name.endsWith(".png"))
                ml += Picture("${dir.path}${it.name}".removeLastChars(4), "png", it)
            else if (it.name.endsWith(".svg"))
                ml += Picture("${dir.path}${it.name}".removeLastChars(4), "svg", it)
            else if (it.isDirectory)
                ml.addAll(listPicturesIn(Picture("${dir.path}${it.name}/", "dir", it)))
        }
        ml.sortBy { pic -> pic.path }
        return ml
    }
}