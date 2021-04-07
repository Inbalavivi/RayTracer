package RayTracing;

public class Box implements Surface {
    Vector centerPos;
    double edgeLen;
    int materialIndex;

    public Box(Vector center, double len, int index){
        this.centerPos=center;
        this.edgeLen=len;
        this.materialIndex=index;
    }
 
	public double intersect(Ray ray)  {
		return 0.5; /// temp!
	}
	// public double boxIntersect(Ray ray)  {
	// 	double offset = (-1)*this.normal.dotProduct(this.vertex1);
	// 	double t = findTIntersection(ray, this.normal,offset);
	// 	if (t<=0) {
	// 		return -1;
	// 	}
	// 	vector point = ray.basePoint.add(ray.directionVector.multByScalar(t));
	// 	boolean first =checkInTriangle(vertex1,vertex2,point,this.normal);
	// 	boolean second =checkInTriangle(vertex2,vertex3,point,this.normal);
	// 	boolean third =checkInTriangle(vertex3,vertex1,point,this.normal);
	// 	if(first && second && third) {
	// 		return t;
	// 	}
	// 	return -1 ;
	// }
	
	// private double findTIntersection(Ray ray, vector normal,double offset) {
	// 	double d = offset;
	// 	double VN=ray.directionVector.dotProduct(normal);
	// 	double P0N=ray.basePoint.dotProduct(normal);
	// 	double t;
	// 	if (VN == 0)
	// 		return -1;
	// 	t = -(P0N+d)/VN;
		
	// 	return t;
	// }

	// private boolean checkInBox(vector vertex1, vector vertex2,vector point, vector normal) {
	// 	vector V1 = vertex2.sub(vertex1);
	// 	vector V2 = point.sub(vertex1);	
	// 	vector V3 = V1.crossProduct(V2);
	// 	if(normal.dotProduct(V3)<0 ) {
	// 		return false;
	// 	}
	// 	return true;
	// }

	
	public Vector getNormal(Vector intersectionPoint) {
		Vector vec=null;
		return vec;
	}
	public int getMterialIndex(){
		return this.materialIndex;
	}
	
	

}
