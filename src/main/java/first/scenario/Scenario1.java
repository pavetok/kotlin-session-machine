package first.scenario;

import first.engine.Engine;

public class Scenario1 {

    public Context2 s1(Context1 context) {
        Engine.crab(this::s2);
        Context2 c = new Context2();
        return c;
    }

    public Context3 s2(Context2 o) {
        return null;
    }

    public Context4 s3(Context3 o) {
        return null;
    }
}
