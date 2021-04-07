package RayTracing;

public class Material {
    Vector diffuseCol;
	Vector specularCol;
	Vector reflectionCol;
	float shininess;
	float transparency;

	public Material (Vector diffuseColor,Vector specularColor,Vector reflectionColor, float shine, float transparent) {
        this.diffuseCol=diffuseColor;
        this.specularCol=specularColor;
        this.reflectionCol=reflectionColor;
        this.shininess=shine;
        this.transparency=transparent;
    }
	
}
