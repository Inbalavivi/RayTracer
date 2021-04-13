package RayTracing;

import java.util.List;
import java.util.Random;

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

	public Vector color(Intersection hit, Ray ray, int recDepth) {
		if (recDepth == 0) {
			Vector color = new Vector(settings.backgroundCol.x, settings.backgroundCol.y, settings.backgroundCol.z);
			return color;
		}
		Vector intersection = ray.p0.add(ray.v.scalarMult(hit.min_t)); /// ?
		Vector N = hit.firstSurface.getNormal(intersection);
		if (N.dotProduct(ray.v) > 0) {
			N = N.scalarMult(-1);
		}
		N.normalize(); // normalize the look-at vector to form the w-axis
		Material mat = materials.get(hit.firstSurface.getMaterialIndex());
		Vector col=null;
		// Phong Shading : color =  I_e + ambient +  ∑_i((diff)+(specular))
		// ∑_i((diff)+(specular)) ==> ∑_i((Ip*Kd*N⋅L)+(Ip*Ks*(R⋅V)^n))
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
			
			// shoft shadows:
			double SoftshadowIntensity = softShadow(light, L.scalarMult(-1), intersection);
			Vector softShadowIntensVec=new Vector(SoftshadowIntensity,SoftshadowIntensity,SoftshadowIntensity);
			Vector shadowIntensVec=new Vector(light.shadowIntensity,light.shadowIntensity,light.shadowIntensity);
			//ligh_intesity = (1 - shadow_intensity) * 1 + shadow_intensity * (%of rays that hit the points from the light source)
			col = (vec.scalarMult(1 - light.shadowIntensity)).add((shadowIntensVec).vecsMult(softShadowIntensVec));
						
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

	private double softShadow(Light light, Vector planeNormal, Vector intersectionPoint) {
		Plane plane = new Plane(planeNormal,planeNormal.dotProduct(light.lightPos),-1);
		Vector v_vec = plane.findVecOnPlane(light.lightPos);
		Vector u_vec = planeNormal.crossProduct(v_vec);
		v_vec.normalize();
		u_vec.normalize();

		Vector corner = (light.lightPos.add(v_vec.scalarMult(-0.5 * light.radius))).add(u_vec.scalarMult(-0.5 * light.radius));
		Vector full_v = (corner.add(v_vec.scalarMult(light.radius))).add(corner.scalarMult(-1));
		Vector full_u = (corner.add(u_vec.scalarMult(light.radius))).add(corner.scalarMult(-1));
		double scalar = 1.0 / this.settings.numShadowRays;
		Vector v = full_v.scalarMult(scalar);
		Vector u = full_u.scalarMult(scalar);
		double sum = 0;
		for (int i = 0; i < this.settings.numShadowRays; i++) {
			for (int j = 0; j < this.settings.numShadowRays; j++) {
				sum += shootRay(light, corner, v, u, i, j, intersectionPoint);
			}
		}
		//this light source will be multiplied by the number of rays that hit the surface divided by the total number of rays we sent.
		return sum / (this.settings.numShadowRays * this.settings.numShadowRays);
	}

    private Vector reflectVector(Ray ray, Vector normal ){
		Vector R = ray.v.add(normal.scalarMult(-2 * normal.dotProduct(ray.v)));   /// formula
        R.normalize();
        return R;
    }

	//  ray-tracing04 p3 Avoiding self shadowing
	private Vector ReflectionColor(Ray ray, Vector normal, Vector IntersectionP, Material mat, int recDepth) {
		Vector color;
		double epsilon = 0.005;
		Vector R = reflectVector( ray, normal);
		Ray reflectionRay = new Ray(IntersectionP.add(R.scalarMult(epsilon)), R);
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

	
	private int shootRay(Light light, Vector corner, Vector v, Vector u, int i, int j, Vector intersection) {
		Random rand = new Random();
		double x = rand.nextDouble();
		double y = rand.nextDouble();
		Vector point = corner.add(v.scalarMult(i + x).add(u.scalarMult(j + y)));
		Vector pointDirction = point.add(intersection.scalarMult(-1));
		double Plength = Math.sqrt(pointDirction.dotProduct(pointDirction));
		pointDirction.normalize();
		Ray lightRay = new Ray(intersection.add(pointDirction.scalarMult(0.001)), pointDirction);
		return Intersection.isIntersect(lightRay, this, Plength);
	}
}