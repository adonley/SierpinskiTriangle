package network.bitmesh.com.sierpinsktriangle.Calculation;

/**
 * Created by Andrew Donley on 5/16/15.
 */
public class Vertex
{
   private float x;
   private float y;

   public Vertex(float x, float y)
   {
      this.x = x;
      this.y = y;
   }

   public float getX()
   {
      return x;
   }

   public void setX(float x)
   {
      this.x = x;
   }

   public float getY()
   {
      return y;
   }

   @Override
   public String toString()
   {
      return "Vertex{" +
            "x=" + x +
            ", y=" + y +
            '}';
   }
}

