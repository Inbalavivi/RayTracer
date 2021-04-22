import java.lang.Math;

public class fishEye {
    public static double calculateR(Vector lookAt, Vector X_if, Camera camera){ // distance from X_if to the center
        Vector vec = lookAt.add(camera.position.scalarMult(-1));
        vec.normalize();
        double distance = Math.pow((X_if.x-vec.x),2) + Math.pow((X_if.y-vec.y),2) + Math.pow((X_if.z-vec.z),2);
        return Math.pow(distance, 0.5);
    }

    public static double calculateTeta(Vector lookAt, Vector X_if, Camera camera) {
        //ten_K_teta = (R*K)/f
        double R = calculateR(lookAt,X_if, camera);
        double K = camera.fishEyeTransVal;
        double f = camera.screenDistance;
        double teta = 0;
        if (K > 0 && K <= 1){
            double ten_K_teta = (R*K)*(1/f);
            double K_teta = Math.atan(ten_K_teta);
            teta = K_teta/K;
        }
        if (K == 0){
            teta = R/f;
        }
        if (K < 0 && K >= -1){
            double sin_K_teta = (R*K)/f;
            double K_teta = Math.asin(sin_K_teta);
            teta = K_teta/K;
        }
        return teta;
    }

    public static boolean checkTeta(double teta) {
        double pai = Math.PI;
        // false = black background
        return !(teta > pai / 2);
    }

    public static double calcDistanceFromCenterToXip(Vector lookAt, Vector X_if, Camera camera){
        double teta = calculateTeta(lookAt, X_if, camera);
        double f = camera.screenDistance;
       // double R  = calculateR(lookAt, X_if);  /// new
        return Math.tan(teta)*f ;     /// new
    }

    public static Vector findXip(Vector lookAt, Vector X_if, Camera camera) {
        double distance = calcDistanceFromCenterToXip(lookAt,X_if, camera);
        X_if.normalize();
        Vector Vec_X_ip = (X_if.scalarMult(distance));
        Vec_X_ip.normalize();
        return Vec_X_ip;  // the new pixel! now the ray will go trough X_ip instead of X_if
    }

}
