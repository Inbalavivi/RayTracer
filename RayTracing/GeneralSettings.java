package RayTracing;

public class GeneralSettings {
    Vector backgroundCol ;
	int numShadowRays;
	int maxNumRec;
	
    public GeneralSettings(Vector col ,int numRays, int numRec){
        this.backgroundCol=col;
        this.numShadowRays=numRays;
        this.maxNumRec=numRec;
    }
	
}
