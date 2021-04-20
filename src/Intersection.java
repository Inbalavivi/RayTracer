import java.util.List;
//public class Intersection {
//	double min_t;
//	Surface firstSurface;

//	public Intersection(double min_t, Surface firstSurface) {
//		this.firstSurface = firstSurface;
//		this.min_t = min_t;
//	}

//	public static Intersection getIntersction(Ray ray, List<Surface> Surfaces) {
//		double t;
//		double min_t = Double.MAX_VALUE;
//		Surface firstSurface = null;
//		for (Surface s : Surfaces) {
//			t = s.intersect(ray);
//			if (t < min_t && t > 0) {
//				firstSurface = s;
//				min_t = t;
//			}
//
//		}
//		Intersection intersection = new Intersection(min_t, firstSurface);
//		return intersection;
//	}
//
//	public static int isIntersect(Ray ray, Scene scene, double Magnitud) {
//		double t;
//		for (Surface s : scene.surfaces) {
//			t = s.intersect(ray);
//			if (t < Magnitud && t > 0) {
//				return 1;
//			}
//		}
//		return 0;
//	}

//}
