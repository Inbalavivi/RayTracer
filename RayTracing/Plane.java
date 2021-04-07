package RayTracing;

public class Plane {
    Vector normal;
	double offset;
	int materialIndex;
	
	public Plane(Vector normal,double offset , int matindex) {
        normal.normalize();
        this.normal=normal;
        this.offset=offset;
        this.materialIndex=matindex;
    }
	
	public double plane_intersect(Ray ray)  {
		double t= (-1) * (ray.p0.dotProduct(this.normal)+ this.offset) / (ray.v.dotProduct(this.normal));
		return t;
	}

	// @Override
	// public vector findNormal(vector intersectionPoint) {
	// 	this.normal.normalize();
	// 	return this.normal;
	// }


	
	// public double findOffset(vector point) {
	// 	return (this.normal.dotProduct(point));
	// }
	
	
	// public vector findVecOnPlane(vector point) {
	// 	double z = -1* (this.normal.data[0]*1+this.normal.data[1]*1+this.offset)/this.normal.data[2];
	// 	double[] z_val = {1,1,z};
	// 	vector z_vec = new vector(z_val);
	// 	vector newVector = z_vec.add(point.multByScalar(-1));
	// 	return newVector;
	// }

}
