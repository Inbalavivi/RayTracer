
public class Box implements Surface {
    Vector center;
    Double edgeLength;
    int materialIndex;

    Vector minExtent;
    Vector maxExtent;

    public Box( Vector center, Double edgeLength, int materialIndex) {
        this.center = center;
        this.edgeLength = edgeLength;
        this.materialIndex=materialIndex;
        // min and max are the minimum and maximum extent of the bounding box. easier way to compute intersection.
        Vector r_vec = new Vector(edgeLength/2,edgeLength/2,edgeLength/2);
        this.minExtent = center.add(r_vec.scalarMult(-1));
        this.maxExtent = center.add(r_vec);
    }

    public double intersect(Ray ray)  {
          double tmin = (minExtent.x - ray.p0.x) / ray.v.x;
          double tmax = (maxExtent.x - ray.p0.x) / ray.v.x;
          if (tmin > tmax) {
            double temp;
            temp = tmin;
            tmin = tmax;
            tmax = temp;
           }
          double tymin = (minExtent.y - ray.p0.y) / ray.v.y;
          double tymax = (maxExtent.y - ray.p0.y) / ray.v.y;

        if (tymin > tymax) {
            double temp;
            temp = tymin;
            tymin = tymax;
            tymax = temp;
        }
        if ((tmin > tymax) || (tymin > tmax)) return 0;
        if (tymin > tmin) tmin = tymin;
        if (tymax < tmax) tmax = tymax;
        double tzmin = (minExtent.z - ray.p0.z) / ray.v.z;
        double tzmax = (maxExtent.z - ray.p0.z) / ray.v.z;

        if (tzmin > tzmax) {
            double temp;
            temp = tzmin;
            tzmin = tzmax;
            tzmax = temp;
        }

        if ((tmin > tzmax) || (tzmin > tmax)) return 0;
        if (tzmin > tmin) tmin = tzmin;
        if (tzmax < tmax) tmax = tzmax;

        return tmin;
    }





//    public double intersect(Ray ray)  {
//        // min and maxExtent are the minimum and maximum extent of the bounding box.
//        // if there is no intersection return -1.
//        double tmin = (minExtent.x - ray.p0.x) / ray.v.x;
//        double tmax = (maxExtent.x - ray.p0.x) / ray.v.x;
//
//        if (tmin > tmax) {
//            double temp;
//            temp = tmin;
//            tmin = tmax;
//            tmax = temp;
//        }
//        double tymin = (minExtent.y - ray.p0.y) / ray.v.y;
//        double tymax = (maxExtent.y - ray.p0.y) / ray.v.y;
//
//        if (tymin > tymax) {
//            double temp;
//            temp = tymin;
//            tymin = tymax;
//            tymax = temp;
//        }
//        if ((tmin > tymax) || (tymin > tmax)) return 0;
//        if (tymin > tmin) tmin = tymin;
//        if (tymax < tmax) tmax = tymax;
//        double tzmin = (minExtent.z - ray.p0.z) / ray.v.z;
//        double tzmax = (maxExtent.z - ray.p0.z) / ray.v.z;
//
//        if (tzmin > tzmax) {
//            double temp;
//            temp = tzmin;
//            tzmin = tzmax;
//            tzmax = temp;
//        }
//
//        if ((tmin > tzmax) || (tzmin > tmax)) return 0;
//        if (tzmin > tmin) tmin = tzmin;
//        if (tzmax < tmax) tmax = tzmax;
//
//        return tmin;
//    }



//    public Vector findNormal(Vector intersectionPoint) {
//        Vector V1 = v2.add(v1).scalarMult(-1);
//        Vector V2 = v3.add(v1).scalarMult(-1);
//        Vector normal = V1.crossProduct(V2);
//        //normal.normalize();
//        return normal;
//    }

    public int getMatIndex(){
        return this.materialIndex;
    }
}