
package com.thesis.distanceguard.camera_module

import java.nio.ByteBuffer

interface FrameProcessor {

    fun process(data: ByteBuffer, frameMetadata: FrameMetadata, graphicOverlay: GraphicOverlay)

    fun stop()
}
