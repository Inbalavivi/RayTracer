package RayTracing;

public class Light {
    Vector lightPos;
	double[] color;
	float specularIntensity;
	float shadowIntensity;
	float radius;
	
	public Light(Vector pos, double[] col, float specularIntens, float shadowIntens, float radiusVal) {
		this.lightPos=pos;
		this.color=col;
		this.specularIntensity=specularIntens;
		this.shadowIntensity=shadowIntens;
		this.radius=radiusVal;
	}
 
}
