package RayTracing;

public class Material {
    double[] diffuseCol;
	double[] specularCol;
	double[] reflectionCol;
	float shininess;
	float transparency;

	public Material (double[] diffuseColor,double[] specularColor,double[] reflectionColor, float shine, float transparent) {
        this.diffuseCol=diffuseColor;
        this.specularCol=specularColor;
        this.reflectionCol=reflectionColor;
        this.shininess=shine;
        this.transparency=transparent;
    }
	
}
