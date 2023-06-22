package com.ozank.simulator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Matrix<T> {
    private final Map<T,Integer> matrix = new HashMap<>();

    public Map<T, Integer> getMatrixM() {
        return matrix;
    }

    public int get(T p){
        return matrix.get(p);
    }

    public void set(T p,int value){

        matrix.put(p,value);
    }

    public boolean containsKey(T p){
        return matrix.containsKey(p);
    }
    public void increment(T p){
        int value = matrix.getOrDefault(p,0);
        matrix.put(p, value+1);
    }
    public void add(T p,int addedValue){
        int value = matrix.getOrDefault(p,0);
        matrix.put(p, value + addedValue);
    }

    public void remove(T p,int removedValue){
        int value = matrix.getOrDefault(p,0);
        matrix.put(p, value - removedValue);
    }

    public void decrement(T p){
        if (!matrix.containsKey(p)){
            throw new IllegalArgumentException();
        }
        int value = matrix.get(p);
        if (value==0) {
            throw new IllegalArgumentException();
        }
        matrix.put(p,value-1);
    }

    public void matrixAddition(Matrix<T> otherMatrix){
        for (T index : otherMatrix.keySet()){
            add(index,otherMatrix.get(index));
        }
    }

    public void matrixSubtraction(Matrix<T> otherMatrix){
        for (T index : otherMatrix.keySet()){
            remove(index,otherMatrix.get(index));
            if (matrix.get(index)==0) { matrix.remove(index); }
        }
    }

    public void divideByScalar(int denominator){
        for (T index : matrix.keySet()){
            matrix.put(index,matrix.get(index)/denominator);
        }
    }

    public Set<T> keySet(){
        return matrix.keySet();
    }

    public void printMatrix(){
        for (T key : matrix.keySet()){
            System.out.println(key + " : " + matrix.get(key));
        }
    }
}
