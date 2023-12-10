// Based on Daniel Shiffman's work which cites the following resources:
// http://youtube.com/thecodingtrain
// http://codingtra.in

// Coding Challenge #113: 4D Hypercube
// https://youtu.be/XE3YDVdQSPo

// Matrix Multiplication
// https://youtu.be/tzsgS19RRc8

package eu.ezytarget.processing_template.realms.tesseract

import processing.core.PVector


object MatrixCalculator {

    fun vector4ToMatrix(v: P4Vector): Array<FloatArray> {
        val m = Array(4) { FloatArray(1) }
        m[0][0] = v.x
        m[1][0] = v.y
        m[2][0] = v.z
        m[3][0] = v.w
        return m
    }

    fun matrixToVector3(matrix: Array<FloatArray>) = PVector(
            matrix[0][0],
            matrix[1][0],
            matrix[2][0]
    )

    fun matrixToVector4(matrix: Array<FloatArray>) = P4Vector(
            x = matrix[0][0],
            y = matrix[1][0],
            z = matrix[2][0],
            w = matrix[3][0]
    )

    fun matmul(a: Array<FloatArray>, b: P4Vector): PVector {
        val m = vector4ToMatrix(b)
        return matrixToVector3(matmul(a, m))
    }

    fun matmul4(a: Array<FloatArray>, b: P4Vector): P4Vector {
        val m = vector4ToMatrix(b)
        return matrixToVector4(matmul(a, m))
    }

    fun matmul(a: Array<FloatArray>, b: Array<FloatArray>): Array<FloatArray> {
        val colsA: Int = a[0].size
        val rowsA = a.size
        val colsB: Int = b[0].size
        val rowsB = b.size
        if (colsA != rowsB) {
            error("Columns of A must match rows of B")
        }

        val result = Array(rowsA) { FloatArray(colsB) }
        for (i in 0 until rowsA) {
            for (j in 0 until colsB) {
                var sum = 0f
                for (k in 0 until colsA) {
                    sum += a[i][k] * b[k][j]
                }
                result[i][j] = sum
            }
        }
        return result
    }

}