package eu.ezytaget.processing.kotlin_template

import processing.core.PConstants.HSB
import kotlin.math.floor

class FluidField(var dt: Float, diffusion: Float, viscosity: Float) {

    var size = 0
    var diff = diffusion
    var visc = viscosity

    private var s: FloatArray
    private var densities: FloatArray

    private var Vx: FloatArray
    private var Vy: FloatArray

    private var Vx0: FloatArray
    private var Vy0: FloatArray

    init {
        size = GlobalConstants.N
        s = FloatArray(GlobalConstants.N * GlobalConstants.N)
        densities = FloatArray(GlobalConstants.N * GlobalConstants.N)
        Vx = FloatArray(GlobalConstants.N * GlobalConstants.N)
        Vy = FloatArray(GlobalConstants.N * GlobalConstants.N)
        Vx0 = FloatArray(GlobalConstants.N * GlobalConstants.N)
        Vy0 = FloatArray(GlobalConstants.N * GlobalConstants.N)
    }

    fun step() {
//        val N = size
        val visc = visc
        val diff = diff
        val dt = dt
        val Vx = Vx
        val Vy = Vy
        val Vx0 = Vx0
        val Vy0 = Vy0
        val s = s
        val density = densities
        diffuse(1, Vx0, Vx, visc, dt)
        diffuse(2, Vy0, Vy, visc, dt)
        project(Vx0, Vy0, Vx, Vy)
        advect(1, Vx, Vx0, Vx0, Vy0, dt)
        advect(2, Vy, Vy0, Vx0, Vy0, dt)
        project(Vx, Vy, Vx0, Vy0)
        diffuse(0, s, density, diff, dt)
        advect(0, density, s, Vx, Vy, dt)
    }

    fun addDensity(x: Int, y: Int, amount: Float) {
        val index = IX(x, y)
        densities[index] += amount
    }

    fun addVelocity(x: Int, y: Int, amountX: Float, amountY: Float) {
        val index = IX(x, y)
        Vx[index] += amountX
        Vy[index] += amountY
    }

    fun drawDensity(pApplet: PApplet) {
        pApplet.colorMode(HSB, 255f)
        val floatScale = GlobalConstants.SCALE.toFloat()
        for (i in 0 until GlobalConstants.N) {
            for (j in 0 until GlobalConstants.N) {
                val x = i * floatScale
                val y = j * floatScale
                val density = densities[IX(i, j)]

//                if (density < 150) {
//                    continue
//                }

                pApplet.fill(100f, (density + 50) % 255, 200f, density)
                pApplet.noStroke()
                pApplet.square(x, y, floatScale)
            }
        }
    }

    fun renderV(pApplet: PApplet) {
        for (i in 0 until GlobalConstants.N) {
            for (j in 0 until GlobalConstants.N) {
                val x = i * GlobalConstants.SCALE.toFloat()
                val y = j * GlobalConstants.SCALE.toFloat()
                val vx = Vx[IX(i, j)]
                val vy = Vy[IX(i, j)]
                pApplet.stroke(255)
                if (!(Math.abs(vx) < 0.1 && Math.abs(vy) <= 0.1)) {
                    pApplet.line(x, y, x + vx * GlobalConstants.SCALE, y + vy * GlobalConstants.SCALE)
                }
            }
        }
    }

    fun fadeD() {
        for (i in densities.indices) {
            val d = densities[i]
            densities[i] = processing.core.PApplet.constrain(d - 0.02f, 0f, 255f)
        }
    }

    private fun diffuse(b: Int, x: FloatArray, x0: FloatArray, diff: Float, dt: Float) {
        val a = dt * diff * (GlobalConstants.N - 2) * (GlobalConstants.N - 2)
        lin_solve(b, x, x0, a, 1 + 4 * a)
    }

    private fun lin_solve(b: Int, x: FloatArray, x0: FloatArray, a: Float, c: Float) {
        val cRecip = 1f / c
        for (k in 0 until GlobalConstants.iter) {
            for (j in 1 until GlobalConstants.N - 1) {
                for (i in 1 until GlobalConstants.N - 1) {
                    x[IX(i, j)] = (x0[IX(i, j)]
                            + a * (x[IX(i + 1, j)]
                            + x[IX(i - 1, j)]
                            + x[IX(i, j + 1)]
                            + x[IX(i, j - 1)])) * cRecip
                }
            }
            set_bnd(b, x)
        }
    }

    private fun project(velocX: FloatArray, velocY: FloatArray, p: FloatArray, div: FloatArray) {
        for (j in 1 until GlobalConstants.N - 1) {
            for (i in 1 until GlobalConstants.N - 1) {
                div[IX(i, j)] = -0.5f * (velocX[IX(i + 1, j)]
                        - velocX[IX(i - 1, j)]
                        + velocY[IX(i, j + 1)]
                        - velocY[IX(i, j - 1)]) / GlobalConstants.N
                p[IX(i, j)] = 0f
            }
        }
        set_bnd(0, div)
        set_bnd(0, p)
        lin_solve(0, p, div, 1f, 4f)
        for (j in 1 until GlobalConstants.N - 1) {
            for (i in 1 until GlobalConstants.N - 1) {
                velocX[IX(i, j)] -= 0.5f * (p[IX(i + 1, j)]
                        - p[IX(i - 1, j)]) * GlobalConstants.N
                velocY[IX(i, j)] -= 0.5f * (p[IX(i, j + 1)]
                        - p[IX(i, j - 1)]) * GlobalConstants.N
            }
        }
        set_bnd(1, velocX)
        set_bnd(2, velocY)
    }

    private fun advect(b: Int, d: FloatArray, d0: FloatArray, velocX: FloatArray, velocY: FloatArray, dt: Float) {
        var i0: Float
        var i1: Float
        var j0: Float
        var j1: Float
        val dtx = dt * (GlobalConstants.N - 2)
        val dty = dt * (GlobalConstants.N - 2)
        var s0: Float
        var s1: Float
        var t0: Float
        var t1: Float
        var tmp1: Float
        var tmp2: Float
        var x: Float
        var y: Float
        val nFloat = GlobalConstants.N.toFloat()
        var iFloat: Float
        var i: Int
        var j = 1
        var jfloat = 1f
        while (j < GlobalConstants.N - 1) {
            i = 1
            iFloat = 1f
            while (i < GlobalConstants.N - 1) {
                tmp1 = dtx * velocX[IX(i, j)]
                tmp2 = dty * velocY[IX(i, j)]
                x = iFloat - tmp1
                y = jfloat - tmp2
                if (x < 0.5f) {
                    x = 0.5f
                } else if (x > nFloat + 0.5f) {
                    x = nFloat + 0.5f
                }
                i0 = floor(x)
                i1 = i0 + 1.0f

                if (y < 0.5f) {
                    y = 0.5f
                } else if (y > nFloat + 0.5f) {
                    y = nFloat + 0.5f
                }

                j0 = floor(y)
                j1 = j0 + 1.0f
                s1 = x - i0
                s0 = 1.0f - s1
                t1 = y - j0
                t0 = 1.0f - t1
                val i0i = i0.toInt()
                val i1i = i1.toInt()
                val j0i = j0.toInt()
                val j1i = j1.toInt()

                // DOUBLE CHECK THIS!!!
                d[IX(i, j)] = s0 * (t0 * d0[IX(i0i, j0i)] + t1 * d0[IX(i0i, j1i)]) +
                        s1 * (t0 * d0[IX(i1i, j0i)] + t1 * d0[IX(i1i, j1i)])
                i++
                iFloat++
            }
            j++
            jfloat++
        }
        set_bnd(b, d)
    }

    private fun set_bnd(b: Int, x: FloatArray) {
        for (i in 1 until GlobalConstants.N - 1) {
            x[IX(i, 0)] = if (b == 2) -x[IX(i, 1)] else x[IX(i, 1)]
            x[IX(i, GlobalConstants.N - 1)] = if (b == 2) -x[IX(i, GlobalConstants.N - 2)] else x[IX(i, GlobalConstants.N - 2)]
        }
        for (j in 1 until GlobalConstants.N - 1) {
            x[IX(0, j)] = if (b == 1) -x[IX(1, j)] else x[IX(1, j)]
            x[IX(GlobalConstants.N - 1, j)] = if (b == 1) -x[IX(GlobalConstants.N - 2, j)] else x[IX(GlobalConstants.N - 2, j)]
        }
        x[IX(0, 0)] = 0.5f * (x[IX(1, 0)] + x[IX(0, 1)])
        x[IX(0, GlobalConstants.N - 1)] = 0.5f * (x[IX(1, GlobalConstants.N - 1)] + x[IX(0, GlobalConstants.N - 2)])
        x[IX(GlobalConstants.N - 1, 0)] = 0.5f * (x[IX(GlobalConstants.N - 2, 0)] + x[IX(GlobalConstants.N - 1, 1)])
        x[IX(GlobalConstants.N - 1, GlobalConstants.N - 1)] = 0.5f * (x[IX(GlobalConstants.N - 2, GlobalConstants.N - 1)] + x[IX(GlobalConstants.N - 1, GlobalConstants.N - 2)])
    }

    private fun IX(x: Int, y: Int): Int {
        var x = x
        var y = y
        x = processing.core.PApplet.constrain(x, 0, GlobalConstants.N - 1)
        y = processing.core.PApplet.constrain(y, 0, GlobalConstants.N - 1)
        return x + y * GlobalConstants.N
    }
}