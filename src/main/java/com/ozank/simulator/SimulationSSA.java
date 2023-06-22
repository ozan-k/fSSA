package com.ozank.simulator;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SimulationSSA {

    Random r = new Random();
    private SimulationModel model;
    private Matrix<PairIndex> matrixM;
    private Matrix<TripleIndex> matrixF;
    private double[] aj;
    private int[] state;
    private int[] state_y;
    private double time;

    private ReactionComponent[] reactionsLeft;
    private ReactionComponent[] reactionsRight;
    private double[] reactionRates;
    private DependentReactions[] reactionDependencies;

    private List<TrajectoryState> trajectory;

    public SimulationSSA(SimulationModel model){
        this.model = model;

        int[] initialState = model.getState();
        state = Arrays.copyOf(initialState,initialState.length);
        state_y = Arrays.copyOf(initialState,initialState.length);

        reactionsLeft = model.getLeft();
        reactionsRight = model.getRight();
        reactionRates = model.getReactionRates();
        reactionDependencies = model.getReactionDependencies();

        aj = new double[reactionsLeft.length];
        for (int i = 1; i<reactionsLeft.length;i++) {
            aj[i] = computePropensity(i);
            aj[0] += aj[i];
        }

        time = 0;
        trajectory = new ArrayList<>();
        updateTrajectory();

        matrixM = new Matrix<>();
        matrixF = new Matrix<>();
        for (int i=0;i<state.length;i++){
            matrixM.set(new PairIndex(i,0),state[i]);
        }
    }

    public static int combinatorial(int setSize,int subsetSize){
        if (setSize==0){ return 0;}
        int min = Math.min(subsetSize,setSize-subsetSize);
        int numerator = 1;
        int denominator = 1;
        for (int i=0;i<min;i++){
            numerator*=(setSize-i);
            denominator*=i+1;
        }
        return numerator/denominator;
    }

    private double computePropensity(int i){
        int moleculeCount;
        double result = reactionRates[i];
        ReactionComponent left = reactionsLeft[i];
        for (Integer moleculeIndex : left.keySet()){
            moleculeCount = state[moleculeIndex];
            if (moleculeCount==0) { return 0; }
            result *= combinatorial(moleculeCount,left.get(moleculeIndex));
        }
        return result;
    }

    private int computeNextReaction(){
        double random = r.nextDouble() * aj[0];
        double sum =0;
        for (int i=1;i< aj.length;i++){
            sum+=aj[i];
            if (sum > random){
                return i;
            }
        }
        return 0;
    }

    private double computeTau(){
        return ( 1 / aj[0] ) * Math.log( 1.0 / r.nextDouble() );
    }


    private void updatePropensities(DependentReactions reactions){
        reactions.getDependentReactions()
                .stream()
                .forEach(i-> {
                    aj[0] -= aj[i];
                    aj[i] = computePropensity(i);
                    aj[0] += aj[i];
                });
    }

    private void updateState(int reactionIndex){
        ReactionComponent left = reactionsLeft[reactionIndex];
        ReactionComponent right = reactionsRight[reactionIndex];
        for (int i : left.keySet()){ state[i] -= left.get(i); }
        for (int i : right.keySet()){state[i] += right.get(i);}
    }



    private void updateTrajectory(){
        int[] newState = Arrays.copyOf(state,state.length);
        trajectory.add(new TrajectoryState(time,newState));
    }

    private int getReactionOrigin(int speciesIndex){
        int random = r.nextInt(state_y[speciesIndex]);
        random++;
        int sum =0;
        for (int i=0;i<reactionsLeft.length;i++){
            PairIndex p = new PairIndex(speciesIndex,i);
            if (matrixM.containsKey(p)){
                sum+= matrixM.get(p);
                if (sum>=random){
                    return i;
                }
            }
        }
        return -1;
    }

    private void updateFluxes(int reactionIndex){
        ReactionComponent left = reactionsLeft[reactionIndex];
        for (Integer speciesIndex : left.keySet()){
            for (int i=0;i< left.get(speciesIndex);i++){
                int sourceReaction = getReactionOrigin(speciesIndex);
                matrixM.decrement(new PairIndex(speciesIndex,sourceReaction));
                state_y[speciesIndex]--;
                matrixF.increment(new TripleIndex(sourceReaction,reactionIndex,speciesIndex));
            }
        }
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        ReactionComponent right = reactionsRight[reactionIndex];
        for (Integer speciesIndex : right.keySet()){
            matrixM.add(new PairIndex(speciesIndex,reactionIndex),right.get(speciesIndex));
            state_y[speciesIndex] += right.get(speciesIndex);
        }
    }

    private void simulationStep(){
        time += computeTau();
        int mu = computeNextReaction();
        updateState(mu);
        updatePropensities(reactionDependencies[mu]);
        updateTrajectory();
        updateFluxes(mu);
    }

    public void simulateWithStepNumber(int n){
        for (int i=0; i<n; i++){
            if (aj[0] == 0) {
                break;
            } else {
                simulationStep();
            }
        }
    };


    public void simulateWithTimeLimit(double endTime){
       while(time <endTime){
            if (aj[0] == 0) {
                break;
            } else {
                simulationStep();
            }
        }
    }

    public Matrix<PairIndex> getM(){
        return matrixM;
    }

    public Matrix<TripleIndex> getF(){
        return matrixF;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void writeToFile(String filePath){
        // "src/main/resources/files-write-names.txt"
        String content =  trajectory.stream().map(x->x.toString()).reduce("", String::concat);
        Path path = Path.of(filePath);
        try {
            Files.writeString(path, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public void printPropensities(){
        System.out.println("Reaction propensities");
        for (int i=0; i< aj.length;i++){
            System.out.println(i + " : " + aj[i]);
        }
        System.out.println();
    }

    public void printTrajectory(){
        for (TrajectoryState ts : trajectory){
            System.out.print(ts);
        }
    }

    public void printSimulationState(){
        String result = IntStream.of(state)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(", "));
        System.out.println(time + "," + result + "\n");
        printPropensities();
    }

    public void printFluxes(){
        for (TripleIndex t : matrixF.keySet()){
            System.out.println(t.toString(model.getMoleculesList()) + " : " + matrixF.get(t));
        }
    }

    public void tester(){
    }

}
