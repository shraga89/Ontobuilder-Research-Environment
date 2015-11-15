package ac.technion.schemamatching.matchers.firstline;
import java.lang.Math;

public class ChiSquareUtils {
    private static final double LOG_SQRT_PI = Math.log(Math.sqrt(Math.PI));
    private static final double I_SQRT_PI = 1 / Math.sqrt(Math.PI);
    public static final int MAX_X = 20; // max value to represent exp(x)
 
   /* POCHISQ -- probability of chi-square value
        Adapted from:
        Hill, I. D. and Pike, M. C. Algorithm 299
        Collected Algorithms for the CACM 1967 p. 243
        Updated for rounding errors based on remark in
        ACM TOMS June 1985, page 185
    */
    public static double pochisq(double x, int df) {
        double a, s;
        double e, c, z;
 
        if (x <= 0.0 || df < 1) {
            return 1.0;
        }
        a = 0.5 * x;
        boolean even = (df & 1) == 0;
        double y = 0;
        if (df > 1) {
            y = ex(-a);
        }
        s = (even ? y : (2.0 * poz(-1*Math.sqrt(x))));
        if (df > 2) {
            x = 0.5 * (df - 1.0);
            z = (even ? 1.0 : 0.5);
            if (a > MAX_X) {
                e = (even ? 0.0 : LOG_SQRT_PI);
                c = Math.log(a);
                while (z <= x) {
                    e = Math.log(z) + e;
                    s += ex(c * z - a - e);
                    z += 1.0;
                }
                return s;
            } else {
                e = (even ? 1.0 : (I_SQRT_PI / Math.sqrt(a)));
                c = 0.0;
                while (z <= x) {
                    e = e * (a / z);
                    c = c + e;
                    z += 1.0;
                }
                return c * y + s;
            }
        } else {
            return s;
        }
    }
 
 
    public static double poz(double z) {
        double y, x, w;
        double Z_MAX = 6.0; // Maximum meaningful z value 
        if (z == 0.0) {
            x = 0.0;
        } else {
            y = 0.5 * Math.abs(z);
            if (y >= (Z_MAX * 0.5)) {
                x = 1.0;
            } else if (y < 1.0) {
                w = y * y;
                x = ((((((((0.000124818987 * w
                        - 0.001075204047) * w + 0.005198775019) * w
                        - 0.019198292004) * w + 0.059054035642) * w
                        - 0.151968751364) * w + 0.319152932694) * w
                        - 0.531923007300) * w + 0.797884560593) * y * 2.0;
            } else {
                y -= 2.0;
                x = (((((((((((((-0.000045255659 * y
                        + 0.000152529290) * y - 0.000019538132) * y
                        - 0.000676904986) * y + 0.001390604284) * y
                        - 0.000794620820) * y - 0.002034254874) * y
                        + 0.006549791214) * y - 0.010557625006) * y
                        + 0.011630447319) * y - 0.009279453341) * y
                        + 0.005353579108) * y - 0.002141268741) * y
                        + 0.000535310849) * y + 0.999936657524;
            }
        }
        return z > 0.0 ? ((x + 1.0) * 0.5) : ((1.0 - x) * 0.5);
    }
 
 
    public static double ex(double x) {
        return (x < -MAX_X) ? 0.0 : Math.exp(x);
    }

	
    public static void main(String[] args) {
	 double x = pochisq(18.5486,6);
	}
}