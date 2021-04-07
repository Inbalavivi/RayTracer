package RayTracing;

import java.util.List;

public class Intersection {
	// double min_t;
	// Primitive min_primitive;

	// public Intersection(double min_t, Primitive min_primitive) {
	// 	this.min_primitive = min_primitive;
	// 	this.min_t = min_t;
	// }

	// public static Intersection FindIntersction(Ray ray, List<Primitive> Primitives) {
	// 	double t;
	// 	double min_t = Double.MAX_VALUE;
	// 	Primitive min_primitive = null;
	// 	for (Primitive primitive : Primitives) {
	// 		t = primitive.intersecte(ray);
	// 		if (t < min_t && t > 0) {
	// 			min_primitive = primitive;
	// 			min_t = t;
	// 		}

	// 	}
	// 	Intersection intersection = new Intersection(min_t, min_primitive);
	// 	return intersection;
	// }

	// public static boolean isIntersect(Ray ray, Scene scene, double disToBeat) {
	// 	double t;
	// 	for (Primitive primitive : scene.Primitives) {
	// 		t = primitive.intersecte(ray);
	// 		if (t < disToBeat && t > 0) {
	// 			return true;
	// 		}
	// 	}
	// 	return false;
	// }

}