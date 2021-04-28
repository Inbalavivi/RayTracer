import java.lang.Math;

public class fishEye {
    public static double calculateR(Vector lookAt, Vector X_if, Camera camera){ // distance from X_if (old pixal) to the center of screen
        Vector centerOfScreen = camera.position.add(lookAt.scalarMult(camera.screenDistance));
        //Vector centerOfScreen = lookAt;
        double r = Math.pow((centerOfScreen.x-X_if.x),2) + Math.pow((centerOfScreen.y-X_if.y),2) + Math.pow((centerOfScreen.z-X_if.z),2);
        return Math.pow(r, 0.5);
    }

    public static double calculateTeta(Vector lookAt, Vector X_if, Camera camera) {
        //ten_K_teta = (R*K)/f
        double R = calculateR(lookAt,X_if, camera);
        double K = camera.fishEyeTransVal;
        double camD = camera.screenDistance;
        double teta = 0;
        if (K > 0 ){
            teta = Math.atan((R*K)/camD)/K;
        }
        if (K == 0){
            teta = R/camD;
        }
        if (K < 0){
            teta = Math.asin((R*K)/camD)/K;
        }
        return teta;
    }

    public static int handleTeta(double teta) {
        double pai = Math.PI;
        if (teta == pai){ return 1;// Vector white_color = new Vector(1.0,1.0,1.0);
        }
        else if (teta > pai/2){ return 2;//Vector black_color = new Vector(0.0,0.0,0);
        }
        return 3;
    }

    public static double calcDistanceFromCenterToXip(Vector lookAt, Vector X_if, Camera camera){
        double teta = calculateTeta(lookAt, X_if, camera);
        double f = camera.screenDistance;
        return Math.tan(teta)*f ;
    }

    public static Vector findXip(Vector lookAt, Vector X_if, Camera camera) {
        double distance = calcDistanceFromCenterToXip(lookAt,X_if, camera);
        double r = calculateR(lookAt, X_if, camera);
        Vector centerOfScreen = camera.position.add(lookAt.scalarMult(camera.screenDistance));
        Vector Vec_X_ip = centerOfScreen.add( ((X_if.add(centerOfScreen.scalarMult(-1))).scalarMult(1/r)).scalarMult(distance) );
        return Vec_X_ip;  // the new pixel! now the ray will go trough X_ip instead of X_if
    }
}
