/*
Based on the Inverse Kinematics project by Daniel Shiffman
http://codingtra.in
http://patreon.com/codingtrain
Video: https://youtu.be/hbgDqyy8bIw
 */

package eu.ezytaget.processing.kotlin_template.boids

import processing.core.PApplet
import processing.core.PApplet.map
import processing.core.PVector
import kotlin.math.cos
import kotlin.math.sin


internal class Segment {
    var a: PVector
    var angle = 0f
    var len: Float
    var b = PVector()
    var parent: Segment? = null
    var child: Segment? = null
    var sw = 0f

    constructor(x: Float, y: Float, len_: Float, i: Int) {
        a = PVector(x, y)
        sw = map(i.toFloat(), 0f, 20f, 1f, 10f)
        len = len_
        calculateB()
    }

    constructor(parent_: Segment?, len_: Float, i: Int) {
        parent = parent_
        sw = map(i.toFloat(), 0f, 20f, 1f, 10f)
        a = parent!!.b.copy()
        len = len_
        calculateB()
    }

    fun follow() {
        val targetX = child!!.a.x
        val targetY = child!!.a.y
        follow(targetX, targetY)
    }

    fun follow(tx: Float, ty: Float) {
        val target = PVector(tx, ty)
        val dir = PVector.sub(target, a)
        angle = dir.heading()
        dir.setMag(len)
        dir.mult(-1f)
        a = PVector.add(target, dir)
    }

    fun calculateB() {
        val dx = len * cos(angle)
        val dy = len * sin(angle)
        b[a.x + dx] = a.y + dy
    }

    fun update() {
        calculateB()
    }

    fun show(pApplet: PApplet) {
        pApplet.stroke(1f, 0.5f)
        pApplet.strokeWeight(1f)
        pApplet.line(a.x, a.y, b.x - 1, b.y - 1)
    }
}