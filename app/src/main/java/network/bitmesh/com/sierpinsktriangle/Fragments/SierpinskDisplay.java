package network.bitmesh.com.sierpinsktriangle.Fragments;

import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import network.bitmesh.com.sierpinsktriangle.Calculation.Triangle;
import network.bitmesh.com.sierpinsktriangle.Calculation.Vertex;
import network.bitmesh.com.sierpinsktriangle.R;

public class SierpinskDisplay extends Fragment implements SurfaceHolder.Callback
{

   private SurfaceView sierpinskSurfaceView;
   private SurfaceHolder surfaceHolder;
   private SierpinskDrawer drawer;
   private Thread drawingThread;

   public SierpinskDisplay() { }

   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState)
   {
      // Inflate the layout for this fragment
      View rootView = inflater.inflate(R.layout.fragment_sierpinsk_display, container, false);

      sierpinskSurfaceView = (SurfaceView) rootView.findViewById(R.id.sierpinsk_surface_view);
      surfaceHolder = sierpinskSurfaceView.getHolder();
      // This fragment implements the Callback interface
      surfaceHolder.addCallback(this);

      return rootView;
   }

   @Override
   public void onStart()
   {
      super.onStart();
   }

   @Override
   public void surfaceCreated(SurfaceHolder holder)
   {
      // If this shit doesn't work then we're fucked anyway - try to keep the
      // computation at this point on this thread
      if(surfaceHolder.getSurface().isValid())
      {
         Canvas canvas = surfaceHolder.lockCanvas();
         int canvasHeight = canvas.getHeight();
         int canvasWidth = canvas.getWidth();

         Log.i("Canvas Size", "Height: " + canvas.getHeight() + "Width: " + canvas.getWidth());

         // Quickly generate a triangle
         Triangle triangle = generateTriangle(canvasWidth, canvasHeight);
         // Get the drawing thread ready to draw
         drawer = new SierpinskDrawer(triangle);

         surfaceHolder.unlockCanvasAndPost(canvas);
      }

      // Initiate and run the drawing thread.
      drawingThread = new Thread(drawer);
      drawer.run();
   }

   @Override
   public void surfaceChanged(SurfaceHolder holder,
                              int format,
                              int width,
                              int height)
   {

   }

   @Override
   public void surfaceDestroyed(SurfaceHolder holder)
   {

   }

   protected double getEuclidDistance(Vertex v1, Vertex v2)
   {
      float diffX = v1.getX() - v2.getX();
      float diffY = v2.getY() - v2.getY();
      return Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
   }

   protected boolean validateTriangle(Triangle triangle, int width, int height)
   {
      int size = Math.min(width, height);

      if(triangle == null)
         return false;

      // Check first and second point distance
      if(getEuclidDistance(triangle.getV1(), triangle.getV2()) < size*.2)
         return false;

      // Check first and third point distance
      if(getEuclidDistance(triangle.getV1(), triangle.getV3()) < size*.2)
         return false;

      // Check second and third point distance
      if(getEuclidDistance(triangle.getV2(), triangle.getV3()) < size*.2)
         return false;

      // Check collinearity
      if(areColinearPoints(triangle.getV1(), triangle.getV2(), triangle.getV3()))
         return false;

      return true;
   }

   protected boolean areColinearPoints(Vertex v1, Vertex v2, Vertex v3)
   {
      double slope = (v1.getY() - v2.getY()) / (v1.getX() - v2.getX());
      if(v3.getY() == (v3.getY() * slope - v1.getY()))
         return true;
      return false;
   }

   protected Vertex generateVertex(double minX, double maxX, double minY, double maxY)
   {
      Vertex vertex = null;

      // Generate new random vertex X and Y coordinates
      Random random = new Random(new Date().getTime());
      int x = random.nextInt((int)Math.floor(maxX-minX)) + (int)Math.floor(minX);
      int y = random.nextInt((int)Math.floor(maxY-minY)) + (int)Math.floor(minY);

      vertex = new Vertex(x,y);

      return vertex;
   }

   protected Triangle generateTriangle(int width, int height)
   {
      Triangle triangle = null;

      int iterations = 0;

      do
      {
         // Let's make sure we aren't spending too much time getting a rando
         // triangle.
         Log.i("generateTriangle", (++iterations) + "th iteration." );
         Vertex v1, v2, v3;
         // Might need to tweek these
         v1 = generateVertex(width*.4, width*.6, 0, height*.2);
         v2 = generateVertex(width*.5, width, 0, height);
         v3 = generateVertex(0, width*.5, .5*height, height);
         //v1 = new Vertex((float)(width*.5),(float)0.0);
         //v2 = new Vertex((float)width,width);
         //v3 = new Vertex((float)0,width);
         triangle = new Triangle(v1, v2, v3);

      } while(!validateTriangle(triangle, width, height));

      Log.i("generateTriangle","Generate Triangle");
      return triangle;
   }

   private class SierpinskDrawer implements Runnable
   {
      Triangle triangle;
      Paint vertexPaint;
      int depth = 10;

      List<Triangle> triangleList = new ArrayList<Triangle>();

      public SierpinskDrawer(Triangle triangle)
      {
         super();
         this.triangle = triangle;
         // Set the stroke size
         vertexPaint = new Paint();
         vertexPaint.setColor(Color.WHITE);
         vertexPaint.setStyle(Paint.Style.STROKE);
         vertexPaint.setAntiAlias(true);
         vertexPaint.setStrokeWidth(.2f);
      }

      @Override
      public void run()
      {
         recurseTri(this.triangle, this.depth);
         drawit();
      }

      protected Vertex calculateHalf(Vertex v1, Vertex v2)
      {
         return new Vertex(Math.abs((v1.getX()+v2.getX())/2),Math.abs((v1.getY()+v2.getY())/2));
      }

      protected synchronized void drawTriangle(Triangle triangle)
      {
         Canvas canvas = surfaceHolder.lockCanvas();

         Path p1 = new Path();
         p1.moveTo(triangle.getV1().getX(), triangle.getV1().getY());
         p1.lineTo(triangle.getV2().getX(), triangle.getV2().getY());
         canvas.drawPath(p1, vertexPaint);
         p1.lineTo(triangle.getV3().getX(), triangle.getV3().getY());
         canvas.drawPath(p1, vertexPaint);
         p1.lineTo(triangle.getV1().getX(), triangle.getV1().getY());
         canvas.drawPath(p1, vertexPaint);

         surfaceHolder.unlockCanvasAndPost(canvas);
      }

      protected void recurseTri(Triangle triangle, int depth)
      {

         triangleList.add(triangle);

         if(depth > 0)
         {
            Vertex halfV12 = calculateHalf(triangle.getV1(),triangle.getV2());
            Vertex halfV13 = calculateHalf(triangle.getV1(),triangle.getV3());
            Vertex halfV23 = calculateHalf(triangle.getV2(),triangle.getV3());

            Triangle triangle1 = new Triangle(halfV13,halfV12,triangle.getV1());
            Triangle triangle2 = new Triangle(halfV12,halfV23,triangle.getV2());
            Triangle triangle3 = new Triangle(halfV13,halfV23,triangle.getV3());

            recurseTri(triangle1, depth - 1);
            recurseTri(triangle2, depth - 1);
            recurseTri(triangle3, depth - 1);
         }

      }

      protected void drawit()
      {
         Canvas canvas = surfaceHolder.lockCanvas();
         canvas.drawColor(Color.TRANSPARENT);

         for(Triangle triangle : triangleList)
         {
            Path p1 = new Path();
            p1.moveTo(triangle.getV1().getX(), triangle.getV1().getY());
            p1.lineTo(triangle.getV2().getX(), triangle.getV2().getY());
            canvas.drawPath(p1, vertexPaint);
            p1.lineTo(triangle.getV3().getX(), triangle.getV3().getY());
            canvas.drawPath(p1, vertexPaint);
            p1.lineTo(triangle.getV1().getX(), triangle.getV1().getY());
            canvas.drawPath(p1, vertexPaint);
         }
         surfaceHolder.unlockCanvasAndPost(canvas);
      }

   }
}
