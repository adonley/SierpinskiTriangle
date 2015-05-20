package network.bitmesh.com.sierpinsktriangle.Calculation;

/**
 * Created by Andrew Donley on 5/16/15.
 */
public class Triangle
{
   // Gotta be 2D Triangle
   private Vertex v1;
   private Vertex v2;
   private Vertex v3;

   public Triangle(Vertex v1,
                   Vertex v2,
                   Vertex v3)
   {
      this.v1 = v1;
      this.v2 = v2;
      this.v3 = v3;
   }

   public Vertex getV1()
   {
      return v1;
   }

   public void setV1(Vertex v1)
   {
      this.v1 = v1;
   }

   public Vertex getV2()
   {
      return v2;
   }

   public void setV2(Vertex v2)
   {
      this.v2 = v2;
   }

   public Vertex getV3()
   {
      return v3;
   }

   public void setV3(Vertex v3)
   {
      this.v3 = v3;
   }

   @Override
   public String toString()
   {
      return "Triangle{" +
            "v1=" + v1 +
            ", v2=" + v2 +
            ", v3=" + v3 +
            '}';
   }
}
