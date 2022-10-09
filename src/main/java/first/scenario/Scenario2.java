package first.scenario;

import static first.engine.Engine.*;

public class Scenario2 {

    private Scenario2 scenario2 = this;
    private Scenario3 scenario3;

    public void s1(Context1 ctx) {
        var c = new Context2();
        send(c, scenario2::s2);
    }

    public void s2(Context2 ctx) {
    }

    public void s3(Context3 ctx) {
        var c = new Context4();
        receive(scenario2::s4, send2(c, scenario3::s1));
    }

    public void s4(Context5 ctx) {

    }
}
