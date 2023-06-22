package com.ozank.simulator;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class MatrixTest {
    Matrix m;
    Matrix f;

    PairIndex p = new PairIndex(1,1);
    TripleIndex t = new TripleIndex(1,1,1);

    @BeforeEach
    public void init(){
        m = new Matrix<PairIndex>();
        f = new Matrix<TripleIndex>();
    }
    @Test
    public void incrementingANewCellSetsThatCellOne(){
        m.increment(p);
        f.increment(t);
        assertEquals(m.get(p),1);
        assertEquals(f.get(t),1);
    }

    @Test
    public void incrementingACellAddsOne(){
        m.increment(p);
        m.increment(p);
        f.increment(t);
        f.increment(t);
        assertEquals(m.get(p),2);
        assertEquals(f.get(t),2);
    }

    @Test
    public void decrementingACellWithOneTwiceThrowsException(){
        m.increment(p);
        m.decrement(p);
        f.increment(t);
        f.decrement(t);
        assertThrows(IllegalArgumentException.class,()-> m.decrement(p));
        assertThrows(IllegalArgumentException.class,()-> f.decrement(t));
    }

    @Test
    public void incrementingWithTwoNewPairsReturnsTwo(){
        PairIndex p1 = new PairIndex(1,1);
        TripleIndex t1 = new TripleIndex(1,1,1);
        m.increment(p);
        m.increment(p1);
        f.increment(t);
        f.increment(t1);
        assertEquals(2,m.get(new PairIndex(1,1)));
        assertEquals(2,f.get(new TripleIndex(1,1,1)));
    }

    @Test
    public void matrixAdditionTest(){
        Matrix<TripleIndex> m1 = new Matrix<>();
        m1.add(new TripleIndex(1,1,1),2);
        m1.add(new TripleIndex(1,2,1),5);
        m1.add(new TripleIndex(1,1,2),4);
        Matrix<TripleIndex> m2 = new Matrix<>();
        m2.add(new TripleIndex(1,1,1),2);
        m2.add(new TripleIndex(1,2,1),3);
        m2.add(new TripleIndex(1,1,4),9);
        //~~~~~~~~~~~~~~~~~~~~~
        m1.matrixAddition(m2);
        for (TripleIndex t : m1.keySet()){
            System.out.println(t + " : " + m1.get(t));
        }
        assertEquals(4,m1.get(new TripleIndex(1,1,1)));
        assertEquals(8,m1.get(new TripleIndex(1,2,1)));
        assertEquals(4,m1.get(new TripleIndex(1,1,2)));
        assertEquals(9,m1.get(new TripleIndex(1,1,4)));
    }

    @Test
    public void matrixRemovalTest(){
        Matrix<TripleIndex> m1 = new Matrix<>();
        m1.add(new TripleIndex(1,1,1),2);
        m1.add(new TripleIndex(1,2,1),5);
        m1.add(new TripleIndex(1,1,2),4);
        // m1.printMatrix();
        // System.out.println("~~~~~~~~");
        Matrix<TripleIndex> m2 = new Matrix<>();
        m2.add(new TripleIndex(1,1,1),2);
        m2.add(new TripleIndex(1,2,1),3);
        m2.add(new TripleIndex(1,1,4),9);
        // m2.printMatrix();
        // System.out.println("~~~~~~~~");
        m1.matrixSubtraction(m2);
        // m1.printMatrix();
        // ~~~~~~~~~~~~~~~~~~~~~~~
        assertFalse(m1.containsKey(new TripleIndex(1, 1, 1)));
        assertEquals(2,m1.get(new TripleIndex(1,2,1)));
        assertEquals(4,m1.get(new TripleIndex(1,1,2)));
        assertEquals(-9,m1.get(new TripleIndex(1,1,4)));
    }

    @Test
    public void matrixDivisionTest(){
        Matrix<TripleIndex> m1 = new Matrix<>();
        m1.add(new TripleIndex(1,1,1),2);
        m1.add(new TripleIndex(1,2,1),5);
        m1.add(new TripleIndex(1,1,2),4);
        m1.add(new TripleIndex(1,1,4),9);
        m1.divideByScalar(2);
        // ~~~~~~~~~~~~~~~~~~~~~~~
        for (TripleIndex t : m1.keySet()){
            System.out.println(t + " : " + m1.get(t));
        }
        assertEquals(1,m1.get(new TripleIndex(1,1,1)));
        assertEquals(2,m1.get(new TripleIndex(1,2,1)));
        assertEquals(2,m1.get(new TripleIndex(1,1,2)));
        assertEquals(4,m1.get(new TripleIndex(1,1,4)));
    }
}
