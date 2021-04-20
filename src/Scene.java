import java.util.List;
import java.util.Random;

public class Scene {
    Camera cam;
    Settings settings;
    List<Surface> surfaces;
    List<Light> lights;
    List<Material> materials;
    List<Surface> newSurfaces;

    public Scene(Camera cam, Settings settings, List<Surface> surfaces, List<Light> lights, List<Material> materials, List<Surface> newSurfaces){
        this.cam = cam;
        this.settings = settings;
        this.surfaces = surfaces;
        this.lights = lights;
        this.materials = materials;
        this.newSurfaces = newSurfaces;
    }
    public Vector color(Surface firstSurface,double min_t, Ray ray, int recDepth) {
        if (recDepth == 0) {
            return  new Vector(this.settings.backgroundCol.x, this.settings.backgroundCol.y, this.settings.backgroundCol.z);
        }
        Vector intersection = ray.p0.add(ray.v.scalarMult(min_t)); /// ?
        Vector N = firstSurface.getNormal(intersection);
        if (N.dotProduct(ray.v) > 0) {
            N = N.scalarMult(-1);
        }
        N.normalize();
        Vector col = new Vector(0,0,0);
        Material mat = materials.get(firstSurface.getMaterialIndex() - 1);

        for (Light light : this.lights) {
            Vector L = light.position.add(intersection.scalarMult(-1));
            Vector tmp=new Vector(0,0,0);
            L.normalize();
            double cTeta = N.dotProduct(L);
            if (cTeta < 0) {
                continue;
            }
            tmp=mat.diffusion.vecsMult(light.color);
            tmp=tmp.scalarMult(cTeta);
            Vector R = N.scalarMult((L.scalarMult(2).dotProduct(N))).add(L.scalarMult(-1));
            double sTeta = Math.pow(R.dotProduct(ray.v.scalarMult(-1)),
                    mat.shininess);
            tmp=tmp.add(mat.specular.vecsMult(light.color).scalarMult(sTeta).scalarMult(light.specularIntensity));
            double SoftshadowIntensity = softShadow(light, L.scalarMult(-1), intersection);
            Vector shadowVec1=new Vector(light.shadowIntensity,light.shadowIntensity,light.shadowIntensity);
            Vector shadowVec2=new Vector(1-light.shadowIntensity,1-light.shadowIntensity,1-light.shadowIntensity);

            col=col.add(tmp.vecsMult((shadowVec2.add(shadowVec1.scalarMult(SoftshadowIntensity)))));

        }
        Vector transfCol = new Vector(0,0,0);
        if (mat.transparency > 0) {
            transfCol = TransparencyColors(mat, N, ray, intersection, recDepth);
        }
        Vector reflectionColor = new Vector(0,0,0);
        if (mat.reflection.x > 0 || mat.reflection.y > 0 || mat.reflection.z > 0) {
            reflectionColor = ReflectionColor(ray, N, intersection, mat, recDepth);
        }
        //output color = (background color) * transparency + (diffuse + specular) * (1 - transparency) + (reflection color)
        col=(transfCol.scalarMult(mat.transparency)).add((col.scalarMult(1 - mat.transparency))).add(reflectionColor);   /// formula
        col.checkRange();
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
        double epsilon = 0.005;
        Vector R = reflectVector( ray, normal);
        Ray reflectionRay = new Ray(IntersectionP.add(R.scalarMult(epsilon)), R);

        Object[] intersection = Scene.getIntersction(reflectionRay,this.surfaces);
        min_t = (double) intersection[0];
        firstSurface = (Surface)intersection[1];

        if (min_t == Double.MAX_VALUE) {
            color=this.settings.backgroundCol.vecsMult(mat.reflection);

        } else {
            Vector tempCol = color(firstSurface,min_t, reflectionRay, recDepth - 1);
            color=tempCol.vecsMult(mat.reflection);

        }
        color.checkRange();
        return color;
    }



//    double min_t;
//    Surface firstSurface;
//    Object[] intersection = Scene.getIntersction(transRay, newSurfaces);
//    min_t = (double) intersection[0];
//    firstSurface = (Surface)intersection[1];

    public Vector TransparencyColors(Material mat, Vector N, Ray ray, Vector intersectionPoint, int recDepth) {
        Vector col;
        double min_t;
        Surface firstSurface;
        double epsilon = 0.005;
        Ray transRay = new Ray(intersectionPoint.add(ray.v.scalarMult(epsilon)), ray.v);
        Object[] intersection = Scene.getIntersction(transRay, newSurfaces);
        min_t = (double) intersection[0];
        firstSurface = (Surface)intersection[1];


        this.newSurfaces.remove(firstSurface);
        if (min_t != Double.MAX_VALUE) {
            Vector tempCol = color(firstSurface,min_t, transRay, recDepth - 1);
            col =tempCol;

        } else {
            col =this.settings.backgroundCol;
        }
        col.checkRange();
        return col;
    }

    public double softShadow(Light light, Vector planeNormal, Vector intersectionPoint) {
        Plane plane = new Plane(planeNormal,planeNormal.dotProduct(light.position),-1);
        Vector v_vec = plane.findVec(light.position);
        Vector u_vec = planeNormal.crossProduct(v_vec);
        v_vec.normalize();
        u_vec.normalize();
        Vector corner = (light.position.add(v_vec.scalarMult(-0.5 * light.radius))).add(u_vec.scalarMult(-0.5 * light.radius));
        Vector full_v = (corner.add(v_vec.scalarMult(light.radius))).add(corner.scalarMult(-1));
        Vector full_u = (corner.add(u_vec.scalarMult(light.radius))).add(corner.scalarMult(-1));
        double scalar = 1.0 / this.settings.numShadowRays;
        Vector v = full_v.scalarMult(scalar);
        Vector u = full_u.scalarMult(scalar);
        double sum = 0;
        for (int i = 0; i < this.settings.numShadowRays; i++) {
            for (int j = 0; j < this.settings.numShadowRays; j++) {
                sum += shootRay(light, corner, v, u, i, j, intersectionPoint);
            }
        }
        //this light source will be multiplied by the number of rays that hit the surface divided by the total number of rays we sent.
        return sum / (this.settings.numShadowRays * this.settings.numShadowRays);
    }

    public int shootRay(Light light, Vector corner, Vector v, Vector u, int i, int j, Vector intersection) {
        Random rand = new Random();
        double x = rand.nextDouble();
        double y = rand.nextDouble();
        Vector point = corner.add(v.scalarMult(i + x).add(u.scalarMult(j + y)));
        Vector pointDirection = point.add(intersection.scalarMult(-1));
        double directionMagnitud = Math.sqrt(pointDirection.dotProduct(pointDirection)); // ||pointDirction||
        pointDirection.normalize();
        Ray lightRay = new Ray(intersection.add(pointDirection.scalarMult(0.001)), pointDirection);
        if(isIntersect(lightRay, this, directionMagnitud)==0){
            return 1;
        }
        return 0;
    }
//    Object[] arr = new Object[6];
//
//    arr[0] = new String("First Pair");
//    arr[1] = new Integer(1);

    public static Object[] getIntersction(Ray ray, List<Surface> Surfaces) { /// return [min_t, firstSurface]
        double t;
        double min_t = Double.MAX_VALUE;
        Surface firstSurface = null;
        Object[] intersection = new Object[2];
        for (Surface s : Surfaces) {
            t = s.intersect(ray);
            if (t < min_t && t > 0) {
                firstSurface = s;
                min_t = t;
            }
        }
        intersection[0] = min_t;
        intersection[1] = firstSurface;
        //Intersection intersection = new Intersection(min_t, firstSurface);
        return intersection;
    }

    public static int isIntersect(Ray ray, Scene scene, double Magnitud) {
        double t;
        for (Surface s : scene.surfaces) {
            t = s.intersect(ray);
            if (t < Magnitud && t > 0) {
                return 1;
            }
        }
        return 0;
    }

}

