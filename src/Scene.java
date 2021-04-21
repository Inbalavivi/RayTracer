import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Scene {
    Camera cam;
    Settings settings;
    List<Surface> surfaces;
    List<Light> lights;
    List<Material> materials;
    List<Surface> newSurfaces;
    double epsilon = 0.005;

    public Scene(Camera cam, Settings settings, List<Surface> surfaces, List<Light> lights, List<Material> materials, List<Surface> newSurfaces){
        this.cam = cam;
        this.settings = settings;
        this.surfaces = surfaces;
        this.lights = lights;
        this.materials = materials;
        this.newSurfaces = newSurfaces;
    }
    public Vector getColor(Surface firstSurface,double min_t, Ray ray, int recDepth) {
        if (recDepth == 0) { //return the background
            return  new Vector(this.settings.backgroundCol.x, this.settings.backgroundCol.y, this.settings.backgroundCol.z);
        }
        Vector intersection = ray.p0.add(ray.v.scalarMult(min_t)); //p0 +tV

        Vector normal = new Vector(0,0,0);
        Vector N = new Vector(0,0,0);
        if( firstSurface instanceof Sphere){
            //N=(P0-P)/|P0-P|
            normal = intersection.add(((Sphere)firstSurface).center.scalarMult(-1)); /// N = P0 - P (P=looAt)
            normal.normalize();
            N = normal;
        }

        if( firstSurface instanceof Plane){
            ((Plane) firstSurface).normal.normalize();
            N = ((Plane) firstSurface).normal;
        }

        if (N.dotProduct(ray.v) > 0) {
            N = N.scalarMult(-1);
        }
        N.normalize();
        Vector col = new Vector(0,0,0);

        int index = 0;
        if(firstSurface instanceof Sphere){index = ((Sphere) firstSurface).materialIndex;}
        if(firstSurface instanceof Plane){index = ((Plane) firstSurface).materialIndex;}
        Material mat = materials.get(index - 1);

        for (Light light : this.lights) {
            Vector L = light.position.add(intersection.scalarMult(-1));
            L.normalize();
            double N_dot_L = N.dotProduct(L);
            if (N_dot_L >= 0) {
                Vector tmp;
                tmp = mat.diffusion.vecsMult(light.color);
                tmp = tmp.scalarMult(N_dot_L);
                Vector R = N.scalarMult((L.scalarMult(2).dotProduct(N))).add(L.scalarMult(-1)); //R = (2L*N)N - L (the highlight vector)
                double Value = Math.pow(R.dotProduct(ray.v.scalarMult(-1)), mat.shininess);
                tmp = tmp.add(mat.specular.vecsMult(light.color).scalarMult(Value).scalarMult(light.specularIntensity));
                double softShadowIntensity = softShadow(light, L.scalarMult(-1), intersection);
                Vector shadowVec1 = new Vector(light.shadowIntensity, light.shadowIntensity, light.shadowIntensity);
                Vector shadowVec2 = new Vector(1 - light.shadowIntensity, 1 - light.shadowIntensity, 1 - light.shadowIntensity);
                col = col.add(tmp.vecsMult((shadowVec2.add(shadowVec1.scalarMult(softShadowIntensity)))));
            }
        }
        Vector reflectionColor = new Vector(0,0,0);
        if (mat.reflection.x > 0 || mat.reflection.y > 0 || mat.reflection.z > 0) {
            reflectionColor = ReflectionColor(ray, N, intersection, mat, recDepth);
        }
        List<Intersection> check =new ArrayList<Intersection>();
        check= Intersection.getAllIntersections(ray,surfaces);
        Vector transfCol = new Vector(0,0,0);

        if (mat.transparency > 0) {
            transfCol = TransparencyColors( ray, intersection, recDepth);
        }
        //output color = (background color) * transparency + (diffuse + specular) * (1 - transparency) + (reflection color)
        col=(transfCol.scalarMult(mat.transparency)).add((col.scalarMult(1 - mat.transparency))).add(reflectionColor);   /// formula
        col.checkBound();
        return col;
    }

    public Vector reflectVector(Ray ray, Vector normal ){
        Vector R = ray.v.add(normal.scalarMult(-2 * normal.dotProduct(ray.v)));   /// formula
        R.normalize();
        return R;
    }

    public Vector ReflectionColor(Ray ray, Vector normal, Vector IntersectionP, Material mat, int recDepth) {
        Vector color;
        double min_t;
        Surface firstSurface;
        Vector R = reflectVector( ray, normal);
        Ray reflectionRay = new Ray(IntersectionP.add(R.scalarMult(epsilon)), R);
        Intersection intersection = Intersection.getMinIntersection(reflectionRay,this.surfaces);

        min_t = intersection.min_t;
        firstSurface = intersection.firstSurface;

        if (min_t == Double.MAX_VALUE) {
            color=this.settings.backgroundCol.vecsMult(mat.reflection);

        } else {
            Vector tempCol = getColor(firstSurface,min_t, reflectionRay, recDepth - 1);
            color=tempCol.vecsMult(mat.reflection);

        }
        color.checkBound();
        return color;
    }

    public Vector TransparencyColors( Ray ray, Vector intersectionPoint, int recDepth) {
        Vector col=new Vector(0,0,0);
        double min_t;
        Surface firstSurface;
        Ray transRay = new Ray(intersectionPoint.add(ray.v.scalarMult(epsilon)), ray.v);
        Intersection intersection = Intersection.getMinIntersection(transRay, newSurfaces);
        min_t = intersection.min_t;
        firstSurface = intersection.firstSurface;
        this.newSurfaces.remove(firstSurface);
        if (min_t == Double.MAX_VALUE) {
            col =this.settings.backgroundCol;

        } else {
            col=col.add(getColor(firstSurface,min_t, transRay, recDepth - 1));
        }
        col.checkBound();
        return col;
    }

    public double softShadow(Light light, Vector planeNormal, Vector intersectionPoint) {
        Plane plane = new Plane(planeNormal,planeNormal.dotProduct(light.position),-1);
        Vector uOnPlane = plane.findVec(light.position); //Specifying the Viewing Coordinates system
        Vector vOnPlane = planeNormal.crossProduct(uOnPlane);
        vOnPlane.normalize();
        uOnPlane.normalize();
        Vector edge = (light.position.add(vOnPlane.scalarMult(-0.5 * light.radius))).add(uOnPlane.scalarMult(-0.5 * light.radius));
        Vector V = (edge.add(vOnPlane.scalarMult(light.radius))).add(edge.scalarMult(-1));
        Vector U = (edge.add(uOnPlane.scalarMult(light.radius))).add(edge.scalarMult(-1));
        double scalar = 1.0 / this.settings.numShadowRays;
        Vector v = V.scalarMult(scalar);
        Vector u = U.scalarMult(scalar);
        double sum = 0;
        for (int i = 0; i < this.settings.numShadowRays; i++) {
            for (int j = 0; j < this.settings.numShadowRays; j++) {
                sum += shootRay( edge, v, u, i, j, intersectionPoint);
            }
        }
        //this light source will be multiplied by the number of rays that hit the surface divided by the total number of rays we sent.
        return sum / Math.pow(this.settings.numShadowRays , 2);
    }

    public int shootRay( Vector edge, Vector v, Vector u, int i, int j, Vector intersection) {
        Random rand = new Random();
        double x = rand.nextDouble();
        double y = rand.nextDouble();
        Vector point = edge.add(v.scalarMult(i + x).add(u.scalarMult(j + y)));
        Vector pointDirection = point.add(intersection.scalarMult(-1));
        double directionMagnitude = Math.sqrt(pointDirection.dotProduct(pointDirection)); // ||pointDirection||
        pointDirection.normalize();
        Ray lightRay = new Ray(intersection.add(pointDirection.scalarMult(epsilon)), pointDirection);
        if(Intersection.checkIfIntersect(lightRay, this, directionMagnitude)==0){
            return 1;
        }
        return 0;
    }


}

