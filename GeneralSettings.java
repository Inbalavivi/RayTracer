package RayTracing;

public class GeneralSetting {
    float [] backgroundCol ;
	int numShadowRays;
	int maxNumRec;
	
    public GeneralSetting(float[] col ,int numRays, int numRec){
        this.backgroundCol=col;
        this.numShadowRays=numRays;
        this.maxNumRec=numRec;
    }
	
}
