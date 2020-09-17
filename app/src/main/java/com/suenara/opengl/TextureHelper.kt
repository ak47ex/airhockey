package com.suenara.opengl

import android.graphics.Bitmap
import android.opengl.GLES20.GL_LINEAR
import android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR
import android.opengl.GLES20.GL_LINEAR_MIPMAP_NEAREST
import android.opengl.GLES20.GL_NEAREST
import android.opengl.GLES20.GL_NEAREST_MIPMAP_LINEAR
import android.opengl.GLES20.GL_NEAREST_MIPMAP_NEAREST
import android.opengl.GLES20.GL_TEXTURE_2D
import android.opengl.GLES20.GL_TEXTURE_MAG_FILTER
import android.opengl.GLES20.GL_TEXTURE_MIN_FILTER
import android.opengl.GLES20.glBindTexture
import android.opengl.GLES20.glGenTextures
import android.opengl.GLES20.glGenerateMipmap
import android.opengl.GLES20.glTexParameteri
import android.opengl.GLUtils

object TextureHelper {
    private const val TAG = "TextureHelper"

    fun loadTexture(
        bitmap: Bitmap,
        minFilter: MinFilter = MinFilter.TRILINEAR,
        magFilter: MagFilter = MagFilter.BILINEAR
    ): Int {
        val textureObjectIds = intArrayOf(0).also { glGenTextures(1, it, 0) }.first()
        requireHandler(textureObjectIds) { "Could not generate a new OpenGL texture object." }
        glBindTexture(GL_TEXTURE_2D, textureObjectIds)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter.glConst)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter.glConst)

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)

        bitmap.recycle()

        glGenerateMipmap(GL_TEXTURE_2D)

        glBindTexture(GL_TEXTURE_2D, 0)

        return textureObjectIds
    }

    enum class MinFilter(val glConst: Int) {
        NEAREST(GL_NEAREST),
        NEAREST_MIPMAP(GL_NEAREST_MIPMAP_NEAREST),
        NEAREST_MIPMAP_INTERPOLATED(GL_NEAREST_MIPMAP_LINEAR),
        BILINEAR(GL_LINEAR),
        BILINEAR_MIPMAP(GL_LINEAR_MIPMAP_NEAREST),
        TRILINEAR(GL_LINEAR_MIPMAP_LINEAR)
    }

    enum class MagFilter(val glConst: Int) {
        NEAREST(GL_NEAREST),
        BILINEAR(GL_LINEAR)
    }
}