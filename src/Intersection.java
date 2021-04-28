import java.util.ArrayList;
import java.util.List;
public class Intersection {
    double min_t;
    Surface firstSurface;

    public Intersection(double min_t, Surface firstSurface) {
        this.firstSurface = firstSurface;
        this.min_t = min_t ;
    }

    public static Intersection getMinIntersection(Ray ray, List<Surface> Surfaces) {
        double min_t = Double.MAX_VALUE; // min_t = infinity
        Surface firstSurface = null;
        double t;
        for (Surface s : Surfaces) {
            t = s.intersect(ray);
            if (t < min_t && t > 0) {
                firstSurface = s;
                min_t = t;
            }
        }
        Intersection intersection = new Intersection(min_t,firstSurface);
        return intersection;
    }

    public static List<Intersection> getAllIntersections(Ray ray, List<Surface> Surfaces) {
        List<Intersection> allIntersections = new ArrayList<>();
        double t;
        for (Surface s : Surfaces) {
            t = s.intersect(ray);
            if (t > 0) {
                Intersection i = new Intersection(t,s);
                allIntersections.add(i);
            }
        }
        return allIntersections;
    }
}
