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

	private Vector color(Intersection hit, Ray ray, int recDepth) {
		if (recDepth == 0) {
			Vector color = new Vector(settings.backgroundCol.x, settings.backgroundCol.y, settings.backgroundCol.z);
			return color;
		}
		Vector intersection = ray.p0.add(ray.v.scalarMult(hit.min_t));
		Vector N = hit.firstSurface.getNormal(intersection);
		if (N.dotProduct(ray.v) > 0) {
			N = N.scalarMult(-1);
		}
		N.normalize();
		Material mat = materials.get(hit.firstSurface.getMterialIndex() - 1);
		Vector col=null;
		for (Light light : this.lights) {
			Vector L = light.lightPos.add(intersection.scalarMult(-1));
			L.normalize();
			double NdotL = N.dotProduct(L);
			if (NdotL < 0) {
				continue;
			}
	
			Vector diffusion=(light.color.vecsMult(mat.diffuseCol)).scalarMult(NdotL);           /// formula diffusion: Ip*Kd*N⋅L
			Vector R = N.scalarMult((L.scalarMult(2).dotProduct(N))).add(L.scalarMult(-1));      /// formula for R : (2*L⋅N)N-L
			double RdotV_n = Math.pow(R.dotProduct(ray.v.scalarMult(-1)),mat.shininess);         
			Vector specular=((light.color.vecsMult(mat.specularCol)).scalarMult(RdotV_n)).scalarMult(light.specularIntensity);   /// formula specular: Ip*Ks*(R⋅V)^n
			Vector vec=diffusion.add(specular); //diff+specular
			
			double SoftshadowIntensity = softShadow(light, L.scalarMult(-1), intersection);
			Vector shadowIntensVec=new Vector(light.shadowIntensity,light.shadowIntensity,light.shadowIntensity);
			Vector softShadowIntensVec=new Vector(SoftshadowIntensity,SoftshadowIntensity,SoftshadowIntensity);
			col = ((vec.scalarMult(1 - light.shadowIntensity)).add(shadowIntensVec)).vecsMult(softShadowIntensVec);
			
		}
		Vector transfCol = null;
		if (mat.transparency > 0) {
			transfCol = culcTransColors(mat, N, ray, intersection, recDepth);
		}

		Vector reflectionColor = null;
		if (mat.reflectionCol.x > 0 || mat.reflectionCol.y > 0 || mat.reflectionCol.z > 0) {
			reflectionColor = ReflectionColor(ray, N, intersection, mat, recDepth);
		}
        //output color = (background color) * transparency + (diffuse + specular) * (1 - transparency) + (reflection color)
		col=(transfCol.scalarMult(mat.transparency)).add((col.scalarMult(1 - mat.transparency))).add(reflectionColor);   /// formula

		
		col.checkRange();
		return col;
	}


    private Vector reflectVector(Ray ray, Vector normel ){
		Vector R = ray.v.add(normel.scalarMult(-2 * normel.dotProduct(ray.v)));   /// formula
        R.normalize();
        return R;
    }

	private Vector ReflectionColor(Ray ray, Vector normel, Vector IntersectionP, Material mat, int recDepth) {
		Vector color;
		Vector R = reflectVector( ray, normel);
		Ray reflectionRay = new Ray(IntersectionP.add(R.scalarMult(0.001)), R);
		Intersection hit = Intersection.FindIntersction(reflectionRay, this.surfaces);
		if (hit.min_t == Double.MAX_VALUE) {
			color=this.settings.backgroundCol.vecsMult(mat.reflectionCol);
		
		} else {
			Vector tempCol = color(hit, reflectionRay, recDepth - 1);
			color=tempCol.vecsMult(mat.reflectionCol);
			
		}
		color.checkRange();
		return color;
	}

	public Vector culcTransColors(Material mat, Vector N, Ray ray, Vector intersectionPoint, int recDepth) {
		Vector col=null;
		Ray transRay = new Ray(intersectionPoint.add(ray.v.scalarMult(0.001)), ray.v);
		Intersection transHit = Intersection.FindIntersction(transRay, UpdatedPrimitives);
		UpdatedPrimitives.remove(transHit.firstSurface);
		if (transHit.min_t != Double.MAX_VALUE) {
			Vector tempCol = color(transHit, transRay, recDepth - 1);
			col =tempCol;
		
		} else {
			col =this.settings.backgroundCol;
		}
		col.checkRange();
		return col;
	}

}
	