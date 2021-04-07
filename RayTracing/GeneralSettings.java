package RayTracing;

public class GeneralSettings {
    float [] backgroundCol ;
	int numShadowRays;
	int maxNumRec;
	
    public GeneralSettings(float[] col ,int numRays, int numRec){
        this.backgroundCol=col;
        this.numShadowRays=numRays;
        this.maxNumRec=numRec;
    }
	
}
