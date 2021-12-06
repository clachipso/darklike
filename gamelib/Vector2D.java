
package clachipso.gamelib;

public class Vector2D 
{
    public double x;
    public double y;

    public Vector2D()
    {
        x = 0;
        y = 0;
    }

    public Vector2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public void set(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public void add(Vector2D otherVector)
    {
        this.x += otherVector.x;
        this.y += otherVector.y;
    }

    public void subtract(Vector2D rightOperand)
    {
        this.x -= rightOperand.x;
        this.y -= rightOperand.y;
    }

    public void scale(double scalar)
    {
        this.x *= scalar;
        this.y *= scalar;
    }

    public boolean equals(Vector2D otherVector)
    {
        boolean equal = false;

        if (this.x == otherVector.x && this.y == otherVector.y)
        {
            equal = true;
        }

        return equal;
    }

    public double magnitude()
    {
        return Math.sqrt((x * x) + (y * y));
    }

    public void rotate(double radians)
    {
        double cosine = Math.cos(radians);
        double sine = Math.sin(radians);

        double tempX = (x * cosine) - (y * sine);
        double tempY = (x * sine) + (y * cosine);
        this.x = tempX;
        this.y = tempY;
    }

    public void rotateAboutVector(double radians, Vector2D point)
    {
        // Translate to origin.
        double transX = x - point.x;
        double transY = y - point.y;

        // Rotate.
        double cosine = Math.cos(radians);
        double sine = Math.sin(radians);

        double rotX = (transX * cosine) - (transY * sine);
        double rotY = (transX * sine) + (transY * cosine);

        // Translate back.
        this.x = rotX + point.x;
        this.y = rotY + point.y;
    }

    public void normalize()
    {
        double mag = this.magnitude();
        x = x / mag;
        y = y / mag;
    }

    public static Vector2D add(Vector2D a, Vector2D b)
    {
        Vector2D sum = new Vector2D(a.x + b.x, a.y + b.y);
        return sum;
    }


    public static Vector2D subtract(Vector2D left, Vector2D right)
    {
        return new Vector2D(left.x - right.x, left.y - right.y);
    }

    public static Vector2D scale(Vector2D vector, double scalar)
    {
        Vector2D scaledVector = new Vector2D();
        scaledVector.x = vector.x * scalar;
        scaledVector.y = vector.y * scalar;
        return scaledVector;
    }

    public static double dotProduct(Vector2D a, Vector2D b)
    {
        return (a.x * b.x) + (a.y * b.y);
    }

    public static double distance(Vector2D a, Vector2D b)
    {
        double xDiff = (b.x - a.x);
        double yDiff = (b.y - a.y);
        return Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
    }
}

