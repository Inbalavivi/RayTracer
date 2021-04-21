import java.util.ArrayList;
import java.util.List;
    public class Intersection {
        double min_t;
        Surface firstSurface;

        public Intersection(double min_t, Surface firstSurface) {
            this.firstSurface = firstSurface;
            this.min_t = min_t;
        }

        public static Intersection getIntersection(Ray ray, List<Surface> Surfaces) { /// return [min_t, firstSurface]
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

        public List<Intersection> getAllIntersections(Ray ray, List<Surface> Surfaces) { /// return [min_t, firstSurface]
            //double min_t = Double.MAX_VALUE; // min_t = infinity
            List<Intersection> allIntersections = new ArrayList<>();
            Surface firstSurface = null;
            //Object[] intersection = new Object[2];
            double t;
            for (Surface s : Surfaces) {
                t = s.intersect(ray);
                if ( t > 0) {
                    //firstSurface = s;
                    //min_t = t;
                    Intersection i=new Intersection(t,s);
                    allIntersections.add(i);
                }
            }
            //intersection[0] = min_t;
            //intersection[1] = firstSurface;
            return allIntersections;
        }

        public static int checkIfIntersect(Ray ray, Scene scene, double Magnitude) {
            double t;
            for (Surface s : scene.surfaces) {
                t = s.intersect(ray);
                if (t < Magnitude && t > 0) {
                    return 1;
                }
            }
            return 0;
        }

}
