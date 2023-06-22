package com.ozank.simulator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestModelSimulate {



    Map<String,Integer> left1 = new HashMap<>() {{
        put("Predator", 1);
    }};
    Map<String,Integer> right1 = new HashMap<>();

    double rate1 = 100.0;

    Map<String,Integer> left2 = new HashMap<>() {{
        put("Prey",1);
    }};

    Map<String,Integer> right2 = new HashMap<>() {{
        put("Prey",2);
    }};

    double rate2 = 300.0;

    Map<String,Integer> left3 = new HashMap<>() {{
        put("Predator",1 );
        put("Prey", 1);
    }};
    Map<String,Integer> right3 = new HashMap<>() {{
        put("Predator", 2);
    }};

    double rate3 = 1.0;

    Reaction r1 = new Reaction("r1",rate1,left1,right1);
    Reaction r2 = new Reaction("r2",rate2,left2,right2);
    Reaction r3 = new Reaction("r3",rate3,left3,right3);

    Map<String,Integer> initialState = new HashMap<>(){{
            put("Predator",100);
            put("Prey", 100);
    }};

    @Test
    void test() {
        List<Reaction> reactions = new ArrayList<>();
        reactions.add(r1);
        reactions.add(r2);
        reactions.add(r3);

        SimulationModel lotkaVoltera = new SimulationModel(reactions,initialState);
        lotkaVoltera.printReactions();
        lotkaVoltera.printDependencies();
        lotkaVoltera.printState();
        SimulationSSA simulation = new SimulationSSA(lotkaVoltera);
        simulation.simulateWithStepNumber(100);
        simulation.printTrajectory();
    }
}
