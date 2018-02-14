package pl.marpiec.neuralnetworks.drivinggame

class Point(val x: Double, val y: Double)

object Geometry {

    fun lineIntersection(p0_x: Double, p0_y: Double, p1_x: Double, p1_y: Double,
                         p2_x: Double, p2_y: Double, p3_x: Double, p3_y: Double): Point? {


        val s10_x = p1_x - p0_x;
        val s10_y = p1_y - p0_y;
        val s32_x = p3_x - p2_x;
        val s32_y = p3_y - p2_y;

        val denom = s10_x * s32_y - s32_x * s10_y;
        if (denom == 0.0)
            return null; // Collinear
        val denomPositive = denom > 0;

        val s02_x = p0_x - p2_x;
        val s02_y = p0_y - p2_y;
        val s_numer = s10_x * s02_y - s10_y * s02_x;
        if ((s_numer < 0) == denomPositive)
            return null; // No collision

        val t_numer = s32_x * s02_y - s32_y * s02_x;
        if ((t_numer < 0) == denomPositive)
            return null; // No collision

        if (((s_numer > denom) == denomPositive) || ((t_numer > denom) == denomPositive))
            return null; // No collision
        // Collision detected
        val t = t_numer / denom;
        return Point(p0_x + (t * s10_x), p0_y + (t * s10_y))
    }


}