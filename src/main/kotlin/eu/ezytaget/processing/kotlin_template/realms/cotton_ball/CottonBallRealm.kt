package eu.ezytaget.processing.kotlin_template.realms.cotton_ball

import processing.core.*
import processing.core.PApplet.map

import processing.core.PConstants.ELLIPSE

import processing.opengl.PShapeOpenGL.createShape
import java.lang.Math.pow
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random


class CottonBallRealm {

//    /* ---------------------------------------------------------------------- */
//    abstract class PshapeElement {
//        var anElement: PShape
//        var elementColor: Float
//        var elementSaturation: Float
//        var elementBright: Float
//        var elementAlpha: Float
//        abstract fun pscreateElement(): PShape
//        fun setElementFill(pcolor: Float, psaturation: Float, pbright: Float, palpha: Float) {
//            elementColor = pcolor
//            elementSaturation = psaturation
//            elementBright = pbright
//            elementAlpha = palpha
//            resetColor()
//        }
//
//        fun resetColor(pGraphics: PGraphics) {
//            anElement.setFill(pGraphics.color(elementColor, elementSaturation, elementBright, elementAlpha))
//            anElement.setStroke(pGraphics.color(elementColor, elementSaturation, elementBright, elementAlpha))
//        }
//
//        fun changeColor(scolor: Float) {
//            elementColor = scolor
//            resetColor()
//        }
//
//        fun changeBright(sbright: Float) {
//            elementBright = sbright
//            resetColor()
//        }
//
//        fun resetSize() {
//            anElement.resetMatrix()
//        }
//
//        fun changeSize(scaleX: Float, scaleY: Float, scaleZ: Float) {
//            anElement.scale(scaleX, scaleY, scaleZ)
//        }
//
//        fun rotate(radX: Float, radY: Float, radZ: Float) {
//            anElement.rotateX(radX)
//            anElement.rotateY(radY)
//            anElement.rotateZ(radZ)
//        }
//
//        fun show(pGraphics: PGraphics) {
//            pGraphics.shape(anElement)
//        }
//
//        init {
//            anElement = pscreateElement()
//            elementColor = 0f
//            elementSaturation = 0f
//            elementBright = 0f
//            elementAlpha = 0f
//        }
//    }
//
//    /* ---------------------------------------------------------------------- */
//    internal class RoundBrush : PshapeElement() {
//        override fun pscreateElement(): PShape {
////            stroke(0)
////            noFill()
//            return createShape(ELLIPSE, 0.0, 0.0, 10.0, 10.0)
//        }
//    }
//
//    /* ---------------------------------------------------------------------- */
//    class Utils {
//        var obj_random: Random
//
//        fun gaussdist(pmean: Float, plimit: Float, pdevi: Float): Float {
//            /**
//             * Gaussian distribution
//             * 1.parameters.
//             * pmean  : mean value
//             * plimit : max value of abs(deviation)
//             * ex. plimit >= 0
//             * pmean = 0.5, plimit = 0.5 -> return value = from 0.0 to 1.0
//             * pdevi  : standard deviation value
//             * ex. good value? -> pdevi = plimit / 2
//             * 2.return.
//             * gaussian distribution
//             */
//            if (plimit == 0f) {
//                return pmean
//            }
//            var gauss = obj_random.nextGaussian() as Float * pdevi
//            // not good idea
//            if (abs(gauss) > plimit) {
//                gauss = pow(plimit, 2f) / gauss
//            }
//            return pmean + gauss
//        }
//
//        init {
//            obj_random = Random()
//        }
//    }
//
///* ---------------------------------------------------------------------- */
//
//    /* ---------------------------------------------------------------------- */
//    var ut: Utils? = null
//    var pRound: PshapeElement? = null, var pEllipse:PshapeElement? = null
//
//    fun setup() {
//        frameRate(60f)
//        size(1200, 1200, PConstants.P3D)
//        colorMode(PConstants.HSB, 360, 100, 100, 100)
//        blendMode(PConstants.SCREEN)
//        strokeWeight(0.0001) // sensitive!
//        smooth(8)
//        //noLoop();
//        //  frameRate(1);
//        ut = Utils()
//        pRound = RoundBrush()
//    }
//
//    fun draw(pGraphics: PGraphics) {
//        pGraphics.background(0, 0, 8)
//        pGraphics.translate(0, 0, 0)
//        pGraphics.camera(0, 1000, 1400,
//                0, 0, 0,
//                0, 1, 0)
//        drawFace()
//    }
//
//
//    fun drawFace(pGraphics: PGraphics) {
//        val locateRound = PVector(0, 0, 0)
//        val divIdo = 0.91f
//        var divKdo = 0.0f // dummy
//        val circleBase = 8.0f
//        val circleMult = 14.0f
//        val radiusRound = 500.0f
//        val baseColor: Float = pGraphics.random(110, 300) // yellow-green are no good for me.
//        var noiseOmtIdo: Float = pGraphics.random(50)
//        var noiseSatIdo: Float = pGraphics.random(50)
//        var noiseBriIdo: Float = pGraphics.random(50)
//        var noiseSizIdo: Float = pGraphics.random(50)
//        val noiseOmtKdoStarter: Float = pGraphics.random(50)
//        val noiseSatKdoStarter: Float = pGraphics.random(50)
//        val noiseBriKdoStarter: Float = pGraphics.random(50)
//        val noiseSizKdoStarter: Float = pGraphics.random(50)
//        var ido = 80f
//        while (ido <= 120) {
//            // Y
//            val radianIdo = PApplet.radians(ido)
//            divKdo = 360f / max(180f / divIdo * sin(radianIdo), 1f)
//            var noiseOmtKdo = noiseOmtKdoStarter
//            var noiseSatKdo = noiseSatKdoStarter
//            var noiseBriKdo = noiseBriKdoStarter
//            var noiseSizKdo = noiseSizKdoStarter
//            var kdo = 0f
//            while (kdo <= 360 - divKdo) {
//                // Z
//
//                // do not draw some part
//                if (noise(noiseOmtIdo, noiseOmtKdo) > ut!!.gaussdist(0.70f, 0.5f, 0.25f)) {
//                    val radianKdo = PApplet.radians(kdo)
//                    locateRound[radiusRound * cos(radianKdo) * sin(radianIdo), radiusRound * sin(radianKdo) * sin(radianIdo)] = radiusRound * cos(radianIdo)
//                    val roundSize: Float = map(noise(noiseSizIdo, noiseSizKdo), 0.0, 1.0, circleMult * circleBase, circleBase)
//                    val roundHue: Float = (baseColor + map(noise(noiseBriIdo, noiseBriKdo), 0.0, 1.0, 0, 160)) % 360
//                    val roundSat: Float = map(noise(noiseSatIdo, noiseSatKdo), 0.0, 1.0, 10.0, 100.0)
//                    val roundBri: Float = map(noise(noiseBriIdo, noiseBriKdo), 0.0, 1.0, 20.0, 255.0)
//                    val roundAlp = 255f
//                    val fctBri: Float = map(locateRound.z, -radiusRound, radiusRound, 0.1, 1.2)
//                    pGraphics.pushMatrix()
//                    pGraphics.translate(locateRound.x, locateRound.y, locateRound.z)
//                    pGraphics.rotateZ(radianKdo) // must be this order Z -> Y
//                    pGraphics.rotateY(radianIdo)
//                    pRound!!.resetSize()
//                    pRound!!.changeSize(roundSize, roundSize, 1.0f)
//                    pRound!!.setElementFill(roundHue, roundSat, roundBri * fctBri, roundAlp)
//                    pRound!!.show()
//                    pGraphics.popMatrix()
//                }
//                noiseOmtKdo += 0.008f
//                noiseSatKdo += 0.006f
//                noiseBriKdo += 0.005f
//                noiseSizKdo += 0.003f
//                kdo += divKdo
//            }
//            noiseOmtIdo += 0.008f
//            noiseSatIdo += 0.006f
//            noiseBriIdo += 0.005f
//            noiseSizIdo += 0.003f
//            ido += divIdo
//        }
//    }
}