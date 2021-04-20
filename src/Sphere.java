public class Sphere implements Surface{
	Vector center;
	double radius;
	int materialIndex;

	public Sphere(Vector center,double radiusVal,int material) {
		this.center = center;
		this.radius=radiusVal;
		this.materialIndex=material;
	}

	public double intersect(Ray ray)  {
		Vector l = this.center.add((ray.p0).scalarMult(-1));
		double t_ca = l.dotProduct(ray.v);
		if(t_ca < 0) {
			return 0;
		}
		double d_2 = (l.dotProduct(l)) - Math.pow(t_ca , 2);
		double r_2 = Math.pow(this.radius, 2);
		if( r_2 < d_2) {
			return 0;
		}
		double t_hc = Math.sqrt(r_2 - d_2);
		double t1 = t_ca - t_hc;
		double t2 =  t_hc + t_ca;
		if (t1<0 && t2<0){
			return 0;
		}
		double min_t = Math.min(t1,t2);
		if (min_t < 0){
			if(min_t == t1){
				return t2;
			}
			else{
				return t1;
			}
		}
		return min_t;
	}
}