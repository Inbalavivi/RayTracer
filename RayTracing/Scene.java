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
		Plane plane = new Plane(planeNormal,plane.findOffset(light.lightPos), );
	
		Vector v_vec = plane.findVecOnPlane(light.lightPos);
		Vector u_vec = planeNormal.crossProduct(v_vec);
		v_vec.normalize();
		u_vec.normalize();

		Vector corner = (light.lightPosition.add(v_vec.multByScalar(-0.5 * light.lightRadius)))
				.add(u_vec.multByScalar(-0.5 * light.lightRadius));
		Vector full_v = (corner.add(v_vec.multByScalar(light.lightRadius))).sub(corner);
		Vector full_u = (corner.add(u_vec.multByScalar(light.lightRadius))).sub(corner);
		double scalar = 1.0 / this.settings.NumberOfShadowRays;
		Vector v = full_v.multByScalar(scalar);
		Vector u = full_u.multByScalar(scalar);
		double sum = 0;
		for (int i = 0; i < this.settings.NumberOfShadowRays; i++) {
			for (int j = 0; j < this.settings.NumberOfShadowRays; j++) {
				sum += pointOnlight(light, corner, v, u, i, j, intersectionPoint);
			}
		}
		//this light source will be multiplied by the number of rays that hit the surface divided by the total number of rays we sent.
		return sum / (this.settings.NumberOfShadowRays * this.settings.NumberOfShadowRays);
	}

    private Vector reflectVector(Ray ray, Vector normel ){
		Vector R = ray.v.add(normel.scalarMult(-2 * normel.dotProduct(ray.v)));   /// formula
        R.normalize();
        return R;
    }
	//  ray-tracing04 p3 Avoiding self shadowing
	private Vector ReflectionColor(Ray ray, Vector normel, Vector IntersectionP, Material mat, int recDepth) {
		Vector color;
		double epsilon = 0.005;
		Vector R = reflectVector( ray, normel);
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

	private Color culcRefColors(Ray ray, vector N, vector IntersectionPoint, Material mat, int recDepth) {
		Color col = new Color();

		vector R = ray.directionVector.add(N.multByScalar(-2 * ray.directionVector.dotProduct(N)));
		R.normalize();
		Ray refRay = new Ray(IntersectionPoint.add(R.multByScalar(0.001)), R);
		Intersection hit = Intersection.FindIntersction(refRay, primitives);

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

	
	private int pointOnlight(Light light, vector corner, vector v, vector u, int i, int j, vector intersectionPoint) {
		Random random1 = new Random();
		double num1 = random1.nextDouble();
		double num2 = random1.nextDouble();
		vector point = corner.add(v.multByScalar(i + num1).add(u.multByScalar(j + num2)));
		vector pointDirction = point.sub(intersectionPoint);
		double Plength = Math.sqrt(pointDirction.dotProduct(pointDirction));
		if (checkDirectLight(pointDirction, intersectionPoint, Plength)) {
			return 0;
		}
		return 1;
	}

	private boolean checkDirectLight(vector L, vector intersectionPoint, double Llength) {

		L.normalize();
		Ray lightRay = new Ray(intersectionPoint.add(L.multByScalar(0.001)), L);
		return Intersection.isIntersect(lightRay, scene, Llength);

	}

	private double[][] computeNewCoordinate() {
		double[][] M = new double[3][3];
		vector Vx = cam.lookAtDirection.crossProduct(cam.camUpVector);
		Vx.normalize();
		cam.camUpVector = (Vx).crossProduct(cam.lookAtDirection);
		cam.camUpVector.normalize();
		M[0] = Vx.data;
		M[1] = cam.camUpVector.data;
		M[2] = cam.lookAtDirection.data;
		return M;
	}


}
	