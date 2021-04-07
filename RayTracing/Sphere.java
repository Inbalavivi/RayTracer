package RayTracing;

public class Sphere implements Surface{
	Vector centerPos;
	double radius;
	int materialIndex;


	public Sphere(Vector center,double radiusVal,int material) {
        this.centerPos = center;
        this.radius=radiusVal;
        this.materialIndex=material;
    }
	
	public double intersect(Ray ray)  {
		Vector l= this.centerPos.add((ray.p0).scalarMult(-1));
		double t_ca = l.dotProduct(ray.v);
		if(t_ca <0) {
			return 0;
		}
		double d_2 = (l.dotProduct(l)) - (t_ca * t_ca);
		double r_2=this.radius * this.radius;
		if(d_2 > r_2) {
			return 0;
		}
		double t_hc = Math.sqrt(r_2 - d_2);
		double t1 =t_ca - t_hc;
		double t2 =t_ca + t_hc;
		if (t1<0 && t2<0){
			return 0;
		}
		double min_t=Math.min(t1,t2);
		if (min_t<0){
			if(min_t==t1){
				return t2;
				}
			else{
				return t1;
				}	
		}
		return min_t;
	}


	public Vector getNormal(Vector intersectionPoint) {
		Vector normal = intersectionPoint.add(this.centerPos.scalarMult(-1));
		normal.normalize();
		return normal;
	}
	
	public int getMterialIndex() {
		return this.materialIndex;
	}

}