# RayTracer
This project was built as part of "Fundamentals of Computer Graphics, Vision and Image Processing" course in Tel-Aviv University.

### Description:
This is an implementation of a basic ray tracer in Java. A ray tracer shoots rays from the observer's eye (the camera) through a screen and into a scene which contains one or more surfaces. It calculates the rays intersection with the surfaces, finds the nearest intersection and calculates the color of the surface according to its material and lighting conditions.

### Implemented Surfaces:
**Spheres** - Each sphere is defined by the position of its center and its radius

**Infinite Planes** - Each plane is defined by its normal N and an offset c along the normal.

**Boxes** - Each box is defined by the position of its center (x, y, z) and its edge length (scalar). All boxes are axis aligned (meaning no rotations) to make the computation of intersections easier.

### Special Features:
**Soft Shadows:** To generate soft shadows, we will send several shadow rays from the light source to a point on the surface. The light intensity that hits the surface from this light source will be multiplied by the number of rays that hit the surface divided by the total number of rays we sent. The sent rays should simulate a light which has a certain area, Each light is defined with a light radius.

**Fisheye Model:** The fisheye lenses give an artistic effect to images. A fisheye lens that is positioned at the camera position, “deforms” the incoming rays from the image plane, therefore we should account for that deformation with regards to the sensor plane.
### Input:
The first parameter is the scene file, and the second is the name of the image file to write. Those two are mandatory. The next two parameters are optional and define the image width and height, respectively. The default size is set to 500x500.

### Output:


The rendered scene (.png file).

### Execution Instructions:

Run the following code in the command line:

```htmlhendahan12@gmail.com
java -jar RayTrace.jar scenes\Spheres.txt scenes\Spheres.png 500 500
```

### Results:

<img src="https://github.com/Inbalavivi/RayTracer/blob/15293695e2e0c27bd54e521125cad76c289e2094/Pool.png" width="200" height="200">    <img src="https://github.com/Inbalavivi/RayTracer/blob/15293695e2e0c27bd54e521125cad76c289e2094/Pool_fish.png" width="200" height="200">  <img src="https://github.com/Inbalavivi/RayTracer/blob/15293695e2e0c27bd54e521125cad76c289e2094/Room1_fish.png" width="200" height="200">

<img src="https://github.com/Inbalavivi/RayTracer/blob/15293695e2e0c27bd54e521125cad76c289e2094/Transparency.png" width="200" height="200">    <img src="https://github.com/Inbalavivi/RayTracer/blob/15293695e2e0c27bd54e521125cad76c289e2094/spheres.png" width="200" height="200">  <img src="https://github.com/Inbalavivi/RayTracer/blob/15293695e2e0c27bd54e521125cad76c289e2094/room1.png" width="200" height="200">

### Team Members:
Hen Dahan- hendahan12@gmail.com

Inbal Avivi- inbalavivi2@gmail.com
