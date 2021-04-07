package RayTracing;

import java.util.List;

public class Scene {
	Camera cam;
	GeneralSettings settings;
    List<Surface> surfaces;
    List<Light> lights;
	List<Material> materials;

    public Scene(Camera cam, GeneralSettings settings, List<Surface> surfaces, List<Light> lights, List<Material> materials){
        this.cam = cam;
        this.settings = settings;
        this.surfaces = surfaces;
        this.lights = lights;
        this.materials = materials;
    }

    private double[] colorRGB(Intersection hit, Ray ray, int recDepth) {
		if (recDepth == 0) {
			double[] color = {settings.backgroundCol[0], settings.backgroundCol[1], settings.backgroundCol[2]};
			return color;
		}
		Vector intersectionPoint = ray.basePoint.add(ray.directionVector.multByScalar(hit.min_t));
		Vector normel = hit.min_primitive.findNormal(intersectionPoint);
		if (normel.dotProduct(ray.directionVector) > 0) {
			normel = normel.multByScalar(-1);
		}
		normel.normalize();
		double[] col;
		Material mat = materials.get(hit.min_primitive.getMterialIndex() - 1);

		for (Light light : scene.lights) {
			Vector L = light.lightPosition.sub(intersectionPoint);
			double rTemp = 0;
			double gTemp = 0;
			double bTemp = 0;
			L.normalize();
			double cTeta = N.dotProduct(L);
			if (cTeta < 0) {
				continue;
			}
			rTemp += mat.diffuseColor[0] * light.lightColor[0] * cTeta;
			gTemp += mat.diffuseColor[1] * light.lightColor[1] * cTeta;
			bTemp += mat.diffuseColor[2] * light.lightColor[2] * cTeta;
			Vector R = N.multByScalar((L.multByScalar(2).dotProduct(N))).sub(L);
			double sTeta = Math.pow(R.dotProduct(ray.directionVector.multByScalar(-1)),
					mat.phongSpecularityCoefficient);
			rTemp += mat.specularColor[0] * light.lightColor[0] * sTeta * (light.specularIntensity);
			gTemp += mat.specularColor[1] * light.lightColor[1] * sTeta * (light.specularIntensity);
			bTemp += mat.specularColor[2] * light.lightColor[2] * sTeta * (light.specularIntensity);

			double SoftshadowIntensity = softShadow(light, L.multByScalar(-1), intersectionPoint);
			col.r += rTemp * ((1 - light.shadowIntensity) + light.shadowIntensity * SoftshadowIntensity);
			col.g += gTemp * ((1 - light.shadowIntensity) + light.shadowIntensity * SoftshadowIntensity);
			col.b += bTemp * ((1 - light.shadowIntensity) + light.shadowIntensity * SoftshadowIntensity);
		}
		double[] transfCol;
		if (mat.transparency > 0) {
			transfCol = culcTransColors(mat, normel, ray, intersectionPoint, recDepth);
		}

		Double[] reflectionColor = null;;
		if (mat.reflectionColor[0] > 0 || mat.reflectionColor[1] > 0 || mat.reflectionColor[2] > 0) {
			reflectionColor = ReflectionColor(ray, normel, intersectionPoint, mat, recDepth);
		}
        // ambient   diff      specular
        //Ka*Ia +  Ip*Kd*N⋅L + Ip*Ks*(R⋅V)n

        
        //output color = (background color) * transparency + (diffuse + specular) * (1 - transparency) + (reflection color)
		color[0] =  transfCol.r * mat.transparency + (col.r * (1 - mat.transparency) + reflectionColor.r);
		color[1] =  transfCol.g * mat.transparency + (col.g * (1 - mat.transparency) + reflectionColor.g);
		color[2] =  transfCol.b * mat.transparency + (col.b * (1 - mat.transparency) + reflectionColor.b);

		if (col.r > 1) {
			col.r = 1;
		}
		if (col.g > 1) {
			col.g = 1;
		}
		if (col.b > 1) {
			col.b = 1;
		}
		return col;
	}


    private Vector reflectVector(Ray ray, Vector normel ){
		Vector R = ray.v.add(normel.scalarMult(-2 * normel.dotProduct(ray.v)));
        R.normalize();
        return R;
    }

	private double[] ReflectionColor(Ray ray, Vector normel, Vector Intersection, Material mat, int recDepth) {
		Vector color;
		Vector R = reflectVector( ray, normel);
		Ray reflectionRay = new Ray(Intersection.add(R.scalarMult(0.001)), R);
        
		Intersection hit = Intersection.FindIntersction(reflectionRay, primitives);

		if (hit.min_t == Double.MAX_VALUE) {

			col.r = (set.backgroundColor[0] * mat.reflectionColor[0]);
			col.g = (set.backgroundColor[1] * mat.reflectionColor[1]);
			col.b = (set.backgroundColor[2] * mat.reflectionColor[2]);
		} else {
			Color tempCol = color(hit, refRay, recDepth - 1);
			col.r = (tempCol.r * mat.reflectionColor[0]);
			col.g = (tempCol.g * mat.reflectionColor[1]);
			col.b = (tempCol.b * mat.reflectionColor[2]);
		}
		if (col.r > 1) {
			col.r = 1;
		}
		if (col.g > 1) {
			col.g = 1;
		}
		if (col.b > 1) {
			col.b = 1;
		}
		return col;
	}

	// public Color culcTransColors(Material mat, vector N, Ray ray, vector intersectionPoint, int recDepth) {
	// 	Color col = new Color();
	// 	Ray transRay = new Ray(intersectionPoint.add(ray.directionVector.multByScalar(0.001)), ray.directionVector);
	// 	Intersection transHit = Intersection.FindIntersction(transRay, UpdatedPrimitives);
	// 	UpdatedPrimitives.remove(transHit.min_primitive);
	// 	if (transHit.min_t != Double.MAX_VALUE) {
	// 		Color tempCol = color(transHit, transRay, recDepth - 1);
	// 		col.r = tempCol.r;
	// 		col.g = tempCol.g;
	// 		col.b = tempCol.b;
	// 	} else {
	// 		col.r = set.backgroundColor[0];
	// 		col.g = set.backgroundColor[1];
	// 		col.b = set.backgroundColor[2];
	// 	}
	// 	if (col.r > 1) {
	// 		col.r = 1;
	// 	}
	// 	if (col.g > 1) {
	// 		col.g = 1;
	// 	}
	// 	if (col.b > 1) {
	// 		col.b = 1;
	// 	}
	// 	return col;
	// }

}
	