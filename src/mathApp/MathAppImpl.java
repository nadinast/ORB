package mathApp;

public class MathAppImpl implements MathApp {

    @Override
    public float do_add(float a, float b) {
        return a+b;
    }

    @Override
    public float do_sqrt(float a) {
        return (float)Math.sqrt(a);
    }
}
